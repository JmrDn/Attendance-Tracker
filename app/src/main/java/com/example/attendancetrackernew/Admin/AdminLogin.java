package com.example.attendancetrackernew.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.Employee.EmployeeDashboard;
import com.example.attendancetrackernew.Employee.EmployeeSharedPreferences;
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
@RequiresApi(api = Build.VERSION_CODES.O)
public class AdminLogin extends AppCompatActivity {
    TextInputEditText emailET;
    TextInputEditText passwordET;
    AppCompatButton loginBtn;
    ProgressBar progressBar;
    AdminSharedPreferences adminDetails;
    TextView createAccBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        initWidgets();


        progressBar.setVisibility(View.GONE);

        loginBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
            loginUser();
        });

        createAccBtn.setOnClickListener(v1->{
            startActivity(new Intent(getApplicationContext(), AdminSignup.class));
        });
    }



    private void loginUser() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        validateUserIfAdmin();
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
    private void validateUserIfAdmin() {
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

                                    if (accountType.equals("adminAccount")){
                                        storeUserDetails(userId);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Successfully Login", Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(getApplicationContext(), AdminDashboard.class));
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
            adminDetails = new AdminSharedPreferences(this);

            DocumentReference userDR = FirebaseFirestore.getInstance().collection("users").document(userId);

            userDR .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();

                                if (documentSnapshot.exists()){
                                    String fullName = documentSnapshot.getString("fullName");
                                    String email = documentSnapshot.getString("email");

                                    adminDetails.setEmail(email);
                                    adminDetails.setFullName(fullName);
                                }
                            }
                        }
                    });
        }
    }

    private void initWidgets() {
        emailET = findViewById(R.id.email_EditText);
        passwordET = findViewById(R.id.password_EditText);

        progressBar = findViewById(R.id.progressbar);

        loginBtn = findViewById(R.id.login_Button);
        createAccBtn = findViewById(R.id.createNewAcc_Textview);
    }


    @Override
    protected void onStart() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), AdminDashboard.class));
        }
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}