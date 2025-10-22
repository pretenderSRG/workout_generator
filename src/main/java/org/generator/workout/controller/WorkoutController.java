package org.generator.workout.controller;

import org.generator.workout.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.EquipmentType;
import org.generator.workout.service.WorkoutGeneratorService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutGeneratorService generatorService;
    private final AppUserRepository userRepository;

    public WorkoutController(WorkoutGeneratorService generatorService, AppUserRepository userRepository) {
        this.generatorService = generatorService;
        this.userRepository = userRepository;
    }

    @PostMapping("/generate")
    public WorkoutProgramResponse generateProgram(
            @RequestParam EquipmentType equipment,
            @RequestParam(defaultValue = "3") int daysPerWeek) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        Long userId = userRepository.findAppUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username)).getId();
        return generatorService.generateProgram(userId,equipment, daysPerWeek);
    }
}
