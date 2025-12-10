package org.generator.workout.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.generator.workout.model.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkoutPlanerService {

    public List<WorkoutDay> planDays(List<Exercise> exercises, int daysPerWeek, SplitType splitType) {

        Map<Integer, List<Exercise>> dayPlan = distributeExercisesByRules(exercises, daysPerWeek, splitType);
        List<WorkoutDay> days = new ArrayList<>();

        // add days to plan
        for (Map.Entry<Integer, List<Exercise>> entry : dayPlan.entrySet()) {
            int dayNumber = entry.getKey();
            List<Exercise> dayExercises = entry.getValue();

            WorkoutDay day = new WorkoutDay(dayNumber, null);
            log.info("Add day № {}", dayNumber);
            int order = 1;
            for (Exercise exercise : dayExercises) {
                ExerciseInDay exerciseInDay = new ExerciseInDay(day, exercise, order++);
                day.getExercises().add(exerciseInDay);
                log.info("Add exercise - {}", exercise.getName());
            }
            days.add(day);
        }
            return days;
    }

        private Map<Integer, List<Exercise>> distributeExercisesByRules(List<Exercise> exercises,
        int daysPerWeek, SplitType splitType){
            Map<Integer, List<Exercise>> plan = new HashMap<>();
            Set<Long> usedExercisesIds = new HashSet<>();

            for (int day = 1; day <= daysPerWeek; day++) {
                List<Exercise> availableExercises = exercises.stream()
                        .filter(ex -> !usedExercisesIds.contains(ex.getId())).toList();

                List<Exercise> dayExercises = switch (splitType) {
                    case PPL -> getExercisesForPPL(availableExercises, day);
                    case UL -> getExercisesForUL(availableExercises, day);
                    case FB -> getExercisesForFB(availableExercises);
                };

                dayExercises = balanceMuscleGroupByDay(dayExercises, splitType, day);

                // Add exercises id to set
                dayExercises.forEach(ex -> usedExercisesIds.add(ex.getId()));

                plan.put(day, dayExercises);

            }
            return plan;
        }

        // PPL: Push, Pull, Legs, Core
        private List<Exercise> getExercisesForPPL (List < Exercise > exercises,int day){
            return switch (day % 4) {
                case 1 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PUSH).toList();
                case 2 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PULL).toList();
                case 3 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.LEGS).toList();
                case 0 -> exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.CORE).toList();
                default -> exercises;
            };

        }

        // UL: UPPER_LOWER
        private List<Exercise> getExercisesForUL (List < Exercise > exercises,int day){
            return (day % 2 == 1) ?
                    exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.PUSH || e.getCategory() == ExerciseCategory.PULL).toList() :
                    exercises.stream().filter(e -> e.getCategory() == ExerciseCategory.LEGS || e.getCategory() == ExerciseCategory.CORE).toList();
        }

        // FL: FULL_BODY
        private List<Exercise> getExercisesForFB (List < Exercise > exercises) {
            Map<MuscleGroup, List<Exercise>> exerciseByMuscle = exercises.stream()
                    .collect(Collectors.groupingBy(Exercise::getMuscleGroup));

            List<Exercise> dayExercise = new ArrayList<>();
            for (List<Exercise> groupExercises : exerciseByMuscle.values()) {
                Collections.shuffle(groupExercises);
                int toTake = Math.min(2, groupExercises.size());
                dayExercise.addAll(groupExercises.subList(0, toTake));
            }

            Collections.shuffle(dayExercise);
            if (dayExercise.size() > 6) {
                dayExercise = dayExercise.subList(0, 6);
            }
            return dayExercise;
        }

        private List<Exercise> balanceMuscleGroupByDay (List < Exercise > exercises, SplitType splitType,int dayPerWeek)
        {
            Map<MuscleGroup, Long> muscleGroup = new HashMap<>();
            List<Exercise> balanceDay = new ArrayList<>();

            int maxExercisePerGroup = 2;
            int maxExercisePerDay = 4;

            if (splitType == SplitType.PPL && (dayPerWeek % 4 == 3)) {
                exercises = exercises.stream()
                        .sorted(Comparator.comparing(ex -> ex.getMuscleGroup() == MuscleGroup.LEGS ? 0 : 1)).toList();
            }

            for (Exercise ex : exercises) {
                long currentCount = muscleGroup.getOrDefault(ex.getMuscleGroup(), 0L);
                if (currentCount < maxExercisePerGroup && balanceDay.size() < maxExercisePerDay) {
                    balanceDay.add(ex);
                    muscleGroup.put(ex.getMuscleGroup(), currentCount + 1);
                }
            }


            return balanceDay;
        }


    }
