package org.generator.workout.config;

import org.generator.workout.model.EquipmentType;
import org.generator.workout.model.Exercise;
import org.generator.workout.model.MuscleGroup;
import org.generator.workout.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final ExerciseRepository exerciseRepository;

    @PostConstruct
    public void init() {
        if (exerciseRepository.count() == 0) {
            // Вправи для BODYWEIGHT
            exerciseRepository.saveAll(Arrays.asList(
                new Exercise("Push-ups", "Classic push-ups", EquipmentType.BODYWEIGHT, MuscleGroup.CHEST, "8-15", 3),
                new Exercise("Pull-ups", "Bodyweight pull-ups", EquipmentType.BODYWEIGHT, MuscleGroup.BACK, "6-12", 3),
                new Exercise("Pike Push-ups", "Shoulder focused push-ups", EquipmentType.BODYWEIGHT, MuscleGroup.SHOULDERS, "8-12", 3),
                new Exercise("Diamond Push-ups", "Triceps focused push-ups", EquipmentType.BODYWEIGHT, MuscleGroup.TRICEPS, "8-12", 3),
                new Exercise("Bodyweight Squats", "Basic bodyweight squats", EquipmentType.BODYWEIGHT, MuscleGroup.LEGS, "15-20", 3),
                new Exercise("Plank", "Hold plank position", EquipmentType.BODYWEIGHT, MuscleGroup.CORE, "30-60s", 3),
                new Exercise("Burpees", "Full body exercise", EquipmentType.BODYWEIGHT, MuscleGroup.FULL_BODY, "10-15", 4)
            ));

            // Вправи для DUMBBELLS
            exerciseRepository.saveAll(Arrays.asList(
                new Exercise("Dumbbell Bench Press", "Chest press with dumbbells", EquipmentType.DUMBBELLS, MuscleGroup.CHEST, "8-12", 4),
                new Exercise("Dumbbell Rows", "Bent-over rows", EquipmentType.DUMBBELLS, MuscleGroup.BACK, "10-12", 4),
                new Exercise("Dumbbell Shoulder Press", "Overhead shoulder press", EquipmentType.DUMBBELLS, MuscleGroup.SHOULDERS, "8-12", 4),
                new Exercise("Dumbbell Bicep Curls", "Bicep curls with dumbbells", EquipmentType.DUMBBELLS, MuscleGroup.BICEPS, "10-15", 3),
                new Exercise("Dumbbell Tricep Extensions", "Overhead tricep extensions", EquipmentType.DUMBBELLS, MuscleGroup.TRICEPS, "10-12", 3),
                new Exercise("Dumbbell Lunges", "Walking or stationary lunges", EquipmentType.DUMBBELLS, MuscleGroup.LEGS, "10-12 each", 3),
                new Exercise("Dumbbell Russian Twists", "Core rotation exercise", EquipmentType.DUMBBELLS, MuscleGroup.CORE, "15-20", 3),
                new Exercise("Dumbbell Thrusters", "Squat to shoulder press", EquipmentType.DUMBBELLS, MuscleGroup.FULL_BODY, "8-12", 4)
            ));

            // Вправи для BARBELL
            exerciseRepository.saveAll(Arrays.asList(
                new Exercise("Barbell Bench Press", "Flat bench press with barbell", EquipmentType.BARBELL, MuscleGroup.CHEST, "6-10", 4),
                new Exercise("Barbell Rows", "Bent-over barbell rows", EquipmentType.BARBELL, MuscleGroup.BACK, "8-12", 4),
                new Exercise("Barbell Shoulder Press", "Military press", EquipmentType.BARBELL, MuscleGroup.SHOULDERS, "6-10", 4),
                new Exercise("Barbell Bicep Curls", "Standing bicep curls", EquipmentType.BARBELL, MuscleGroup.BICEPS, "8-12", 3),
                new Exercise("Barbell Skull Crushers", "Lying tricep extensions", EquipmentType.BARBELL, MuscleGroup.TRICEPS, "8-12", 3),
                new Exercise("Barbell Squats", "Back squats with barbell", EquipmentType.BARBELL, MuscleGroup.LEGS, "6-10", 4),
                new Exercise("Barbell Rollouts", "Core exercise with barbell", EquipmentType.BARBELL, MuscleGroup.CORE, "8-12", 3),
                new Exercise("Barbell Clean and Press", "Olympic lift variation", EquipmentType.BARBELL, MuscleGroup.FULL_BODY, "5-8", 4)
            ));

            // Вправи для RESISTANCE_BANDS
            exerciseRepository.saveAll(Arrays.asList(
                new Exercise("Band Chest Press", "Chest press with resistance band", EquipmentType.RESISTANCE_BANDS, MuscleGroup.CHEST, "12-15", 3),
                new Exercise("Band Rows", "Seated or standing rows", EquipmentType.RESISTANCE_BANDS, MuscleGroup.BACK, "12-15", 3),
                new Exercise("Band Lateral Raises", "Shoulder lateral raises", EquipmentType.RESISTANCE_BANDS, MuscleGroup.SHOULDERS, "12-15", 3),
                new Exercise("Band Bicep Curls", "Bicep curls with band", EquipmentType.RESISTANCE_BANDS, MuscleGroup.BICEPS, "15-20", 3),
                new Exercise("Band Tricep Pushdowns", "Tricep extensions", EquipmentType.RESISTANCE_BANDS, MuscleGroup.TRICEPS, "12-15", 3),
                new Exercise("Band Squats", "Squats with band resistance", EquipmentType.RESISTANCE_BANDS, MuscleGroup.LEGS, "15-20", 3),
                new Exercise("Band Wood Chops", "Rotational core exercise", EquipmentType.RESISTANCE_BANDS, MuscleGroup.CORE, "12-15 each", 3),
                new Exercise("Band Deadlifts", "Full body pull exercise", EquipmentType.RESISTANCE_BANDS, MuscleGroup.FULL_BODY, "10-12", 4)
            ));

            // Вправи для GYM
            exerciseRepository.saveAll(Arrays.asList(
                new Exercise("Machine Chest Press", "Chest press machine", EquipmentType.GYM, MuscleGroup.CHEST, "10-12", 4),
                new Exercise("Lat Pulldown", "Lat pulldown machine", EquipmentType.GYM, MuscleGroup.BACK, "10-12", 4),
                new Exercise("Machine Shoulder Press", "Shoulder press machine", EquipmentType.GYM, MuscleGroup.SHOULDERS, "10-12", 4),
                new Exercise("Cable Bicep Curls", "Bicep curls on cable machine", EquipmentType.GYM, MuscleGroup.BICEPS, "12-15", 3),
                new Exercise("Cable Tricep Pushdown", "Tricep pushdown on cable", EquipmentType.GYM, MuscleGroup.TRICEPS, "12-15", 3),
                new Exercise("Leg Press", "Leg press machine", EquipmentType.GYM, MuscleGroup.LEGS, "10-15", 4),
                new Exercise("Cable Crunches", "Ab crunches on cable", EquipmentType.GYM, MuscleGroup.CORE, "15-20", 3),
                new Exercise("Assault Bike", "Full body cardio machine", EquipmentType.GYM, MuscleGroup.FULL_BODY, "30-60s", 5)
            ));
        }
    }
}