package com.reliaquest.server.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.datafaker.Faker;
import net.datafaker.providers.base.Twitter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MockEmployeeServiceTest {

    @Mock
    private Faker faker;

    @Mock
    private Twitter twitter;

    private MockEmployeeService mockEmployeeService;
    private List<MockEmployee> mockEmployees;
    private MockEmployee employee1;
    private MockEmployee employee2;
    private MockEmployee employee3;

    @BeforeEach
    void setUp() {
        // Set up test data
        mockEmployees = new ArrayList<>();

        // Create test employees
        employee1 = MockEmployee.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .salary(75000)
                .age(30)
                .title("Software Engineer")
                .email("jdoe@company.com")
                .build();

        employee2 = MockEmployee.builder()
                .id(UUID.randomUUID())
                .name("Jane Smith")
                .salary(85000)
                .age(35)
                .title("Senior Developer")
                .email("jsmith@company.com")
                .build();

        employee3 = MockEmployee.builder()
                .id(UUID.randomUUID())
                .name("Bob Johnson")
                .salary(65000)
                .age(25)
                .title("Junior Developer")
                .email("bjohnson@company.com")
                .build();

        // Add employees to the list
        mockEmployees.add(employee1);
        mockEmployees.add(employee2);
        mockEmployees.add(employee3);

        // Properly mock the Twitter provider
        when(faker.twitter()).thenReturn(twitter);
        when(twitter.userName()).thenReturn("testuser");

        // Initialize the service with our mock data
        mockEmployeeService = new MockEmployeeService(faker, mockEmployees);
    }

    @Test
    void findById_shouldReturnEmployee_whenIdExists() {
        // Given an existing employee ID
        UUID id = employee1.getId();

        // When findById is called
        Optional<MockEmployee> result = mockEmployeeService.findById(id);

        // Then the employee should be returned
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(employee1);
    }

    @Test
    void findById_shouldReturnEmpty_whenIdDoesNotExist() {
        // Given a non-existent employee ID
        UUID nonExistentId = UUID.randomUUID();

        // When findById is called
        Optional<MockEmployee> result = mockEmployeeService.findById(nonExistentId);

        // Then no employee should be returned
        assertThat(result).isEmpty();
    }

    @Test
    void findByNameFragment_shouldReturnMatchingEmployees() {
        // Given a name fragment
        String nameFragment = "Jo";

        // When findByNameFragment is called
        List<MockEmployee> result = mockEmployeeService.findByNameFragment(nameFragment);

        // Then employees with matching names should be returned
        assertThat(result).hasSize(2);
        assertThat(result).contains(employee1, employee3);
    }

    @Test
    void findByNameFragment_shouldBeCaseInsensitive() {
        // Given a name fragment in different case
        String nameFragment = "jo";

        // When findByNameFragment is called
        List<MockEmployee> result = mockEmployeeService.findByNameFragment(nameFragment);

        // Then employees with matching names should be returned, ignoring case
        assertThat(result).hasSize(2);
        assertThat(result).contains(employee1, employee3);
    }

    @Test
    void getHighestSalary_shouldReturnHighestSalary() {
        // When getHighestSalary is called
        Integer result = mockEmployeeService.getHighestSalary();

        // Then the highest salary should be returned
        assertThat(result).isEqualTo(85000);
    }

    @Test
    void getHighestSalary_shouldReturnZero_whenNoEmployees() {
        // Given an empty list of employees
        mockEmployeeService = new MockEmployeeService(faker, new ArrayList<>());

        // When getHighestSalary is called
        Integer result = mockEmployeeService.getHighestSalary();

        // Then zero should be returned
        assertThat(result).isEqualTo(0);
    }

    @Test
    void getTop10HighestEarningEmployeeNames_shouldReturnSortedNames() {
        // When getTop10HighestEarningEmployeeNames is called
        List<String> result = mockEmployeeService.getTop10HighestEarningEmployeeNames();

        // Then the names should be returned in descending order of salary
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo("Jane Smith");
        assertThat(result.get(1)).isEqualTo("John Doe");
        assertThat(result.get(2)).isEqualTo("Bob Johnson");
    }

    @Test
    void create_shouldAddNewEmployee() {
        // Given a CreateMockEmployeeInput
        CreateMockEmployeeInput input = new CreateMockEmployeeInput();
        input.setName("Alice Brown");
        input.setSalary(90000);
        input.setAge(28);
        input.setTitle("DevOps Engineer");

        // When create is called
        MockEmployee result = mockEmployeeService.create(input);

        // Then a new employee should be created and added to the list
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Alice Brown");
        assertThat(result.getSalary()).isEqualTo(90000);
        assertThat(result.getAge()).isEqualTo(28);
        assertThat(result.getTitle()).isEqualTo("DevOps Engineer");
        assertThat(result.getEmail()).contains("testuser");

        assertThat(mockEmployees).hasSize(4);
        assertThat(mockEmployees).contains(result);
    }

    @Test
    void delete_shouldRemoveEmployee_whenNameMatches() {
        // Given a DeleteMockEmployeeInput with an existing name
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput();
        input.setName("John Doe");

        // When delete is called
        boolean result = mockEmployeeService.delete(input);

        // Then the employee should be removed and true returned
        assertThat(result).isTrue();
        assertThat(mockEmployees).hasSize(2);
        assertThat(mockEmployees).doesNotContain(employee1);
    }

    @Test
    void delete_shouldReturnFalse_whenNameDoesNotMatch() {
        // Given a DeleteMockEmployeeInput with a non-existent name
        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput();
        input.setName("Nonexistent Name");

        // When delete is called
        boolean result = mockEmployeeService.delete(input);

        // Then false should be returned and no employees removed
        assertThat(result).isFalse();
        assertThat(mockEmployees).hasSize(3);
    }

    @Test
    void deleteEmployeeById_shouldRemoveEmployee_whenIdMatches() {
        // Given an existing employee ID
        String id = employee1.getId().toString();

        // When deleteEmployeeById is called
        boolean result = mockEmployeeService.deleteEmployeeById(id);

        // Then the employee should be removed and true returned
        assertThat(result).isTrue();
        assertThat(mockEmployees).hasSize(2);
        assertThat(mockEmployees).doesNotContain(employee1);
    }

    @Test
    void deleteEmployeeById_shouldReturnFalse_whenIdDoesNotMatch() {
        // Given a non-existent employee ID
        String nonExistentId = UUID.randomUUID().toString();

        // When deleteEmployeeById is called
        boolean result = mockEmployeeService.deleteEmployeeById(nonExistentId);

        // Then false should be returned and no employees removed
        assertThat(result).isFalse();
        assertThat(mockEmployees).hasSize(3);
    }
}
