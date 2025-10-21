package org.generator.workout.controller;

import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.EquipmentType;
import org.generator.workout.model.WorkoutProgram;
import org.generator.workout.service.WorkoutGeneratorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

    private final WorkoutGeneratorService generatorService;

    public WorkoutController(WorkoutGeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    @PostMapping("/generate")
    public WorkoutProgramResponse generateProgram(
            @RequestParam EquipmentType equipment,
            @RequestParam(defaultValue = "3") int daysPerWeek) {
        return generatorService.generateProgram(equipment, daysPerWeek);
    }
}
