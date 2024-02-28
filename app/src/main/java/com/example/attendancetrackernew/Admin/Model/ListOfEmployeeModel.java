package com.example.attendancetrackernew.Admin.Model;

public class ListOfEmployeeModel {
    String fullName;
    String employeeNum;
    String position;
    String phoneNum;
    String birthday;
    String email;
    String fileNameOfQRCode;

    public ListOfEmployeeModel(String fullName, String employeeNum, String position,
                               String phoneNum, String birthday, String email,
                               String fileNameOfQRCode) {
        this.fullName = fullName;
        this.employeeNum = employeeNum;
        this.position = position;
        this.phoneNum = phoneNum;
        this.birthday = birthday;
        this.email = email;
        this.fileNameOfQRCode = fileNameOfQRCode;
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

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getFileNameOfQRCode() {
        return fileNameOfQRCode;
    }
}
