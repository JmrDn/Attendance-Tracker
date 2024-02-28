package com.example.attendancetrackernew.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AdminSignup extends AppCompatActivity {
    AppCompatButton registerBtn;
    TextInputEditText fullNameET;
    TextInputLayout  fullNameTIL;
    TextInputEditText emailET;
    TextInputEditText passwordET;
    TextInputEditText confirmPasswordET;
    TextInputLayout emailTIL;
    TextInputLayout passwordTIL;
    TextInputLayout confirmPasswordTIL;
    ProgressBar progressBar;
    boolean noError = true;

    AdminSharedPreferences adminDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_signup);
        initWidgets();
        validateEditText();
        setUpRegisterProcess();

        progressBar.setVisibility(View.GONE);

    }

    private void setUpRegisterProcess() {

        registerBtn.setOnClickListener(v->{
            hideButtonShowProgressBar();
           if (noError){
               disableEdittextError();

               String email = emailET.getText().toString();
               String fullName = fullNameET.getText().toString();
               String password = passwordET.getText().toString();
               String confirmPassword = confirmPasswordET.getText().toString();

               if (email.isEmpty()){
                   emailTIL.setError("Email field is empty");
                   hideProgressBarShowButton();
               }
               else if (fullName.isEmpty()){
                   fullNameTIL.setError("Full name field is empty");
                   hideProgressBarShowButton();
               }
               else if (password.isEmpty()){
                   passwordTIL.setError("Password field is empty");
                   hideProgressBarShowButton();
               }
               else if(confirmPassword.isEmpty()){
                   confirmPasswordTIL.setError("Confirm password field is empty");
                   hideProgressBarShowButton();
               }
               else if(!password.equals(confirmPassword) || !confirmPassword.equals(password)){
                   passwordTIL.setError("Password not match");
                   confirmPasswordTIL.setError("Password not match");
                   hideProgressBarShowButton();
               }
               else{
                   hideButtonShowProgressBar();
                   registerUser(email, fullName, password);
               }
           }
           else{
               Toast.makeText(getApplicationContext(), "Fill correctly the field", Toast.LENGTH_LONG).show();
               hideProgressBarShowButton();
           }


        });
    }

    private void registerUser(String email, String fullName, String password) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        HashMap<String, Object> userDetails = new HashMap<>();
                        userDetails.put("email", email);
                        userDetails.put("fullName", fullName);
                        userDetails.put("accountType", "adminAccount");

                        adminDetails = new AdminSharedPreferences(AdminSignup.this);
                        adminDetails.setFullName(fullName);
                        adminDetails.setEmail(email);

                        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                                .set(userDetails)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Log.d("TAG", "User details added to database");
                                        }
                                        else{
                                            Log.d("TAG", "User details failed to add on database");
                                        }
                                    }
                                });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getApplicationContext(), AdminDashboard.class));
                                Toast.makeText(getApplicationContext(), "Account created Successfully", Toast.LENGTH_LONG).show();
                            }
                        },3000);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TAG", e.getMessage());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        hideProgressBarShowButton();
                    }
                });
    }

    private void hideProgressBarShowButton(){
        progressBar.setVisibility(View.GONE);
        registerBtn.setVisibility(View.VISIBLE);
    }
    private void hideButtonShowProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        registerBtn.setVisibility(View.GONE);
    }

    private void disableEdittextError() {
        fullNameTIL.setErrorEnabled(false);
        emailTIL.setErrorEnabled(false);
        passwordTIL.setErrorEnabled(false);
        confirmPasswordTIL.setErrorEnabled(false);
    }

    private void validateEditText() {

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailTIL.setError("Invalid Email");
                    noError = false;
                }
                else{
                    emailTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();

                if (password.length() < 8){
                    passwordTIL.setError("Weak password");
                    noError = false;
                }
                else{
                    passwordTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initWidgets() {

        emailET = findViewById(R.id.email_EditText);
        fullNameET = findViewById(R.id.fullName_EditText);
        passwordET = findViewById(R.id.password_EditText);
        confirmPasswordET = findViewById(R.id.confirmPassword_EditText);

        fullNameTIL = findViewById(R.id.fullName_TextInputLayout);
        emailTIL = findViewById(R.id.email_TextInputLayout);
        passwordTIL = findViewById(R.id.password_TextInputLayout);
        confirmPasswordTIL = findViewById(R.id.confirmPassword_TextInputLayout);

        progressBar = findViewById(R.id.progressbar);
        registerBtn = findViewById(R.id.register_Button);

    }
}