package com.example.attendancetrackernew.Admin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetrackernew.Admin.Model.DailyReportsModel;
import com.example.attendancetrackernew.Admin.Model.SubDailyReportsModel;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DailyReportsAdapter extends RecyclerView.Adapter<DailyReportsAdapter.MyViewHolder> {
    Context context;
    ArrayList<DailyReportsModel> list;

    public DailyReportsAdapter(Context context, ArrayList<DailyReportsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DailyReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_attendance_report_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DailyReportsAdapter.MyViewHolder holder, int position) {
        DailyReportsModel model = list.get(position);
        holder.fullNameTV.setText("Fullname: " + model.getFullName());
        holder.employeeNumAndPositionTV.setText("I.D. & Position: " + model.getEmployeeNum() + " | " + model.getPosition());
        holder.lateTV.setText(model.getLate());
        holder.overTimeTV.setText(model.getOverTime());
        holder.leaveEarlyTV.setText(model.getLeaveEarly());

        setUpRecyclerView(holder.recyclerView, context, model.getEmployeeNum(), model.getDateId());

    }



    private void setUpRecyclerView(RecyclerView recyclerView, Context context, String employeeNum, String dateId) {
        ArrayList<SubDailyReportsModel> list = new ArrayList<>();
        SubDailyReportsAdapter myAdapter = new SubDailyReportsAdapter(context, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myAdapter);

        String year = DateAndTimeUtils.getYear(dateId);
        String month = DateAndTimeUtils.getMonth(dateId);

        FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                .collection("attendance_year"+ year).document(month+year).collection(dateId)
                .document(employeeNum)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear();
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                String timeIn = documentSnapshot.getString("timeIn");
                                String timeOut = documentSnapshot.getString("timeOut");
                                String dateId = documentSnapshot.getString("dateId");
                                String employeeNumFromDailyData = documentSnapshot.getString("employeeNumber");

                                String formattedDate = DateAndTimeUtils.convertToDateWordFormat(dateId);


                                list.add(new SubDailyReportsModel(formattedDate, timeIn, timeOut));

                            }
                            if (myAdapter != null)
                                myAdapter.notifyDataSetChanged();

                        } else {
                            Log.d("TAG", "Failed to retrieve daily attendance data");
                        }
                    }
                });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView fullNameTV;
        TextView employeeNumAndPositionTV;
        TextView lateTV;
        TextView leaveEarlyTV;
        TextView overTimeTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview);
            fullNameTV = itemView.findViewById(R.id.fullName_Textview);
            employeeNumAndPositionTV = itemView.findViewById(R.id.employeeNumAndPosition_TextView);
            lateTV = itemView.findViewById(R.id.late_Textview);
            leaveEarlyTV = itemView.findViewById(R.id.leaveEarly_Textview);
            overTimeTV = itemView.findViewById(R.id.overtime_Textview);
        }
    }
}
