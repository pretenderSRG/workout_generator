package org.generator.workout.model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_days")
@Getter
@Setter
public class WorkoutDay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer dayNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id", nullable = false)
    private WorkoutProgram program;

    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExerciseInDay> exercises = new ArrayList<>();

    public WorkoutDay() {
    }

    public WorkoutDay(Integer dayNumber, WorkoutProgram program) {
        this.dayNumber = dayNumber;
        this.program = program;

    }
}
