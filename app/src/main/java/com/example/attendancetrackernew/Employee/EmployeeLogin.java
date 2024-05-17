package com.example.attendancetrackernew.Employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmployeeLogin extends AppCompatActivity {
    TextInputEditText emailET;
    TextInputEditText passwordET;
    AppCompatButton loginBtn;
    ProgressBar progressBar;
    EmployeeSharedPreferences employeeDetails;
    TextView createNewAccBtn;
    TextView forgotPasswordBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_login);
        initWidgets();

        progressBar.setVisibility(View.GONE);

        loginBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
            loginUser();
        });
        
        forgotPasswordBtn.setOnClickListener(v->{
            startActivity(new Intent(EmployeeLogin.this, EmployeeForgotPassword.class));
        });

        createNewAccBtn.setOnClickListener(v1->{
            startActivity(new Intent(getApplicationContext(), EmployeeSignup.class));
        });
    }

    private void loginUser() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        validateUserIfEmployee();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Failed to log in "+ e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void validateUserIfEmployee() {
        String userId = FirebaseAuth.getInstance().getUid();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(userId);

        if (documentReference != null){
            documentReference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();

                                if (documentSnapshot.exists()){
                                    String accountType = documentSnapshot.getString("accountType");

                                    if (accountType.equals("employeeAccount")){
                                        storeUserDetails(userId);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Successfully Login", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(getApplicationContext(), EmployeeDashboard.class));
                                            }

                                    },3000);

                                    }
                                    else{
                                        progressBar.setVisibility(View.GONE);
                                        loginBtn.setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(), "Failed to login, Invalid Account", Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().signOut();
                                    }
                                }
                            }
                        }
                    });
        }
    }

    private void storeUserDetails(String userId) {
    if (getApplicationContext() != null){
        employeeDetails = new EmployeeSharedPreferences(this);

        DocumentReference userDR = FirebaseFirestore.getInstance().collection("users").document(userId);

        userDR .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()){
                                String employeeNum = documentSnapshot.getString("employeeNumber");

                                storeUserDetailsToSharedPref(employeeNum, employeeDetails);
                            }
                        }
                    }
                });
    }
    }

    private void storeUserDetailsToSharedPref(String employeeNum, EmployeeSharedPreferences employeeDetails) {
        FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            if (document.exists()){
                                String fullName = document.getString("fullName");
                                String email = document.getString("email");
                                String phoneNumber = document.getString("phoneNumber");
                                String birthdate = document.getString("birthDate");
                                String position = document.getString("position");
                                String imageURL = document.getString("qrCodeImageURL");

                                employeeDetails.setFullName(fullName);
                                employeeDetails.setEmail(email);
                                employeeDetails.setPhoneNumber(phoneNumber);
                                employeeDetails.setBirthday(birthdate);
                                employeeDetails.setPosition(position);
                                employeeDetails.setEmployeeNumber(employeeNum);
                                employeeDetails.setImageURL(imageURL);
                            }
                        }
                    }
                });
    }

    private void initWidgets() {
        emailET = findViewById(R.id.email_EditText);
        passwordET = findViewById(R.id.password_EditText);

        progressBar = findViewById(R.id.progressbar);

        loginBtn = findViewById(R.id.login_Button);

        createNewAccBtn = findViewById(R.id.createNewAcc_Textview);
        
        forgotPasswordBtn = findViewById(R.id.forgotPassword_TextView);
    }

    @Override
    protected void onStart() {
       if (FirebaseAuth.getInstance().getCurrentUser() != null){
           if (getApplicationContext() != null){
               employeeDetails = new EmployeeSharedPreferences(this);

               Intent intent = getIntent();

               String employeeNumber = intent.getStringExtra("employeeNumber");

               if (employeeNumber != null){
                   storeUserDetailsToSharedPref(employeeNumber, employeeDetails);
               }
               startActivity(new Intent(getApplicationContext(), EmployeeDashboard.class));
           }
       }
        super.onStart();
    }
}