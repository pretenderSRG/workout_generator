package org.generator.workout.service;

import lombok.RequiredArgsConstructor;
import org.generator.workout.dto.ExerciseInDayResponse;
import org.generator.workout.dto.ExerciseResponse;
import org.generator.workout.dto.WorkoutDayResponse;
import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.*;
import org.generator.workout.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmartWorkoutGeneratorService {

    private final ExerciseRepository exerciseRepository;

    public WorkoutProgramResponse generateSmartProgram(Long userId, EquipmentType equipment, SplitType splitType, int daysPerWeek) {
        List<Exercise> exercises = exerciseRepository.findByEquipment(equipment);

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises found for equipment: " + equipment);
        }

        Map<Integer, List<Exercise>> dayPlan = distributeExercisesByRules(exercises, daysPerWeek, splitType);

        return buildResponse(userId, dayPlan, equipment, splitType, daysPerWeek);
    }


    private Map<Integer, List<Exercise>> distributeExercisesByRules(List<Exercise> exercises, int daysPerWeek, SplitType splitType) {

        Map<Integer, List<Exercise>> plan = new HashMap<>();
        List<Exercise> allExercises = new ArrayList<>(exercises);

        // Exercises from previous day
        List<Exercise> previousDayExercises = new ArrayList<>();

        for (int day = 1; day <= daysPerWeek; day++) {
            List<Exercise> availableExercises = new ArrayList<>(allExercises);

            List<Exercise> dayToAvoid = new ArrayList<>(previousDayExercises);

            if (!dayToAvoid.isEmpty()) {
                availableExercises = availableExercises.stream()
                        .filter(ex -> dayToAvoid.stream().noneMatch(prev -> prev.getId().equals(ex.getId())))
                        .filter(ex -> !dayToAvoid.stream()
                                .anyMatch((prev -> prev.getMuscleGroup() == ex.getMuscleGroup() ||
                            prev.getSecondaryMuscles().contains(ex.getMuscleGroup()) ||
                            ex.getSecondaryMuscles().contains(prev.getMuscleGroup()) ||
                            prev.getSecondaryMuscles().stream().anyMatch(ex.getSecondaryMuscles()::contains)
                    )))
                        .toList();
            }

            List<Exercise> dayExercises = switch (splitType) {
                case PPL -> getExercisesForPPL(availableExercises, day);
                case UL -> getExercisesForUL(availableExercises, day);
                case FB -> getExercisesForFB(availableExercises);
            };

            dayExercises = balanceMuscleGroupByDay(dayExercises);

            plan.put(day, dayExercises);
            previousDayExercises = dayExercises;
        }
        return plan;
    }

    // PPL: Push, Pull, Legs, Core
    private List<Exercise> getExercisesForPPL(List<Exercise> exercises, int day) {
        return switch (day % 4) {
            case 1 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PUSH).toList();
            case 2 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PULL).toList();
            case 3 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.LEGS).toList();
            case 4 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.CORE).toList();
            default -> exercises;
        };

    }

    // UL: UPPER_LOWER
    private List<Exercise> getExercisesForUL(List<Exercise> exercises, int day) {
        return (day % 2 == 1) ?
                exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PUSH || e.getCategory() == ExerciseCategory.PULL).toList() :
                exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.LEGS || e.getCategory() == ExerciseCategory.CORE).toList();
    }

     // FL: FULL_BODY
    private List<Exercise> getExercisesForFB(List<Exercise> exercises) {
        Map<MuscleGroup, List<Exercise>> exerciseByMuscle = exercises.stream()
                .collect(Collectors.groupingBy(Exercise::getMuscleGroup));

        List<Exercise> dayExercise = new ArrayList<>();
        for (List<Exercise> groupExercises : exerciseByMuscle.values()) {
            Collections.shuffle(groupExercises);
            int toTake = Math.min(2, groupExercises.size());
            dayExercise.addAll(groupExercises.subList(0, toTake));
        }

        Collections.shuffle(dayExercise);
        if (dayExercise.size() > 6) {
            dayExercise = dayExercise.subList(0, 6);
        }
        return dayExercise;


    }


    private List<Exercise> balanceMuscleGroupByDay(List<Exercise> exercises) {
        Map<MuscleGroup, Long> muscleGroup = new HashMap<>();
        List<Exercise> balanceDay = new ArrayList<>();

        // Maximum exercises per group
        int maxPerGroup = 2;

        for (Exercise ex : exercises) {
            long currentCount = muscleGroup.getOrDefault(ex.getMuscleGroup(), 0L);
            if (currentCount < maxPerGroup) {
                balanceDay.add(ex);
                muscleGroup.put(ex.getMuscleGroup(), currentCount + 1);
            }
        }
        // Maximum number of exercises per day
        int maxExAtDay = 4;
        if (balanceDay.size() < maxExAtDay) {
            for (Exercise ex : exercises) {
                if (balanceDay.contains(ex)) {
                    continue;
                }
                boolean overlaps = false;
                for (Exercise existing : balanceDay) {
                    if (existing.getMuscleGroup() == ex.getMuscleGroup() ||
                            existing.getSecondaryMuscles().contains(ex.getMuscleGroup()) ||
                            ex.getSecondaryMuscles().contains(existing.getMuscleGroup())) {
                        overlaps = true;
                        break;
                    }
                }
                if (!overlaps && balanceDay.size() < maxExAtDay) {
                    balanceDay.add(ex);
                }
            }
        }
        return balanceDay;
    }

    private WorkoutProgramResponse buildResponse(Long userId, Map<Integer, List<Exercise>> dayPlan,
                                                 EquipmentType equipment, SplitType splitType, int daysPerWeek) {

        String programName = "My Smart " + daysPerWeek + "-day " + equipment.name() + " program";
        LocalDateTime createdAt = LocalDateTime.now();

        List<WorkoutDayResponse> dayResponses = dayPlan.entrySet().stream()
                .map(entry -> {
                    int dayNumber = entry.getKey();
                    List<Exercise> exercises = entry.getValue();

                    List<ExerciseInDayResponse> exerciseInDayResponses = exercises.stream()
                            .map(exercise -> new ExerciseInDayResponse(
                                    null,
                                    new ExerciseResponse(
                                            exercise.getId(),
                                            exercise.getName(),
                                            exercise.getDescription(),
                                            exercise.getEquipment().name(),
                                            exercise.getMuscleGroup().name(),
                                            exercise.getReps(),
                                            exercise.getSets()
                                    ),
                                    exercises.indexOf(exercise) + 1
                            )).toList();
                    return new WorkoutDayResponse(
                            null,
                            dayNumber,
                            exerciseInDayResponses
                    );
                }).toList();
        return new WorkoutProgramResponse(
                null,
                programName,
                equipment.name(),
                splitType.name(),
                daysPerWeek,
                createdAt,
                dayResponses
        );
    }

}

