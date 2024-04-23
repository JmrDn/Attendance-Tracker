package com.example.attendancetrackernew.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class AdminListOfEmployees extends AppCompatActivity {
    Toast toast;
    ListOfEmployeeAdapter adapter;
    ArrayList<ListOfEmployeeModel> list;
    RecyclerView recyclerView;
    TextView numOfEmployeeTV;
    Toolbar toolbar;
    CardView addEmployeeBtn;
    CardView deleteEmployeeBtn;
    TextView toolbarTitleTV;
    SearchView searchView;
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
                            list.clear();
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty() && querySnapshot != null){

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
        toolbarTitleTV = findViewById(R.id.toolbarTitle_Textview);

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
            showAddEmployeeDialog();

        });

        deleteEmployeeBtn.setOnClickListener(v1->{
            showDeleteAllEmployeeDialog();
        });


    }

    @SuppressLint("ResourceType")
    private void showAddEmployeeDialog() {
        AdminSharedPreferences adminDetails = new AdminSharedPreferences(AdminListOfEmployees.this);
        Dialog dialog = new Dialog(AdminListOfEmployees.this);
        dialog.setContentView(R.layout.add_employee_option_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.show();

        AppCompatButton yesBtn, cancelBtn;

        yesBtn = dialog.findViewById(R.id.yes_Button);
        cancelBtn = dialog.findViewById(R.id.cancel_Button);

        yesBtn.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            adminDetails.logout();

            Toast.makeText(getApplicationContext(), "Log out successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), AdminAddEmployee.class));
        });

        cancelBtn.setOnClickListener(v1->{
            dialog.dismiss();
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        toolbarTitleTV.setVisibility(View.VISIBLE);
        getMenuInflater().inflate(R.menu.admin_list_of_employee_search_menu, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.search_icon);
        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint("Search by fullname");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textSearch(query);
//                adapter.stopListening();
                recyclerView.setAdapter(adapter);
//                adapter.startListening();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textSearch(newText);
//                adapter.stopListening();
                recyclerView.setAdapter(adapter);
//                adapter.startListening();
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                toolbarTitleTV.setVisibility(View.GONE);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            }
            else{
              recyclerView.setOnTouchListener(new View.OnTouchListener() {
                  @Override
                  public boolean onTouch(View v, MotionEvent event) {
                      if(v.hasFocus()){
                          searchView.setIconified(true);
                          toolbarTitleTV.setVisibility(View.GONE);
                          Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

                      }
                      else{
                          toolbarTitleTV.setVisibility(View.VISIBLE);
                          Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                      }
                      return false;
                  }
              });
            }



        });

        searchView.setOnCloseListener(() -> {
            textSearch("");
            return false;
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void textSearch(String string) {

        if(!string.isEmpty()){

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
                                list.clear();
                                QuerySnapshot querySnapshot = task.getResult();
                                if (!querySnapshot.isEmpty() && querySnapshot != null){

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

                                        if (fullName.toLowerCase().contains(string.toLowerCase())){
                                            list.add(new ListOfEmployeeModel(fullName, employeeNum, position,
                                                    phoneNum,birthday, email, qrCodeImageURL));
                                        }


                                    }
                                    if (adapter != null)
                                        adapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        }
    }

}