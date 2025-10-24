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

    public WorkoutProgramResponse generateProgram(Long userId, EquipmentType equipment, int daysPerWeek) {
        System.out.println("Generating program for user ID: " + userId);
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<Exercise> exercises =  exerciseRepository.findByEquipment(equipment);

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises found for equipment: " + equipment);
        }

        String programName = String.format("My %d-day %s program", daysPerWeek, equipment.name());
        WorkoutProgram program = new WorkoutProgram(programName, equipment, daysPerWeek, user);

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

        WorkoutProgram savedProgram =  programRepository.save(program);

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
                savedProgram.getDaysPerWeek(),
                savedProgram.getCreatedAt(),
                dayResponses
        );
    }

    public List<WorkoutProgramResponse> getUserWorkoutProgram(Long userId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<WorkoutProgram> programs = programRepository.findByUser(user);

        return programs.stream()
                .map(program -> {
                    List<WorkoutDayResponse> dayResponses = program.getDays().stream()
                            .map(day -> new WorkoutDayResponse(
                                    day.getId(),
                                    day.getDayNumber(),
                                    day.getExercises().stream()
                                            .map(exerciseInDay -> new ExerciseInDayResponse(
                                                    exerciseInDay.getId(),
                                                    new ExerciseResponse(
                                                            exerciseInDay.getExercise().getId(),
                                                            exerciseInDay.getExercise().getName(),
                                                            exerciseInDay.getExercise().getDescription(),
                                                            exerciseInDay.getExercise().getEquipment().name(),
                                                            exerciseInDay.getExercise().getMuscleGroup().name(),
                                                            exerciseInDay.getExercise().getReps(),
                                                            exerciseInDay.getExercise().getSets()
                                                    ),
                                                    exerciseInDay.getOrderInDay()
                                                    ))
                                            .toList()
                                    ))
                            .toList();
                          return new WorkoutProgramResponse(
                                  program.getId(),
                                  program.getName(),
                                  program.getEquipmentType().name(),
                                  program.getDaysPerWeek(),
                                  program.getCreatedAt(),
                                  dayResponses
                          );

                }).toList();
    }
}
