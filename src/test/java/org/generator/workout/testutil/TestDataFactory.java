package org.generator.workout.testutil;

import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TestDataFactory {

    public static AppUser createUser() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");
        return user;
    }

    public static List<Exercise> createBodyweightExercise() {
        return Arrays.asList(
                new Exercise("Push-up", "Push", EquipmentType.BODYWEIGHT, MuscleGroup.CHEST, "10", 3),
                new Exercise("Bench Press", "Push", EquipmentType.BODYWEIGHT, MuscleGroup.CHEST, "8", 4),
                new Exercise("Pull-up", "Pull", EquipmentType.BODYWEIGHT, MuscleGroup.BACK, "6", 3),
                new Exercise("Bent-over Row", "Pull", EquipmentType.BODYWEIGHT, MuscleGroup.BACK, "8", 3),
                new Exercise("Squat", "Legs", EquipmentType.BODYWEIGHT, MuscleGroup.LEGS, "8", 4),
                new Exercise("Deadlift", "Legs", EquipmentType.BODYWEIGHT, MuscleGroup.LEGS, "6", 3),
                new Exercise("Plank", "Core", EquipmentType.BODYWEIGHT, MuscleGroup.CORE, "60", 3),
                new Exercise("Russian Twist", "Core", EquipmentType.BODYWEIGHT, MuscleGroup.CORE, "20", 3)
        );
    }

    public static WorkoutDay createWorkoutDay(int dayNumber, WorkoutProgram program, List<Exercise> exercises) {
        WorkoutDay day = new WorkoutDay(dayNumber, program);
        int order = 1;
        for (Exercise ex : exercises) {
            ExerciseInDay exerciseInDay = new ExerciseInDay(day, ex, order);
            day.getExercises().add(exerciseInDay);
        }
        return day;
    }

    public static WorkoutProgram createWorkoutProgramWithDays(AppUser user, EquipmentType equipment,
                                                              SplitType splitType, int daysPerWeek,
                                                              List<Exercise> exercises) {
        WorkoutProgram program = new WorkoutProgram(
                "Test Program", equipment, splitType, daysPerWeek, user
        );

        program.setId(999L);
        program.setCreatedAt(LocalDateTime.now());

        for (int i = 0; i < daysPerWeek && i < exercises.size(); i++) {
            List<Exercise> dayExercise = List.of(exercises.get(i));
            WorkoutDay day = createWorkoutDay(i + 1, program, dayExercise);
            program.getDays().add(day);
        }
         return program;
    }

    public static WorkoutProgramResponse createWorkoutProgramResponse(Long id, String name, String equipment, String splitType, int daysPerWeek) {

        WorkoutProgramResponse response = new WorkoutProgramResponse();
        response.setId(id);
        response.setName(name);
        response.setEquipmentType(equipment);
        response.setSplitType(splitType);
        response.setDaysPerWeek(daysPerWeek);
        response.setCreatedAt(LocalDateTime.now());
        response.setDays(List.of());

        return response;
    }

}
