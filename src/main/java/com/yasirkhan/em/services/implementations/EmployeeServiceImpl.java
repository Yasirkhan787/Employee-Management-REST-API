package com.yasirkhan.em.services.implementations;

import com.yasirkhan.em.dtos.EmployeeRequest;
import com.yasirkhan.em.dtos.EmployeeResponse;
import com.yasirkhan.em.entities.Employee;
import com.yasirkhan.em.exceptions.ResourceAlreadyExist;
import com.yasirkhan.em.exceptions.ResourceNotFoundException;
import com.yasirkhan.em.repositories.EmployeeRepository;
import com.yasirkhan.em.services.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeServiceImpl(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Override
    public EmployeeResponse addEmployee(EmployeeRequest request) {

        // Check if email already exists
        if (repository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExist("User with Email: " + request.email() + " is already exist");
        }


        Employee emp = Employee.builder()
                .name(request.name())
                .email(request.email())
                .department(request.department())
                .salary(request.salary())
                .joiningDate(request.joiningDate())
                .build();

        Employee saved = repository.save(emp);
        return mapToResponse(saved);
    }

    @Override
    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {

        Employee dbEmployee = findEmployeeById(id);
        dbEmployee.setName(request.name());
        dbEmployee.setEmail(request.email());
        dbEmployee.setDepartment(request.department());
        dbEmployee.setSalary(request.salary());
        Employee updated = repository.save(dbEmployee);
        return mapToResponse(updated);
    }

    @Override
    public void deleteEmployee(UUID id) {
        Employee dbEmployee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        repository.delete(dbEmployee);
    }

    /**
         * We Call repository findAll method, and it takes a Pageable request.
         * @param pageable We pass pageable to repository that include pageNumber(page), pageSize(size)
         * and Sort (sorterBy and sortOrder)
         * @return It returns the Slice<Employee> by default it Return Page<Employee> but we overwrite
         * it in repository interface by writing Derived Query to Optimize Query because default to
         * return page hibernate run 2 query one for data chunks and other to count all elements.
     */
    @Override
    public List<EmployeeResponse> getAllEmployees(Pageable pageable) {
        return repository
                .findAllBy(pageable)
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse getEmployeeById(UUID id) {
        Employee dbEmployee = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        return mapToResponse(dbEmployee);
    }

    // Helper methods
    private EmployeeResponse mapToResponse(Employee emp) {
        return new EmployeeResponse(
                emp.getId(),
                emp.getName(),
                emp.getEmail(),
                emp.getDepartment(),
                emp.getSalary(),
                emp.getJoiningDate()
        );
    }

    private Employee findEmployeeById(UUID employeeId) {
        return repository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
    }
}