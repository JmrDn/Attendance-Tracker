package com.example.attendancetrackernew.Admin.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetrackernew.Admin.Model.MonthlyReportsModel;
import com.example.attendancetrackernew.Admin.Model.PayrollReportsForExcelModel;
import com.example.attendancetrackernew.Admin.Model.PayrollReportsModel;
import com.example.attendancetrackernew.Admin.Model.PayrollSubReportsModel;
import com.example.attendancetrackernew.Admin.Reports.AdminMonthlyAttendanceReports;
import com.example.attendancetrackernew.Admin.Reports.AdminPayrollSubReports;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.Utils.CalendarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class PayrollReportsAdapter extends RecyclerView.Adapter<PayrollReportsAdapter.MyViewHolder> {
    Context context;
    ArrayList<PayrollReportsModel> list;

    public PayrollReportsAdapter(Context context, ArrayList<PayrollReportsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PayrollReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payroll_reports_layout, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayrollReportsAdapter.MyViewHolder holder, int position) {
        PayrollReportsModel model = list.get(position);
        
        holder.payrollDateTV.setText(model.getPayslipDate());
        holder.itemView.setOnClickListener(v->{
            Intent intent = new Intent(context, AdminPayrollSubReports.class);
            intent.putExtra("payslipId", model.getPayslipId());
            intent.putExtra("payslipDate", model.getPayslipDate());
            context.startActivity(intent);
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView payrollDateTV;
        RecyclerView recyclerView;
        AppCompatButton exportBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            
            payrollDateTV = itemView.findViewById(R.id.payrollDate_TextView);
            recyclerView = itemView.findViewById(R.id.recyclerview);
            exportBtn = itemView.findViewById(R.id.exportAsExcel_Button);





        }
    }

    private void setUpDataToExport(String payslipId, String payslipDate, Context context, ArrayList<PayrollReportsForExcelModel> listForExcel) {

    }
}
