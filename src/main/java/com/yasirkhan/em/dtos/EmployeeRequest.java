package com.yasirkhan.em.dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank(message = "Name cannot be empty")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Department cannot be empty")
        String department,

        @NotNull(message = "Salary cannot be null")
        @Positive(message = "Salary must be greater than zero")
        Double salary,

        @NotNull(message = "Joining date is required")
        @PastOrPresent(message = "Joining date cannot be in the future")
        LocalDate joiningDate) {
}
