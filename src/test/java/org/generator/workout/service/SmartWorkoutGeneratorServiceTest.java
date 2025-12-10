package org.generator.workout.service;

import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.exception.EntityNotFoundException;
import org.generator.workout.model.*;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.repository.ExerciseRepository;
import org.generator.workout.repository.WorkoutProgramRepository;
import org.generator.workout.testutil.TestDataFactory;
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

@ExtendWith(MockitoExtension.class)
public class SmartWorkoutGeneratorServiceTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private WorkoutPlanerService workoutPlanerService;

    @Mock
    private WorkoutProgramRepository programRepository;

    @InjectMocks
    private SmartWorkoutGeneratorService service;

    private AppUser user;
    private List<Exercise> exercises;

    @BeforeEach
    void setUp() {
       AppUser user = TestDataFactory.createUser();
       List<Exercise> exercises = TestDataFactory.createBodyweightExercise();
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.generateSmartProgram(999L, EquipmentType.BODYWEIGHT, SplitType.PPL, 3)
        );

        assertEquals("User not found: 999", exception.getMessage());
    }

    @Test
    void shouldGenerateProgramSuccessfully() {
        // arrange
        WorkoutProgram tempProgram = new WorkoutProgram();

        WorkoutDay day1 = new WorkoutDay(1, tempProgram);
        WorkoutDay day2 = new WorkoutDay(2, tempProgram);
        WorkoutDay day3 = new WorkoutDay(3, tempProgram);


        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(exerciseRepository.findByEquipment(EquipmentType.BODYWEIGHT)).thenReturn(exercises);

    when(workoutPlanerService.planDays(exercises, 3, SplitType.PPL))
            .thenReturn(Arrays.asList(day1, day2, day3));

        when(programRepository.save(any(WorkoutProgram.class))).thenAnswer(invocation -> {
            WorkoutProgram program = invocation.getArgument(0);
            program.setId(100L);
            program.setCreatedAt(LocalDateTime.of(2025, 12, 4, 10, 0));
            return program;
        });

        // act
        WorkoutProgramResponse response = service.generateSmartProgram(1L, EquipmentType.BODYWEIGHT, SplitType.PPL, 3);

        // assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("PPL", response.getSplitType());
        assertEquals(3, response.getDays().size());
    }

}

