package org.generator.workout.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.generator.workout.model.*;
import org.generator.workout.repository.ExerciseRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ExerciseRepository exerciseRepository;

    @PostConstruct
    public void init() {
        if (exerciseRepository.count() == 0) {
            System.out.println("DataInitializer: No exercises found, adding default exercises...");

            // BODYWEIGHT exercises
            exerciseRepository.saveAll(Arrays.asList(
                createExercise("Push-ups", "Classic push-ups", EquipmentType.BODYWEIGHT, MuscleGroup.CHEST,
                        ExerciseCategory.PUSH, ExerciseDifficulty.BEGINNER, ExerciseType.COMPOUND,
                        "8-15", 3, List.of(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS)),

                createExercise("Pull-ups", "Bodyweight pull-ups", EquipmentType.BODYWEIGHT, MuscleGroup.BACK,
                        ExerciseCategory.PULL, ExerciseDifficulty.INTERMEDIATE, ExerciseType.COMPOUND,
                        "6-12", 3, List.of(MuscleGroup.BICEPS, MuscleGroup.SHOULDERS)),

                createExercise("Pike Push-ups", "Shoulder focused push-ups", EquipmentType.BODYWEIGHT, MuscleGroup.SHOULDERS,
                        ExerciseCategory.PUSH, ExerciseDifficulty.INTERMEDIATE, ExerciseType.COMPOUND,
                        "8-12", 3, List.of(MuscleGroup.TRICEPS)),

                createExercise("Diamond Push-ups", "Triceps focused push-ups", EquipmentType.BODYWEIGHT, MuscleGroup.TRICEPS,
                        ExerciseCategory.PUSH, ExerciseDifficulty.INTERMEDIATE, ExerciseType.ISOLATION,
                        "8-12", 3, List.of(MuscleGroup.CHEST)),

                createExercise("Bodyweight Squats", "Basic bodyweight squats", EquipmentType.BODYWEIGHT, MuscleGroup.LEGS,
                        ExerciseCategory.LEGS, ExerciseDifficulty.BEGINNER, ExerciseType.COMPOUND,
                        "15-20", 3, List.of(MuscleGroup.CORE)),

                createExercise("Plank", "Hold plank position", EquipmentType.BODYWEIGHT, MuscleGroup.CORE,
                        ExerciseCategory.CORE, ExerciseDifficulty.BEGINNER, ExerciseType.ISOLATION,
                        "30-60s", 3, List.of(MuscleGroup.SHOULDERS, MuscleGroup.LEGS)),

                createExercise("Burpees", "Full body exercise", EquipmentType.BODYWEIGHT, MuscleGroup.FULL_BODY,
                        ExerciseCategory.CORE, ExerciseDifficulty.ADVANCED, ExerciseType.COMPOUND,
                        "10-15", 4, List.of(MuscleGroup.CHEST, MuscleGroup.LEGS, MuscleGroup.CORE)))
            );

            // DUMBBELLS exercises
            exerciseRepository.saveAll(Arrays.asList(
                createExercise("Dumbbell Bench Press", "Chest press with dumbbells", EquipmentType.DUMBBELLS, MuscleGroup.CHEST,
                        ExerciseCategory.PUSH, ExerciseDifficulty.INTERMEDIATE, ExerciseType.COMPOUND,
                        "8-12", 4, List.of(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS)),

                createExercise("Dumbbell Rows", "Bent-over rows", EquipmentType.DUMBBELLS, MuscleGroup.BACK,
                        ExerciseCategory.PULL, ExerciseDifficulty.INTERMEDIATE, ExerciseType.COMPOUND,
                        "10-12", 4, List.of(MuscleGroup.BICEPS, MuscleGroup.SHOULDERS)),

                createExercise("Dumbbell Shoulder Press", "Overhead shoulder press", EquipmentType.DUMBBELLS, MuscleGroup.SHOULDERS,
                        ExerciseCategory.PUSH, ExerciseDifficulty.INTERMEDIATE, ExerciseType.COMPOUND,
                        "8-12", 4, List.of(MuscleGroup.TRICEPS)),

                createExercise("Dumbbell Bicep Curls", "Bicep curls with dumbbells", EquipmentType.DUMBBELLS, MuscleGroup.BICEPS,
                        ExerciseCategory.PULL, ExerciseDifficulty.BEGINNER, ExerciseType.ISOLATION,
                        "10-15", 3, List.of(MuscleGroup.CORE)),

                createExercise("Dumbbell Tricep Extensions", "Overhead tricep extensions", EquipmentType.DUMBBELLS, MuscleGroup.TRICEPS,
                        ExerciseCategory.PUSH, ExerciseDifficulty.INTERMEDIATE, ExerciseType.ISOLATION,
                        "10-12", 3, List.of(MuscleGroup.SHOULDERS)),

                createExercise("Dumbbell Lunges", "Walking or stationary lunges", EquipmentType.DUMBBELLS, MuscleGroup.LEGS,
                        ExerciseCategory.LEGS, ExerciseDifficulty.INTERMEDIATE, ExerciseType.COMPOUND,
                        "10-12 each", 3, List.of(MuscleGroup.CORE)),

                createExercise("Dumbbell Russian Twists", "Core rotation exercise", EquipmentType.DUMBBELLS, MuscleGroup.CORE,
                        ExerciseCategory.CORE, ExerciseDifficulty.INTERMEDIATE, ExerciseType.ISOLATION,
                        "15-20", 3, List.of(MuscleGroup.SHOULDERS)),

                createExercise("Dumbbell Thrusters", "Squat to shoulder press", EquipmentType.DUMBBELLS, MuscleGroup.FULL_BODY,
                        ExerciseCategory.PUSH, ExerciseDifficulty.ADVANCED, ExerciseType.COMPOUND,
                        "8-12", 4, List.of(MuscleGroup.LEGS, MuscleGroup.SHOULDERS, MuscleGroup.CORE)))
            );

            // BARBELL exercises
            exerciseRepository.saveAll(Arrays.asList(
                createExercise("Barbell Bench Press", "Flat bench press with barbell", EquipmentType.BARBELL, MuscleGroup.CHEST,
                        ExerciseCategory.PUSH, ExerciseDifficulty.INTERMEDIATE, ExerciseType.COMPOUND,
                        "6-10", 4, List.of(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS)),

                createExercise("Barbell Rows", "Bent-over barbell rows", EquipmentType.BARBELL, MuscleGroup.BACK,
                        ExerciseCategory.PULL, ExerciseDifficulty.INTERMEDIATE, ExerciseType.COMPOUND,
                        "8-12", 4, List.of(MuscleGroup.BICEPS)),

                createExercise("Barbell Shoulder Press", "Military press", EquipmentType.BARBELL, MuscleGroup.SHOULDERS,
                        ExerciseCategory.PUSH, ExerciseDifficulty.INTERMEDIATE, ExerciseType.COMPOUND,
                        "6-10", 4, List.of(MuscleGroup.TRICEPS)),

                createExercise("Barbell Squats", "Back squats with barbell", EquipmentType.BARBELL, MuscleGroup.LEGS,
                        ExerciseCategory.LEGS, ExerciseDifficulty.ADVANCED, ExerciseType.COMPOUND,
                        "6-10", 4, List.of(MuscleGroup.CORE)),

                createExercise("Barbell Deadlift", "Full body lift from floor", EquipmentType.BARBELL, MuscleGroup.FULL_BODY,
                        ExerciseCategory.PULL, ExerciseDifficulty.ADVANCED, ExerciseType.COMPOUND,
                        "5-8", 4, List.of(MuscleGroup.LEGS, MuscleGroup.BACK, MuscleGroup.CORE)))
            );

            // GYM machines
            exerciseRepository.saveAll(Arrays.asList(
                createExercise("Machine Chest Press", "Chest press machine", EquipmentType.GYM, MuscleGroup.CHEST,
                        ExerciseCategory.PUSH, ExerciseDifficulty.BEGINNER, ExerciseType.COMPOUND,
                        "10-12", 4, List.of(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS)),

                createExercise("Lat Pulldown", "Lat pulldown machine", EquipmentType.GYM, MuscleGroup.BACK,
                        ExerciseCategory.PULL, ExerciseDifficulty.BEGINNER, ExerciseType.COMPOUND,
                        "10-12", 4, List.of(MuscleGroup.BICEPS)),

                createExercise("Cable Bicep Curls", "Bicep curls on cable", EquipmentType.GYM, MuscleGroup.BICEPS,
                        ExerciseCategory.PULL, ExerciseDifficulty.BEGINNER, ExerciseType.ISOLATION,
                        "12-15", 3, List.of()),

                createExercise("Leg Press", "Leg press machine", EquipmentType.GYM, MuscleGroup.LEGS,
                        ExerciseCategory.LEGS, ExerciseDifficulty.BEGINNER, ExerciseType.COMPOUND,
                        "10-15", 4, List.of(MuscleGroup.CORE)),

                createExercise("Cable Crunches", "Ab crunches on cable", EquipmentType.GYM, MuscleGroup.CORE,
                        ExerciseCategory.CORE, ExerciseDifficulty.INTERMEDIATE, ExerciseType.ISOLATION,
                        "15-20", 3, List.of(MuscleGroup.FULL_BODY)))
            );
        }
         System.out.println("DataInitializer: Initialization complete.");
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
