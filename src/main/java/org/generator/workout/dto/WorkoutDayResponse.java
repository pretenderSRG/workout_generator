package org.generator.workout.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkoutDayResponse {
    private Long id;
    private Integer dayNumber;
    private List<ExerciseInDayResponse> exercise;

    public WorkoutDayResponse(Long id, Integer dayNumber, List<ExerciseInDayResponse> exercise) {
        this.id = id;
        this.dayNumber = dayNumber;
        this.exercise = exercise;
    }
}
