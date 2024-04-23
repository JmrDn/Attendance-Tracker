package com.example.attendancetrackernew.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetrackernew.Admin.Model.DailyReportsModel;
import com.example.attendancetrackernew.Admin.Model.MonthlyReportsModel;
import com.example.attendancetrackernew.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MonthlyReportsAdapter extends RecyclerView.Adapter<MonthlyReportsAdapter.MyViewHolder> {
    Context context;
    ArrayList<MonthlyReportsModel> list;

    public MonthlyReportsAdapter(Context context, ArrayList<MonthlyReportsModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public MonthlyReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mothly_attendance_report_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MonthlyReportsAdapter.MyViewHolder holder, int position) {
        MonthlyReportsModel model = list.get(position);
        holder.employeeIdTV.setText(model.getEmployeeID());
        holder.employeeNameTV.setText(model.getEmployeeName());
        holder.presentDaysTV.setText(model.getPresentDays());
        holder.lateTV.setText(model.getLate());
        holder.leaveEarlyTV.setText(model.getLeaveEarly());
        holder.overtimeTV.setText(model.getOvertime());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView employeeNameTV;
        TextView employeeIdTV;
        TextView presentDaysTV;
        TextView lateTV;
        TextView leaveEarlyTV;
        TextView overtimeTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            employeeIdTV = itemView.findViewById(R.id.employeeID_TextView);
            employeeNameTV = itemView.findViewById(R.id.employeeName_TextView);
            presentDaysTV = itemView.findViewById(R.id.presentDays_TextView);
            lateTV = itemView.findViewById(R.id.late_TextView);
            leaveEarlyTV = itemView.findViewById(R.id.leaveEarly_TextView);
            overtimeTV = itemView.findViewById(R.id.overtime_TextView);
        }
    }
}
