package org.generator.workout.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "exercise_in_day")
@Getter
@Setter
public class ExerciseInDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id", nullable = false)
    private WorkoutDay day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private Integer orderInDay;

    public ExerciseInDay() {
    }

    public ExerciseInDay(WorkoutDay day, Exercise exercise, Integer orderInDay) {
        this.day = day;
        this.exercise = exercise;
        this.orderInDay = orderInDay;
    }
}
