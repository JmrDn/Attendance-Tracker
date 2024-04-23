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
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.attendancetrackernew.Admin.Adapter.AttendanceAdapter;
import com.example.attendancetrackernew.Admin.Model.AttendanceModel;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
@RequiresApi(api = Build.VERSION_CODES.O)
public class AdminAttendance extends AppCompatActivity {
    RecyclerView recyclerView;
    AttendanceAdapter myAdapter;
    ArrayList<AttendanceModel> list;
    Toolbar toolbar;
    TextView dateTodayTV;
    RelativeLayout noDataLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance);
        initWidgets();
        setUpToolbar();
        setUpRecyclerview();
    }


    private void setUpRecyclerview() {
        list = new ArrayList<>();
        myAdapter = new AttendanceAdapter(AdminAttendance.this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminAttendance.this));
        recyclerView.setAdapter(myAdapter);

        String dateId = DateAndTimeUtils.getDateIdFormat();
        String year = DateAndTimeUtils.getYear(dateId);
        String month = DateAndTimeUtils.getMonth(dateId);

        FirebaseFirestore.getInstance().collection("attendance_year"+year).document(month+year)
                .collection(dateId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot =task.getResult();
                            if(!querySnapshot.isEmpty() && querySnapshot != null){
                                noDataLayout.setVisibility(View.GONE);
                                list.clear();

                                for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    String fullName = documentSnapshot.getString("fullName");
                                    String timeIn = documentSnapshot.getString("timeIn");
                                    String timeOut = documentSnapshot.getString("timeOut");

                                    list.add(new AttendanceModel(timeIn, timeOut, fullName));
                                }

                                if (myAdapter != null)
                                    myAdapter.notifyDataSetChanged();
                            }
                            else{
                                noDataLayout.setVisibility(View.VISIBLE);
                            }
                        }
                        else{
                            Log.d("TAG", "Failed to retrieve attendance today");
                        }
                    }
                });

    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        dateTodayTV.setText("As of " + DateAndTimeUtils.getDateWordFormat());
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.recyclerview);

        dateTodayTV = findViewById(R.id.attendanceDate_Textview);
        noDataLayout = findViewById(R.id.noData_Layout);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}