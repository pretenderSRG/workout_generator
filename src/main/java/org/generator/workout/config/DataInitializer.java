package org.generator.workout.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.generator.workout.model.*;
import org.generator.workout.repository.ExerciseRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ExerciseRepository exerciseRepository;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void init() throws IOException {
        if (exerciseRepository.count() == 0) {
            System.out.println("DataInitializer: No exercises found> loading from JSON...");

            ClassPathResource resource = new ClassPathResource("exercises.json");
            ExercisesData  data = objectMapper.readValue(resource.getInputStream(), ExercisesData.class);

            exerciseRepository.saveAll(data.getExercises());
            System.out.println("DataInitializer: Loaded " + data.getExercises().size() + " exercises from JSON.");
        } else {
            System.out.println("DataInitializer: Exercises already exist in DB");
        }


    }

    @Getter
    @Setter
    private static class ExercisesData {
        private List<Exercise> exercises;

    }


    private Exercise createExercise(String name, String description, EquipmentType equipment,
                                    MuscleGroup primary, ExerciseCategory category,
                                    ExerciseDifficulty difficult, ExerciseType type,
                                    String reps, Integer sets, List<MuscleGroup> secondary) {

        Exercise exercise = new Exercise(name, description, equipment, primary, reps, sets);
        exercise.setCategory(category);
        exercise.setDifficulty(difficult);
        exercise.setType(type);
        exercise.setSecondaryMuscles(secondary);
        return exercise;
    }
}
