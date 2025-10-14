package org.generator.workout.service;

import org.generator.workout.model.*;
import org.generator.workout.repository.ExerciseRepository;
import org.generator.workout.repository.WorkoutProgramRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public WorkoutProgram generateProgram(EquipmentType equipment, int daysPerWeek) {
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

        return programRepository.save(program);
    }
}
