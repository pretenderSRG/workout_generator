package org.generator.workout.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference("day-exercises")
    private WorkoutDay day;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private Integer orderInDay;

    private String notes;
    private Integer customSets;
    private String customReps;

    public ExerciseInDay() {
    }

    public ExerciseInDay(WorkoutDay day, Exercise exercise, Integer orderInDay) {
        this.day = day;
        this.exercise = exercise;
        this.orderInDay = orderInDay;
    }
}
