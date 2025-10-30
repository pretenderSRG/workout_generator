package org.generator.workout.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.generator.workout.dto.ExerciseInDayResponse;
import org.generator.workout.dto.ExerciseResponse;
import org.generator.workout.dto.WorkoutDayResponse;
import org.generator.workout.dto.WorkoutProgramResponse;
import org.generator.workout.model.EquipmentType;
import org.generator.workout.model.Exercise;
import org.generator.workout.model.ExerciseCategory;
import org.generator.workout.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SmartWorkoutGeneratorService {

    private final ExerciseRepository exerciseRepository;

    public WorkoutProgramResponse generateSmartProgram(Long userId, EquipmentType equipment, int daysPerWeek, String splitType) {
        List<Exercise> exercises = exerciseRepository.findByEquipment(equipment);

        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("No exercises found for equipment: " + equipment);
        }

        Map<Integer, List<Exercise>> dayPlan = distributeExercisesByRules(exercises, daysPerWeek, splitType);

        return buildResponse(userId, dayPlan, equipment, daysPerWeek);
    }

    private Map<Integer,List<Exercise>> distributeExercisesByRules(List<Exercise> exercises, int daysPerWeek, String splitType) {

        Map<Integer, List<Exercise>> plan = new HashMap<>();

        List<Exercise> push = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PUSH).toList();

        List<Exercise> pull = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PULL).toList();

        List<Exercise> legs = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.LEGS).toList();

        List<Exercise> core = exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.CORE).toList();

        if(daysPerWeek >= 1) plan.put(1, push);
        if(daysPerWeek >= 2) plan.put(2, pull);
        if(daysPerWeek >= 3) plan.put(3, legs);
        if(daysPerWeek >= 4) plan.put(4, core);

        if (daysPerWeek >= 5) {
            for (int day = 5; day < daysPerWeek; day++) {
                List<Exercise> randomDay = new ArrayList<>();
                randomDay.addAll(push);
                randomDay.addAll(pull);
                randomDay.addAll(legs);
                randomDay.addAll(core);
                Collections.shuffle(randomDay);
                plan.put(day, randomDay.subList(0, Math.min(4, randomDay.size())));
            }
        }

        return plan;
    }

     private WorkoutProgramResponse buildResponse(Long userId, Map<Integer, List<Exercise>> dayPlan,
                                                  EquipmentType equipment, int daysPerWeek) {

        String programName = "My Smart " + daysPerWeek + "-day " + equipment.name() + " program";
         LocalDateTime createdAt = LocalDateTime.now();

         List<WorkoutDayResponse> dayResponses = dayPlan.entrySet().stream()
                 .map(entry -> {
                     int dayNumber = entry.getKey();
                     List<Exercise> exercises = entry.getValue();

                     List<ExerciseInDayResponse> exerciseInDayResponses = exercises.stream()
                             .map(exercise -> new ExerciseInDayResponse(
                                     null,
                                     new ExerciseResponse(
                                             exercise.getId(),
                                             exercise.getName(),
                                             exercise.getDescription(),
                                             exercise.getEquipment().name(),
                                             exercise.getMuscleGroup().name(),
                                             exercise.getReps(),
                                             exercise.getSets()
                                     ),
                                     exercises.indexOf(exercise) +1
                             )).toList();
                     return new WorkoutDayResponse(
                             null,
                             dayNumber,
                             exerciseInDayResponses
                     );
                 }).toList();
        return  new WorkoutProgramResponse(
                null,
                programName,
                equipment.name(),
                daysPerWeek,
                createdAt,
                dayResponses
        );
    }

}

