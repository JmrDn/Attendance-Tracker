package com.example.attendancetrackernew.Admin.Model;

public class DailyReportsModel {
    String fullName;
    String employeeNum;
    String position;
    String late;
    String leaveEarly;
    String overTime;
    String dateId;

    public DailyReportsModel(String fullName, String employeeNum, String position, String late, String leaveEarly, String overTime,String dateId) {
        this.fullName = fullName;
        this.employeeNum = employeeNum;
        this.position = position;
        this.late = late;
        this.leaveEarly = leaveEarly;
        this.overTime = overTime;
        this.dateId = dateId;
    }
    public String getDateId(){return dateId;}

    public String getFullName() {
        return fullName;
    }

    public String getEmployeeNum() {
        return employeeNum;
    }

    public String getPosition() {
        return position;
    }

    public String getLate() {
        return late;
    }

    public String getLeaveEarly() {
        return leaveEarly;
    }

    public String getOverTime() {
        return overTime;
    }
}
