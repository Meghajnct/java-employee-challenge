package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private static final int MAX_RETRIES = 3;
    private final RestTemplate restTemplate;
    private final String baseUrl;

    public EmployeeService(RestTemplate restTemplate, @Value("${employee.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees");

        ResponseEntity<EmployeeResponse> response =
                restTemplate.exchange(baseUrl, HttpMethod.GET, null, EmployeeResponse.class);

        // Capture the HTTP status code
        HttpStatusCode statusCode = response.getStatusCode();
        logger.debug("getAllEmployees response status code: {}", statusCode);

        EmployeeResponse responseBody = response.getBody();
        if (responseBody == null || responseBody.getData() == null) {
            logger.warn("No employees found or response body is null");
            return List.of();
        }

        // Set the status code as a string in the response body
        responseBody.setStatusCode(statusCode.toString());
        return responseBody.getData();
    }

    public List<Employee> searchEmployeesByName(String searchString) {
        logger.info("Searching employees with name containing: {}", searchString);
        List<Employee> employees = getAllEmployees();
        return employees.stream()
                .filter(emp -> emp.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Optional<Employee> getEmployeeById(String id) {
        if (id == null || id.isEmpty()) {
            logger.warn("Employee ID was null or empty");
            return Optional.empty();
        }

        logger.info("Fetching employee with id: {}", id);

        ResponseEntity<EmployeeResponse> response =
                restTemplate.exchange(baseUrl + "/" + id, HttpMethod.GET, null, EmployeeResponse.class);

        // Capture the HTTP status code
        HttpStatusCode statusCode = response.getStatusCode();
        logger.debug("getEmployeeById response status code: {}", statusCode);

        EmployeeResponse responseBody = response.getBody();
        if (responseBody == null
                || responseBody.getData() == null
                || responseBody.getData().isEmpty()) {
            logger.warn("No employees found or response body is null");
            return Optional.empty();
        }
        return Optional.of(responseBody.getData().get(0));
    }

    public Integer getHighestSalary() {
        logger.info("Calculating highest salary");
        return getAllEmployees().stream()
                .map(Employee::getSalary)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public List<String> getTopTenHighestEarningEmployeeNames() {
        logger.info("Fetching top 10 highest earning employee names");
        return getAllEmployees().stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    public Optional<Employee> createEmployee(EmployeeInput input) {
        logger.info("Creating new employee");

        HttpEntity<EmployeeInput> requestEntity = new HttpEntity<>(input);
        ResponseEntity<EmployeeResponse> response =
                restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, EmployeeResponse.class);
        EmployeeResponse body = response.getBody();
        if (body == null || body.getData() == null || body.getData().isEmpty()) {
            throw new RuntimeException("Failed to create employee: Empty response");
        }
        return Optional.of(body.getData().get(0));
    }

    public String deleteEmployeeById(String id) {
        if (id == null || id.isEmpty()) {
            logger.warn("Employee ID was null or empty");
            return "Employee ID cannot be null or empty";
        }

        logger.info("Deleting employee with id: {}", id);

        // First get the employee by ID
        List<Employee> employees = getAllEmployees();
        if (employees.isEmpty()) {
            return "No employees found to delete.";
        }
        Employee targetEmployee = employees.stream()
                .filter(emp -> emp.getId().equals(id))
                .findFirst()
                .orElse(null);
        if (targetEmployee == null) {
            return "Employee not found with id: " + id;
        }

        // Check if there are multiple employees with the same name
        List<Employee> employeesWithSameName = employees.stream()
                .filter(emp -> !emp.getId().equals(id) && emp.getName().equals(targetEmployee.getName()))
                .toList();

        if (!employeesWithSameName.isEmpty()) {
            return "Cannot delete employee with id: " + id + ". Found "
                    + employeesWithSameName.size() + " other employee(s) with the same name: "
                    + targetEmployee.getName();
        }

        try {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", targetEmployee.getName());

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody);
            ResponseEntity<Map> response =
                    restTemplate.exchange(baseUrl, HttpMethod.DELETE, request, Map.class);

            if (response.getBody() != null && !response.getBody().isEmpty()) {
                return "Successfully deleted employee with id: " + id;
            } else {
                return "Failed to delete employee with id: " + id;
            }
        } catch (HttpClientErrorException.NotFound e) {
            return "Employee not found with id: " + id;
        } catch (Exception e) {
            logger.error("Error deleting employee with id {}: {}", id, e.getMessage());
            return "Error deleting employee with id: " + id + ". Error: " + e.getMessage();
        }
    }
}
