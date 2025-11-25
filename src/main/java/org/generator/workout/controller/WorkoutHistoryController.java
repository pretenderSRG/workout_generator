package org.generator.workout.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.generator.workout.dto.RecordExerciseRequest;
import org.generator.workout.dto.WorkoutHistoryResponse;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.service.WorkoutHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workouts/history")
@RequiredArgsConstructor
public class WorkoutHistoryController {

    private final WorkoutHistoryService historyService;
    private final AppUserRepository appUserRepository;

    @PostMapping("/record")
    public ResponseEntity<String> recordWorkout(@RequestParam Long programId,
                                               @Valid @RequestBody List<@Valid RecordExerciseRequest> exercises) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Long userId = appUserRepository.findAppUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User noy found: " + username)).getId();

        historyService.recordWorkout(userId, programId, exercises);

        return ResponseEntity.ok("Workout record successfully");

    }

    @GetMapping
    public List<WorkoutHistoryResponse> getUserWorkoutHistory() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Long userId = appUserRepository.findAppUserByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username)).getId();

        return historyService.getUserWorkoutHistory(userId);

    }

}
