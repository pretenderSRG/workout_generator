package org.generator.workout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseInDayResponse {
    private Long id;
    private ExerciseResponse exercise;
    private Integer orderInDay;

    public ExerciseInDayResponse(Long id, ExerciseResponse exercise, Integer orderInDay) {
        this.id = id;
        this.exercise = exercise;
        this.orderInDay = orderInDay;
    }
}
