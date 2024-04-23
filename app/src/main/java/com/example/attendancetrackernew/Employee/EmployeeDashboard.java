package com.example.attendancetrackernew.Employee;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.SelectRole;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
@SuppressLint("DefaultLocale")
@RequiresApi(api = Build.VERSION_CODES.O)
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
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_dashboard);
        initWidgets();
        setUpToolbar();
        setEmployeeInfo();

        qrCodeImageView.setOnClickListener(v->{
            focusImageview();
        });
    }

    private void focusImageview() {
        Dialog dialog = new Dialog(this);
        employeeDetails = new EmployeeSharedPreferences(this);

        dialog.setContentView(R.layout.employee_highlight_qrcode_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.primary_border));
        dialog.setCancelable(true);
        dialog.show();

        ImageView qrCodeImageview1;
        AppCompatButton downloadBtn;
        TextView nameTV;

        nameTV = dialog.findViewById(R.id.name_Textview);
        qrCodeImageview1 = dialog.findViewById(R.id.qrCode_ImageView);
        downloadBtn = dialog.findViewById(R.id.download_Button);

        nameTV.setText(employeeDetails.getFullName());
        retrieveQrCodeImage(employeeDetails.getImageURL(), qrCodeImageview1);

        downloadBtn.setOnClickListener(v->{
            FileOutputStream fileOutputStream = null;

            //Download file
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/Download");
            directory.mkdir();

            String fileName = String.format(employeeDetails.getFullName() + "%d.jpg", System.currentTimeMillis());
            File outfile = new File(directory, fileName);

            Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();

            try {

                Bitmap bitmap = ((BitmapDrawable) qrCodeImageview1.getDrawable()).getBitmap();
                fileOutputStream = new FileOutputStream(outfile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(outfile));
                sendBroadcast(intent);

            } catch(FileNotFoundException e){
                e.printStackTrace();

            } catch(IOException e){
                e.printStackTrace();
            }
        });

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

            retrieveQrCodeImage(employeeDetails.getImageURL(), qrCodeImageView);

        }
    }

    private void retrieveQrCodeImage(String imageURL, ImageView qrCode_ImageView) {

//        storageReference = FirebaseStorage.getInstance().getReference("images/employeesQRCODE/"+ employeeNumber);
//
//        try {
//            File localFile = File.createTempFile("tempfile", "jpg");
//            storageReference.getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                            bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                            qrCode_ImageView.setImageBitmap(bitmap);
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("TAG", "Failed to retrieve Profile Picture: " + e.getCause());
//
//                        }
//                    });
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        Glide.with(EmployeeDashboard.this).load(imageURL).into(qrCode_ImageView);
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
            showLogoutDialog();
        }
        else if (itemId == R.id.updateProfile){
            startActivity(new Intent(getApplicationContext(), EmployeeUpdateProfile.class));
        }
        else if (itemId == R.id.payslip){
            startActivity(new Intent(getApplicationContext(), EmployeePayslip.class));
        }

        return  true;
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
            EmployeeSharedPreferences userDetails = new EmployeeSharedPreferences(EmployeeDashboard.this);
            userDetails.logout();
            Toast.makeText(getApplicationContext(), "Successfully log out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), SelectRole.class));
            logoutDialog.dismiss();
        });

        cancelBtn.setOnClickListener(v1->{
            logoutDialog.dismiss();
        });
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