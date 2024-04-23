package com.example.attendancetrackernew.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetrackernew.Admin.Model.SubDailyReportsModel;
import com.example.attendancetrackernew.R;

import java.util.ArrayList;

public class SubDailyReportsAdapter extends RecyclerView.Adapter<SubDailyReportsAdapter.MyViewHolder> {
    Context context;
    ArrayList<SubDailyReportsModel> list;

    public SubDailyReportsAdapter(Context context, ArrayList<SubDailyReportsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SubDailyReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attendance_report_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubDailyReportsAdapter.MyViewHolder holder, int position) {
        SubDailyReportsModel model = list.get(position);
        holder.dateTV.setText(model.getDate());
        holder.timeInTV.setText(model.getTimeIN());
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
            dateTV = itemView.findViewById(R.id.date_Textview);
            timeInTV = itemView.findViewById(R.id.timeIn_Textview);
            timeOutTV = itemView.findViewById(R.id.timeOut_Textview);
        }
    }
}
