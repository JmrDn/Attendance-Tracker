package com.example.attendancetrackernew.Admin.Model;

public class SubDailyReportsModel {
    String date;
    String timeIN;
    String timeOut;

    public SubDailyReportsModel(String date, String timeIN, String timeOut) {
        this.date = date;
        this.timeIN = timeIN;
        this.timeOut = timeOut;
    }

    public String getDate() {
        return date;
    }

    public String getTimeIN() {
        return timeIN;
    }

    public String getTimeOut() {
        return timeOut;
    }
}
