package com.example.attendancetrackernew.Admin.Model;

public class PayrollReportsModel {
    String payslipId;
    String payslipDate;

    public PayrollReportsModel(String payslipId, String payslipDate) {
        this.payslipId = payslipId;
        this.payslipDate = payslipDate;
    }

    public String getPayslipId() {
        return payslipId;
    }

    public String getPayslipDate() {
        return payslipDate;
    }
}
