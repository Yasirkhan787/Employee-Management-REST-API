package com.yasirkhan.em.services;

import com.yasirkhan.em.dtos.EmployeeRequest;
import com.yasirkhan.em.dtos.EmployeeResponse;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    EmployeeResponse addEmployee(EmployeeRequest request);
    EmployeeResponse updateEmployee(UUID employeeId, EmployeeRequest updateRequest);
    void deleteEmployee(UUID employeeId);
    List<EmployeeResponse> getAllEmployees(String pageNumber, String pageSize);
    EmployeeResponse getEmployeeById(UUID employeeId);
}
