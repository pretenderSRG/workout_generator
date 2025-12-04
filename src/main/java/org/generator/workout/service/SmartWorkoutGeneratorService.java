package org.generator.workout.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.generator.workout.dto.ExerciseInDayResponse;
import org.generator.workout.dto.ExerciseResponse;
import org.generator.workout.dto.WorkoutDayResponse;
import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.exception.EntityNotFoundException;
import org.generator.workout.model.*;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.repository.ExerciseRepository;
import org.generator.workout.repository.WorkoutProgramRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmartWorkoutGeneratorService {

    private final ExerciseRepository exerciseRepository;
    private final AppUserRepository userRepository;
    private final WorkoutProgramRepository programRepository;
    private final WorkoutPlanerService workoutPlanerService;

    @Transactional
    public WorkoutProgramResponse generateSmartProgram(Long userId, EquipmentType equipment, SplitType splitType, int daysPerWeek) {
        log.info("Generating workout program for user ID: {}, equipment: {}, splitType: {}, daysPerWeek: {}",
                userId, equipment, splitType, daysPerWeek);

        AppUser user = getUserId(userId);

        List<Exercise> exercises = getExerciseByEquipment(equipment);

        WorkoutProgram program = createWorkoutProgram(user, equipment, splitType, daysPerWeek);
        List<WorkoutDay> days = workoutPlanerService.planDays(exercises, daysPerWeek, splitType);
        attachDaysToProgram(program, days);

        WorkoutProgram savedProgram = programRepository.save(program);

        return buildResponse(savedProgram, equipment, splitType, daysPerWeek);
    }

    private AppUser getUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.debug("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found: " + userId);
                });
    }

    private List<Exercise> getExerciseByEquipment(EquipmentType equipment) {
        List<Exercise> exercises = exerciseRepository.findByEquipment(equipment);
        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises found for equipment: " + equipment);
        }
        return exercises;
    }

     private WorkoutProgram createWorkoutProgram(AppUser user, EquipmentType equipment, SplitType splitType, int daysPerWeek) {
        String workoutProgramName = "My Smart " + daysPerWeek + "-day " + equipment.name() + " program";
        log.info("Created program for user {}", user.getUsername());
        return new WorkoutProgram(workoutProgramName, equipment, splitType, daysPerWeek, user);
    }

     private void attachDaysToProgram(WorkoutProgram program, List<WorkoutDay>  days) {
        for (WorkoutDay day : days) {
            day.setProgram(program);
            program.getDays().add(day);
        }
     }

    private WorkoutProgramResponse buildResponse(WorkoutProgram program, EquipmentType equipment, SplitType
            splitType, int daysPerWeek) {

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
