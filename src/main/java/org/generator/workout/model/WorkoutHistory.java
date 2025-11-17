package org.generator.workout.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_history")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private WorkoutProgram program;

    private LocalDateTime date;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "history_id")
    private List<HistoryExercise> exercises = new ArrayList<>();

    public WorkoutHistory(Long id, AppUser user, WorkoutProgram program, LocalDateTime date, List<HistoryExercise> exercises) {
        this.id = id;
        this.user = user;
        this.program = program;
        this.date = date;
        this.exercises = exercises;
    }
}
