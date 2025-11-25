package org.generator.workout.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkoutDayResponse {
    private Long id;

    @Min(value = 1)
    @Max(value = 7)
    private Integer dayNumber;

    private List<ExerciseInDayResponse> exercise;

    public WorkoutDayResponse(Long id, Integer dayNumber, List<ExerciseInDayResponse> exercise) {
        this.id = id;
        this.dayNumber = dayNumber;
        this.exercise = exercise;
    }
}
