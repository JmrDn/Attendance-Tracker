package com.example.attendancetrackernew.Admin.Model;

public class AttendanceModel {
    String timeIn;
    String timeOut;
    String fullName;
    public AttendanceModel(String timeIn, String timeOut, String fullName){
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.fullName = fullName;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public String getFullName() {
        return fullName;
    }
}
