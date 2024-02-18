package com.example.attendancetrackernew;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.attendancetrackernew.Admin.AdminLogin;
import com.example.attendancetrackernew.Employee.EmployeeLogin;

public class SelectRole extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);
    }

    public void onButtonClicked(View view) {
        Intent intent = null;
        int id = view.getId();

        if (id == R.id.admin_Button){
            intent = new Intent(getApplicationContext(), AdminLogin.class);
        }
        else if (id == R.id.employee_Button){
            intent = new Intent(getApplicationContext(), EmployeeLogin.class);
        }

        if (intent != null)
            startActivity(intent);
    }
}