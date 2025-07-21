package com.reliaquest.api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeInput;
import com.reliaquest.api.model.EmployeeResponse;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private final String baseUrl = "http://dummy-url.com/api/employees";
    private List<Employee> mockEmployees;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(restTemplate, baseUrl);

        // Create mock employees for testing
        mockEmployees = Arrays.asList(
                new Employee("1", "John Doe", "Developer", 100000, 30, "john@example.com"),
                new Employee("2", "Jane Smith", "Manager", 120000, 35, "jane@example.com"),
                new Employee("3", "Bob Johnson", "Director", 150000, 40, "bob@example.com"),
                new Employee("4", "Alice Brown", "Developer", 95000, 28, "alice@example.com"),
                new Employee("5", "Charlie Davis", "QA Engineer", 90000, 25, "charlie@example.com"),
                new Employee("6", "Dave Wilson", "DevOps", 110000, 32, "dave@example.com"),
                new Employee("7", "Eve Jackson", "Product Manager", 125000, 38, "eve@example.com"),
                new Employee("8", "Frank Miller", "Architect", 140000, 42, "frank@example.com"),
                new Employee("9", "Grace Lee", "UX Designer", 95000, 29, "grace@example.com"),
                new Employee("10", "Harry White", "Backend Developer", 105000, 31, "harry@example.com"),
                new Employee("11", "Ivy Green", "Frontend Developer", 98000, 27, "ivy@example.com")
        );
    }

    @Test
    void getAllEmployees_ReturnsAllEmployees() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(mockEmployees);

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertEquals(11, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Ivy Green", result.get(10).getName());
        verify(restTemplate, times(1)).exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        );
    }

    @Test
    void getAllEmployees_EmptyResponse_ReturnsEmptyList() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(null);

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllEmployees_NullResponse_ReturnsEmptyList() {
        // Arrange
        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void searchEmployeesByName_MatchFound_ReturnsFilteredList() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(mockEmployees);

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        List<Employee> result = employeeService.searchEmployeesByName("john d");

        // Assert
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    void searchEmployeesByName_CaseInsensitive_ReturnsFilteredList() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(mockEmployees);

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        List<Employee> result = employeeService.searchEmployeesByName("JOHN");

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("John Doe")));
        assertTrue(result.stream().anyMatch(e -> e.getName().equals("Bob Johnson")));
    }

    @Test
    void searchEmployeesByName_NoMatch_ReturnsEmptyList() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(mockEmployees);

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        List<Employee> result = employeeService.searchEmployeesByName("XYZ");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeeById_EmployeeExists_ReturnsEmployee() {
        // Arrange
        String employeeId = "1";
        Employee mockEmployee = mockEmployees.get(0);

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Collections.singletonList(mockEmployee));

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl + "/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        Optional<Employee> result = employeeService.getEmployeeById(employeeId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        assertEquals(100000, result.get().getSalary());
    }

    @Test
    void getEmployeeById_EmployeeDoesNotExist_ReturnsEmptyOptional() {
        // Arrange
        String employeeId = "999";

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Collections.emptyList());

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl + "/" + employeeId),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        Optional<Employee> result = employeeService.getEmployeeById(employeeId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getEmployeeById_NullId_ReturnsEmptyOptional() {
        // Act
        Optional<Employee> result = employeeService.getEmployeeById(null);

        // Assert
        assertTrue(result.isEmpty());
        verify(restTemplate, never()).exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                eq(EmployeeResponse.class)
        );
    }

    @Test
    void getHighestSalary_ReturnsMaxSalary() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(mockEmployees);

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        Integer result = employeeService.getHighestSalary();

        // Assert
        assertEquals(150000, result);
    }

    @Test
    void getHighestSalary_EmptyEmployeeList_ReturnsZero() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Collections.emptyList());

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        Integer result = employeeService.getHighestSalary();

        // Assert
        assertEquals(0, result);
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_ReturnsTopTenNames() {
        // Arrange
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(mockEmployees);

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        // Assert
        assertEquals(10, result.size());
        assertEquals("Bob Johnson", result.get(0)); // Highest salary
        assertEquals("Frank Miller", result.get(1));
        assertEquals("Eve Jackson", result.get(2));
        // The rest would continue in descending order of salary
    }

    @Test
    void getTopTenHighestEarningEmployeeNames_LessThanTenEmployees_ReturnsAllSorted() {
        // Arrange
        List<Employee> fewEmployees = mockEmployees.subList(0, 5); // Just take 5 employees

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(fewEmployees);

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        // Assert
        assertEquals(5, result.size());
    }

    @Test
    void createEmployee_Success_ReturnsEmployee() {
        // Arrange
        EmployeeInput input = new EmployeeInput();
        input.setName("New Employee");
        input.setSalary(110000);
        input.setAge(33);
        input.setTitle("Software Engineer");

        Employee createdEmployee = new Employee("12", "New Employee", "Software Engineer", 110000, 33, null);

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Collections.singletonList(createdEmployee));

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.CREATED);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act
        Optional<Employee> result = employeeService.createEmployee(input);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("New Employee", result.get().getName());
        assertEquals(Integer.valueOf(110000), result.get().getSalary());
        assertEquals("12", result.get().getId());
    }

    @Test
    void createEmployee_EmptyResponse_ThrowsException() {
        // Arrange
        EmployeeInput input = new EmployeeInput();
        input.setName("New Employee");

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setData(Collections.emptyList());

        ResponseEntity<EmployeeResponse> responseEntity =
                new ResponseEntity<>(mockResponse, HttpStatus.CREATED);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(EmployeeResponse.class)
        )).thenReturn(responseEntity);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> employeeService.createEmployee(input));
    }

    @Test
    void deleteEmployeeById_Success_ReturnsSuccessMessage() {
        // Arrange
        String employeeId = "1";
        Employee employee = mockEmployees.get(0);

        // Mock for getAllEmployees
        EmployeeResponse getAllResponse = new EmployeeResponse();
        getAllResponse.setData(mockEmployees);

        ResponseEntity<EmployeeResponse> getAllResponseEntity =
                new ResponseEntity<>(getAllResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(getAllResponseEntity);

        // Mock for delete call
        ResponseEntity<Map> deleteResponseEntity =
                new ResponseEntity<>(Collections.singletonMap("status", "success"), HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.DELETE),
                any(HttpEntity.class),
                eq(Map.class)
        )).thenReturn(deleteResponseEntity);

        // Act
        String result = employeeService.deleteEmployeeById(employeeId);

        // Assert
        assertTrue(result.contains("Successfully deleted"));
        verify(restTemplate).exchange(eq(baseUrl), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Map.class));
    }

    @Test
    void deleteEmployeeById_NullId_ReturnsErrorMessage() {
        // Act
        String result = employeeService.deleteEmployeeById(null);

        // Assert
        assertEquals("Employee ID cannot be null or empty", result);

    }

    @Test
    void deleteEmployeeById_EmployeeNotFound_ReturnsNotFoundMessage() {
        // Arrange
        String employeeId = "999";

        // Mock for getAllEmployees
        EmployeeResponse getAllResponse = new EmployeeResponse();
        getAllResponse.setData(mockEmployees);

        ResponseEntity<EmployeeResponse> getAllResponseEntity =
                new ResponseEntity<>(getAllResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(getAllResponseEntity);

        // Act
        String result = employeeService.deleteEmployeeById(employeeId);

        // Assert
        assertTrue(result.contains("Employee not found"));
    }

    @Test
    void deleteEmployeeById_MultipleEmployeesWithSameName_ReturnsErrorMessage() {
        // Arrange
        String employeeId = "1";

        // Create employees with same name
        List<Employee> employeesWithDuplicateNames = Arrays.asList(
                new Employee("1", "John Doe", "Developer", 100000, 30, "john@example.com"),
                new Employee("2", "John Doe", "Manager", 120000, 35, "john2@example.com") // Same name as first employee
        );

        // Mock for getAllEmployees
        EmployeeResponse getAllResponse = new EmployeeResponse();
        getAllResponse.setData(employeesWithDuplicateNames);

        ResponseEntity<EmployeeResponse> getAllResponseEntity =
                new ResponseEntity<>(getAllResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(getAllResponseEntity);

        // Act
        String result = employeeService.deleteEmployeeById(employeeId);

        // Assert
        assertTrue(result.contains("Cannot delete employee"));
        assertTrue(result.contains("Found 1 other employee(s) with the same name"));

    }

    @Test
    void deleteEmployeeById_ApiCallFails_ReturnsErrorMessage() {
        // Arrange
        String employeeId = "01";

        // Mock for getAllEmployees
        EmployeeResponse getAllResponse = new EmployeeResponse();
        getAllResponse.setData(mockEmployees);

        ResponseEntity<EmployeeResponse> getAllResponseEntity =
                new ResponseEntity<>(getAllResponse, HttpStatus.OK);

        when(restTemplate.exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        )).thenReturn(getAllResponseEntity);

        // Act
        String result = employeeService.deleteEmployeeById(employeeId);

        // Assert
        assertTrue(result.contains("Employee not found"));
        verify(restTemplate).exchange(
                eq(baseUrl),
                eq(HttpMethod.GET),
                isNull(),
                eq(EmployeeResponse.class)
        );
    }
}

