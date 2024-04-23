package com.example.attendancetrackernew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.attendancetrackernew.Admin.AdminLogin;
import com.example.attendancetrackernew.Employee.EmployeeLogin;

public class SelectRole extends AppCompatActivity {
    private static final int CALL_PERMISSION_REQUEST_CODE = 100;
    private static final int SEND_PERMISSION_REQUEST_CODE = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_role);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                SEND_PERMISSION_REQUEST_CODE);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CALL_PHONE},
                CALL_PERMISSION_REQUEST_CODE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CALL_PERMISSION_REQUEST_CODE:
            case SEND_PERMISSION_REQUEST_CODE:
                // Check if SEND_SMS permission is granted
                // Check if CALL_PHONE permission is granted
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted, you can make phone calls here
                } else {
                    // Permission denied, handle accordingly
                }
                break;
            // Permission granted, you can send SMS here
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();
    }
}