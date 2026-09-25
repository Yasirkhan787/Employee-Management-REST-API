package com.yasirkhan.em.security;

import com.yasirkhan.em.dtos.EmployeeResponse;
import com.yasirkhan.em.repositories.EmployeeRepository;
import org.springframework.stereotype.Component;

@Component("employeeSecurity")
public class EmployeeSecurityValidator {


    /**
     * Used by @PostAuthorize to check a freshly fetched DTO.
     */
    public boolean isOwner(EmployeeResponse response, String authName) {
        if (response == null || authName == null) {
            return true;
        }
        return authName.equals(response.username()) || authName.equals(response.email());
    }
}
