package org.generator.workout.service;

import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.exception.EntityNotFoundException;
import org.generator.workout.model.*;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.repository.ExerciseRepository;
import org.generator.workout.repository.WorkoutProgramRepository;
import org.generator.workout.specefication.WorkoutProgramSpec;
import org.generator.workout.testutil.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class WorkoutGeneratorServiceTest {

    @Mock
    private AppUserRepository userRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutProgramRepository programRepository;

    @InjectMocks
    private WorkoutGeneratorService service;

    private AppUser user;
    private List<Exercise> exercises;

    @BeforeEach
    void setUp() {
        user = TestDataFactory.createUser();
        exercises = TestDataFactory.createBodyweightExercise();
    }

    @Test
    void generateProgram_ShouldThrowExceptionWhenUserNotFound() {
        // arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // act
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> service.generateProgram(999L, EquipmentType.BODYWEIGHT, SplitType.PPL, 3)
        );

        // assert
        assertEquals("User not found: 999", exception.getMessage());
    }

    @Test
    void generateProgram_ShouldGenerateProgramSuccessfully() {
        // arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(exerciseRepository.findByEquipment(EquipmentType.BODYWEIGHT)).thenReturn(exercises);

        when(programRepository.save(any(WorkoutProgram.class))).thenAnswer(invocate -> {
            WorkoutProgram program = invocate.getArgument(0);
            program.setId(100L);
            program.setCreatedAt(LocalDateTime.of(2025, 12, 4, 10, 0));

            return program;
        });

        // act
        WorkoutProgramResponse response = service.generateProgram(1L, EquipmentType.BODYWEIGHT,
                SplitType.PPL, 3);

        // assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("PPL", response.getSplitType());
        assertEquals(3, response.getDaysPerWeek());
    }

    @Test
    void generateProgram_ShouldThrowExceptionWhenNoExceptionFound() {
        // arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(exerciseRepository.findByEquipment(EquipmentType.BARBELL)).thenReturn(List.of());

        // act
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.generateProgram(1L, EquipmentType.BARBELL, SplitType.PPL, 3));

        // assert
        assertEquals("No exercises found for equipment: BARBELL", exception.getMessage());
    }

    @Test
    void getUserWorkoutProgram_ShouldReturnFilteredAndPageResults_WhenFiltersProvided() {
        // arrange
        WorkoutProgram program1 = TestDataFactory.createWorkoutProgramWithDays(user,
                EquipmentType.DUMBBELLS,
                SplitType.PPL,
                3,
                exercises);
        program1.setCreatedAt(LocalDateTime.of(2025, 1, 1, 10, 10));
        program1.setId(1L);

        WorkoutProgram program2 = TestDataFactory.createWorkoutProgramWithDays(user,
                EquipmentType.GYM,
                SplitType.FB,
                2,
                exercises);
        program2.setCreatedAt(LocalDateTime.of(2025, 2, 1, 10, 10));
        program2.setId(2L);

        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        when(programRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(program1), pageable, 1));

        // act
        Page<WorkoutProgramResponse> result = service.getUserWorkoutProgram(1L, EquipmentType.DUMBBELLS,
                null, null, null, pageable);

        // assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("DUMBBELLS", result.getContent().get(0).getEquipmentType());


    }

    @Test
    void getWorkoutProgramById_ShouldReturnProgram_WhenProgramExistsAndBelongUser() {
        // arrange
        WorkoutProgram program = TestDataFactory.createWorkoutProgramWithDays(user, EquipmentType.BODYWEIGHT, SplitType.PPL, 3, exercises);
        program.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(programRepository.findByIdAndUser(1L, user)).thenReturn(Optional.of(program));

        // act
        WorkoutProgramResponse response = service.getWorkoutProgramById(1L, 1L);

        // assert
        assertNotNull(response);
        assertEquals("BODYWEIGHT", response.getEquipmentType());
        assertEquals("PPL", response.getSplitType());
    }

    @Test
    void getWorkoutProgramById_ShouldThrowEntityNotFoundException_WhenProgramDoesNotExist() {
        // arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(programRepository.findByIdAndUser(999L, user)).thenReturn(Optional.empty());

        // act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.getWorkoutProgramById(1L, 999L));

        // assert
        assertEquals("Workout program with ID 999 not found for current user", exception.getMessage());
    }

    @Test
    void deleteWorkoutProgramById_ShouldDeleteProgram_WhenProgramExistAndBelongUser() {
        // arrange
        WorkoutProgram program = TestDataFactory.createWorkoutProgramWithDays(user, EquipmentType.GYM, SplitType.FB, 2, exercises);
        program.setId(99L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(programRepository.findByIdAndUser(99L, user)).thenReturn(Optional.of(program));

        // act
        service.deleteWorkoutProgramById(1L, 99L);

        // assert
        verify(programRepository, times(1)).delete(program);
    }

    @Test
    void deleteWorkoutProgramById_ShouldThrowEntityNotFoundException_WhenProgramDoesNotExists() {
        // arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(programRepository.findByIdAndUser(99L, user)).thenReturn(Optional.empty());

        // act
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.deleteWorkoutProgramById(1L, 99L));

        // assert
        assertEquals("Workout program with ID 99 not found for current user", exception.getMessage());

    }
}
