package com.yasirkhan.em.specifications;

import com.yasirkhan.em.entities.Employee;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

public class EmployeeSpecification {

    public static Specification<Employee> getEmployeeSpecification(String search) {
        return (root, criteriaQuery, criteriaBuilder) -> {

            if (search == null || search.isEmpty()) {
                return criteriaBuilder.conjunction(); // Return always true (Use when user applies no filters)
            }
            String likeSearch = "%"+ search +"%";
            List<Predicate> predicate = new ArrayList<>();
            predicate.add(criteriaBuilder.like(root.get("id").as(String.class),likeSearch));
            predicate.add(criteriaBuilder.like(root.get("name"),likeSearch));
            predicate.add(criteriaBuilder.like(root.get("email"),likeSearch));
            predicate.add(criteriaBuilder.like(root.get("department"),likeSearch));
            predicate.add(criteriaBuilder.like(root.get("salary").as(String.class),likeSearch));
            return criteriaBuilder.or(predicate.toArray(new Predicate[0]));
        };
    }
}
