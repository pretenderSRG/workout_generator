package org.generator.workout.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.generator.workout.model.SplitType;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.service.SmartWorkoutGeneratorService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.EquipmentType;
import org.generator.workout.service.WorkoutGeneratorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public List<WorkoutProgramResponse> getUserWorkoutPrograms(@RequestParam(required = false) EquipmentType equipment,
                                                               @RequestParam(required = false) SplitType splitType,
                                                               @RequestParam(required = false)
                                                                       @DateTimeFormat(pattern = "dd-MM-yyyy")
                                                               LocalDate createdAt) {
        return generatorService.getUserWorkoutProgram(getUserId(), equipment, splitType, createdAt);
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
            @RequestParam(defaultValue = "PPL") SplitType splitType,
            @RequestParam(defaultValue = "3") @Min(1) @Max(7) int daysPerWeek
    ) {
        Long userId = getUserId();
        return smartGeneratorService.generateSmartProgram(userId, equipment, splitType, daysPerWeek);
    }
}
