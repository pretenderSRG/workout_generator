package org.generator.workout.dto;

import lombok.Getter;
import lombok.Setter;
import org.generator.workout.model.WorkoutDay;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class WorkoutProgramResponse {
    private Long id;
    private String name;
    private String equipmentType;
    private Integer daysPerWeek;
    private LocalDateTime createdAt;
    private List<WorkoutDayResponse> days;

    public WorkoutProgramResponse(Long id, String name, String equipmentType,
                                  Integer daysPerWeek, LocalDateTime createdAt,
                                  List<WorkoutDayResponse> days) {
        this.id = id;
        this.name = name;
        this.equipmentType = equipmentType;
        this.daysPerWeek = daysPerWeek;
        this.createdAt = createdAt;
        this.days = days;
    }
}
