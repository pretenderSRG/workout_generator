package org.generator.workout.service;

import org.generator.workout.dto.ExerciseInDayResponse;
import org.generator.workout.dto.ExerciseResponse;
import org.generator.workout.dto.WorkoutDayResponse;
import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.*;
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

    public WorkoutGeneratorService(ExerciseRepository exerciseRepository,
                                   WorkoutProgramRepository programRepository) {
        this.exerciseRepository = exerciseRepository;
        this.programRepository = programRepository;
    }

    public WorkoutProgramResponse generateProgram(EquipmentType equipment, int daysPerWeek) {
        List<Exercise> exercises =  exerciseRepository.findByEquipment(equipment);

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises found fir equipment: " + equipment);
        }

        String programName = String.format("My %d-day %s program", daysPerWeek, equipment.name());
        WorkoutProgram program = new WorkoutProgram(programName, equipment, daysPerWeek);

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
}
