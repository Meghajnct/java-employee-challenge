package com.reliaquest.server.service;

import com.reliaquest.server.config.ServerConfiguration;
import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import java.util.*;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MockEmployeeService {

    private final Faker faker;

    @Getter
    private final List<MockEmployee> mockEmployees;

    public Optional<MockEmployee> findById(@NonNull UUID uuid) {
        return mockEmployees.stream()
                .filter(mockEmployee -> Objects.nonNull(mockEmployee.getId())
                        && mockEmployee.getId().equals(uuid))
                .peek(employee -> log.info("found the employee: {}", employee))
                .findFirst();
    }

    public List<MockEmployee> findByNameFragment(@NonNull String nameFragment) {
        return mockEmployees.stream()
                .filter(emp ->
                        emp.getName() != null && emp.getName().toLowerCase().contains(nameFragment.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Integer getHighestSalary() {
        return mockEmployees.stream()
                .map(MockEmployee::getSalary)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        return mockEmployees.stream()
                .sorted(Comparator.comparing(MockEmployee::getSalary, Comparator.nullsLast(Integer::compareTo))
                        .reversed())
                .limit(10)
                .map(MockEmployee::getName)
                .collect(Collectors.toList());
    }

    public MockEmployee create(@NonNull CreateMockEmployeeInput input) {
        final var mockEmployee = MockEmployee.from(
                ServerConfiguration.EMAIL_TEMPLATE.formatted(
                        faker.twitter().userName().toLowerCase()),
                input);
        mockEmployees.add(mockEmployee);
        log.debug("Added employee: {}", mockEmployee);
        return mockEmployee;
    }

    public boolean delete(@NonNull DeleteMockEmployeeInput input) {
        final var mockEmployee = mockEmployees.stream()
                .filter(employee -> Objects.nonNull(employee.getName())
                        && employee.getName().equalsIgnoreCase(input.getName()))
                .findFirst();
        if (mockEmployee.isPresent()) {
            mockEmployees.remove(mockEmployee.get());
            log.debug("Removed employee: {}", mockEmployee.get());
            return true;
        }

        return false;
    }

    public boolean deleteEmployeeById(@NonNull String id) {
        final var mockEmployee = mockEmployees.stream()
                .filter(employee -> Objects.nonNull(employee.getId())
                        && employee.getId().toString().equalsIgnoreCase(id))
                .findFirst();
        if (mockEmployee.isPresent()) {
            mockEmployees.remove(mockEmployee.get());
            log.debug("Removed employee by ID: {}", mockEmployee.get());
            return true;
        }

        return false;
    }
}
