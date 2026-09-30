package com.yasirkhan.em.dtos;

import java.util.UUID;

public record SalaryInsightResponse(
        UUID employeeId,
        String name,
        String department,
        Double salary,
        BenchmarkResponse benchmark
) {}