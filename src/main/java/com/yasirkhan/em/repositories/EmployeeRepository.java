package com.yasirkhan.em.repositories;

import com.yasirkhan.em.entities.Employee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    Slice<Employee> findAllBy(Pageable pageable);

    boolean existsByEmail(String email);
}
