package com.example.attendancetrackernew.Admin.Model;

public class PayrollReportsForExcelModel {
    String employeeName;
    String employeeID;
    String basicSalary;
    String allowance;
    String totalSalary;
    String totalHoursWorked;
    String totalOvertime;
    String grossSalary;
    String pagIbig;
    String sss;
    String philHealth;
    String withHoldingTax;
    String totalLate;
    String cashAdvance;
    String rental;
    String totalDeduction;
    String netSalary;

    public PayrollReportsForExcelModel(String employeeName, String employeeID, String basicSalary,
                                  String allowance, String totalSalary, String totalHoursWorked,
                                  String totalOvertime, String grossSalary, String pagIbig,
                                  String sss, String philHealth, String withHoldingTax, String totalLate,
                                  String cashAdvance, String rental, String totalDeduction, String netSalary) {
        this.employeeName = employeeName;
        this.employeeID = employeeID;
        this.basicSalary = basicSalary;
        this.allowance = allowance;
        this.totalSalary = totalSalary;
        this.totalHoursWorked = totalHoursWorked;
        this.totalOvertime = totalOvertime;
        this.grossSalary = grossSalary;
        this.pagIbig = pagIbig;
        this.sss = sss;
        this.philHealth = philHealth;
        this.withHoldingTax = withHoldingTax;
        this.totalLate = totalLate;
        this.cashAdvance = cashAdvance;
        this.rental = rental;
        this.totalDeduction = totalDeduction;
        this.netSalary = netSalary;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getBasicSalary() {
        return basicSalary;
    }

    public String getAllowance() {
        return allowance;
    }

    public String getTotalSalary() {
        return totalSalary;
    }

    public String getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public String getTotalOvertime() {
        return totalOvertime;
    }

    public String getGrossSalary() {
        return grossSalary;
    }

    public String getPagIbig() {
        return pagIbig;
    }

    public String getSss() {
        return sss;
    }

    public String getPhilHealth() {
        return philHealth;
    }

    public String getWithHoldingTax() {
        return withHoldingTax;
    }

    public String getTotalLate() {
        return totalLate;
    }

    public String getCashAdvance() {
        return cashAdvance;
    }

    public String getRental() {
        return rental;
    }

    public String getTotalDeduction() {
        return totalDeduction;
    }

    public String getNetSalary() {
        return netSalary;
    }


}