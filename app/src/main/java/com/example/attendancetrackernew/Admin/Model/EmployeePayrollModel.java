package com.example.attendancetrackernew.Admin.Model;

public class EmployeePayrollModel {
    String fullName;
    String employeeNum;
    String position;

    public EmployeePayrollModel(String fullName, String employeeNum, String position) {
        this.fullName = fullName;
        this.employeeNum = employeeNum;
        this.position = position;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmployeeNum() {
        return employeeNum;
    }

    public String getPosition() {
        return position;
    }
}
