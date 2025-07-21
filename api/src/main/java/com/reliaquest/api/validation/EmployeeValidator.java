package com.reliaquest.api.validation;

import com.reliaquest.api.model.EmployeeInput;

/**
 * Utility class for employee-related validation
 */
public class EmployeeValidator {

    /**
     * Validates the employee input data
     * @param input The employee input to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateEmployeeInput(EmployeeInput input) {
        if (input == null) {
            throw new IllegalArgumentException("Employee input cannot be null");
        }
        if (input.getName() == null || input.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee name cannot be blank");
        }
        if (input.getSalary() == null) {
            throw new IllegalArgumentException("Employee salary cannot be null");
        }
        if (input.getSalary() <= 0) {
            throw new IllegalArgumentException("Employee salary must be positive");
        }
        if (input.getAge() == null) {
            throw new IllegalArgumentException("Employee age cannot be null");
        }
        if (input.getAge() < 16 || input.getAge() > 75) {
            throw new IllegalArgumentException("Employee age must be between 16 and 75");
        }
        if (input.getTitle() == null || input.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Employee title cannot be blank");
        }
    }

    /**
     * Validates an employee ID
     * @param id The employee ID to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateEmployeeId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be blank");
        }
    }
}
