package org.generator.workout.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class HistoryExerciseResponse {

    private Long id;
    private String exercise;
    private Integer sets;
    private Integer reps;

}
