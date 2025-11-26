package org.generator.workout.service;

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

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkoutGeneratorService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutProgramRepository programRepository;
    private final AppUserRepository userRepository;

    public WorkoutGeneratorService(ExerciseRepository exerciseRepository,
                                   WorkoutProgramRepository programRepository,
                                   AppUserRepository userRepository) {
        this.exerciseRepository = exerciseRepository;
        this.programRepository = programRepository;
        this.userRepository = userRepository;
    }

    private AppUser verifyUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }

    public WorkoutProgramResponse generateProgram(Long userId, EquipmentType equipment, SplitType splitType, int daysPerWeek) {
        System.out.println("Generating program for user ID: " + userId);
        AppUser user = verifyUser(userId);

        List<Exercise> exercises = exerciseRepository.findByEquipment(equipment);

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises found for equipment: " + equipment);
        }

        String programName = String.format("My %d-day %s program", daysPerWeek, equipment.name());
        WorkoutProgram program = new WorkoutProgram(programName, equipment, splitType, daysPerWeek, user);

        int exercisesPerDay = Math.min(4, (int) Math.ceil((double) exercises.size() / daysPerWeek));
        int exercisesIndex = 0;

        for (int dayNum = 1; dayNum <= daysPerWeek; dayNum++) {
            WorkoutDay day = new WorkoutDay(dayNum, program);
            program.getDays().add(day);

            for (int order = 1; order <= exercisesPerDay && exercisesIndex < exercises.size(); order++) {
                Exercise exercise = exercises.get(exercisesIndex++);
                ExerciseInDay exerciseInDay = new ExerciseInDay(day, exercise, order);
                day.getExercises().add(exerciseInDay);
            }
        }

        WorkoutProgram savedProgram = programRepository.save(program);

        List<WorkoutDayResponse> dayResponses = savedProgram.getDays().stream()
                .map(day -> new WorkoutDayResponse(
                        day.getId(),
                        day.getDayNumber(),
                        day.getExercises().stream()
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
                                .collect(Collectors.toList())
                ))
                .toList();
        return new WorkoutProgramResponse(
                savedProgram.getId(),
                savedProgram.getName(),
                savedProgram.getEquipmentType().name(),
                savedProgram.getSplitType().name(),
                savedProgram.getDaysPerWeek(),
                savedProgram.getCreatedAt(),
                dayResponses
        );
    }

    public List<WorkoutProgramResponse> getUserWorkoutProgram(Long userId, EquipmentType equipment, SplitType splitType) {
        AppUser user = verifyUser(userId);

        List<WorkoutProgram> programs;

        if (equipment != null && splitType != null) {
            programs = programRepository.findByUserAndEquipmentTypeAndSplitType(user, equipment, splitType);
        } else if (equipment != null) {
            programs = programRepository.findByUserAndEquipmentType(user, equipment);
        } else if (splitType != null) {
            programs = programRepository.findByUserAndSplitType(user, splitType);
        } else {
            programs = programRepository.findByUser(user);
        }

            return programs.stream()
                    .map(program -> buildResponse(
                            program,
                            program.getEquipmentType(),
                            program.getSplitType(),
                            program.getDaysPerWeek()
                    )).toList();
    }

    public WorkoutProgramResponse getWorkoutProgramById(Long userId, Long programId) {
        AppUser user = verifyUser(userId);

        WorkoutProgram program = programRepository.findByIdAndUser(programId, user)
                .orElseThrow(() -> new IllegalArgumentException("Program not found or access denied: " + programId));

        List<WorkoutDayResponse> dayResponses = program.getDays().stream()
                .map(day -> new WorkoutDayResponse(
                        day.getId(),
                        day.getDayNumber(),
                        day.getExercises().stream()
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
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new WorkoutProgramResponse(
                program.getId(),
                program.getName(),
                program.getEquipmentType().name(),
                program.getSplitType().name(),
                program.getDaysPerWeek(),
                program.getCreatedAt(),
                dayResponses
        );
    }

    public void deleteWorkoutProgramById(Long userId, Long programId) {
        AppUser user = verifyUser(userId);

        WorkoutProgram program = programRepository.findByIdAndUser(programId, user)
                .orElseThrow(() -> new IllegalArgumentException("Program not found or access denied: " + programId));

        programRepository.delete(program);

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
