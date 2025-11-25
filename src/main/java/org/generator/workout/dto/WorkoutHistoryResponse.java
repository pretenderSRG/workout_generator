package org.generator.workout.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.generator.workout.model.HistoryExercise;

import java.util.List;

@Getter
@Setter
public class WorkoutHistoryResponse {

    private Long id;

    @NotNull
    @NotBlank
    private String username;

    private String workoutProgram;
    private List<HistoryExerciseResponse> historyExerciseResponses;

    public WorkoutHistoryResponse(Long id, String username, String workoutProgram, List<HistoryExerciseResponse> historyExercisesResponse) {
        this.id = id;
        this.username = username;
        this.workoutProgram = workoutProgram;
        this.historyExerciseResponses = historyExercisesResponse;
    }
}
