package org.generator.workout.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.generator.workout.dto.ExerciseInDayResponse;
import org.generator.workout.dto.ExerciseResponse;
import org.generator.workout.dto.WorkoutDayResponse;
import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.*;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.repository.ExerciseRepository;
import org.generator.workout.repository.WorkoutProgramRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmartWorkoutGeneratorService {

    private final ExerciseRepository exerciseRepository;
    private final AppUserRepository userRepository;
    private final WorkoutProgramRepository programRepository;

    @Transactional
    public WorkoutProgramResponse generateSmartProgram(Long userId, EquipmentType equipment, SplitType splitType, int daysPerWeek) {
        log.info("Generating workout program for user ID: {}, equipment: {}, splitType: {}, daysPerWeek: {}",
                userId, equipment, splitType, daysPerWeek);

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() ->{
                    log.debug("User not found with ID: {}", userId);
                    return new IllegalArgumentException("User not found: " + userId);
                });

        List<Exercise> exercises = exerciseRepository.findByEquipment(equipment);

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises found for equipment: " + equipment);
        }

        // create workout program
        String programName = "My Smart " + daysPerWeek + "-day " + equipment.name() + " program";
        WorkoutProgram program = new WorkoutProgram(programName, equipment, splitType, daysPerWeek, user);

        log.info("Created program with user {}", program.getUser());

        Map<Integer, List<Exercise>> dayPlan = distributeExercisesByRules(exercises, daysPerWeek, splitType);

        // add days to plan
        for (Map.Entry<Integer, List<Exercise>> entry : dayPlan.entrySet()) {
            int dayNumber = entry.getKey();
            List<Exercise> dayExercises = entry.getValue();

            WorkoutDay day = new WorkoutDay(dayNumber, program);
            program.getDays().add(day);
            log.info("Add day № {}", dayNumber);

            int order = 1;
            for (Exercise exercise : dayExercises) {
                ExerciseInDay exerciseInDay = new ExerciseInDay(day, exercise, order++);
                day.getExercises().add(exerciseInDay);
                log.info("Add exercise - {}", exercise.getName());
            }
        }

        log.info("About to save program with user ID: {}", program.getUser().getId());
        // save program to DB
        WorkoutProgram savedProgram = programRepository.save(program);

        log.info("Successfully generated program ID: {} for user ID: {}", savedProgram.getId(), userId);
        // return DTO
        return buildResponse(savedProgram, equipment, splitType, daysPerWeek);
    }


    private Map<Integer, List<Exercise>> distributeExercisesByRules(List<Exercise> exercises, int daysPerWeek, SplitType splitType) {
        Map<Integer, List<Exercise>> plan = new HashMap<>();
        Set<Long> usedExercisesIds = new HashSet<>();

        for (int day = 1; day <= daysPerWeek; day++) {
            List<Exercise> availableExercises = exercises.stream()
                    .filter(ex -> !usedExercisesIds.contains(ex.getId())).toList();

            List<Exercise> dayExercises = switch (splitType) {
                case PPL -> getExercisesForPPL(availableExercises, day);
                case UL -> getExercisesForUL(availableExercises, day);
                case FB -> getExercisesForFB(availableExercises);
            };

            dayExercises = balanceMuscleGroupByDay(dayExercises, splitType, day);

            // Add exercises id to set
            dayExercises.forEach(ex -> usedExercisesIds.add(ex.getId()));

            plan.put(day, dayExercises);

        }
        return plan;
    }


    // PPL: Push, Pull, Legs, Core
    private List<Exercise> getExercisesForPPL(List<Exercise> exercises, int day) {
        return switch (day % 4) {
            case 1 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PUSH).toList();
            case 2 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PULL).toList();
            case 3 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.LEGS).toList();
            case 0 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.CORE).toList();
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

    private List<Exercise> balanceMuscleGroupByDay(List<Exercise> exercises, SplitType splitType, int dayPerWeek) {
        Map<MuscleGroup, Long> muscleGroup = new HashMap<>();
        List<Exercise> balanceDay = new ArrayList<>();

        int maxExercisePerGroup = 2;
        int maxExercisePerDay = 4;

        if (splitType == SplitType.PPL && (dayPerWeek % 4 == 3)) {
            exercises = exercises.stream()
                    .sorted(Comparator.comparing(ex -> ex.getMuscleGroup() == MuscleGroup.LEGS ? 0 : 1)).toList();
        }

        for (Exercise ex : exercises) {
            long currentCount = muscleGroup.getOrDefault(ex.getMuscleGroup(), 0L);
            if (currentCount < maxExercisePerGroup && balanceDay.size() < maxExercisePerDay) {
                balanceDay.add(ex);
                muscleGroup.put(ex.getMuscleGroup(), currentCount + 1);
            }
        }


        return balanceDay;
    }

    private WorkoutProgramResponse buildResponse(WorkoutProgram program, EquipmentType equipment, SplitType splitType, int daysPerWeek) {

        List<WorkoutDayResponse> dayResponses = program.getDays().stream()
                .sorted(Comparator.comparing(WorkoutDay::getDayNumber))
                .map(day -> {
                    List<ExerciseInDayResponse> exerciseInDayResponses = day.getExercises().stream()
                            .sorted(Comparator.comparing(ExerciseInDay::getOrderInDay))
                            .map(exInDay -> new ExerciseInDayResponse(
                                    exInDay.getId(),
                                    new ExerciseResponse(
                                            exInDay.getExercise().getId(),
                                            exInDay.getExercise().getName(),
                                            exInDay.getExercise().getDescription(),
                                            exInDay.getExercise().getEquipment().name(),
                                            exInDay.getExercise().getMuscleGroup().name(),
                                            exInDay.getExercise().getReps(),
                                            exInDay.getExercise().getSets()
                                    ),
                                    exInDay.getOrderInDay()
                            ))
                            .toList();

                    return new WorkoutDayResponse(
                            day.getId(),
                            day.getDayNumber(),
                            exerciseInDayResponses
                    );
                })
                .toList();

        return new WorkoutProgramResponse(
                program.getId(),
                program.getName(),
                equipment.name(),
                splitType.name(),
                program.getDaysPerWeek(),
                program.getCreatedAt(),
                dayResponses
        );
    }
}

