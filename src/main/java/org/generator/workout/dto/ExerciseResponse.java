package org.generator.workout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseResponse {
    private Long id;
    private String name;
    private String description;
    private String equipment;
    private String muscleGroup;
    private String reps;
    private Integer sets;

    public ExerciseResponse(Long id, String name, String description, String equipment, String muscleGroup, String reps, Integer sets) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.equipment = equipment;
        this.muscleGroup = muscleGroup;
        this.reps = reps;
        this.sets = sets;
    }
}
