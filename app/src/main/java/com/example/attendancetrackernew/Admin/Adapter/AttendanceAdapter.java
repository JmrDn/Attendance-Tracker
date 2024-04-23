package com.example.attendancetrackernew.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetrackernew.Admin.Model.AttendanceModel;
import com.example.attendancetrackernew.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {
    Context context;
    ArrayList<AttendanceModel> list;

    public AttendanceAdapter(Context context, ArrayList<AttendanceModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public AttendanceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_attendance_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.MyViewHolder holder, int position) {
        AttendanceModel model = list.get(position);
        holder.timeInTV.setText(model.getTimeIn());
        holder.timeOutTV.setText(model.getTimeOut());
        holder.fullNameTV.setText(model.getFullName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView timeInTV,
                timeOutTV,
                fullNameTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            timeInTV = itemView.findViewById(R.id.timeIn_Textview);
            timeOutTV = itemView.findViewById(R.id.timeOut_Textview);
            fullNameTV = itemView.findViewById(R.id.fullName_Textview);
        }
    }
}
