package org.generator.workout.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "history_exercise")
@Getter
@Setter
@NoArgsConstructor
public class HistoryExercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private Integer actualSets;
    private Integer actualReps;

    public HistoryExercise(Long id, Exercise exercise, Integer actualSets, Integer actualReps) {
        this.id = id;
        this.exercise = exercise;
        this.actualSets = actualSets;
        this.actualReps = actualReps;
    }
}
