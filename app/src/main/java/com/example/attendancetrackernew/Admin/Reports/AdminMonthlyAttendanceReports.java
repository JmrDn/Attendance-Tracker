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
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.Admin.Adapter.DailyReportsAdapter;
import com.example.attendancetrackernew.Admin.Adapter.MonthlyReportsAdapter;
import com.example.attendancetrackernew.Admin.Model.DailyModelForExcel;
import com.example.attendancetrackernew.Admin.Model.DailyReportsModel;
import com.example.attendancetrackernew.Admin.Model.MonthlyReportsModel;
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

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdminMonthlyAttendanceReports extends AppCompatActivity {
    TextView monthYearTV;
    RecyclerView recyclerView;
    MonthlyReportsAdapter adapter;
    ArrayList<MonthlyReportsModel> list;
    AppCompatButton nextBtn;
    AppCompatButton previousBtn;
    RelativeLayout noDataLayout;
    AppCompatButton exportBtn;
    LinearLayout titleColumnLayout;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_monthly_attendance_reports);
        initWidgets();
        setUpToolbar();
        setUpMonthView();
        setUpMonthButton();

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
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeActionContentDescription("Back");
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


        noDataLayout.setVisibility(View.VISIBLE);
        titleColumnLayout.setVisibility(View.GONE);
//        listForExcel = new ArrayList<>();

        list = new ArrayList<>();
        adapter = new MonthlyReportsAdapter(AdminMonthlyAttendanceReports.this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminMonthlyAttendanceReports.this));
        recyclerView.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("employees")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty() && querySnapshot != null){
                                list.clear();


                                for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                    String fullName = documentSnapshot.getString("fullName");
                                    String employeeNum = documentSnapshot.getString("employeeNumber");
                                    String position = documentSnapshot.getString("position");
                                    Log.d("TAG", "EMPLOYEE");
                                    retrieveDailyData(employeeNum, fullName, position, dateId, list);
                                }
                                if (adapter != null)
                                    adapter.notifyDataSetChanged();


                            }
                            else{
                                Log.d("TAG", "Failed to retrieve employee data");
                            }
                        }
                        else{
                            Log.d("TAG", "Failed to retrieve employee data");
                        }

                    }
                });

        exportBtn.setOnClickListener(v->{
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = hssfWorkbook.createSheet("Monthly Attendance Reports");

            HSSFRow hssfRow = hssfSheet.createRow(0);
            HSSFCell cellTitle = hssfRow.createCell(0);
            cellTitle.setCellValue("Monthly Attendance Reports");

            HSSFRow hssfRow1 = hssfSheet.createRow(1);
            HSSFCell dateCell = hssfRow1.createCell(0);

            String month = CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate);
            dateCell.setCellValue(month);


            HSSFRow hssfRow3 = hssfSheet.createRow(3);

            HSSFCell nameCell = hssfRow3.createCell(0);
            nameCell.setCellValue("Employee ID");

            HSSFCell positionCell = hssfRow3.createCell(1);
            positionCell.setCellValue("Employee Name");

            HSSFCell timeInCell = hssfRow3.createCell(2);
            timeInCell.setCellValue("Present Days");

            HSSFCell timeOutCell = hssfRow3.createCell(3);
            timeOutCell.setCellValue("Late");

            HSSFCell lateCell = hssfRow3.createCell(4);
            lateCell.setCellValue("Leave Early");

            HSSFCell leaveEarlyCell = hssfRow3.createCell(5);
            leaveEarlyCell.setCellValue("Overtime");




            if (list != null && list.size() != 0){
                for (int i = 0; i < list.size(); i++){
                    MonthlyReportsModel model = list.get(i);

                    HSSFRow row = hssfSheet.createRow(i + 4);

                    HSSFCell nameCellValue = row.createCell(0);
                    nameCellValue.setCellValue(model.getEmployeeID());

                    HSSFCell positionCellValue = row.createCell(1);
                    positionCellValue.setCellValue(model.getEmployeeName());

                    HSSFCell timeInCellValue = row.createCell(2);
                    timeInCellValue.setCellValue(model.getPresentDays());

                    HSSFCell timeOutCellValue = row.createCell(3);
                    timeOutCellValue.setCellValue(model.getLate());

                    HSSFCell lateCellValue = row.createCell(4);
                    lateCellValue.setCellValue(model.getLeaveEarly());

                    HSSFCell leaveEarlyCellValue = row.createCell(5);
                    leaveEarlyCellValue.setCellValue(model.getOvertime());



                }
                saveWork(hssfWorkbook);
            }
            else{
                Toast.makeText(AdminMonthlyAttendanceReports.this, "No data to export", Toast.LENGTH_SHORT).show();
            }



        });


    }
    private void saveWork(HSSFWorkbook hssfWorkbook) {
        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // Internal Storage


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            File fileOutput = new File(storageVolume.getDirectory().getPath()+"/Download/MonthlyAttendanceReport.xls");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);
                hssfWorkbook.write(fileOutputStream);
                fileOutputStream.close();
                hssfWorkbook.close();
                Toast.makeText(AdminMonthlyAttendanceReports.this, "File exported", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(AdminMonthlyAttendanceReports.this, "File failed to export", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }
        else{
            Toast.makeText(AdminMonthlyAttendanceReports.this, "Failed to Export, Applicable only for Android 11 and above", Toast.LENGTH_LONG).show();
        }


    }

    private void retrieveDailyData(String employeeNum, String fullName, String position, String dateId,
                                   ArrayList<MonthlyReportsModel> list) {

       String year = DateAndTimeUtils.getYear(dateId);
       int yearInt = Integer.parseInt(year);

        // Extract month substring
        int monthInt = Integer.parseInt(dateId.substring(2, 4)); // Parse month substring to integer
        String monthWord = DateAndTimeUtils.getMonth(dateId);
        String monthNum = dateId.substring(2,4);
        Log.d("TAG", String.valueOf(monthInt));
//       String month = String.valueOf(dateId.charAt(2) + dateId.charAt(3));

        YearMonth yearMonth = YearMonth.of(yearInt, monthInt);
        int daysInMonth = yearMonth.lengthOfMonth();


       final int[] presentDays = {0};

        for(int i = 1; i <= daysInMonth; i++) {
            String day;
            if (i < 10){
                day = "0" + i;
            }
            else{
                day = String.valueOf(i);
            }

            String date = day+monthNum+year;

        }

        FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                .collection("attendance_year"+ year)
                .document(monthWord+year).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()){
                                String late = documentSnapshot.getString("late");
                                String leaveEarly = documentSnapshot.getString("leaveEarly");
                                String overTime = documentSnapshot.getString("overTime");
                                String presentDays = "0";

                                if(documentSnapshot.contains("presentDays")){
                                    presentDays = documentSnapshot.getString("presentDays");
                                }
                                noDataLayout.setVisibility(View.GONE);
                                titleColumnLayout.setVisibility(View.VISIBLE);
                                list.add(new MonthlyReportsModel(employeeNum, fullName, presentDays, late,overTime , leaveEarly));

                            }

                            if (adapter != null){
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    private void setUpMonthView() {
        monthYearTV.setText(DateAndTimeUtils.getDateWithWordFormat());

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

        noDataLayout = findViewById(R.id.noData_Layout);

        exportBtn = findViewById(R.id.exportAsExcel_Button);
        titleColumnLayout = findViewById(R.id.titleColumn_Layout);

        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}