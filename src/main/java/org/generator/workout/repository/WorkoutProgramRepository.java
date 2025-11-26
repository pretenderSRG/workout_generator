package org.generator.workout.repository;

import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.AppUser;
import org.generator.workout.model.EquipmentType;
import org.generator.workout.model.SplitType;
import org.generator.workout.model.WorkoutProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutProgramRepository extends JpaRepository<WorkoutProgram, Long> {
    List<WorkoutProgram> findByUser(AppUser user);

    Optional<WorkoutProgram> findByIdAndUser(Long id, AppUser user);

    List<WorkoutProgram> findByUserAndEquipmentType(AppUser user, EquipmentType equipment);

    List<WorkoutProgram> findByUserAndSplitType(AppUser user, SplitType splitType);

    List<WorkoutProgram> findByUserAndEquipmentTypeAndSplitType(AppUser user, EquipmentType equipmentType, SplitType splitType);

}
