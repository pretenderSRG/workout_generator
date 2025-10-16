package org.generator.workout.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_programs")
@Getter
@Setter
public class WorkoutProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private EquipmentType equipmentType;

    @Column(nullable = false)
    private Integer daysPerWeek;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("program-days")
    private List<WorkoutDay> days = new ArrayList<>();

    public WorkoutProgram() {
    }

    public WorkoutProgram(String name,
                          EquipmentType equipmentType,
                          Integer daysPerWeek) {
        this.name = name;
        this.equipmentType = equipmentType;
        this.daysPerWeek = daysPerWeek;
    }

}
