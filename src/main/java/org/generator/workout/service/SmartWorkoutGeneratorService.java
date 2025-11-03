package org.generator.workout.service;

import jakarta.persistence.criteria.CriteriaBuilder;
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



    private Map<Integer,List<Exercise>> distributeExercisesByRules(List<Exercise> exercises, int daysPerWeek, SplitType splitType) {

        return switch (splitType) {
            case PPL -> distributeByPPL(exercises, daysPerWeek);
            case UL -> distributeByUL(exercises, daysPerWeek);
            case FB -> distributeByFB(exercises, daysPerWeek);
            default -> distributeByPPL(exercises, daysPerWeek);
        };

    }

    private Map<Integer, List<Exercise>> distributeByFB(List<Exercise> exercises, int daysPerWeek) {
        Map<Integer, List<Exercise>> plan = new HashMap<>();

        List<Exercise> allExercises = new ArrayList<>(exercises);
        Collections.shuffle(allExercises);

        int exercisePerDay = Math.max(1, allExercises.size()/daysPerWeek);

        for (int day = 1; day <= daysPerWeek; day++) {
            int start = (day - 1) * exercisePerDay;
            int end = Math.min(day * exercisePerDay, allExercises.size());
            plan.put(day, allExercises.subList(start, end));
        }
        return plan;
    }

    // PPL: Push, Pull, Legs, Core
    private Map<Integer, List<Exercise>> distributeByPPL(List<Exercise> exercises, int daysPerWeek) {
        Map<Integer, List<Exercise>> plan = new HashMap<>();

        List<Exercise> push = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PUSH).toList();

        List<Exercise> pull = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PULL).toList();

        List<Exercise> legs = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.LEGS).toList();

        List<Exercise> core = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.CORE).toList();

        if (daysPerWeek >= 1) plan.put(1, balanceMuscleGroupByDay(push));
        if (daysPerWeek >= 2) plan.put(2, balanceMuscleGroupByDay(pull));
        if (daysPerWeek >= 3) plan.put(3, balanceMuscleGroupByDay(legs));
        if (daysPerWeek >= 4) plan.put(4, balanceMuscleGroupByDay(core));

        if (daysPerWeek >=5) {
            for (int day = 5; day <= daysPerWeek; day++) {
                List<Exercise> randomDay = new ArrayList<>();
                randomDay.addAll(push);
                randomDay.addAll(pull);
                randomDay.addAll(legs);
                randomDay.addAll(core);
                Collections.shuffle(randomDay);
                plan.put(day, randomDay.subList(0, Math.min(4, randomDay.size())));
            }
        }
        return plan;

    }

    // UPPER_LOWER
    private Map<Integer, List<Exercise>> distributeByUL(List<Exercise> exercises, int daysPerWeek) {
        Map<Integer, List<Exercise>> plan = new HashMap<>();

        List<Exercise> upper = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PUSH
                || e.getCategory() == ExerciseCategory.PULL).toList();

        List<Exercise> lower = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.LEGS ||
                e.getCategory() == ExerciseCategory.CORE).toList();

        if (daysPerWeek >= 1) plan.put(1, balanceMuscleGroupByDay(upper));
        if (daysPerWeek >= 2) plan.put(2, balanceMuscleGroupByDay(lower));

        if(daysPerWeek >= 3) {
            for (int day = 3; day <= daysPerWeek; day++) {
                if (day %2 == 1) {
                    plan.put(day, balanceMuscleGroupByDay(upper));
                } else {
                    plan.put(day, balanceMuscleGroupByDay(lower));
                }
            }
        }
        return plan;
    }

    private List<Exercise> balanceMuscleGroupByDay(List<Exercise> exercises) {
        Map<MuscleGroup, Long> muscleGroup = new HashMap<>();
        List<Exercise> balanceDay = new ArrayList<>();

        // Maximum exercises per group
        int maxPerGroup = 2;

        for (Exercise ex: exercises) {
            long currentCount = muscleGroup.getOrDefault(ex.getMuscleGroup(), 0L);
            if (currentCount < maxPerGroup) {
                balanceDay.add(ex);
                muscleGroup.put(ex.getMuscleGroup(), currentCount +1);
            }
        }
        // Maximum number of exercises per day
        int maxExAtDay = 4;
        if (balanceDay.size() < maxExAtDay) {
            for (Exercise ex: exercises) {
                if (balanceDay.contains(ex)) {
                    continue;
                }
                boolean overlaps = false;
                for (Exercise existing: balanceDay) {
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
                                     exercises.indexOf(exercise) +1
                             )).toList();
                     return new WorkoutDayResponse(
                             null,
                             dayNumber,
                             exerciseInDayResponses
                     );
                 }).toList();
        return  new WorkoutProgramResponse(
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

