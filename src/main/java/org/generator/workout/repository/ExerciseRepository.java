package org.generator.workout.repository;

import org.generator.workout.model.EquipmentType;
import org.generator.workout.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByEquipment(EquipmentType equipment);
}
