package com.yasirkhan.em.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(UUID id, String name, String email, String department, double salary, LocalDate joiningDate) {
}
