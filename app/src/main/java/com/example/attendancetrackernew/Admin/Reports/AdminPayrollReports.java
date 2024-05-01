package com.example.attendancetrackernew.Admin.Reports;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.attendancetrackernew.Admin.Adapter.PayrollReportsAdapter;
import com.example.attendancetrackernew.Admin.Adapter.PayrollSubReportsAdapter;
import com.example.attendancetrackernew.Admin.Model.PayrollReportsModel;
import com.example.attendancetrackernew.Admin.Model.PayrollSubReportsModel;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.Utils.CalendarUtils;
import com.example.attendancetrackernew.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdminPayrollReports extends AppCompatActivity {
    PayrollReportsAdapter adapter;
    ArrayList<PayrollReportsModel> list;
    RecyclerView recyclerView;
    Toolbar toolbar;

    AppCompatButton nextBtn;
    AppCompatButton previousBtn;
    TextView monthYearTV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_payroll_reports);

        initWidgets();
        setUpToolbar();

        setUpMonthButton();
        CalendarUtils.selectedDate = LocalDate.now();
        String dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.selectedDate);
        monthYearTV.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        monthYearTV.setOnClickListener(v->{showDatePickerDialog();});

        setUpRecyclerview(dateId);
    }

    private void setUpMonthButton() {
        nextBtn.setOnClickListener(v->{nextDayAction();});
        previousBtn.setOnClickListener(v->{previousDayAction();});
    }
    private void setUpMonthView() {
        monthYearTV.setText(DateAndTimeUtils.getDateWithWordFormat());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void previousDayAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setUpDayView();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void nextDayAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setUpDayView();

    }

    private void setUpDayView(){
        monthYearTV.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        String dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.selectedDate);
        setUpRecyclerview(dateId);

    }

    private void showDatePickerDialog() {

        // Show a date picker dialog to select a date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        @SuppressLint("SetTextI18n")
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            // Set the selected date to the Textview
            monthYearTV.setText(String.format(new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date(year1 - 1900, monthOfYear, dayOfMonth))));

            String dateId = String.format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(year1 - 1900, monthOfYear, dayOfMonth)));
            CalendarUtils.selectedDate = DateAndTimeUtils.getLocalDate(dateId);
            dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.selectedDate);
            setUpRecyclerview(dateId);

        }, year, month, day);

        // Display the date picker dialog
        datePickerDialog.show();
    }
    private void setUpRecyclerview(String dateId) {
        list = new ArrayList<>();
        adapter = new PayrollReportsAdapter(AdminPayrollReports.this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminPayrollReports.this));
        recyclerView.setAdapter(adapter);

        list.clear();

        list.add(new PayrollReportsModel(getPayslipPreviousMonthId(dateId), getPayslipPreviousMonthDate(dateId)));
        list.add(new PayrollReportsModel(getPayslipCurrentMonthId(dateId), getPayslipCurrentMonthDate(dateId)));
        list.add(new PayrollReportsModel(getPayslipNextMonthId(dateId), getPayslipNextMonthDate(dateId)));

        if (adapter != null)
            adapter.notifyDataSetChanged();


    }

    public String getPayslipPreviousMonthId(String dateId){
        String firstMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getPreviousMonthDateId(dateId));
        String secondMonth = DateAndTimeUtils.getMonth(dateId);
        String year = DateAndTimeUtils.getYear(dateId);

        return firstMonth + "26To" + secondMonth + "10_" + year;

    }

    public String getPayslipPreviousMonthDate(String dateId){
        String firstMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getPreviousMonthDateId(dateId));
        String secondMonth = DateAndTimeUtils.getMonth(dateId);
        String year = DateAndTimeUtils.getYear(dateId);

        String upperCaseFirstLetterOfFirstMonth = firstMonth.substring(0,1).toUpperCase() + firstMonth.substring(1);
        String upperCaseFirstLetterOfSecondMonth = secondMonth.substring(0,1).toUpperCase() + secondMonth.substring(1);

       return upperCaseFirstLetterOfFirstMonth + " 26 to " + upperCaseFirstLetterOfSecondMonth + " 10 " + year;
    }

    public String getPayslipNextMonthId(String dateId){
        String firstMonth = DateAndTimeUtils.getMonth(dateId);
        String secondMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getNextMonthDateId(dateId));
        String year = DateAndTimeUtils.getYear(dateId);

        return  firstMonth + "26To" + secondMonth + "10_" + year;
    }

    public String getPayslipNextMonthDate(String dateId){
        String firstMonth = DateAndTimeUtils.getMonth(dateId);
        String secondMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getNextMonthDateId(dateId));
        String year = DateAndTimeUtils.getYear(dateId);

        String upperCaseFirstLetterOfFirstMonth = firstMonth.substring(0,1).toUpperCase() + firstMonth.substring(1);
        String upperCaseFirstLetterOfSecondMonth = secondMonth.substring(0,1).toUpperCase() + secondMonth.substring(1);

        return upperCaseFirstLetterOfFirstMonth + " 26 to " + upperCaseFirstLetterOfSecondMonth + " 10 " + year;
    }

    public String getPayslipCurrentMonthId(String dateId){
        String firstMonth = DateAndTimeUtils.getMonth(dateId);
        String year = DateAndTimeUtils.getYear(dateId);

        return firstMonth + "11To25" + year;

    }

    public String getPayslipCurrentMonthDate(String dateId){
        String firstMonth = DateAndTimeUtils.getMonth(dateId);
        String year = DateAndTimeUtils.getYear(dateId);


        String upperCaseFirstLetterOfFirstMonth = firstMonth.substring(0,1).toUpperCase() + firstMonth.substring(1);


        return upperCaseFirstLetterOfFirstMonth + " 11 to 25 " + year;
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeActionContentDescription("Back");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        monthYearTV = findViewById(R.id.monthDayYear_Textview);
        recyclerView = findViewById(R.id.recyclerview);

        nextBtn = findViewById(R.id.nextMonthAction_Button);
        previousBtn = findViewById(R.id.previousMonthAction_Button);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}