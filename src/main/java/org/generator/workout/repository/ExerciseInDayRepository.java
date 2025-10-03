package org.generator.workout.repository;

import org.generator.workout.model.ExerciseInDay;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseInDayRepository extends JpaRepository<ExerciseInDay, Long> {
}
