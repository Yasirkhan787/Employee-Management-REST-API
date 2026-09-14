package com.yasirkhan.em.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 50)
    private String department;

    @Column(nullable = false)
    private double salary;;

    @Column(name = "joining_date", nullable = false, updatable = false)
    private LocalDate joiningDate;

    /*
        Default constructor for hibernate to create the object via reflection.
        Making it protected to prevent from creating empty, invalid Employee Object
     */
    protected Employee(){}

    // Private constructor that takes the Builder
    private Employee(Builder builder) {
        this.name = builder.name;
        this.email = builder.email;
        this.department = builder.department;
        this.salary = builder.salary;
        this.joiningDate = builder.joiningDate;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    // Static method to get a new builder instance
    public static Builder builder() {
        return new Builder();
    }

    // Static Nested Builder class
    public static class Builder{
        private String name;
        private String email;
        private String department;
        private Double salary;
        private LocalDate joiningDate;

        public Builder name(String name) {
            this.name = name;
            return this; // Returning 'this' enables method chaining
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder salary(Double salary) {
            this.salary = salary;
            return this;
        }

        public Builder joiningDate(LocalDate joiningDate) {
            this.joiningDate = joiningDate;
            return this;
        }

        // The final build method that build the employee
        public Employee build() {
            return new Employee(this);
        }

    }

}
