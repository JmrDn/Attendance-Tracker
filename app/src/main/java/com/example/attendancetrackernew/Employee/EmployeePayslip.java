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


        FirebaseFirestore.getInstance().collection("employees").document(userDetails.getEmployeeNumber())
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