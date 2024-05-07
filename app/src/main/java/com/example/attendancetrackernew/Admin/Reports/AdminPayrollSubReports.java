package com.example.attendancetrackernew.Admin.Reports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.Admin.Adapter.PayrollSubReportsAdapter;
import com.example.attendancetrackernew.Admin.Model.PayrollReportsForExcelModel;
import com.example.attendancetrackernew.Admin.Model.PayrollSubReportsModel;
import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class AdminPayrollSubReports extends AppCompatActivity {
    Toolbar toolbar;
    TextView toolbarTitleTV;
    RecyclerView recyclerView;
    AppCompatButton exportBtn;
    ArrayList<PayrollSubReportsModel> list;
    PayrollSubReportsAdapter adapter;
    RelativeLayout noDataLayout;
    LinearLayout columnTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_payroll_sub_reports);
        initWidgets();
        setUpToolbar();

        columnTitle.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        String payslipId = intent.getStringExtra("payslipId");
        String payslipDate = intent.getStringExtra("payslipDate");
        toolbarTitleTV.setText(payslipDate);
        
        setUpRecyclerview(payslipId, payslipDate);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    private void setUpRecyclerview(String payslipId, String payslipDate) {
        list = new ArrayList<>();
        adapter = new PayrollSubReportsAdapter(AdminPayrollSubReports.this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminPayrollSubReports.this));
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("employees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();



                            if (!querySnapshot.isEmpty() && querySnapshot != null){
                                list.clear();
                                for (DocumentSnapshot documentSnapshot: task.getResult()){
                                    String employeeName = documentSnapshot.getString("fullName");
                                    String employeeID = documentSnapshot.getString("employeeNumber");
                                    String allowance = "0";
                                    String basicSalary = "0";
                                    String cashAdvance = "0";
                                    String sss = "0";
                                    String pagIbig = "0";
                                    String philHealth = "0";
                                    String rental = "0";
                                    String withHoldingTax = "0";
                                    String position = documentSnapshot.getString("position");

                                    if (documentSnapshot.contains("allowance")){
                                         allowance = documentSnapshot.getString("allowance");
                                    }
                                    if (documentSnapshot.contains("basicSalary")){
                                        basicSalary = documentSnapshot.getString("basicSalary");
                                    }
                                    if (documentSnapshot.contains("cashAdvance")){
                                        cashAdvance = documentSnapshot.getString("cashAdvance");
                                    }
                                    if (documentSnapshot.contains("sss")){
                                        sss = documentSnapshot.getString("sss");
                                    }
                                    if (documentSnapshot.contains("pagibigFund")){
                                        pagIbig = documentSnapshot.getString("pagibigFund");
                                    }
                                    if (documentSnapshot.contains("philHealth")){
                                        philHealth = documentSnapshot.getString("philHealth");
                                    }
                                    if (documentSnapshot.contains("rental")){
                                        rental = documentSnapshot.getString("rental");
                                    }
                                    if (documentSnapshot.contains("withHoldingTax")){
                                        withHoldingTax = documentSnapshot.getString("withHoldingTax");
                                    }


                                    Log.d("TAG", "Reached data");

                                    setUpData(employeeID,employeeName,allowance,basicSalary,cashAdvance,sss,pagIbig,philHealth,rental, withHoldingTax, payslipId, list, adapter, position);

                                }


                            }
                        }
                    }
                });

        exportBtn.setOnClickListener(v->{

            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = hssfWorkbook.createSheet("Daily Attendance Reports");

            HSSFRow hssfRow = hssfSheet.createRow(0);
            HSSFCell cellTitle = hssfRow.createCell(0);
            cellTitle.setCellValue("Payroll Reports");

            HSSFRow hssfRow1 = hssfSheet.createRow(1);
            HSSFCell dateCell = hssfRow1.createCell(0);

            dateCell.setCellValue(payslipDate);


            HSSFRow hssfRow3 = hssfSheet.createRow(3);

            HSSFCell nameCell = hssfRow3.createCell(0);
            nameCell.setCellValue("Employee ID");

            HSSFCell positionCell = hssfRow3.createCell(1);
            positionCell.setCellValue("Employee Name");

            HSSFCell basicSalaryCell = hssfRow3.createCell(2);
            basicSalaryCell.setCellValue("Basic Salary");

            HSSFCell allowanceCell = hssfRow3.createCell(3);
            allowanceCell.setCellValue("Allowance");

            HSSFCell totalSalaryCell = hssfRow3.createCell(4);
            totalSalaryCell.setCellValue("Total Salary");

            HSSFCell totalHoursWorkedCell = hssfRow3.createCell(5);
            totalHoursWorkedCell.setCellValue("Total Hours Worked");

            HSSFCell totalOvertimeCell = hssfRow3.createCell(6);
            totalOvertimeCell.setCellValue("Total Overtime / min");

            HSSFCell grossSalaryCell = hssfRow3.createCell(7);
            grossSalaryCell.setCellValue("Gross Salary");

            HSSFCell pagibigCell = hssfRow3.createCell(8);
            pagibigCell.setCellValue("Pagibig");

            HSSFCell sssCell = hssfRow3.createCell(9);
            sssCell.setCellValue("SSS");

            HSSFCell philHealthCell = hssfRow3.createCell(10);
            philHealthCell.setCellValue("Philhealth");

            HSSFCell withHoldingTaxCell = hssfRow3.createCell(11);
            withHoldingTaxCell.setCellValue("With Holding Tax");

            HSSFCell totalLateCell = hssfRow3.createCell(12);
            totalLateCell.setCellValue("Total Late");

            HSSFCell cashAdvanceCell = hssfRow3.createCell(13);
            cashAdvanceCell.setCellValue("Cash Advance");

            HSSFCell rentalCell = hssfRow3.createCell(14);
            rentalCell.setCellValue("Rental");

            HSSFCell totalDeductionCell = hssfRow3.createCell(15);
            totalDeductionCell.setCellValue("Total Deduction");

            HSSFCell netSalaryCell = hssfRow3.createCell(16);
            netSalaryCell.setCellValue("Net Salary");




            if (list != null && list.size() != 0){
                for (int i = 0; i < list.size(); i++){
                    PayrollSubReportsModel modelForExcel = list.get(i);

                    HSSFRow row = hssfSheet.createRow(i + 4);

                    HSSFCell nameCellValue = row.createCell(0);
                    nameCellValue.setCellValue(modelForExcel.getEmployeeID());

                    HSSFCell positionCellValue = row.createCell(1);
                    positionCellValue.setCellValue(modelForExcel.getEmployeeName());

                    HSSFCell basicSalaryCellValue = row.createCell(2);
                    basicSalaryCellValue.setCellValue(modelForExcel.getBasicSalary());

                    HSSFCell allowanceCellValue = row.createCell(3);
                    allowanceCellValue.setCellValue(modelForExcel.getAllowance());

                    HSSFCell totalSalaryCellValue = row.createCell(4);
                    totalSalaryCellValue.setCellValue(modelForExcel.getTotalSalary());

                    HSSFCell totalHoursWorkedCellValue = row.createCell(5);
                    totalHoursWorkedCellValue.setCellValue(modelForExcel.getTotalHoursWorked());

                    HSSFCell totalOvertimeCellValue = row.createCell(6);
                    totalOvertimeCellValue.setCellValue(modelForExcel.getTotalOvertime());

                    HSSFCell grossSalaryCellValue = row.createCell(7);
                    grossSalaryCellValue.setCellValue(modelForExcel.getGrossSalary());

                    HSSFCell pagibigCellValue = row.createCell(8);
                    pagibigCellValue.setCellValue(modelForExcel.getPagIbig());

                    HSSFCell sssCellValue = row.createCell(9);
                    sssCellValue.setCellValue(modelForExcel.getSss());

                    HSSFCell philHealthCellValue = row.createCell(10);
                    philHealthCellValue.setCellValue(modelForExcel.getPhilHealth());

                    HSSFCell withHoldingTaxCellValue = row.createCell(11);
                    withHoldingTaxCellValue.setCellValue(modelForExcel.getWithHoldingTax());

                    HSSFCell totalLateCellValue = row.createCell(12);
                    totalLateCellValue.setCellValue(modelForExcel.getTotalLate());

                    HSSFCell cashAdvanceCellValue = row.createCell(13);
                    cashAdvanceCellValue.setCellValue(modelForExcel.getCashAdvance());

                    HSSFCell rentalCellValue = row.createCell(14);
                    rentalCellValue.setCellValue(modelForExcel.getRental());

                    HSSFCell totalDeductionCellValue = row.createCell(15);
                    totalDeductionCellValue.setCellValue(modelForExcel.getTotalDeduction());

                    HSSFCell netSalaryCellValue = row.createCell(16);
                    netSalaryCellValue.setCellValue(modelForExcel.getNetSalary());



                }
                saveWork(hssfWorkbook,payslipId);
            }
            else{
                Toast.makeText(AdminPayrollSubReports.this, "No data to export", Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void saveWork(HSSFWorkbook hssfWorkbook, String payslipId) {
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // Internal Storage

        String path = "/Download/PayrollReports_" + payslipId + ".xls";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            File fileOutput = new File(storageVolume.getDirectory().getPath() + path);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);
                hssfWorkbook.write(fileOutputStream);
                fileOutputStream.close();
                hssfWorkbook.close();
                Toast.makeText(AdminPayrollSubReports.this, "File exported", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(AdminPayrollSubReports.this, "File failed to export", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }
        else{
            Toast.makeText(AdminPayrollSubReports.this, "Failed to Export, Applicable only for Android 11 and above", Toast.LENGTH_LONG).show();
        }
    }

    private void setUpData(String employeeID, String employeeName, String allowance,
                           String basicSalary, String cashAdvance, String sss, String pagIbig,
                           String philHealth, String rental, String withHoldingTax, String payslipId,
                           ArrayList<PayrollSubReportsModel> list, PayrollSubReportsAdapter adapter, String position) {
        FirebaseFirestore.getInstance().collection("employees").document(employeeID)
                .collection("payslip").document(payslipId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                noDataLayout.setVisibility(View.GONE);
                                columnTitle.setVisibility(View.VISIBLE);
                                String totalLate = documentSnapshot.getString("totalLate");
                                String totalLeaveEarly = documentSnapshot.getString("totalLeaveEarly");
                                String totalOvertime = documentSnapshot.getString("totalOvertime");
                                String totalWorkedHours = documentSnapshot.getString("totalWorkedHours");



                                float totalWorkHoursFloat = (float) Integer.parseInt(totalWorkedHours) / 60;
                                totalWorkedHours = String.valueOf(totalWorkHoursFloat);

                                float overtimeFloat = (float) Integer.parseInt(totalOvertime) / 60;

                                Log.d("TAG", "Exist");


                                float basicSalaryFloat = 0;
                                float lateDeduction = 0;
                                int totalLateInt = Integer.parseInt(totalLate);

                                //Computation of Salary

                                if (position.equals("Engineer")){

                                    basicSalaryFloat = (float) (totalWorkHoursFloat * 192.31);
                                    overtimeFloat = (float) (overtimeFloat * 192.31);

                                    if (totalLateInt > 15 && totalLateInt < 30)
                                        lateDeduction = (float) 96.16;
                                    else if (totalLateInt > 30  && totalLateInt <= 60)
                                        lateDeduction = (float) 192.31;
                                    else
                                        lateDeduction = (float) ((Integer.parseInt(totalLate) / 60) * 192.31);

                                }
                                else if (position.equals("Draftsman")){

                                    basicSalaryFloat = (float) (totalWorkHoursFloat * 92.79);
                                    overtimeFloat = (float) (overtimeFloat * 92.79);

                                    if (totalLateInt> 15 && totalLateInt < 30)
                                        lateDeduction = (float) 46.4;
                                    else if (totalLateInt > 30 && totalLateInt <= 60)
                                        lateDeduction = (float) 92.79;
                                    else
                                        lateDeduction = (float) ((float) (totalLateInt / 60) * 92.79);
                                }
                                else if (position.equals("Safety Officer")){
                                    basicSalaryFloat = (float) (totalWorkHoursFloat * 92.79);
                                    overtimeFloat = (float) (overtimeFloat * 92.79);

                                    if (totalLateInt> 15 && totalLateInt < 30)
                                        lateDeduction = (float) 46.4;
                                    else if (totalLateInt > 30 && totalLateInt <= 60)
                                        lateDeduction = (float) 92.79;
                                    else
                                        lateDeduction = (float) ((float) (totalLateInt / 60) * 92.79);
                                }
                                else if (position.equals("Foreman")){
                                    basicSalaryFloat = (float) (totalWorkHoursFloat * 90.63);
                                    overtimeFloat = (float) (overtimeFloat * 90.63);

                                    if (totalLateInt> 15 && totalLateInt < 30)
                                        lateDeduction = (float) 45.32;
                                    else if (totalLateInt > 30 && totalLateInt <= 60)
                                        lateDeduction = (float) 90.63;
                                    else
                                        lateDeduction = (float) ((float) (totalLateInt / 60) * 90.63);
                                }
                                else if (position.equals("Office Staff")){
                                    basicSalaryFloat = (float) (totalWorkHoursFloat * 92.79);
                                    overtimeFloat = (float) (overtimeFloat * 92.79);

                                    if (totalLateInt> 15 && totalLateInt < 30)
                                        lateDeduction = (float) 46.4;
                                    else if (totalLateInt > 30 && totalLateInt <= 60)
                                        lateDeduction = (float) 92.79;
                                    else
                                        lateDeduction = (float) ((float) (totalLateInt / 60) * 92.79);
                                }
                                else if (position.equals("Labor")){
                                    basicSalaryFloat = (float) (totalWorkHoursFloat * 62.5);
                                    overtimeFloat = (float) (overtimeFloat * 62.5);

                                    if (totalLateInt> 15 && totalLateInt < 30)
                                        lateDeduction = (float) 31.25;
                                    else if (totalLateInt > 30 && totalLateInt <= 60)
                                        lateDeduction = (float) 62.5;
                                    else
                                        lateDeduction = (float) ((float) (totalLateInt / 60) * 62.5);
                                }



                                //Computation of total salary
                                float totalSalaryFloat = (float) Integer.parseInt(allowance) + Integer.parseInt(basicSalary);

                                //Computation of Gross Salary
                                float grossSalaryFloat = (float) basicSalaryFloat + overtimeFloat + Integer.parseInt(allowance);

                                //Computation of Total Deduction
                                float totalDeductionFloat = (float) (Integer.parseInt(pagIbig) + Integer.parseInt(sss) + Integer.parseInt(philHealth)
                                                            + Integer.parseInt(withHoldingTax) + Integer.parseInt(cashAdvance) + Integer.parseInt(rental) + lateDeduction);

                                //Computation of Net Salary
                                float netSalaryFloat = grossSalaryFloat - totalDeductionFloat;


                                String totalSalary = String.valueOf(totalSalaryFloat);
                                String grossSalary = String.valueOf(grossSalaryFloat);
                                String totalDeduction = String.valueOf(totalDeductionFloat);
                                String netSalary = String.valueOf(netSalaryFloat);

                                //TODO Computation of Total Salary, Gross Salary, Total Deduction and Net Salary

                                list.add(new PayrollSubReportsModel(employeeName, employeeID, basicSalary, allowance,
                                        totalSalary, totalWorkedHours, totalOvertime,  grossSalary, pagIbig, sss, philHealth,
                                        withHoldingTax, totalLate, cashAdvance, rental, totalDeduction, netSalary));

                                if(adapter != null){
                                    adapter.notifyDataSetChanged();
                                }
                            }

                        }
                    }
                });
    }
    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        toolbarTitleTV = findViewById(R.id.toolbarTitle_TextView);
        recyclerView = findViewById(R.id.recyclerview);
        
        exportBtn = findViewById(R.id.exportAsExcel_Button);

        noDataLayout = findViewById(R.id.noData_Layout);
        columnTitle = findViewById(R.id.columnTitle_LinearLayout);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}