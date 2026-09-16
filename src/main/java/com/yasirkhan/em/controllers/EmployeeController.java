package com.yasirkhan.em.controllers;

import com.yasirkhan.em.dtos.EmployeeRequest;
import com.yasirkhan.em.dtos.EmployeeResponse;
import com.yasirkhan.em.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    // Add New Employee
    @PostMapping
    public ResponseEntity<EmployeeResponse> addEmployee(
            @Valid @RequestBody EmployeeRequest request)
    {
        EmployeeResponse response = service.addEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update an Employee
    @PutMapping("{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeRequest request)
    {
        EmployeeResponse response = service.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    // Delete Employee
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Get All Employees
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false, defaultValue = "1") String pageNumber,
            @RequestParam(required = false, defaultValue = "5") String pageSize
    ) {
        return ResponseEntity.ok(service.getAllEmployees(pageNumber, pageSize));
    }

    // Get Employee By ID
    @GetMapping("{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }
}