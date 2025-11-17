package org.generator.workout.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class HistoryExerciseResponse {

    private Long id;
    private String exerciseName;
    private Integer sets;
    private Integer reps;

    public HistoryExerciseResponse(Long id, String exerciseName, Integer sets, Integer reps) {
        this.id = id;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
    }
}
