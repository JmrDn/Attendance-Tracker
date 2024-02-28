package com.example.attendancetrackernew;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.attendancetrackernew.Admin.AdminLogin;
import com.example.attendancetrackernew.Employee.EmployeeLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()){
                                    String accountType = documentSnapshot.getString("accountType");
                                    String employeeNumber = documentSnapshot.getString("employeeNumber");

                                    if (accountType.equals("employeeAccount"))
                                        proceedToEmployeeCase(employeeNumber);
                                    else if (accountType.equals("adminAccount"))
                                        proceedToAdminCase();
                                }
                            }
                        }
                    });
        }
        else{
            startActivity(new Intent(getApplicationContext(), SelectRole.class));
        }
    }

    private void proceedToAdminCase() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), AdminLogin.class));
            }
        },2500);
    }

    private void proceedToEmployeeCase(String employeeNumber) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent intent = new Intent(getApplicationContext(), EmployeeLogin.class);

                    intent.putExtra("employeeNumber", employeeNumber);

                    startActivity(intent);
            }
        },2500);
    }
}