package org.generator.workout.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class WorkoutHistoryResponse {

    private Long id;
    private String username;
    private String workoutProgram;
    private HistoryExerciseResponse historyExercises;


}
