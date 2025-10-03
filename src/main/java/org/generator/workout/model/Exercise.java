package org.generator.workout.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentType equipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MuscleGroup muscleGroup;

    private String reps;
    private Integer sets;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExerciseInDay> inDays = new ArrayList<>();

    public Exercise(String name, String description, EquipmentType equipment, MuscleGroup muscleGroup, String reps, Integer sets) {
        this.name = name;
        this.description = description;
        this.equipment = equipment;
        this.muscleGroup = muscleGroup;
        this.reps = reps;
        this.sets = sets;
    }
}
