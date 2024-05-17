package com.example.attendancetrackernew.Admin.Adapter;

import static android.content.Context.STORAGE_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetrackernew.Admin.AdminEmployeePayroll;
import com.example.attendancetrackernew.Admin.AdminEmployeePayrollEdit;
import com.example.attendancetrackernew.Admin.Model.EmployeePayrollModel;
import com.example.attendancetrackernew.Admin.Reports.AdminMonthlyAttendanceReports;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.Utils.CalendarUtils;
import com.example.attendancetrackernew.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhanobut.dialogplus.DialogPlus;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
@RequiresApi(api = Build.VERSION_CODES.O)
public class EmployeePayRollAdapter extends RecyclerView.Adapter<EmployeePayRollAdapter.MyViewHolder> {
    Context context;
    ArrayList<EmployeePayrollModel> list;

    public EmployeePayRollAdapter(Context context, ArrayList<EmployeePayrollModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public EmployeePayRollAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_payroll_layout, parent, false);
        return new MyViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EmployeePayRollAdapter.MyViewHolder holder, int position) {
        EmployeePayrollModel model = list.get(position);
        holder.fullNameTV.setText("Fullname: " + model.getFullName());
        holder.employeeNumAndPositionTV.setText(model.getEmployeeNum() +  " | " +  model.getPosition());
        holder.periodTV.setText("Period: " + DateAndTimeUtils.getPeriod());

        holder.editBtn.setOnClickListener(v->{
            Intent intent = new Intent(context, AdminEmployeePayrollEdit.class);

            intent.putExtra("employeeNumber", model.getEmployeeNum());
            intent.putExtra("fullName", model.getFullName());
            intent.putExtra("position", model.getPosition());

            context.startActivity(intent);
        });
        holder.itemView.setOnClickListener(v->{
            DialogPlus dialogPlus = showPayrollDetailsDialog(model);
            dialogPlus.show();
        });

    }

    private DialogPlus showPayrollDetailsDialog(EmployeePayrollModel model) {
        DialogPlus dialogPlus = DialogPlus.newDialog(context)
                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.pop_up_employee_payroll_layout))
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT)
                .create();

        View view = dialogPlus.getHolderView();

        LinearLayout exportButton = view.findViewById(R.id.exportButton);
        TextView fullname_TextView = view.findViewById(R.id.textViewFullnameValue);
        TextView employeeID_TextView = view.findViewById(R.id.textViewIDValue);
        TextView position_TextView = view.findViewById(R.id.textViewPositionValue);
        TextView period_TextView = view.findViewById(R.id.period_toolbar_title);
        TextView basicSalary_TextView = view.findViewById(R.id.textViewBasicSalaryValue);
        TextView allowance_TextView = view.findViewById(R.id.textViewAllowanceValue);
        TextView overTime_TextView = view.findViewById(R.id.textViewOvertimeValue);
        TextView grossSalary_TextView = view.findViewById(R.id.textViewGrossSalaryValue);
        TextView sss_TextView = view.findViewById(R.id.textViewSSSValue);
        TextView philHealth_TextView = view.findViewById(R.id.textViewPhilHealthValue);
        TextView pagibigFund_TextView = view.findViewById(R.id.textViewPagIbigFundValue);
        TextView witholdingTax_TextView = view.findViewById(R.id.textViewWitholdingTaxValue);
        TextView lateUnderTime_TextView = view.findViewById(R.id.textViewLateValue);
        TextView cashAdvance_TextView = view.findViewById(R.id.textViewCashAdvanceValue);
        TextView rental_TextView = view.findViewById(R.id.textViewRentalValue);
        TextView totalDeduction_TextView = view.findViewById(R.id.textViewTotalDeductionValue);
        TextView netSalary_TextView = view.findViewById(R.id.textViewNetSalaryValue);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view1 -> dialogPlus.dismiss());

        // Set data from the payroll object
        fullname_TextView.setText(model.getFullName());
        employeeID_TextView.setText(model.getEmployeeNum());
        position_TextView.setText(model.getPosition());
        period_TextView.setText(DateAndTimeUtils.getPeriod());

        String employeeNum = model.getEmployeeNum();
        String position = model.getPosition();


        FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                              if (task.isSuccessful()){
                                  DocumentSnapshot documentSnapshot = task.getResult();
                                  if (documentSnapshot.exists()){

                                      if (documentSnapshot.contains("basicSalary"))
                                          basicSalary_TextView.setText(documentSnapshot.getString("basicSalary"));
                                      if(documentSnapshot.contains("allowance"))
                                          allowance_TextView.setText(documentSnapshot.getString("allowance"));
                                      if(documentSnapshot.contains("overTime"))
                                          overTime_TextView.setText(documentSnapshot.getString("overTime"));
                                      if(documentSnapshot.contains("grossSalary"))
                                          grossSalary_TextView.setText(documentSnapshot.getString("grossSalary"));
                                      if(documentSnapshot.contains("sss"))
                                          sss_TextView.setText(documentSnapshot.getString("sss"));
                                      if(documentSnapshot.contains("philHealth"))
                                          philHealth_TextView.setText(documentSnapshot.getString("philHealth"));
                                      if(documentSnapshot.contains("pagibigFund"))
                                          pagibigFund_TextView.setText(documentSnapshot.getString("pagibigFund"));
                                      if(documentSnapshot.contains("withHoldingTax"))
                                          witholdingTax_TextView.setText(documentSnapshot.getString("withHoldingTax"));
                                      if(documentSnapshot.contains("late"))
                                          lateUnderTime_TextView.setText(documentSnapshot.getString("late"));
                                      if(documentSnapshot.contains("cashAdvance"))
                                          cashAdvance_TextView.setText(documentSnapshot.getString("cashAdvance"));
                                      if(documentSnapshot.contains("rental"))
                                          rental_TextView.setText(documentSnapshot.getString("rental"));
                                      if(documentSnapshot.contains("totalDeduction"))
                                          totalDeduction_TextView.setText(documentSnapshot.getString("totalDeduction"));
                                      if(documentSnapshot.contains("netSalary"))
                                          netSalary_TextView.setText(documentSnapshot.getString("netSalary"));

                                      String dateId = DateAndTimeUtils.getDateIdFormat();
                                      String dayOfMonth = DateAndTimeUtils.getDay(dateId);
                                      int dayOfMonthInt = Integer.parseInt(dayOfMonth);
                                      Log.d("TAG", String.valueOf(dayOfMonthInt));


                                      String semiMonthName = "";
                                      String year = DateAndTimeUtils.getYear(dateId);
                                      String payslipDate;

                                      if (dayOfMonthInt > 25 && dayOfMonthInt < 32){

                                          String firstMonth = DateAndTimeUtils.getMonth(dateId);
                                          String secondMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getNextMonthDateId(dateId));

                                          Log.d("TAG", "First Month:" + firstMonth);
                                          Log.d("TAG", "Second Month:" + secondMonth);
                                          Log.d("TAG", "1");

                                          semiMonthName = firstMonth + "26To" + secondMonth + "10_" + year;

                                          String upperCaseFirstLetterOfFirstMonth = firstMonth.substring(0,1).toUpperCase() + firstMonth.substring(1);
                                          String upperCaseFirstLetterOfSecondMonth = secondMonth.substring(0,1).toUpperCase() + secondMonth.substring(1);

                                          payslipDate = upperCaseFirstLetterOfFirstMonth + " 26 to " + upperCaseFirstLetterOfSecondMonth + " 10 " + year;



                                      }

                                      else if (dayOfMonthInt > 0 && dayOfMonthInt < 11){
                                          String firstMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getPreviousMonthDateId(dateId));
                                          String secondMonth = DateAndTimeUtils.getMonth(dateId);

                                          Log.d("TAG", "First Month:" + firstMonth);
                                          Log.d("TAG", "Second Month:" + secondMonth);
                                          Log.d("TAG", "2");

                                          semiMonthName = firstMonth + "26To" + secondMonth + "10_" + year;

                                          String upperCaseFirstLetterOfFirstMonth = firstMonth.substring(0,1).toUpperCase() + firstMonth.substring(1);
                                          String upperCaseFirstLetterOfSecondMonth = secondMonth.substring(0,1).toUpperCase() + secondMonth.substring(1);

                                          payslipDate = upperCaseFirstLetterOfFirstMonth + " 26 to " + upperCaseFirstLetterOfSecondMonth + " 10 " + year;
                                      }
                                      else if (dayOfMonthInt > 10 && dayOfMonthInt < 26){
                                          String month = DateAndTimeUtils.getMonth(dateId);

                                          semiMonthName = month + "11To25_" + year;

                                          String upperCaseMonth = month.substring(0,1).toUpperCase() + month.substring(1);

                                          payslipDate = month + " 11 to 25 " + year;
                                      } else {
                                          payslipDate = "";
                                      }


                                      FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                                              .collection("payslip").document(semiMonthName).get()
                                              .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                  @Override
                                                  public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                      if (task.isSuccessful()){
                                                          DocumentSnapshot documentSnapshot1 = task.getResult();
                                                          if (documentSnapshot1.exists()){
                                                              String totalLate = documentSnapshot1.getString("totalLate");
                                                              String totalLeaveEarly = documentSnapshot1.getString("totalLeaveEarly");
                                                              String totalOvertime = documentSnapshot1.getString("totalOvertime");
                                                              String totalWorkedHours = documentSnapshot1.getString("totalWorkedHours");
                                                              overTime_TextView.setText(totalOvertime + " m");


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
                                                              float totalSalaryFloat = (float) Integer.parseInt(allowance_TextView.getText().toString()) + Integer.parseInt(basicSalary_TextView.getText().toString());

                                                              //Computation of Gross Salary
                                                              float grossSalaryFloat = (float) basicSalaryFloat + overtimeFloat + Integer.parseInt(allowance_TextView.getText().toString());

                                                              //Computation of Total Deduction
                                                              float totalDeductionFloat = (float) (Integer.parseInt(pagibigFund_TextView.getText().toString()) + Integer.parseInt(sss_TextView.getText().toString()) + Integer.parseInt(philHealth_TextView.getText().toString())
                                                                      + Integer.parseInt(witholdingTax_TextView.getText().toString()) + Integer.parseInt(cashAdvance_TextView.getText().toString()) + Integer.parseInt(rental_TextView.getText().toString()) + lateDeduction);

                                                              //Computation of Net Salary
                                                              float netSalaryFloat = grossSalaryFloat - totalDeductionFloat;


                                                              String totalSalary = String.valueOf(totalSalaryFloat);
                                                              String grossSalary = String.valueOf(grossSalaryFloat);
                                                              String totalDeduction = String.valueOf(totalDeductionFloat);
                                                              String netSalary = String.valueOf(netSalaryFloat);

                                                              grossSalary_TextView.setText(grossSalary);
                                                              totalDeduction_TextView.setText(totalDeduction);
                                                              netSalary_TextView.setText(netSalary);


                                                              exportButton.setOnClickListener(v->{
                                                                  HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                                                                  HSSFSheet hssfSheet = hssfWorkbook.createSheet("Employee Payroll");

                                                                  HSSFRow hssfRow = hssfSheet.createRow(0);
                                                                  HSSFCell cellTitle = hssfRow.createCell(0);
                                                                  cellTitle.setCellValue("Employee Payroll");

                                                                  HSSFRow hssfRow1 = hssfSheet.createRow(1);
                                                                  HSSFCell dateCell = hssfRow1.createCell(0);

                                                                  dateCell.setCellValue(payslipDate);


                                                                  HSSFRow hssfRow2 = hssfSheet.createRow(2);

                                                                  HSSFCell nameCell = hssfRow2.createCell(0);
                                                                  nameCell.setCellValue("Employee ID");

                                                                  HSSFCell nameValueCell = hssfRow2.createCell(1);
                                                                  nameValueCell.setCellValue(employeeNum);

                                                                  HSSFRow hssfRow3 = hssfSheet.createRow(3);

                                                                  HSSFCell positionCell = hssfRow3.createCell(0);
                                                                  positionCell.setCellValue("Employee Name");

                                                                  HSSFCell positionValueCell = hssfRow3.createCell(1);
                                                                  positionValueCell.setCellValue(fullname_TextView.getText().toString());

                                                                  HSSFRow hssfRow5 = hssfSheet.createRow(5);

                                                                  HSSFCell basicSalaryCell = hssfRow5.createCell(0);
                                                                  basicSalaryCell.setCellValue("Basic Salary");

                                                                  HSSFCell basicSalaryValueCell = hssfRow5.createCell(1);
                                                                  basicSalaryValueCell.setCellValue(basicSalary_TextView.getText().toString());

                                                                  HSSFRow hssfRow6 = hssfSheet.createRow(6);

                                                                  HSSFCell allowanceCell = hssfRow6.createCell(0);
                                                                  allowanceCell.setCellValue("Allowance");

                                                                  HSSFCell allowanceValueCell = hssfRow6.createCell(1);
                                                                  allowanceValueCell.setCellValue(allowance_TextView.getText().toString());

                                                                  HSSFRow hssfRow7 = hssfSheet.createRow(7);

                                                                  HSSFCell overtimeCell = hssfRow7.createCell(0);
                                                                  overtimeCell.setCellValue("Overtime");

                                                                  HSSFCell overtimeValueCell = hssfRow7.createCell(1);
                                                                  overtimeValueCell.setCellValue(overTime_TextView.getText().toString());

                                                                  HSSFRow hssfRow8 = hssfSheet.createRow(8);

                                                                  HSSFCell grossSalaryCell = hssfRow8.createCell(0);
                                                                  grossSalaryCell.setCellValue("Gross Salary");

                                                                  HSSFCell grossSalaryValueCell = hssfRow8.createCell(1);
                                                                  grossSalaryValueCell.setCellValue(grossSalary_TextView.getText().toString());

                                                                  HSSFRow hssfRow9 = hssfSheet.createRow(9);

                                                                  HSSFCell sssCell = hssfRow9.createCell(0);
                                                                  sssCell.setCellValue("SSS");

                                                                  HSSFCell sssValueCell = hssfRow9.createCell(1);
                                                                  sssValueCell.setCellValue(sss_TextView.getText().toString());

                                                                  HSSFRow hssfRow10 = hssfSheet.createRow(10);

                                                                  HSSFCell philHealthCell = hssfRow10.createCell(0);
                                                                  philHealthCell.setCellValue("PhilHeath");

                                                                  HSSFCell philHealthValueCell = hssfRow10.createCell(1);
                                                                  philHealthValueCell.setCellValue(philHealth_TextView.getText().toString());

                                                                  HSSFRow hssfRow11 = hssfSheet.createRow(11);

                                                                  HSSFCell pagibigFundCell = hssfRow11.createCell(0);
                                                                  pagibigFundCell.setCellValue("Pagibig Fund");

                                                                  HSSFCell pagibigFundValueCell = hssfRow11.createCell(1);
                                                                  pagibigFundValueCell.setCellValue(pagibigFund_TextView.getText().toString());

                                                                  HSSFRow hssfRow12 = hssfSheet.createRow(12);

                                                                  HSSFCell withHoldingTaxCell = hssfRow12.createCell(0);
                                                                  withHoldingTaxCell.setCellValue("Withholding Tax");

                                                                  HSSFCell withHoldingTaxValueCell = hssfRow12.createCell(1);
                                                                  withHoldingTaxValueCell.setCellValue(witholdingTax_TextView.getText().toString());

                                                                  HSSFRow hssfRow13 = hssfSheet.createRow(13);

                                                                  HSSFCell lateCell = hssfRow13.createCell(0);
                                                                  lateCell.setCellValue("Late / Undertime");

                                                                  HSSFCell lateValueCell = hssfRow13.createCell(1);
                                                                  lateValueCell.setCellValue(lateUnderTime_TextView.getText().toString());

                                                                  HSSFRow hssfRow14 = hssfSheet.createRow(14);

                                                                  HSSFCell cashAdvanceCell = hssfRow14.createCell(0);
                                                                  cashAdvanceCell.setCellValue("Cash Advance");

                                                                  HSSFCell cashAdvanceValueCell = hssfRow14.createCell(1);
                                                                  cashAdvanceValueCell.setCellValue(cashAdvance_TextView.getText().toString());

                                                                  HSSFRow hssfRow15 = hssfSheet.createRow(15);

                                                                  HSSFCell rentalCell = hssfRow15.createCell(0);
                                                                  rentalCell.setCellValue("Rental");

                                                                  HSSFCell rentalValueCell = hssfRow15.createCell(1);
                                                                  rentalValueCell.setCellValue(rental_TextView.getText().toString());

                                                                  HSSFRow hssfRow16 = hssfSheet.createRow(16);

                                                                  HSSFCell totalDeductionCell = hssfRow16.createCell(0);
                                                                  totalDeductionCell.setCellValue("Total Deduction");

                                                                  HSSFCell totalDeductionValueCell = hssfRow16.createCell(1);
                                                                  totalDeductionValueCell.setCellValue(totalDeduction_TextView.getText().toString());

                                                                  HSSFRow hssfRow17 = hssfSheet.createRow(17);

                                                                  HSSFCell netSalaryCell = hssfRow17.createCell(0);
                                                                  netSalaryCell.setCellValue("Net Salary");

                                                                  HSSFCell netSalaryValueCell = hssfRow17.createCell(1);
                                                                  netSalaryValueCell.setCellValue(netSalary_TextView.getText().toString());

                                                                  saveWork(hssfWorkbook, fullname_TextView.getText().toString());
                                                              });
                                                          }
                                                      }
                                                  }
                                              });
                                  }
                                  else{
                                      Log.d("TAG", "Document doesn't exit");
                                  }
                              }
                              else{
                                  Log.d("TAG", "Failed to retrieve employee data");
                              }
                            }
                        });


//        exportButton.setOnClickListener(view1 -> showExportOptionsDialog(model));
//        printButton.setOnClickListener(view1 -> showPrintDialog(model));

        return dialogPlus;
    }

    private void saveWork(HSSFWorkbook hssfWorkbook, String name) {
        StorageManager storageManager = (StorageManager) context.getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // Internal Storage


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            File fileOutput = new File(storageVolume.getDirectory().getPath()+"/Download/" + name + "Payroll.xls");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);
                hssfWorkbook.write(fileOutputStream);
                fileOutputStream.close();
                hssfWorkbook.close();
                Toast.makeText(context, "File exported", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "File failed to export", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }
        else{
            Toast.makeText(context, "Failed to Export, Applicable only for Android 11 and above", Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTV,
                employeeNumAndPositionTV,
                periodTV;
        ImageView editBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameTV = itemView.findViewById(R.id.fullName_Textview);
            employeeNumAndPositionTV = itemView.findViewById(R.id.employeeNumAndPosition_Textview);
            periodTV = itemView.findViewById(R.id.period_Textview);
            editBtn = itemView.findViewById(R.id.edit_ImageView);
        }
    }
}
