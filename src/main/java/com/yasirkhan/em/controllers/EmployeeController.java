package com.yasirkhan.em.controllers;

import com.yasirkhan.em.dtos.EmployeeRequest;
import com.yasirkhan.em.dtos.EmployeeResponse;
import com.yasirkhan.em.dtos.EmployeeSearchCriteria;
import com.yasirkhan.em.services.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

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
    @PreAuthorize("hasAuthority('EMPLOYEE_ADD')")
    @PostMapping
    public ResponseEntity<EmployeeResponse> addEmployee(
            @Valid @RequestBody EmployeeRequest request)
    {
        EmployeeResponse response = service.addEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Update an Employee
    @PreAuthorize("hasAuthority('EMPLOYEE_UPDATE')")
    @PutMapping("{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeRequest request)
    {
        EmployeeResponse response = service.updateEmployee(id, request);
        return ResponseEntity.ok(response);
    }

    // Delete Employee
    @PreAuthorize("hasAuthority('EMPLOYEE_DELETE')")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        service.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    // Get All Employees
    /**
         * Instead of manually defining @RequestParam for pageNumber, pageSize, sortBy, and sortDir,
         * we can delete all of them and just ask Spring for a Pageable object directly in our Controller.
         * Spring will automatically look at the URL and construct the PageRequest and Sort objects for us!
         * </br>
         * Default Values: </br>
         * Page = 0 Spring Data pagination is 0-indexed. Page 0 is the first page. * </br>
         * Size = 10 </br>
         * Sort = Unsorted </br>
         * We can override Spring Default by Using @PageableDefault annotation
     */
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW_ALL')")
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @PageableDefault(page = 0, size = 5, sort = "id", direction = Sort.Direction.ASC)
            Pageable pageable,
            @RequestParam(required = false) EmployeeSearchCriteria search
    ) {
        return ResponseEntity.ok(service.getAllEmployees(search, pageable));
    }

    // Get Employee By ID
    @PreAuthorize("hasAuthority('EMPLOYEE_VIEW')")
    @GetMapping("{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getEmployeeById(id));
    }
}