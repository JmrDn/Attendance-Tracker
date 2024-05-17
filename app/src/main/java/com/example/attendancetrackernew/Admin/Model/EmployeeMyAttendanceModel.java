package com.example.attendancetrackernew.Admin.Model;

public class EmployeeMyAttendanceModel {
    String date;
    String timeIn;
    String timeOut;

    public EmployeeMyAttendanceModel(String date, String timeIn, String timeOut) {
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getDate() {
        return date;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }
}
