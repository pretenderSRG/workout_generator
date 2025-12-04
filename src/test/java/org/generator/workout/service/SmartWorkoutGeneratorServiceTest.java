package org.generator.workout.service;

import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.exception.EntityNotFoundException;
import org.generator.workout.model.*;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.repository.ExerciseRepository;
import org.generator.workout.repository.WorkoutProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class SmartWorkoutGeneratorServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private WorkoutProgramRepository programRepository;

    @InjectMocks
    private SmartWorkoutGeneratorService service;

    private AppUser user;
    private List<Exercise> exercises;

    @BeforeEach
    void setUp() {
        user = new AppUser();
        user.setId(1L);
        user.setUsername("testuser");

         exercises = Arrays.asList(
                new Exercise("Push-up", "Push", EquipmentType.BODYWEIGHT, MuscleGroup.CHEST, "10", 3),
                new Exercise("Bench Press", "Push", EquipmentType.BODYWEIGHT, MuscleGroup.CHEST, "8", 4),
                new Exercise("Pull-up", "Pull", EquipmentType.BODYWEIGHT, MuscleGroup.BACK, "6", 3),
                new Exercise("Bent-over Row", "Pull", EquipmentType.BODYWEIGHT, MuscleGroup.BACK, "8", 3),
                new Exercise("Squat", "Legs", EquipmentType.BODYWEIGHT, MuscleGroup.LEGS, "8", 4),
                new Exercise("Deadlift", "Legs", EquipmentType.BODYWEIGHT, MuscleGroup.LEGS, "6", 3),
                new Exercise("Plank", "Core", EquipmentType.BODYWEIGHT, MuscleGroup.CORE, "60", 3),
                new Exercise("Russian Twist", "Core", EquipmentType.BODYWEIGHT, MuscleGroup.CORE, "20", 3)
        );

         lenient().when(programRepository.save(any(WorkoutProgram.class))).thenAnswer(invocation -> {
             WorkoutProgram program = invocation.getArgument(0);
             program.setId(100L);
             program.setCreatedAt(LocalDateTime.of(2025, 12, 3, 10, 0));
             // set ids to day in program
             for (int i = 0; i < program.getDays().size(); i++) {
                 WorkoutDay day = program.getDays().get(i);
                 day.setId((long) i + 1);
                 // set exercise id in day
                 for (int j = 0; j < day.getExercises().size(); j++) {
                     ExerciseInDay e = day.getExercises().get(j);
                     e.setId((long) (i * 10 + j + 1));
                 }
             }
             return program;
         });

         lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));

         lenient().when(exerciseRepository.findByEquipment(EquipmentType.BODYWEIGHT)).thenReturn(exercises.stream()
                 .filter(e -> e.getEquipment() == EquipmentType.BODYWEIGHT).toList()
         );

    }

    @Test
    void shouldGeneratePPLProgramWith3Days() {
        // arrange
        int daysPerWeek = 3;
        EquipmentType equipment = EquipmentType.BODYWEIGHT;
        SplitType splitType = SplitType.PPL;

        // act
        WorkoutProgramResponse response = service.generateSmartProgram(1L, equipment, splitType,daysPerWeek);

        // assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("My Smart 3-day BODYWEIGHT program", response.getName());
        assertEquals("BODYWEIGHT", response.getEquipmentType());
        assertEquals("PPL", response.getSplitType());
        assertEquals(3, response.getDays().size());

        var day1 = response.getDays().get(0);
        assertEquals(1, day1.getDayNumber());
        assertTrue(day1.getExercise().stream().allMatch(e -> e.getExercise().getEquipment().equals("BODYWEIGHT")));
    }

    @Test
    void shouldGenerateULProgramWith4Days() {
        // arrange
        int daysPerWeek = 4;
        EquipmentType equipment = EquipmentType.BODYWEIGHT;
        SplitType splitType = SplitType.UL;

        // act
        WorkoutProgramResponse response = service.generateSmartProgram(1L, equipment, splitType, daysPerWeek);

        // assert
        assertEquals(4, response.getDays().size());
        assertEquals("UL", response.getSplitType());

    }
    @Test
    void shouldGenerateFBProgramWith2Days() {
        // arrange
        int daysPerWeek = 2;
        EquipmentType equipment = EquipmentType.BODYWEIGHT;
        SplitType splitType = SplitType.FB;

        // act
        WorkoutProgramResponse response = service.generateSmartProgram(1L, equipment, splitType, daysPerWeek);

        // assert
        assertEquals(2, response.getDays().size());
        assertEquals("FB", response.getSplitType());

    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(exerciseRepository.findByEquipment(EquipmentType.BODYWEIGHT)).thenReturn(exercises);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.generateSmartProgram(999L, EquipmentType.BODYWEIGHT, SplitType.FB, 4)
        );

        assertEquals("User not found: 999", exception.getMessage());
    }

}

