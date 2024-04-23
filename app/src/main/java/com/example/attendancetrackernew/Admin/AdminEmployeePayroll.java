package com.example.attendancetrackernew.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.attendancetrackernew.Admin.Adapter.EmployeePayRollAdapter;
import com.example.attendancetrackernew.Admin.Model.EmployeePayrollModel;
import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
@RequiresApi(api = Build.VERSION_CODES.O)
public class AdminEmployeePayroll extends AppCompatActivity {
    RecyclerView recyclerView;
    EmployeePayRollAdapter myAdapter;
    ArrayList<EmployeePayrollModel> list;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_employee_payroll);
        initWidgets();
        setUpToolbar();
        setUpRecyclerview();
    }


    private void setUpRecyclerview() {
        list = new ArrayList<>();
        myAdapter = new EmployeePayRollAdapter(AdminEmployeePayroll.this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminEmployeePayroll.this));
        recyclerView.setAdapter(myAdapter);

        FirebaseFirestore.getInstance().collection("employees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty() && querySnapshot != null){
                                list.clear();
                                for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    String fullName = documentSnapshot.getString("fullName");
                                    String position = documentSnapshot.getString("position");
                                    String employeeNumber = documentSnapshot.getString("employeeNumber");

                                    list.add(new EmployeePayrollModel(fullName, employeeNumber, position));
                                }
                                if (myAdapter != null)
                                    myAdapter.notifyDataSetChanged();
                            }
                        }
                        else{
                            Log.d("TAG", "Failed to retrieve employee data");
                        }
                    }
                });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    private void initWidgets() {
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}