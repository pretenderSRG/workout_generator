package org.generator.workout.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.generator.workout.dto.HistoryExerciseResponse;
import org.generator.workout.dto.RecordExerciseRequest;
import org.generator.workout.dto.WorkoutHistoryResponse;
import org.generator.workout.model.AppUser;
import org.generator.workout.model.HistoryExercise;
import org.generator.workout.model.WorkoutHistory;
import org.generator.workout.model.WorkoutProgram;
import org.generator.workout.repository.AppUserRepository;
import org.generator.workout.repository.ExerciseRepository;
import org.generator.workout.repository.WorkoutHistoryRepository;
import org.generator.workout.repository.WorkoutProgramRepository;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WorkoutHistoryService {

    private final WorkoutProgramRepository workoutProgramRepository;
    private final WorkoutHistoryRepository workoutHistoryRepository;
    private final ExerciseRepository exerciseRepository;
    private final AppUserRepository userRepository;

    @Transactional
    public void recordWorkout(Long userId, Long programId, List<RecordExerciseRequest> exercises) {
        AppUser user =  userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        WorkoutProgram program = workoutProgramRepository.findByIdAndUser(programId, user)
                .orElseThrow(() -> new IllegalArgumentException("Program not found: " + programId));

        WorkoutHistory history = new WorkoutHistory();

        history.setUser(user);
        history.setProgram(program);
        history.setDate(program.getCreatedAt());

        List<HistoryExercise> historyExercises = exercises.stream()
                .map(req -> {
                    HistoryExercise hisEx = new HistoryExercise();
                    hisEx.setExercise(exerciseRepository.findById(req.getExerciseId()).orElse(null));
                    hisEx.setActualSets(req.getActualSets());
                    hisEx.setActualReps(req.getActualReps());
                    return hisEx;
                }).toList();
        history.setExercises(historyExercises);
        workoutHistoryRepository.save(history);
    }

    public List<WorkoutHistoryResponse> getUserWorkoutHistory(Long userId) {
        AppUser user =  userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        List<WorkoutHistory> histories = workoutHistoryRepository.findByUser(user);

        return histories.stream()
                .map(hist -> new WorkoutHistoryResponse(
                        hist.getId(),
                        hist.getUser().getUsername(),
                        hist.getProgram().getName(),
                        hist.getExercises().stream()
                                .map(ex -> new HistoryExerciseResponse(
                                        ex.getId(),
                                        ex.getExercise().getName(),
                                        ex.getActualSets(),
                                        ex.getActualReps())).toList())).toList();

    }

}
