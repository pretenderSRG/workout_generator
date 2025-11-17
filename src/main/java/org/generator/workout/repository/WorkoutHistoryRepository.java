package org.generator.workout.repository;

import org.generator.workout.model.AppUser;
import org.generator.workout.model.WorkoutHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutHistoryRepository extends JpaRepository<WorkoutHistory, Long> {
    List<WorkoutHistory> findByUser(AppUser user);
}
