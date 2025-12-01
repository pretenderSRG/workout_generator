package org.generator.workout.repository;

import org.generator.workout.model.AppUser;
import org.generator.workout.model.EquipmentType;
import org.generator.workout.model.SplitType;
import org.generator.workout.model.WorkoutProgram;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutProgramRepository extends JpaRepository<WorkoutProgram, Long>, JpaSpecificationExecutor<WorkoutProgram> {

    Page<WorkoutProgram> findByIdAndUser(Long id, AppUser user, Pageable pageable);
    WorkoutProgram findByIdAndUser(Long id, AppUser user);

//    List<WorkoutProgram> findByUserAndEquipmentType(AppUser user, EquipmentType equipment);
//
//    List<WorkoutProgram> findByUserAndSplitType(AppUser user, SplitType splitType);
//
//    List<WorkoutProgram> findByUserAndEquipmentTypeAndSplitType(AppUser user, EquipmentType equipmentType, SplitType splitType);

}
