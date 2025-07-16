package com.reliaquest.server.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import com.reliaquest.server.service.MockEmployeeService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MockEmployeeController.class)
class MockEmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MockEmployeeService mockEmployeeService;

    private MockEmployee employee1;
    private MockEmployee employee2;
    private List<MockEmployee> employees;

    @BeforeEach
    void setUp() {
        // Set up test data
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        employee1 = MockEmployee.builder()
                .id(id1)
                .name("John Doe")
                .salary(75000)
                .age(30)
                .title("Software Engineer")
                .email("jdoe@company.com")
                .build();

        employee2 = MockEmployee.builder()
                .id(id2)
                .name("Jane Smith")
                .salary(85000)
                .age(35)
                .title("Senior Developer")
                .email("jsmith@company.com")
                .build();

        employees = Arrays.asList(employee1, employee2);
    }

    @Test
    void getEmployees_shouldReturnAllEmployees() throws Exception {
        // Given
        when(mockEmployeeService.getMockEmployees()).thenReturn(employees);

        // When & Then
        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].employee_name", is("John Doe")))
                .andExpect(jsonPath("$.data[1].employee_name", is("Jane Smith")));
    }

    @Test
    void getEmployee_shouldReturnEmployee_whenIdExists() throws Exception {
        // Given
        UUID id = employee1.getId();
        when(mockEmployeeService.findById(id)).thenReturn(Optional.of(employee1));

        // When & Then
        mockMvc.perform(get("/api/v1/employee/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.employee_name", is("John Doe")))
                .andExpect(jsonPath("$.data.employee_salary", is(75000)))
                .andExpect(jsonPath("$.data.employee_age", is(30)))
                .andExpect(jsonPath("$.data.employee_title", is("Software Engineer")));
    }

    @Test
    void getEmployee_shouldReturnNotFound_whenIdDoesNotExist() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(mockEmployeeService.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/employee/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getEmployeesByNameSearch_shouldReturnMatchingEmployees() throws Exception {
        // Given
        String nameFragment = "John";
        when(mockEmployeeService.findByNameFragment(nameFragment)).thenReturn(List.of(employee1));

        // When & Then
        mockMvc.perform(get("/api/v1/employee/search/{searchString}", nameFragment))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].employee_name", is("John Doe")));
    }

    @Test
    void getHighestSalaryOfEmployees_shouldReturnHighestSalary() throws Exception {
        // Given
        when(mockEmployeeService.getHighestSalary()).thenReturn(85000);

        // When & Then
        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(85000)));
    }

    @Test
    void getTop10HighestEarningEmployeeNames_shouldReturnNames() throws Exception {
        // Given
        List<String> topEmployeeNames = Arrays.asList("Jane Smith", "John Doe");
        when(mockEmployeeService.getTop10HighestEarningEmployeeNames()).thenReturn(topEmployeeNames);

        // When & Then
        mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0]", is("Jane Smith")))
                .andExpect(jsonPath("$.data[1]", is("John Doe")));
    }

    @Test
    void createEmployee_shouldCreateAndReturnEmployee() throws Exception {
        // Given
        CreateMockEmployeeInput input = new CreateMockEmployeeInput();
        input.setName("Alice Brown");
        input.setSalary(90000);
        input.setAge(28);
        input.setTitle("DevOps Engineer");

        MockEmployee newEmployee = MockEmployee.builder()
                .id(UUID.randomUUID())
                .name("Alice Brown")
                .salary(90000)
                .age(28)
                .title("DevOps Engineer")
                .email("abrown@company.com")
                .build();

        when(mockEmployeeService.create(any(CreateMockEmployeeInput.class))).thenReturn(newEmployee);

        // When & Then
        mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.employee_name", is("Alice Brown")))
                .andExpect(jsonPath("$.data.employee_salary", is(90000)))
                .andExpect(jsonPath("$.data.employee_age", is(28)))
                .andExpect(jsonPath("$.data.employee_title", is("DevOps Engineer")));

        verify(mockEmployeeService).create(any(CreateMockEmployeeInput.class));
    }

    @Test
    void deleteEmployee_shouldReturnTrue_whenEmployeeDeleted() throws Exception {
        // Given
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput();
        input.setName("John Doe");

        when(mockEmployeeService.delete(any(DeleteMockEmployeeInput.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(true)));

        verify(mockEmployeeService).delete(any(DeleteMockEmployeeInput.class));
    }

    @Test
    void deleteEmployee_shouldReturnFalse_whenEmployeeNotFound() throws Exception {
        // Given
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput();
        input.setName("Nonexistent Name");

        when(mockEmployeeService.delete(any(DeleteMockEmployeeInput.class))).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", is(false)));

        verify(mockEmployeeService).delete(any(DeleteMockEmployeeInput.class));
    }
}
