package org.generator.workout.controller;

import jakarta.validation.Valid;
import org.generator.workout.dto.WorkoutDayResponse;
import org.generator.workout.model.SplitType;
import org.generator.workout.model.WorkoutProgram;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.repository.WorkoutProgramRepository;
import org.generator.workout.service.SmartWorkoutGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.EquipmentType;
import org.generator.workout.service.WorkoutGeneratorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutGeneratorService generatorService;
    private final AppUserRepository userRepository;
    private final SmartWorkoutGeneratorService smartGeneratorService;

    public WorkoutController(WorkoutGeneratorService generatorService,
                             AppUserRepository userRepository, SmartWorkoutGeneratorService smartGeneratorService) {
        this.generatorService = generatorService;
        this.userRepository = userRepository;
        this.smartGeneratorService = smartGeneratorService;
    }

    private Long getUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        return userRepository.findAppUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username)).getId();

    }

    @GetMapping
    public List<WorkoutProgramResponse> getUserWorkoutPrograms() {
        return generatorService.getUserWorkoutProgram(getUserId());
    }

    @GetMapping("/{id}")
    public WorkoutProgramResponse getWorkoutProgramById(@PathVariable Long id) {

        return generatorService.getWorkoutProgramById(getUserId(), id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkoutProgram(@PathVariable Long id) {
        generatorService.deleteWorkoutProgramById(getUserId(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate")
    public WorkoutProgramResponse generateProgram(
            @RequestParam EquipmentType equipment,
            @RequestParam SplitType splitType,
            @RequestParam(defaultValue = "3") int daysPerWeek) {

        return generatorService.generateProgram(getUserId(), equipment, splitType, daysPerWeek);
    }

    @PostMapping("/generate-smart")
    public WorkoutProgramResponse generateSmartWorkout(
            @RequestParam EquipmentType equipment,
            @RequestParam(defaultValue = "SplitType.PPL") SplitType splitType,
            @RequestParam(defaultValue = "3") int daysPerWeek
            ) {
        Long userId = getUserId();
        return smartGeneratorService.generateSmartProgram(userId, equipment, splitType, daysPerWeek);
    }
}
