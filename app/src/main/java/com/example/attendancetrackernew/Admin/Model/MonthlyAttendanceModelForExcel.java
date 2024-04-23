package com.example.attendancetrackernew.Admin.Model;

public class MonthlyAttendanceModelForExcel {
    String employeeID;
    String employeeName;
    String presentDays;
    String late;
    String overtime;
    String leaveEarly;

    public MonthlyAttendanceModelForExcel(String employeeID, String employeeName, String presentDays, String late, String overtime, String leaveEarly) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.presentDays = presentDays;
        this.late = late;
        this.overtime = overtime;
        this.leaveEarly = leaveEarly;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getPresentDays() {
        return presentDays;
    }

    public String getLate() {
        return late;
    }

    public String getOvertime() {
        return overtime;
    }

    public String getLeaveEarly() {
        return leaveEarly;
    }
}
