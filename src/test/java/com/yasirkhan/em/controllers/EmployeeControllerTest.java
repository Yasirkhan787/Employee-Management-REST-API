package com.yasirkhan.em.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yasirkhan.em.dtos.EmployeeRequest;
import com.yasirkhan.em.dtos.EmployeeResponse;
import com.yasirkhan.em.dtos.EmployeeSearchCriteria;
import com.yasirkhan.em.services.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService service;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeRequest validRequest;
    private EmployeeResponse expectedResponse;
    private UUID employeeId;

    @BeforeEach
    void setUp() {
        employeeId = UUID.randomUUID();

        validRequest = new EmployeeRequest(
                "Yasir Khan",
                "yasir@example.com",
                "Engineering",
                90000.0,
                LocalDate.of(2026, 1, 15)
        );

        expectedResponse = new EmployeeResponse(
                employeeId,
                "Yasir Khan",
                "yasir@example.com",
                "Engineering",
                90000.0,
                LocalDate.of(2026, 1, 15)
        );
    }

    @Test
    void addEmployee_ValidRequest_ReturnsCreated() throws Exception {
        when(service.addEmployee(any(EmployeeRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(employeeId.toString()))
                .andExpect(jsonPath("$.name").value("Yasir Khan"));
    }

    @Test
    void addEmployee_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Create an invalid request (empty name, negative salary)
        EmployeeRequest invalidRequest = new EmployeeRequest(
                "",
                "invalid-email",
                "Engineering",
                -100.0,
                LocalDate.now().plusDays(5) // Future date
        );

        mockMvc.perform(post("/api/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        // We could also assert the specific validation error messages here if needed.
    }

    @Test
    void updateEmployee_ValidRequest_ReturnsOk() throws Exception {
        when(service.updateEmployee(eq(employeeId), any(EmployeeRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/v1/employees/{id}", employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yasir Khan"));
    }

    @Test
    void deleteEmployee_ExistingId_ReturnsNoContent() throws Exception {
        doNothing().when(service).deleteEmployee(employeeId);

        mockMvc.perform(delete("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getEmployeeById_ExistingId_ReturnsOk() throws Exception {
        when(service.getEmployeeById(employeeId)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/employees/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("yasir@example.com"));
    }

    @Test
    void getAllEmployees_WithFilters_ReturnsOk() throws Exception {
        List<EmployeeResponse> responses = Collections.singletonList(expectedResponse);

        // Mock the service to return our list when called with ANY criteria and pageable
        when(service.getAllEmployees(any(EmployeeSearchCriteria.class), any(Pageable.class)))
                .thenReturn(responses);

        mockMvc.perform(get("/api/v1/employees")
                        .param("department", "Engineering")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].department").value("Engineering"));
    }
}