package org.generator.workout.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.generator.workout.dto.WorkoutDayResponse;
import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.exception.EntityNotFoundException;
import org.generator.workout.model.AppUser;
import org.generator.workout.model.EquipmentType;
import org.generator.workout.model.SplitType;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.service.SmartWorkoutGeneratorService;
import org.generator.workout.service.WorkoutGeneratorService;
import org.generator.workout.testutil.TestDataFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(WorkoutController.class)
@WithMockUser(roles = "USER")
public class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkoutGeneratorService workoutGeneratorService;

    @MockitoBean
    private SmartWorkoutGeneratorService smartWorkoutGeneratorService;

    @MockitoBean
    private AppUserRepository appUserRepository;

    @Test
    @WithMockUser(username = "testuser")
    void getWorkouts_ShouldReturnPageOfPrograms_WhenFiltersProvided() throws Exception{
        // arrange
        AppUser mockUser = TestDataFactory.createUser();
        when(appUserRepository.findAppUserByUsername("testuser")).thenReturn(Optional.of(mockUser));

        WorkoutProgramResponse response1 = TestDataFactory.createWorkoutProgramResponse(
                1L, "My 3-day DUMBBELLS program","DUMBBELLS", "PPL", 3
        );

        Page<WorkoutProgramResponse> page = new PageImpl<>(List.of(response1), PageRequest.of(0, 10), 1);

        when(workoutGeneratorService.getUserWorkoutProgram(eq(mockUser.getId()), any(), any(), any(), any(), any()))
                .thenReturn(page);

        // act & assert
        mockMvc.perform(get("/api/workouts?page=0&size=10&equipment=DUMBBELLS")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getWorkoutById_ShouldReturnProgram_WhenFound() throws Exception {
        // arrange
        AppUser mockUser = TestDataFactory.createUser();
        when(appUserRepository.findAppUserByUsername("testuser"))
                .thenReturn(Optional.of(mockUser));

        WorkoutProgramResponse response = TestDataFactory.createWorkoutProgramResponse(100L, "Test Program", "GYM", "FB", 2);

        when(workoutGeneratorService.getWorkoutProgramById(eq(mockUser.getId()), eq(100L))).thenReturn(response);

        // act & arrange
        mockMvc.perform(get("/api/workouts/100").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.name").value("Test Program"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getWorkoutById_Should404_WhenNotFoud() throws Exception {
        // arrange
        AppUser mockUser = TestDataFactory.createUser();
        when(appUserRepository.findAppUserByUsername("testuser")).thenReturn(Optional.of(mockUser));

        when(workoutGeneratorService.getWorkoutProgramById(eq(mockUser.getId()), eq(999L)))
                .thenThrow(new EntityNotFoundException("Workout program with ID 999 not found for current user"));
        // act & assert
        mockMvc.perform(get("/api/workouts/999").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    @WithMockUser(username = "testuser")
    void generateWorkout_ShouldReturnPrograms_WhenValidInput() throws Exception {
        // arrange
        AppUser mockUser = TestDataFactory.createUser();
        when(appUserRepository.findAppUserByUsername("testuser")).thenReturn(Optional.of(mockUser));

        WorkoutProgramResponse response = TestDataFactory.createWorkoutProgramResponse(
                101L,
                "My Smart 2-day DUMBBELLS program",
                "DUMBBELLS", "FB", 2);

        when(workoutGeneratorService.generateProgram(eq(mockUser.getId()), eq(EquipmentType.DUMBBELLS), eq(SplitType.FB), eq(2)))
                .thenReturn(response);

        // act & assert
        mockMvc.perform(post("/api/workouts/generate")
                        .param("equipment", "DUMBBELLS")
                        .param("splitType", "FB")
                        .param("daysPerWeek", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(101L))
                .andExpect(jsonPath("$.equipmentType").value("DUMBBELLS"))
                .andExpect(jsonPath("$.splitType").value("FB"));
    }

     @Test
    @WithMockUser(username = "testuser")
    void generateSmartWorkout_ShouldReturnPrograms_WhenValidInput() throws Exception {
        // arrange
        AppUser mockUser = TestDataFactory.createUser();
        when(appUserRepository.findAppUserByUsername("testuser")).thenReturn(Optional.of(mockUser));

        WorkoutProgramResponse response = TestDataFactory.createWorkoutProgramResponse(
                102L,
                "My Smart 2-day GYM program",
                "GYM", "FB", 2);

        when(smartWorkoutGeneratorService.generateSmartProgram(eq(mockUser.getId()), eq(EquipmentType.GYM), eq(SplitType.FB), eq(2)))
                .thenReturn(response);

        // act & assert
        mockMvc.perform(post("/api/workouts/generate")
                        .param("equipment", "GYM")
                        .param("splitType", "FB")
                        .param("daysPerWeek", "2")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(102L))
                .andExpect(jsonPath("$.equipmentType").value("GYM"))
                .andExpect(jsonPath("$.splitType").value("FB"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteWorkout_ShouldReturn204_WhenSuccessful() throws Exception {
        // arrange
        AppUser mockUser = TestDataFactory.createUser();
        when(appUserRepository.findAppUserByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // act & assert
        mockMvc.perform(delete("/api/workouts/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(workoutGeneratorService, timeout(1)).deleteWorkoutProgramById(eq(mockUser.getId()), eq(1L));
    }





}
