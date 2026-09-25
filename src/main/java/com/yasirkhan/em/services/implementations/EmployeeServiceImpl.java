package com.yasirkhan.em.services.implementations;

import com.yasirkhan.em.dtos.EmployeeRequest;
import com.yasirkhan.em.dtos.EmployeeResponse;
import com.yasirkhan.em.dtos.EmployeeSearchCriteria;
import com.yasirkhan.em.entities.Employee;
import com.yasirkhan.em.entities.User;
import com.yasirkhan.em.entities.enums.Role;
import com.yasirkhan.em.exceptions.ResourceAlreadyExist;
import com.yasirkhan.em.exceptions.ResourceNotFoundException;
import com.yasirkhan.em.repositories.EmployeeRepository;
import com.yasirkhan.em.repositories.UserRepository;
import com.yasirkhan.em.services.EmployeeService;
import com.yasirkhan.em.specifications.EmployeeSpecification;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public EmployeeResponse addEmployee(EmployeeRequest request) {

        // Check if email already exists
        if (employeeRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExist("User with Email: " + request.email() + " is already exist");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new ResourceAlreadyExist("User with username: " + request.username() + " is already exist");
        }

        // Automatically save user first (because we put CascadeType.ALL with one to one mapping annotation in Employee class)
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.EMPLOYEE)
                .build();

        Employee emp = Employee.builder()
                .name(request.name())
                .email(request.email())
                .department(request.department())
                .salary(request.salary())
                .joiningDate(request.joiningDate())
                .user(user)
                .build();

        Employee saved = employeeRepository.save(emp);
        return mapToResponse(saved);
    }

    @Override
    public EmployeeResponse updateEmployee(UUID id, EmployeeRequest request) {

        Employee dbEmployee = findEmployeeById(id);
        dbEmployee.setName(request.name());
        dbEmployee.setEmail(request.email());
        dbEmployee.setDepartment(request.department());
        dbEmployee.setSalary(request.salary());
        Employee updated = employeeRepository.save(dbEmployee);
        return mapToResponse(updated);
    }

    @Override
    public void deleteEmployee(UUID id) {
        Employee dbEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        employeeRepository.delete(dbEmployee);
    }

    /**
         * We Call repository findAll method, and it takes a Pageable request.
         * @param search This hold the field name by which we perform filtration
         * @param pageable We pass pageable to repository that include pageNumber(page), pageSize(size)
         * and Sort (sorterBy and sortOrder)
         * @return It returns the Slice<Employee> by default it Return Page<Employee> but we overwrite
         * it in repository interface by writing Derived Query to Optimize Query because default to
         * return page hibernate run 2 query one for data chunks and other to count all elements.
     */
    @Override
    public List<EmployeeResponse> getAllEmployees(EmployeeSearchCriteria search, Pageable pageable) {


        return employeeRepository
                .findAllBy(EmployeeSpecification.getEmployeeSpecification(search), pageable)
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PostAuthorize("hasAuthority('EMPLOYEE_VIEW_ALL') or @employeeSecurity.isOwner(returnObject, authentication.name)")
    public EmployeeResponse getEmployeeById(UUID id) {
        Employee dbEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        return mapToResponse(dbEmployee);
    }

    // Helper methods
    private EmployeeResponse mapToResponse(Employee emp) {
        return new EmployeeResponse(
                emp.getId(),
                emp.getUser().getUsername(),
                emp.getName(),
                emp.getEmail(),
                emp.getDepartment(),
                emp.getUser().getRole().name(),
                emp.getSalary(),
                emp.getJoiningDate()
        );
    }

    private Employee findEmployeeById(UUID employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
    }
}