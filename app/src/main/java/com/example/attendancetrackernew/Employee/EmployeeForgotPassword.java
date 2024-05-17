package com.example.attendancetrackernew.Employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class EmployeeForgotPassword extends AppCompatActivity {
    TextInputLayout emailTIL;
    TextInputEditText emailET;
    AppCompatButton submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_forgot_password);
        initWidgets();
        
        submitBtn.setOnClickListener(v->{
            String EMAIL = emailET.getText().toString();
            
            if (EMAIL.isEmpty()){
                emailTIL.setError("Enter your email");
            }
            else{
                forgotPassword(EMAIL);
            }
        });
    }

    private void forgotPassword(String email) {

        FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(getApplicationContext(), "Successfully send, Please check your email to reset password", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), EmployeeLogin.class));
                }
                else{
                    Toast.makeText(getApplicationContext(), "Enter valid email", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initWidgets() {
        emailTIL = findViewById(R.id.email_TextInputLayout);
        emailET = findViewById(R.id.email_EditText);
        
        submitBtn = findViewById(R.id.submit_Button);
    }
}