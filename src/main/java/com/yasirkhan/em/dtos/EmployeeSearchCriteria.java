package com.yasirkhan.em.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeSearchCriteria(
        String search, // Global search for name and email only
        UUID id,
        String name,
        String email,
        String department,
        Double salary,
        LocalDate startDate,
        LocalDate endDate
        ) {
}
