package com.example.attendancetrackernew.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.Employee.EmployeeDashboard;
import com.example.attendancetrackernew.Employee.EmployeeSharedPreferences;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.SelectRole;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboard extends AppCompatActivity {
    CardView attendanceBtn,
            scanBtn,
            reportsBtn,
            employeePayRollBtn,
            listOfEmployeeBtn,
            logoutBtn;
    AdminSharedPreferences adminDetails;
    TextView adminName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initWidgets();
        setUpAdminName();
        setUpCardClickListeners();
    }
    private void setUpAdminName() {
        adminDetails = new AdminSharedPreferences(AdminDashboard.this);

        adminName.setText(adminDetails.getFullName());


    }

    private void setUpCardClickListeners() {
        attendanceBtn.setOnClickListener(v->{

        });
        listOfEmployeeBtn.setOnClickListener(v1->{
            startActivity(new Intent(getApplicationContext(), AdminListOfEmployees.class));
        });

        logoutBtn.setOnClickListener(v->{
            showLogoutDialog();
        });
    }

    private void showLogoutDialog() {

        Dialog logoutDialog = new Dialog(this);
        logoutDialog.setContentView(R.layout.logout_dialog);
        logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        logoutDialog.show();

        AppCompatButton logoutBtn, cancelBtn;
        cancelBtn = logoutDialog.findViewById(R.id.cancel_Button);
        logoutBtn = logoutDialog.findViewById(R.id.logout_Button);

        logoutBtn.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            AdminSharedPreferences userDetails = new AdminSharedPreferences(AdminDashboard.this);
            userDetails.logout();
            Toast.makeText(getApplicationContext(), "Successfully log out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), SelectRole.class));
            logoutDialog.dismiss();
        });

        cancelBtn.setOnClickListener(v1->{
            logoutDialog.dismiss();
        });
    }

    private void initWidgets() {
        attendanceBtn = findViewById(R.id.attendanceSummary_CardView);
        scanBtn = findViewById(R.id.scan_CardView);
        reportsBtn = findViewById(R.id.reports_Cardview);
        employeePayRollBtn = findViewById(R.id.employeePayroll_CardView);
        listOfEmployeeBtn = findViewById(R.id.listOfEmployee_CardView);
        logoutBtn = findViewById(R.id.logout_CardView);

        adminName = findViewById(R.id.adminName_Textview);
    }


}