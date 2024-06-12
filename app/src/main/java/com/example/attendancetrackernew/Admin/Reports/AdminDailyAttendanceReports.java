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
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.Admin.Adapter.DailyReportsAdapter;
import com.example.attendancetrackernew.Admin.Model.DailyModelForExcel;
import com.example.attendancetrackernew.Admin.Model.DailyReportsModel;
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
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)

@SuppressLint("WrongViewCast")
public class AdminDailyAttendanceReports extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    DailyReportsAdapter myAdapter;
    ArrayList<DailyReportsModel> list;
    List<String> employeeNumList;
    ProgressBar progressBar;
    AppCompatButton exportBtn;
    AppCompatButton nextDayBtn, previousDayBtn;
    TextView monthDayYearTV;
    RelativeLayout noDataLayout;
    ArrayList<DailyModelForExcel> listForExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_daily_attendance_reports);
        initWidgets();
        setUpToolbar();
        setUpDayButton();

        CalendarUtils.selectedDate = LocalDate.now();
        String dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.selectedDate);
        monthDayYearTV.setText(CalendarUtils.monthDayYearFromDate(CalendarUtils.selectedDate));
        monthDayYearTV.setOnClickListener(v->{showDatePickerDialog();});
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
            monthDayYearTV.setText(String.format(new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(new Date(year1 - 1900, monthOfYear, dayOfMonth))));

            String dateId = String.format(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(year1 - 1900, monthOfYear, dayOfMonth)));
            CalendarUtils.selectedDate = DateAndTimeUtils.getLocalDate(dateId);
            dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.selectedDate);
            setUpRecyclerview(dateId);

        }, year, month, day);

        // Display the date picker dialog
        datePickerDialog.show();

    }

    private void setUpDayButton() {
        nextDayBtn.setOnClickListener(v->{nextDayAction();});
        previousDayBtn.setOnClickListener(v->{previousDayAction();});
    }

    private void setUpDayView(){
        monthDayYearTV.setText(CalendarUtils.monthDayYearFromDate(CalendarUtils.selectedDate));
        String dateId = DateAndTimeUtils.parseDateToDateId(CalendarUtils.selectedDate);
        setUpRecyclerview(dateId);


    }

    private void saveWork(HSSFWorkbook hssfWorkbook) {
        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // Internal Storage


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            File  fileOutput = new File(storageVolume.getDirectory().getPath()+"/Download/DailyAttendanceReport.xls");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(fileOutput);
                hssfWorkbook.write(fileOutputStream);
                fileOutputStream.close();
                hssfWorkbook.close();
                Toast.makeText(AdminDailyAttendanceReports.this, "File exported", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(AdminDailyAttendanceReports.this, "File failed to export", Toast.LENGTH_SHORT).show();
                throw new RuntimeException(e);
            }
        }
        else{
            Toast.makeText(AdminDailyAttendanceReports.this, "Failed to Export, Applicable only for Android 11 and above", Toast.LENGTH_LONG).show();
        }


    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void previousDayAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        setUpDayView();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void nextDayAction() {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        setUpDayView();

    }
    private void setUpRecyclerview(String dateId) {


        noDataLayout.setVisibility(View.VISIBLE);
        listForExcel = new ArrayList<>();

        list = new ArrayList<>();
        myAdapter = new DailyReportsAdapter(AdminDailyAttendanceReports.this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminDailyAttendanceReports.this));
        recyclerView.setAdapter(myAdapter);

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
                            if (myAdapter != null)
                                myAdapter.notifyDataSetChanged();


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
            HSSFSheet hssfSheet = hssfWorkbook.createSheet("Daily Attendance Reports");

            HSSFRow hssfRow = hssfSheet.createRow(0);
            HSSFCell cellTitle = hssfRow.createCell(0);
            cellTitle.setCellValue("Daily Attendance Reports");

            HSSFRow hssfRow1 = hssfSheet.createRow(1);
            HSSFCell dateCell = hssfRow1.createCell(0);

            String month = CalendarUtils.monthDayYearFromDate(CalendarUtils.selectedDate);
            dateCell.setCellValue(month);


            HSSFRow hssfRow3 = hssfSheet.createRow(3);

            HSSFCell nameCell = hssfRow3.createCell(0);
            nameCell.setCellValue("Name");

            HSSFCell positionCell = hssfRow3.createCell(1);
            positionCell.setCellValue("Position");

            HSSFCell timeInCell = hssfRow3.createCell(2);
            timeInCell.setCellValue("Time In");

            HSSFCell timeOutCell = hssfRow3.createCell(3);
            timeOutCell.setCellValue("TimeOut");

            HSSFCell lateCell = hssfRow3.createCell(4);
            lateCell.setCellValue("Late");

            HSSFCell leaveEarlyCell = hssfRow3.createCell(5);
            leaveEarlyCell.setCellValue("Leave Early");

            HSSFCell overtimeCell = hssfRow3.createCell(6);
            overtimeCell.setCellValue("Overtime");


           if (listForExcel != null && listForExcel.size() != 0){
               for (int i = 0; i < listForExcel.size(); i++){
                   DailyModelForExcel model = listForExcel.get(i);

                   HSSFRow row = hssfSheet.createRow(i + 4);

                   HSSFCell nameCellValue = row.createCell(0);
                   nameCellValue.setCellValue(model.getName());

                   HSSFCell positionCellValue = row.createCell(1);
                   positionCellValue.setCellValue(model.getPosition());

                   HSSFCell timeInCellValue = row.createCell(2);
                   timeInCellValue.setCellValue(model.getTimeIn());

                   HSSFCell timeOutCellValue = row.createCell(3);
                   timeOutCellValue.setCellValue(model.getTimeOut());

                   HSSFCell lateCellValue = row.createCell(4);
                   lateCellValue.setCellValue(model.getLate());

                   HSSFCell leaveEarlyCellValue = row.createCell(5);
                   leaveEarlyCellValue.setCellValue(model.getLeave());

                   HSSFCell overtimeCellValue = row.createCell(6);
                   overtimeCellValue.setCellValue(model.getOverTime());

               }
               saveWork(hssfWorkbook);
           }
           else{
               Toast.makeText(AdminDailyAttendanceReports.this, "No data to export", Toast.LENGTH_SHORT).show();
           }



        });


    }

    private String convertToHoursMinutes(String time) {

        int timeHour = Integer.parseInt(time) / 60;
        int timeMinutes = Integer.parseInt(time) % 60;
        String hour = "";
        String min = "";

        if (timeHour == 1){
            hour = "hr";
        }
        else if (timeHour == 0){
            hour = "";
        }
        else{
            hour = "hrs";
        }

        if (timeMinutes == 1){
            min = "min";
        }
        else if (timeMinutes == 0){
            min = "";
        }
        else{
            min = "mins";
        }

        String convertedHoursOfTime = "";
        String convertedMinutesOfTime = "";



        if (!hour.isEmpty()){

            convertedHoursOfTime = timeHour + hour;
        }


        if(!min.isEmpty()){
            convertedMinutesOfTime = timeMinutes + min;
        }

        if (hour.isEmpty() && min.isEmpty()){
            return "0";
        }
        else{
            return convertedHoursOfTime + " " + convertedMinutesOfTime;
        }
    }

    private void retrieveDailyData(String employeeNum, String fullName, String position, String dateId,
                                    ArrayList<DailyReportsModel> list) {

        String year = DateAndTimeUtils.getYear(dateId);
        String month = DateAndTimeUtils.getMonth(dateId);



        FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                .collection("attendance_year"+ year)
                .document(month+year).collection(dateId).document(employeeNum)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()){
                                String timeIn = "";
                                String timeOut = "";
                                String late = "0";
                                String leaveEarly = "0";
                                String overTime = "0";
                                if (documentSnapshot.contains("timeIn"))
                                    timeIn = documentSnapshot.getString("timeIn");
                                if (documentSnapshot.contains("timeOut"))
                                    timeOut = documentSnapshot.getString("timeOut");
                                if(documentSnapshot.contains("late"))
                                    late = documentSnapshot.getString("late");
                                if(documentSnapshot.contains("leaveEarly"))
                                    leaveEarly = documentSnapshot.getString("leaveEarly");
                                if (documentSnapshot.contains("overTime"))
                                    overTime = documentSnapshot.getString("overTime");

                               if (!timeIn.isEmpty() && !timeOut.isEmpty() && timeIn != "" && timeOut != ""){
                                   noDataLayout.setVisibility(View.GONE);

                                   list.add(new DailyReportsModel(fullName, employeeNum, position, late, leaveEarly, overTime, dateId));
                                   listForExcel.add(new DailyModelForExcel(fullName,position, timeIn, timeOut, late, leaveEarly, overTime));
                                   if (myAdapter != null){
                                       myAdapter.notifyDataSetChanged();

                               }

                                }
                            }



                        }
                    }



                });



    }



    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }


    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerview);
        exportBtn = findViewById(R.id.exportAsExcel_Button);

        previousDayBtn = findViewById(R.id.previousDayAction_Button);
        nextDayBtn = findViewById(R.id.nextDayAction_Button);
        monthDayYearTV = findViewById(R.id.monthDayYear_Textview);

        noDataLayout = findViewById(R.id.noData_Layout);


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}