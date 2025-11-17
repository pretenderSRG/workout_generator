package org.generator.workout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordExerciseRequest {
    private Long exerciseId;
    private Integer actualSets;
    private Integer actualReps;
}
