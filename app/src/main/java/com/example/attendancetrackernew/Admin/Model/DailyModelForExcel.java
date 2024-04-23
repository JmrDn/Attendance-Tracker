package com.example.attendancetrackernew.Admin.Model;

public class DailyModelForExcel {
    String name;
    String position;
    String timeIn;
    String timeOut;
    String late;
    String leave;
    String overTime;

    public DailyModelForExcel(String name, String position, String timeIn, String timeOut, String late, String leave, String overTime) {
        this.name = name;
        this.position = position;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.late = late;
        this.leave = leave;
        this.overTime = overTime;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public String getLate() {
        return late;
    }

    public String getLeave() {
        return leave;
    }

    public String getOverTime() {
        return overTime;
    }
}
