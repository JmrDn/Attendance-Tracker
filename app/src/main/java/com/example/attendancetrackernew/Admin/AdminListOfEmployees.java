package com.example.attendancetrackernew.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.Admin.Adapter.ListOfEmployeeAdapter;
import com.example.attendancetrackernew.Admin.Model.ListOfEmployeeModel;
import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminListOfEmployees extends AppCompatActivity {
    Toast toast;
    ListOfEmployeeAdapter adapter;
    ArrayList<ListOfEmployeeModel> list;
    RecyclerView recyclerView;
    TextView numOfEmployeeTV;
    Toolbar toolbar;
    CardView addEmployeeBtn;
    CardView deleteEmployeeBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_list_of_employees);
        initWidgets();
        setUpToolbar();
        setUpClickCardView();
        setUpRecyclerview();

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    private void setUpRecyclerview() {
        list = new ArrayList<>();
        adapter = new ListOfEmployeeAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("employees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty() && querySnapshot != null){
                                list.clear();
                                int i = querySnapshot.size();
                                numOfEmployeeTV.setText(String.valueOf(i));

                                for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    String fullName = documentSnapshot.getString("fullName");
                                    String employeeNum = documentSnapshot.getString("employeeNumber");
                                    String position = documentSnapshot.getString("position");
                                    String phoneNum = documentSnapshot.getString("phoneNumber");
                                    String birthday = documentSnapshot.getString("birthDate");
                                    String email = documentSnapshot.getString("email");
                                    String qrCodeImageURL = documentSnapshot.getString("qrCodeImageURL");

                                    list.add(new ListOfEmployeeModel(fullName, employeeNum, position,
                                            phoneNum,birthday, email, qrCodeImageURL));
                                }
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void initWidgets() {
        recyclerView = findViewById(R.id.listOfEmployee_Recyclerview);

        numOfEmployeeTV = findViewById(R.id.numberOfEmployee_Textview);

        toolbar = findViewById(R.id.toolbar);

        addEmployeeBtn = findViewById(R.id.addEmployee_Cardview);
        deleteEmployeeBtn = findViewById(R.id.delete_Cardview);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public void setUpClickCardView() {

        addEmployeeBtn.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), AdminAddEmployee.class));
        });

        deleteEmployeeBtn.setOnClickListener(v1->{
            showDeleteAllEmployeeDialog();
        });


    }

    @SuppressLint("ResourceType")
    private void showDeleteAllEmployeeDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.delete_employee_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();

        AppCompatButton yesBtn, cancelBtn;
        yesBtn = dialog.findViewById(R.id.yes_Button);
        cancelBtn = dialog.findViewById(R.id.cancel_Button);

        yesBtn.setOnClickListener(v->{

            FirebaseFirestore.getInstance().collection("employees")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty() && querySnapshot != null){
                                    for (QueryDocumentSnapshot documentSnapshot:task.getResult()){
                                        FirebaseFirestore.getInstance().collection("employees")
                                                .document(documentSnapshot.getId())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast toast = Toast.makeText(AdminListOfEmployees.this,  "All employees are deleted", Toast.LENGTH_SHORT);
                                                        toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                                                        toast.show();
                                                        dialog.dismiss();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast toast = Toast.makeText(AdminListOfEmployees.this,  e.getMessage(), Toast.LENGTH_SHORT);
                                                        toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                                                        toast.show();
                                                        dialog.dismiss();
                                                    }
                                                });
                                    }
                                }
                                else{
                                    Toast toast = Toast.makeText(AdminListOfEmployees.this,  "No data to be delete", Toast.LENGTH_SHORT);
                                    toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                                    toast.show();
                                    dialog.dismiss();
                                }
                            }
                        }
                    });

        });

        cancelBtn.setOnClickListener(v1->{
            dialog.dismiss();
        });
    }
}