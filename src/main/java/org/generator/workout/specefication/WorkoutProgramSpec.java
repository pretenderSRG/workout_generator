package org.generator.workout.specefication;

import org.generator.workout.model.AppUser;
import org.generator.workout.model.EquipmentType;
import org.generator.workout.model.SplitType;
import org.generator.workout.model.WorkoutProgram;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class WorkoutProgramSpec {
    public static Specification<WorkoutProgram> hasUser(AppUser user) {
        return ((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user"), user));
    }

    public static Specification<WorkoutProgram> hasEquipmentType(EquipmentType equipmentType) {
        return ((root, query, criteriaBuilder) ->
                equipmentType == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("equipmentType"), equipmentType));
    }

    public static Specification<WorkoutProgram> hasSplitType(SplitType splitType) {
        return ((root, query, criteriaBuilder) ->
                splitType == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("splitType"), splitType));
    }

    public static Specification<WorkoutProgram> createdAtAfter(LocalDate date) {
        return ((root, query, criteriaBuilder) ->
               date == null ?
                       criteriaBuilder.conjunction() :
                       criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), date.atStartOfDay()));
    }

    public static Specification<WorkoutProgram> hasDaysPerWeek(Integer daysPerWeek) {
        return ((root, query, criteriaBuilder) ->
                daysPerWeek == null ?
                        criteriaBuilder.conjunction() :
                        criteriaBuilder.equal(root.get("daysPerWeek"), daysPerWeek));
    }


    public static Specification<WorkoutProgram> withFilters(AppUser user, EquipmentType equipmentType, SplitType splitType,
                                                            LocalDate createdAt, Integer daysPerWeek) {
        Specification<WorkoutProgram> spec = Specification.where(null);

        if (user != null) {
            spec = spec.and(hasUser(user));
        }

        if (equipmentType != null) {
            spec = spec.and(hasEquipmentType(equipmentType));
        }

        if (splitType != null) {
            spec = spec.and(hasSplitType(splitType));
        }

        if (createdAt != null) {
            spec = spec.and(createdAtAfter(createdAt));
        }

        if (daysPerWeek != null) {
            spec = spec.and(hasDaysPerWeek(daysPerWeek));
        }

        return spec;


    }
}
