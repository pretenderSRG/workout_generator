package org.generator.workout.repository;

import org.generator.workout.model.AppUser;
import org.generator.workout.model.WorkoutProgram;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutProgramRepository extends JpaRepository<WorkoutProgram, Long> {
    List<WorkoutProgram> findByUser(AppUser user);
}
