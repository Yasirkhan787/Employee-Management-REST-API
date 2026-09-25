package com.yasirkhan.em.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(UUID id, String username, String name, String email, String department, String role, double salary, LocalDate joiningDate) {
}
