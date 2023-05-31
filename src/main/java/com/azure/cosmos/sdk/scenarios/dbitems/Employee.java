package com.azure.cosmos.sdk.scenarios.dbitems;

import java.util.UUID;

public class Employee {

    private String id;

    private String firstName;

    private String lastName;

    private String department;

    public Employee() {
        this.id = UUID.randomUUID().toString();
        this.department = UUID.randomUUID().toString();
    }

    // TODO: Add all fields
    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("Employee Id: %s\n", id));
        stringBuilder.append(String.format("Employee Department: %s\n", department));

        return stringBuilder.toString();
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
