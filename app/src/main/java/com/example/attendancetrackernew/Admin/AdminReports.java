package com.example.attendancetrackernew.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.attendancetrackernew.Admin.Reports.AdminDailyAttendanceReports;
import com.example.attendancetrackernew.Admin.Reports.AdminMonthlyAttendanceReports;
import com.example.attendancetrackernew.Admin.Reports.AdminPayrollReports;
import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminReports extends AppCompatActivity {
    LinearLayout dailyReportsBtn;

    LinearLayout monthlyReports;
    LinearLayout employeePayrollReports;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_reports);
        initWidgets();
        setUpToolBar();
        setUpReportsClickListener();

    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setHomeActionContentDescription("Back");
    }

    private void setUpReportsClickListener() {
        dailyReportsBtn.setOnClickListener(v->{startActivity(new Intent(getApplicationContext(), AdminDailyAttendanceReports.class));});
        monthlyReports.setOnClickListener(v->{startActivity(new Intent(getApplicationContext(), AdminMonthlyAttendanceReports.class));});
        employeePayrollReports.setOnClickListener(v->{startActivity(new Intent(getApplicationContext(), AdminPayrollReports.class));});
    }

    private void initWidgets() {
        dailyReportsBtn = findViewById(R.id.dailyTime_LinearLayout);
        monthlyReports = findViewById(R.id.monthlyTime_LinearLayout);
        employeePayrollReports = findViewById(R.id.empPayroll_LinearLayout);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}