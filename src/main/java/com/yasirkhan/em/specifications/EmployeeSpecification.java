package com.yasirkhan.em.specifications;

import com.yasirkhan.em.dtos.EmployeeSearchCriteria;
import com.yasirkhan.em.entities.Employee;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    public static Specification<Employee> getEmployeeSpecification(EmployeeSearchCriteria searchCriteria) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (searchCriteria.search() != null && !searchCriteria.search().isEmpty()) {
                String likeSearch = "%" + searchCriteria.search() + "%";
                Predicate nameMatch = criteriaBuilder.like(root.get("name"), likeSearch);
                Predicate emailMatch = criteriaBuilder.like(root.get("email"), likeSearch);
                Predicate departmentMatch = criteriaBuilder.like(root.get("department"), likeSearch);
                predicates.add(criteriaBuilder.or(nameMatch, emailMatch, departmentMatch));
            }

            if (searchCriteria.id() != null) {
                // UUIDs use exact match if we want to use like wee need to convert it into String
                predicates.add(criteriaBuilder.equal(root.get("id"), searchCriteria.id()));
            }

            if (searchCriteria.name() != null && !searchCriteria.name().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + searchCriteria.name() + "%"));
            }

            if (searchCriteria.email() != null && !searchCriteria.email().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("email"), "%" + searchCriteria.email() + "%"));
            }

            if (searchCriteria.department() != null && !searchCriteria.department().isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("department"), "%" + searchCriteria.department() + "%"));
            }

            if (searchCriteria.startDate() != null && searchCriteria.endDate() != null) {
                predicates.add(criteriaBuilder.between(root.get("joiningDate"), searchCriteria.startDate(), searchCriteria.endDate()));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}