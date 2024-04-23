package com.example.attendancetrackernew.Admin.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.attendancetrackernew.Admin.AdminEmployeePayroll;
import com.example.attendancetrackernew.Admin.AdminEmployeePayrollEdit;
import com.example.attendancetrackernew.Admin.Model.EmployeePayrollModel;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.orhanobut.dialogplus.DialogPlus;

import java.util.ArrayList;
@RequiresApi(api = Build.VERSION_CODES.O)
public class EmployeePayRollAdapter extends RecyclerView.Adapter<EmployeePayRollAdapter.MyViewHolder> {
    Context context;
    ArrayList<EmployeePayrollModel> list;

    public EmployeePayRollAdapter(Context context, ArrayList<EmployeePayrollModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public EmployeePayRollAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_payroll_layout, parent, false);
        return new MyViewHolder(view);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EmployeePayRollAdapter.MyViewHolder holder, int position) {
        EmployeePayrollModel model = list.get(position);
        holder.fullNameTV.setText("Fullname: " + model.getFullName());
        holder.employeeNumAndPositionTV.setText(model.getEmployeeNum() +  " | " +  model.getPosition());
        holder.periodTV.setText("Period: " + DateAndTimeUtils.getPeriod());

        holder.editBtn.setOnClickListener(v->{
            Intent intent = new Intent(context, AdminEmployeePayrollEdit.class);

            intent.putExtra("employeeNumber", model.getEmployeeNum());
            intent.putExtra("fullName", model.getFullName());
            intent.putExtra("position", model.getPosition());

            context.startActivity(intent);
        });
        holder.itemView.setOnClickListener(v->{
            DialogPlus dialogPlus = showPayrollDetailsDialog(model);
            dialogPlus.show();
        });

    }

    private DialogPlus showPayrollDetailsDialog(EmployeePayrollModel model) {
        DialogPlus dialogPlus = DialogPlus.newDialog(context)
                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.pop_up_employee_payroll_layout))
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT)
                .create();

        View view = dialogPlus.getHolderView();

        LinearLayout exportButton = view.findViewById(R.id.exportButton);
        TextView fullname_TextView = view.findViewById(R.id.textViewFullnameValue);
        TextView employeeID_TextView = view.findViewById(R.id.textViewIDValue);
        TextView position_TextView = view.findViewById(R.id.textViewPositionValue);
        TextView period_TextView = view.findViewById(R.id.period_toolbar_title);
        TextView basicSalary_TextView = view.findViewById(R.id.textViewBasicSalaryValue);
        TextView allowance_TextView = view.findViewById(R.id.textViewAllowanceValue);
        TextView overTime_TextView = view.findViewById(R.id.textViewOvertimeValue);
        TextView grossSalary_TextView = view.findViewById(R.id.textViewGrossSalaryValue);
        TextView sss_TextView = view.findViewById(R.id.textViewSSSValue);
        TextView philHealth_TextView = view.findViewById(R.id.textViewPhilHealthValue);
        TextView pagibigFund_TextView = view.findViewById(R.id.textViewPagIbigFundValue);
        TextView witholdingTax_TextView = view.findViewById(R.id.textViewWitholdingTaxValue);
        TextView lateUnderTime_TextView = view.findViewById(R.id.textViewLateValue);
        TextView cashAdvance_TextView = view.findViewById(R.id.textViewCashAdvanceValue);
        TextView rental_TextView = view.findViewById(R.id.textViewRentalValue);
        TextView totalDeduction_TextView = view.findViewById(R.id.textViewTotalDeductionValue);
        TextView netSalary_TextView = view.findViewById(R.id.textViewNetSalaryValue);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view1 -> dialogPlus.dismiss());

        // Set data from the payroll object
        fullname_TextView.setText(model.getFullName());
        employeeID_TextView.setText(model.getEmployeeNum());
        position_TextView.setText(model.getPosition());
        period_TextView.setText(DateAndTimeUtils.getPeriod());


        FirebaseFirestore.getInstance().collection("employees").document(model.getEmployeeNum())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                              if (task.isSuccessful()){
                                  DocumentSnapshot documentSnapshot = task.getResult();
                                  if (documentSnapshot.exists()){

                                      if (documentSnapshot.contains("basicSalary"))
                                          basicSalary_TextView.setText(documentSnapshot.getString("basicSalary"));
                                      if(documentSnapshot.contains("allowance"))
                                          allowance_TextView.setText(documentSnapshot.getString("allowance"));
                                      if(documentSnapshot.contains("overTime"))
                                          overTime_TextView.setText(documentSnapshot.getString("overTime"));
                                      if(documentSnapshot.contains("grossSalary"))
                                          grossSalary_TextView.setText(documentSnapshot.getString("grossSalary"));
                                      if(documentSnapshot.contains("sss"))
                                          sss_TextView.setText(documentSnapshot.getString("sss"));
                                      if(documentSnapshot.contains("philHealth"))
                                          philHealth_TextView.setText(documentSnapshot.getString("philHealth"));
                                      if(documentSnapshot.contains("pagibigFund"))
                                          pagibigFund_TextView.setText(documentSnapshot.getString("pagibigFund"));
                                      if(documentSnapshot.contains("withHoldingTax"))
                                          witholdingTax_TextView.setText(documentSnapshot.getString("withHoldingTax"));
                                      if(documentSnapshot.contains("late"))
                                          lateUnderTime_TextView.setText(documentSnapshot.getString("late"));
                                      if(documentSnapshot.contains("cashAdvance"))
                                          cashAdvance_TextView.setText(documentSnapshot.getString("cashAdvance"));
                                      if(documentSnapshot.contains("rental"))
                                          rental_TextView.setText(documentSnapshot.getString("rental"));
                                      if(documentSnapshot.contains("totalDeduction"))
                                          totalDeduction_TextView.setText(documentSnapshot.getString("totalDeduction"));
                                      if(documentSnapshot.contains("netSalary"))
                                          netSalary_TextView.setText(documentSnapshot.getString("netSalary"));
                                  }
                                  else{
                                      Log.d("TAG", "Document doesn't exit");
                                  }
                              }
                              else{
                                  Log.d("TAG", "Failed to retrieve employee data");
                              }
                            }
                        });


//        exportButton.setOnClickListener(view1 -> showExportOptionsDialog(model));
//        printButton.setOnClickListener(view1 -> showPrintDialog(model));

        return dialogPlus;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTV,
                employeeNumAndPositionTV,
                periodTV;
        ImageView editBtn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameTV = itemView.findViewById(R.id.fullName_Textview);
            employeeNumAndPositionTV = itemView.findViewById(R.id.employeeNumAndPosition_Textview);
            periodTV = itemView.findViewById(R.id.period_Textview);
            editBtn = itemView.findViewById(R.id.edit_ImageView);
        }
    }
}
