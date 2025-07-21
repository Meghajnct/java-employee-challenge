package com.reliaquest.api.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import org.springframework.http.HttpMethod;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    private void setupMockServer() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getAllEmployees_ReturnsEmployeeList() throws Exception {
        // Setup mock response from external API
        setupMockServer();

        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setStatus("success");
        mockResponse.setData(Arrays.asList(
            new Employee("1", "John Doe", "Developer", 100000, 30, "john@example.com"),
            new Employee("2", "Jane Smith", "Manager", 120000, 35, "jane@example.com")
        ));

        // Configure mock server to return our prepared response
        mockServer.expect(MockRestRequestMatchers.requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(
                        objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON
                ));

        // Perform the test
        mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].employee_name").value("Jane Smith"));

        mockServer.verify();
    }

    @Test
    void getEmployeeById_ExistingEmployee_ReturnsEmployee() throws Exception {
        setupMockServer();

        // Setup mock response from external API
        String employeeId = "1";
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setStatus("success");
        // Wrap the single employee in a list since setData expects a List<Employee>
        mockResponse.setData(Arrays.asList(
            new Employee(employeeId, "John Doe", "Developer", 100000, 30, "john@example.com")
        ));

        // Configure mock server
        mockServer.expect(MockRestRequestMatchers.requestTo("http://localhost:8112/api/v1/employee/" + employeeId))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(
                        objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON
                ));

        // Perform the test
        mockMvc.perform(get("/api/v1/employee/{id}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(employeeId))
                .andExpect(jsonPath("$.employee_name").value("John Doe"));

        mockServer.verify();
    }

    @Test
    void getEmployeeById_NonExistingEmployee_ReturnsNotFound() throws Exception {
        setupMockServer();

        String employeeId = "999";

        // Mock a 404 response
        mockServer.expect(MockRestRequestMatchers.requestTo("http://localhost:8112/api/v1/employee/" + employeeId))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));

        // Perform the test
        mockMvc.perform(get("/api/v1/employee/{id}", employeeId))
                .andExpect(status().isNotFound());

        mockServer.verify();
    }

    @Test
    void searchEmployeesByName_MatchingEmployees_ReturnsFilteredList() throws Exception {
        setupMockServer();

        String searchName = "John";
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setStatus("success");
        mockResponse.setData(Arrays.asList(
            new Employee("1", "John Doe", "Developer", 100000, 30, "john@example.com")
        ));

        // Configure only the endpoint that's actually being called
        mockServer.expect(MockRestRequestMatchers.requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(
                        objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON
                ));


        // Perform the test
        mockMvc.perform(get("/api/v1/employee/search/{searchString}", searchName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].employee_name").value("John Doe"));

        mockServer.verify();
    }

    @Test
    void getHighestSalary_ReturnsSalaryValue() throws Exception {
        setupMockServer();

        // Setup mock response with employees having different salaries
        EmployeeResponse mockResponse = new EmployeeResponse();
        mockResponse.setStatus("success");
        mockResponse.setData(Arrays.asList(
            new Employee("1", "John Doe", "Developer", 100000, 30, "john@example.com"),
            new Employee("2", "Jane Smith", "Manager", 120000, 35, "jane@example.com"),
            new Employee("3", "Bob Johnson", "Director", 150000, 40, "bob@example.com")
        ));

        // Configure mock server
        mockServer.expect(MockRestRequestMatchers.requestTo("http://localhost:8112/api/v1/employee"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                .andRespond(MockRestResponseCreators.withSuccess(
                        objectMapper.writeValueAsString(mockResponse), MediaType.APPLICATION_JSON
                ));

        // Perform the test
        mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andExpect(content().string("150000"));

        mockServer.verify();
    }
}
