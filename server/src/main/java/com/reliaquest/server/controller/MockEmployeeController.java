package com.reliaquest.server.controller;

import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import com.reliaquest.server.model.Response;
import com.reliaquest.server.service.MockEmployeeService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class MockEmployeeController {

    private final MockEmployeeService mockEmployeeService;

    @GetMapping()
    public Response<List<MockEmployee>> getEmployees() {
        return Response.handledWith(mockEmployeeService.getMockEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<MockEmployee>> getEmployee(@PathVariable("id") UUID uuid) {
        return mockEmployeeService
                .findById(uuid)
                .map(employee -> ResponseEntity.ok(Response.handledWith(employee)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.handled()));
    }

    @GetMapping("/search/{searchString}")
    public Response<List<MockEmployee>> getEmployeesByNameSearch(@PathVariable("searchString") String nameFragment) {
        return Response.handledWith(mockEmployeeService.findByNameFragment(nameFragment));
    }

    @GetMapping("/highestSalary")
    public Response<Integer> getHighestSalaryOfEmployees() {
        return Response.handledWith(mockEmployeeService.getHighestSalary());
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    public Response<List<String>> getTop10HighestEarningEmployeeNames() {

        return Response.handledWith(mockEmployeeService.getTop10HighestEarningEmployeeNames());
    }

    @PostMapping()
    public Response<MockEmployee> createEmployee(@Valid @RequestBody CreateMockEmployeeInput input) {
        return Response.handledWith(mockEmployeeService.create(input));
    }

    @DeleteMapping()
    public Response<Boolean> deleteEmployee(@Valid @RequestBody DeleteMockEmployeeInput input) {
        return Response.handledWith(mockEmployeeService.delete(input));
    }

    @DeleteMapping("/{id}")
    public Response<Boolean> deleteEmployeeById(@PathVariable String id) {
        return Response.handledWith(mockEmployeeService.deleteEmployeeById(id));
    }
}
