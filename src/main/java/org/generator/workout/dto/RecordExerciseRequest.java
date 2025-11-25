package org.generator.workout.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecordExerciseRequest {

    @NotNull(message = "Exercise ID is required")
    private Long exerciseId;

    @Min(value = 1, message = "Actual sets must be at least 1")
    private Integer actualSets;

    @Min(value = 1, message = "Actual reps must be at least 1")
    private Integer actualReps;
}
