package com.example.attendancetrackernew.Employee;

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
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.Admin.Adapter.EmployeeMyAttendanceAdapter;
import com.example.attendancetrackernew.Admin.Adapter.MonthlyReportsAdapter;
import com.example.attendancetrackernew.Admin.Model.DailyModelForExcel;
import com.example.attendancetrackernew.Admin.Model.DailyReportsModel;
import com.example.attendancetrackernew.Admin.Model.EmployeeMyAttendanceModel;
import com.example.attendancetrackernew.Admin.Model.MonthlyReportsModel;
import com.example.attendancetrackernew.Admin.Reports.AdminMonthlyAttendanceReports;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.Utils.CalendarUtils;
import com.example.attendancetrackernew.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class EmployeeMyAttendance extends AppCompatActivity {
    TextView monthYearTV;
    RecyclerView recyclerView;
    AppCompatButton nextBtn;
    AppCompatButton previousBtn;
    Toolbar toolbar;
    ArrayList<EmployeeMyAttendanceModel> list;
    EmployeeMyAttendanceAdapter adapter;
    RelativeLayout noDataLayout;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_my_attendance);
        initWidgets();
        setUpMonthButton();
        setUpToolbar();

        CalendarUtils.selectedDate = LocalDate.now();
        String dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.selectedDate);
        monthYearTV.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        monthYearTV.setOnClickListener(v->{showDatePickerDialog();});
        monthYearTV.setOnClickListener(v->{
            showDatePickerDialog();
        });
        setUpRecyclerview(dateId);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);


    }

    private void setUpMonthButton() {
        nextBtn.setOnClickListener(v->{nextDayAction();});
        previousBtn.setOnClickListener(v->{previousDayAction();});
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



    private void setUpRecyclerview(String dateId) {


        noDataLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        EmployeeSharedPreferences employeeData = new EmployeeSharedPreferences(EmployeeMyAttendance.this);

        String employeeNum = employeeData.getEmployeeNumber();

        list = new ArrayList<>();
        adapter = new EmployeeMyAttendanceAdapter(EmployeeMyAttendance.this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(EmployeeMyAttendance.this));
        recyclerView.setAdapter(adapter);


       int day = 1;
       fetchDataForDay(day, dateId, list, employeeNum);


    }




    private void fetchDataForDay(int day, String dateId, ArrayList<EmployeeMyAttendanceModel> list, String employeeNum) {
        String year = DateAndTimeUtils.getYear(dateId);



        String monthWord = DateAndTimeUtils.getMonth(dateId);
        String monthNum = dateId.substring(2,4);
        String dayStr = (day < 10) ? "0" + day : String.valueOf(day);
        String date = dayStr + monthNum + year;

        FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                .collection("attendance_year" + year)
                .document(monthWord + year).collection(date)
                .document(employeeNum).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();

                        if (documentSnapshot.exists()) {
                            String timeIn = documentSnapshot.getString("timeIn");
                            String timeOut = documentSnapshot.getString("timeOut");

                            if (!timeIn.isEmpty() && !timeOut.isEmpty() && timeIn != "" && timeOut != "") {
                                noDataLayout.setVisibility(View.GONE);

                                String timeInSchedule = "08:00"; // 8AM
                                String timeOutSchedule = "17:00"; // 5PM

                                String formattedTimeIn = DateAndTimeUtils.convertAMAndPMFormatInto24HrsFormat(timeIn);
                                String formattedTimeOut = DateAndTimeUtils.convertAMAndPMFormatInto24HrsFormat(timeOut);

                                if (LocalTime.parse(formattedTimeIn).isBefore(LocalTime.parse(timeInSchedule))) {
                                    timeIn = timeIn + " On Time";
                                } else {
                                    timeIn = timeIn + " Late";
                                }

                                if (LocalTime.parse(formattedTimeOut).isBefore(LocalTime.parse(timeOutSchedule))) {
                                    timeOut = timeOut + " Leave Early";
                                } else {
                                    timeOut = timeOut + " On Time";
                                }

                                String finalDate = date;
                                finalDate = DateAndTimeUtils.convertToDateWordFormatWithDayName(date);
                                list.add(new EmployeeMyAttendanceModel(finalDate, timeIn, timeOut));

                                // Notify the adapter after adding data for each day
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();

                                }
                                if (adapter != null && adapter.getItemCount() > 0){
                                    progressBar.setVisibility(View.GONE);
                                }

                                Log.d("TAG", dayStr);
                            }
                        }
                        else{

                        }
                    }

                    // After completing the task for the current day,
                    // proceed to fetch data for the next day (if available)
                    if (day <= 31) {
                        fetchDataForDay(day + 1, dateId, list, employeeNum);

                    }


                    if (day == 31){
                        if (list.size() == 0){
                            noDataLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }


                    }

                });


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

    private void initWidgets() {
        monthYearTV = findViewById(R.id.monthDayYear_Textview);
        recyclerView = findViewById(R.id.recyclerview);

        nextBtn = findViewById(R.id.nextMonthAction_Button);
        previousBtn = findViewById(R.id.previousMonthAction_Button);

        toolbar = findViewById(R.id.toolbar);

        noDataLayout = findViewById(R.id.noData_Layout);

        progressBar = findViewById(R.id.progressbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}