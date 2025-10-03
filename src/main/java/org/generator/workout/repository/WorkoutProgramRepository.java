package org.generator.workout.repository;

import org.generator.workout.model.WorkoutProgram;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkoutProgramRepository extends JpaRepository<WorkoutProgram, Long> {
}
