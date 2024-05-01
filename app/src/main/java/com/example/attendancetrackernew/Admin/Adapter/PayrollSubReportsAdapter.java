package com.example.attendancetrackernew.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetrackernew.Admin.Model.PayrollSubReportsModel;
import com.example.attendancetrackernew.R;

import java.util.ArrayList;

public class PayrollSubReportsAdapter extends RecyclerView.Adapter<PayrollSubReportsAdapter.MyViewHolder> {
    Context context;
    ArrayList<PayrollSubReportsModel> list;

    public PayrollSubReportsAdapter(Context context, ArrayList<PayrollSubReportsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PayrollSubReportsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.payroll_report_sub_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PayrollSubReportsAdapter.MyViewHolder holder, int position) {
        PayrollSubReportsModel model = list.get(position);

        holder.employeeNameTV.setText(model.getEmployeeName());
        holder.employeeIDTV.setText(model.getEmployeeID());
        holder.basicSalaryTV.setText(model.getBasicSalary());
        holder.allowanceTV.setText(model.getAllowance());
        holder.totalSalaryTV.setText(model.getTotalSalary());
        holder.totalHoursWorkedTV.setText(model.getTotalHoursWorked());
        holder.totalOvertimeTV.setText(model.getTotalOvertime());
        holder.grossSalaryTV.setText(model.getGrossSalary());
        holder.pagIbigTV.setText(model.getPagIbig());
        holder.sssTV.setText(model.getSss());
        holder.philHealthTV.setText(model.getPhilHealth());
        holder.withHoldingTaxTV.setText(model.getWithHoldingTax());
        holder.totalLateTV.setText(model.getTotalLate());
        holder.cashAdvanceTV.setText(model.getCashAdvance());
        holder.rentalTV.setText(model.getRental());
        holder.totalDeductionTV.setText(model.getTotalDeduction());
        holder.netSalaryTV.setText(model.getNetSalary());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView employeeNameTV;
        TextView employeeIDTV;
        TextView basicSalaryTV;
        TextView allowanceTV;
        TextView totalSalaryTV;
        TextView totalHoursWorkedTV;
        TextView totalOvertimeTV;
        TextView grossSalaryTV;
        TextView pagIbigTV;
        TextView sssTV;
        TextView philHealthTV;
        TextView withHoldingTaxTV;
        TextView totalLateTV;
        TextView cashAdvanceTV;
        TextView rentalTV;
        TextView totalDeductionTV;
        TextView netSalaryTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            employeeNameTV = itemView.findViewById(R.id.employeeName_TextView);
            employeeIDTV = itemView.findViewById(R.id.employeeID_TextView);
            basicSalaryTV = itemView.findViewById(R.id.basicSalary_TextView);
            allowanceTV = itemView.findViewById(R.id.allowance_TextView);
            totalSalaryTV = itemView.findViewById(R.id.totalSalary_TextView);
            totalHoursWorkedTV = itemView.findViewById(R.id.totalHours_TextView);
            totalOvertimeTV = itemView.findViewById(R.id.totalOvertime_TextView);
            grossSalaryTV = itemView.findViewById(R.id.grossSalary_TextView);
            pagIbigTV = itemView.findViewById(R.id.pagibig_TextView);
            sssTV = itemView.findViewById(R.id.sss_TextView);
            philHealthTV = itemView.findViewById(R.id.philHealth_TextView);
            withHoldingTaxTV = itemView.findViewById(R.id.withHoldingTax_TextView);
            totalLateTV = itemView.findViewById(R.id.totalLate_TextView);
            cashAdvanceTV = itemView.findViewById(R.id.cashAdvance_TextView);
            rentalTV = itemView.findViewById(R.id.rental_TextView);
            totalDeductionTV = itemView.findViewById(R.id.totalDeduction_TextView);
            netSalaryTV = itemView.findViewById(R.id.netSalary_TextView);

        }
    }
}
