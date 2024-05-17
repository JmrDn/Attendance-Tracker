package com.example.attendancetrackernew.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetrackernew.Admin.Model.EmployeeMyAttendanceModel;
import com.example.attendancetrackernew.R;

import java.util.ArrayList;
import java.util.Collections;

public class EmployeeMyAttendanceAdapter extends RecyclerView.Adapter<EmployeeMyAttendanceAdapter.MyViewHolder> {
    Context context;
    ArrayList<EmployeeMyAttendanceModel> list;
    public EmployeeMyAttendanceAdapter (Context context, ArrayList<EmployeeMyAttendanceModel> list){
        this.context = context;
        this.list = list;

    }
    @NonNull
    @Override
    public EmployeeMyAttendanceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_my_attendance_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeMyAttendanceAdapter.MyViewHolder holder, int position) {
        EmployeeMyAttendanceModel model = list.get(position);
        holder.dateTV.setText(model.getDate());
        holder.timeInTV.setText(model.getTimeIn());
        holder.timeOutTV.setText(model.getTimeOut());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTV;
        TextView timeInTV;
        TextView timeOutTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            dateTV = itemView.findViewById(R.id.date_TextView);
            timeOutTV = itemView.findViewById(R.id.timeOut_TextView);
            timeInTV = itemView.findViewById(R.id.timeIn_TextView);
        }
    }
}
