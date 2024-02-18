package com.example.attendancetrackernew.Employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.SelectRole;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class EmployeeDashboard extends AppCompatActivity {
    TextView fullNameTV;
    TextView emailTV;
    TextView positionTV;
    TextView phoneNumberTV;
    TextView birthDateTV;
    TextView employeeNumTV;
    EmployeeSharedPreferences employeeDetails;
    Toolbar toolbar;
    ImageView qrCodeImageView;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);
        initWidgets();
        setUpToolbar();

        setEmployeeInfo();
    }



    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void setEmployeeInfo() {
        if (getApplicationContext() != null){
            employeeDetails = new EmployeeSharedPreferences(this);

            emailTV.setText(employeeDetails.getEmail());
            phoneNumberTV.setText(employeeDetails.getPhoneNumber());
            positionTV.setText(employeeDetails.getBirthday());
            birthDateTV.setText(employeeDetails.getBirthday());
            fullNameTV.setText(employeeDetails.getFullName());
            employeeNumTV.setText(employeeDetails.getEmployeeNumber());

            retrieveQrCodeImage(employeeDetails.getEmployeeNumber());

        }
    }

    private void retrieveQrCodeImage(String employeeNumber) {

        storageReference = FirebaseStorage.getInstance().getReference("images/employeesQRCODE/"+ employeeNumber);

        try {
            File localFile = File.createTempFile("tempfile", "jpg");
            storageReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            qrCodeImageView.setImageBitmap(bitmap);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Failed to retrieve Profile Picture: " + e.getCause());

                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initWidgets() {

        emailTV = findViewById(R.id.email_TextView);
        phoneNumberTV = findViewById(R.id.phoneNumber_TextView);
        positionTV = findViewById(R.id.position_TextView);
        birthDateTV = findViewById(R.id.dateOfBirth_TextView);
        employeeNumTV = findViewById(R.id.employeeNumber_TextView);
        fullNameTV = findViewById(R.id.fullName_TextView);

        toolbar = findViewById(R.id.toolbar);

        qrCodeImageView = findViewById(R.id.qrCode_ImageView);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.employee_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            EmployeeSharedPreferences sharedPreferences = new EmployeeSharedPreferences(this);
            sharedPreferences.logout();
            startActivity(new Intent(getApplicationContext(), SelectRole.class));
        }
        return  true;
    }
}