package com.example.attendancetrackernew.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AdminEmployeePayrollEdit extends AppCompatActivity {
    Toolbar toolbar;
    TextInputEditText basicSalaryET;
    TextInputEditText allowanceET;
    TextInputEditText totalSalaryET;
    TextInputEditText overTimeET;
    TextInputEditText grossSalaryET;
    TextInputEditText sssET;
    TextInputEditText philHealthET;
    TextInputEditText pagibigFundET;
    TextInputEditText withHoldingTaxET;
    TextInputEditText lateET;
    TextInputEditText cashAdvanceET;
    TextInputEditText rentalET;
    TextInputEditText totalDeductionET;
    TextInputEditText netSalaryET;
    
    AppCompatButton resetBtn;
    AppCompatButton saveBtn;
    
    TextView fullNameTV;
    TextView employeeNumAndPositionTV;
    TextView periodTV;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_employee_payroll_edit);
        initWidgets();
        setUpToolbar();
        disableEditText();
        fillEmployeeInfo();
        fillPayRollInfo();
        totalSalaryET.setEnabled(false);
        totalDeductionET.setEnabled(false);
        netSalaryET.setEnabled(false);
        lateET.setEnabled(false);
        overTimeET.setEnabled(false);
        
        saveBtn.setOnClickListener(v->{saveEmployeePayRoll();});
        resetBtn.setOnClickListener(v->{showResetDialog();});

    }

    private void disableEditText() {
        totalDeductionET.setEnabled(false);
        totalSalaryET.setEnabled(false);
        grossSalaryET.setEnabled(false);
        netSalaryET.setEnabled(false);
    }

    private void fillPayRollInfo() {
        Intent intent = getIntent();
        String employeeNum = intent.getStringExtra("employeeNumber");
        String position = intent.getStringExtra("position");
        FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){

                                if (documentSnapshot.contains("basicSalary"))
                                    basicSalaryET.setText(documentSnapshot.getString("basicSalary"));
                                if(documentSnapshot.contains("allowance"))
                                    allowanceET.setText(documentSnapshot.getString("allowance"));
                                if(documentSnapshot.contains("overTime"))
                                    overTimeET.setText(documentSnapshot.getString("overTime"));
                                if(documentSnapshot.contains("grossSalary"))
                                    grossSalaryET.setText(documentSnapshot.getString("grossSalary"));
                                if(documentSnapshot.contains("sss"))
                                    sssET.setText(documentSnapshot.getString("sss"));
                                if(documentSnapshot.contains("philHealth"))
                                    philHealthET.setText(documentSnapshot.getString("philHealth"));
                                if(documentSnapshot.contains("pagibigFund"))
                                    pagibigFundET.setText(documentSnapshot.getString("pagibigFund"));
                                if(documentSnapshot.contains("withHoldingTax"))
                                    withHoldingTaxET.setText(documentSnapshot.getString("withHoldingTax"));
                                if(documentSnapshot.contains("late"))
                                    lateET.setText(documentSnapshot.getString("late"));
                                if(documentSnapshot.contains("cashAdvance"))
                                    cashAdvanceET.setText(documentSnapshot.getString("cashAdvance"));
                                if(documentSnapshot.contains("rental"))
                                    rentalET.setText(documentSnapshot.getString("rental"));
                                if(documentSnapshot.contains("totalDeduction"))
                                    totalDeductionET.setText(documentSnapshot.getString("totalDeduction"));
                                if(documentSnapshot.contains("netSalary"))
                                    netSalaryET.setText(documentSnapshot.getString("netSalary"));

                                String dateId = DateAndTimeUtils.getDateIdFormat();
                                String dayOfMonth = DateAndTimeUtils.getDay(dateId);
                                int dayOfMonthInt = Integer.parseInt(dayOfMonth);
                                Log.d("TAG", String.valueOf(dayOfMonthInt));


                                String semiMonthName = "";
                                String year = DateAndTimeUtils.getYear(dateId);
                                String payslipDate = "";

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
                                                        overTimeET.setText(totalOvertime + " m");


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
                                                        float totalSalaryFloat = (float) Integer.parseInt(allowanceET.getText().toString()) + Integer.parseInt(basicSalaryET.getText().toString());

                                                        //Computation of Gross Salary
                                                        float grossSalaryFloat = (float) basicSalaryFloat + overtimeFloat + Integer.parseInt(allowanceET.getText().toString());

                                                        //Computation of Total Deduction
                                                        float totalDeductionFloat = (float) (Integer.parseInt(pagibigFundET.getText().toString()) + Integer.parseInt(sssET.getText().toString()) + Integer.parseInt(philHealthET.getText().toString())
                                                                + Integer.parseInt(withHoldingTaxET.getText().toString()) + Integer.parseInt(cashAdvanceET.getText().toString()) + Integer.parseInt(rentalET.getText().toString()) + lateDeduction);

                                                        //Computation of Net Salary
                                                        float netSalaryFloat = grossSalaryFloat - totalDeductionFloat;


                                                        String totalSalary = String.valueOf(totalSalaryFloat);
                                                        String grossSalary = String.valueOf(grossSalaryFloat);
                                                        String totalDeduction = String.valueOf(totalDeductionFloat);
                                                        String netSalary = String.valueOf(netSalaryFloat);

                                                        totalSalaryET.setText(totalSalary);
                                                        grossSalaryET.setText(grossSalary);
                                                        totalDeductionET.setText(totalDeduction);
                                                        netSalaryET.setText(netSalary);
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
    }

    private void showResetDialog() {
        Dialog dialog = new Dialog(AdminEmployeePayrollEdit.this);
        dialog.setContentView(R.layout.dialog_reset_employee_payroll_);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();

        AppCompatButton cancelBtn  = dialog.findViewById(R.id.cancel_Button);
        AppCompatButton yesBtn = dialog.findViewById(R.id.yes_Button);

        Intent intent = getIntent();
        String employeeNum = intent.getStringExtra("employeeNumber");

        yesBtn.setOnClickListener(v->{

            HashMap<String, Object> payrollDetails = new HashMap<>();
            payrollDetails.put("basicSalary", "0");
            payrollDetails.put("allowance", "0");
            payrollDetails.put("totalSalary", "0");
            payrollDetails.put("overTime", "0");
            payrollDetails.put("grossSalary", "0");
            payrollDetails.put("sss", "0");
            payrollDetails.put("philHealth", "0");
            payrollDetails.put("pagibigFund", "0");
            payrollDetails.put("withHoldingTax", "0");
            payrollDetails.put("late", "0");
            payrollDetails.put("cashAdvance", "0");
            payrollDetails.put("rental", "0");
            payrollDetails.put("totalDeduction", "0.00");
            payrollDetails.put("netSalary", "0.00");

            FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                    .update(payrollDetails)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AdminEmployeePayrollEdit.this, "Successfully reset", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(AdminEmployeePayrollEdit.this, AdminEmployeePayroll.class));
                            dialog.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminEmployeePayrollEdit.this, "Failed to reset: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });

        });

        cancelBtn.setOnClickListener(v->{dialog.dismiss();});
    }


    private void fillEmployeeInfo() {
        Intent intent = getIntent();
        
        String fullName = "Full Name : " + intent.getStringExtra("fullName");
        String employeeNumAndPosition = "I.D. & Position: " + intent.getStringExtra("employeeNumber") + " | " + intent.getStringExtra("position");
        String period = DateAndTimeUtils.getPeriod();
        
        fullNameTV.setText(fullName);
        employeeNumAndPositionTV.setText(employeeNumAndPosition);
        periodTV.setText(period);
    }

    private void  saveEmployeePayRoll() {
        Intent intent = getIntent();
        String employeeNum = intent.getStringExtra("employeeNumber");
        
         String basicSalary = basicSalaryET.getText().toString();
         String allowance = allowanceET.getText().toString();
         String totalSalary = totalSalaryET.getText().toString();
         String overTime = overTimeET.getText().toString();
         String grossSalary = grossSalaryET.getText().toString();
         String sss = sssET.getText().toString();
         String philHealth = philHealthET.getText().toString();
         String pagibigFund = pagibigFundET.getText().toString();
         String withHoldingTax = withHoldingTaxET.getText().toString();
         String late = lateET.getText().toString();
         String cashAdvance = cashAdvanceET.getText().toString();
         String rental = rentalET.getText().toString();
         String totalDeduction = totalDeductionET.getText().toString();
         String netSalary = netSalaryET.getText().toString();
         overTime = overTime.replace("m", "");
         overTime = overTime.trim();

         //Calculate Total Salary
        int totalSalaryInt = Integer.parseInt(basicSalary) + Integer.parseInt(allowance);
        totalSalary = String.valueOf(totalSalaryInt);

        //Calculate Gross Salary
        int grossSalaryInt = totalSalaryInt + Integer.parseInt(overTime);
        grossSalary = String.valueOf(grossSalaryInt);

        //Calculate Total Deduction
        int totalDeductionInt = Integer.parseInt(late) + Integer.parseInt(cashAdvance) + Integer.parseInt(rental);
        totalDeduction = String.valueOf(totalDeductionInt);

        //Calculate Net Salary
        int netSalaryInt = grossSalaryInt - totalDeductionInt;
        netSalary = String.valueOf(netSalaryInt);


        HashMap<String, Object> payrollDetails = new HashMap<>();
        payrollDetails.put("basicSalary", basicSalary);
        payrollDetails.put("allowance", allowance);
        payrollDetails.put("totalSalary", totalSalary);
        payrollDetails.put("overTime", overTime);
        payrollDetails.put("grossSalary", grossSalary);
        payrollDetails.put("sss", sss);
        payrollDetails.put("philHealth", philHealth);
        payrollDetails.put("pagibigFund", pagibigFund);
        payrollDetails.put("withHoldingTax", withHoldingTax);
        payrollDetails.put("late", late);
        payrollDetails.put("cashAdvance", cashAdvance);
        payrollDetails.put("rental", rental);
        payrollDetails.put("totalDeduction", totalDeduction);
        payrollDetails.put("netSalary", netSalary);

        FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                .update(payrollDetails)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AdminEmployeePayrollEdit.this, "Successfully save", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AdminEmployeePayrollEdit.this, AdminEmployeePayroll.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminEmployeePayrollEdit.this, "Failed to save " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
        
        
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        
        basicSalaryET = findViewById(R.id.basicSalary_EditText);
        allowanceET = findViewById(R.id.allowance_EditText);
        totalSalaryET = findViewById(R.id.totalSalary_EditText);
        overTimeET = findViewById(R.id.overTime_EditText);
        grossSalaryET = findViewById(R.id.grossSalary_EditText);
        sssET = findViewById(R.id.sss_EditText);
        philHealthET = findViewById(R.id.philHealth_EditText);
        pagibigFundET = findViewById(R.id.pagibigFund_EditText);
        withHoldingTaxET = findViewById(R.id.withHoldingTax_EditText);
        lateET = findViewById(R.id.late_EditText);
        cashAdvanceET = findViewById(R.id.cashAdvance_EditText);
        rentalET = findViewById(R.id.rental_EditText);
        totalDeductionET = findViewById(R.id.totalDeduction_EditText);
        netSalaryET = findViewById(R.id.netSalary_EditText);
        
        saveBtn = findViewById(R.id.save_Button);
        resetBtn = findViewById(R.id.reset_Button);
        
        fullNameTV = findViewById(R.id.fullName_TextView);
        employeeNumAndPositionTV = findViewById(R.id.employeeNumAndPosition_TextView);
        periodTV = findViewById(R.id.period_Textview);
        
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}