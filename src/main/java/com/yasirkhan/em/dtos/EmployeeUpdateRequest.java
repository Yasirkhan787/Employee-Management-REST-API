package com.yasirkhan.em.dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EmployeeUpdateRequest(
        String name,

        String email,

        String department,

        Double salary,

        LocalDate joiningDate,

        String username,

        String password ) {
}
