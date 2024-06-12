package com.example.attendancetrackernew.Employee;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EmployeePayslip extends AppCompatActivity {
    LinearLayout exportButton;
    TextView fullname_TextView;
    TextView employeeID_TextView;
    TextView position_TextView;
    TextView  toolbarTitleTV;
    TextView basicSalary_TextView;
    TextView allowance_TextView;
    TextView overTime_TextView;
    TextView grossSalary_TextView;
    TextView sss_TextView;
    TextView philHealth_TextView;
    TextView pagibigFund_TextView;
    TextView witholdingTax_TextView;
    TextView lateUnderTime_TextView;
    TextView cashAdvance_TextView;
    TextView rental_TextView;
    TextView totalDeduction_TextView;
    TextView netSalary_TextView;
    TextView periodTV;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_payslip);

        initWidgets();
        setUpToolbar();
        setUpPayslipInfo();

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @SuppressLint("SetTextI18n")
    private void setUpPayslipInfo() {
        EmployeeSharedPreferences userDetails = new EmployeeSharedPreferences(EmployeePayslip.this);
        fullname_TextView.setText(userDetails.getFullName());
        employeeID_TextView.setText(userDetails.getEmployeeNumber());
        position_TextView.setText(userDetails.getPosition());
        toolbarTitleTV.setText("My Payslip");
        periodTV.setText("Period: " + DateAndTimeUtils.getPeriod());
        String employeeNum = userDetails.getEmployeeNumber();
        String semiMonthName = DateAndTimeUtils.getSemiMonthId();
        String position = userDetails.getPosition();


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
                                                        int overtimeHour = Integer.parseInt(totalOvertime) / 60;
                                                        int overtimeMinutes = Integer.parseInt(totalOvertime) % 60;
                                                        String hour = "";
                                                        String min = "";


                                                        if (overtimeHour == 1){
                                                            hour = "hr";
                                                        }
                                                        else{
                                                            hour = "hrs";
                                                        }

                                                        if (overtimeMinutes == 1){
                                                            min = "min";
                                                        }
                                                        else{
                                                            min = "mins";
                                                        }

                                                        String convertedHoursOfOvertime = "";
                                                        String convertedMinutesOfOvertime = "";
                                                        String convertedOvertime = "";


                                                        if (!hour.isEmpty()){

                                                            convertedHoursOfOvertime = overtimeHour + hour;
                                                        }


                                                        if(!min.isEmpty()){
                                                            convertedMinutesOfOvertime = overtimeMinutes + min;
                                                        }

                                                        convertedOvertime = convertedHoursOfOvertime + " " + convertedMinutesOfOvertime;
                                                        overTime_TextView.setText(convertedOvertime);



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

    private void initWidgets() {
         exportButton =  findViewById(R.id.exportButton);
         fullname_TextView  =  findViewById(R.id.textViewFullnameValue);
         employeeID_TextView =  findViewById(R.id.textViewIDValue);
         position_TextView =  findViewById(R.id.textViewPositionValue);
          toolbarTitleTV =  findViewById(R.id.period_toolbar_title);
         basicSalary_TextView =  findViewById(R.id.textViewBasicSalaryValue);
         allowance_TextView =  findViewById(R.id.textViewAllowanceValue);
         overTime_TextView =  findViewById(R.id.textViewOvertimeValue);
         grossSalary_TextView =  findViewById(R.id.textViewGrossSalaryValue);
         sss_TextView =  findViewById(R.id.textViewSSSValue);
         philHealth_TextView =  findViewById(R.id.textViewPhilHealthValue);
         pagibigFund_TextView =  findViewById(R.id.textViewPagIbigFundValue);
         witholdingTax_TextView =  findViewById(R.id.textViewWitholdingTaxValue);
         lateUnderTime_TextView =  findViewById(R.id.textViewLateValue);
         cashAdvance_TextView =  findViewById(R.id.textViewCashAdvanceValue);
         rental_TextView =  findViewById(R.id.textViewRentalValue);
         totalDeduction_TextView =  findViewById(R.id.textViewTotalDeductionValue);
         netSalary_TextView =  findViewById(R.id.textViewNetSalaryValue);
         periodTV = findViewById(R.id.period_Textview);
         toolbar =  findViewById(R.id.toolbar);
    }
}