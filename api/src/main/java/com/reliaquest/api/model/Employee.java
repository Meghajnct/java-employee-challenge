package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Employee {
    private String id;

    @JsonProperty("employee_name")
    private String name;

    @JsonProperty("employee_title")
    private String designation;

    @JsonProperty("employee_salary")
    private Integer salary;

    @JsonProperty("employee_age")
    private Integer age;

    @JsonProperty("employee_email")
    private String email;

    public Employee() {}

    public Employee(String id, String name, String designation, Integer salary, Integer age, String email) {
        this.id = id;
        this.name = name;
        this.designation = designation;
        this.salary = salary;
        this.age = age;
        this.email = email;
    }
}
