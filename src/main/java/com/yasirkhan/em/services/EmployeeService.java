package com.yasirkhan.em.services;

import com.yasirkhan.em.dtos.EmployeeRequest;
import com.yasirkhan.em.dtos.EmployeeResponse;
import com.yasirkhan.em.dtos.EmployeeSearchCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {
    EmployeeResponse addEmployee(EmployeeRequest request);
    EmployeeResponse updateEmployee(UUID employeeId, EmployeeRequest updateRequest);
    void deleteEmployee(UUID employeeId);
    List<EmployeeResponse> getAllEmployees(EmployeeSearchCriteria search, Pageable pageable);
    EmployeeResponse getEmployeeById(UUID employeeId);
}
