package org.generator.workout.repository;

import org.generator.workout.model.HistoryExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryExerciseRepository extends JpaRepository<HistoryExercise, Long> {
}
