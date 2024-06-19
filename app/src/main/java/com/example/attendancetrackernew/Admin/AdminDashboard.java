package com.example.attendancetrackernew.Admin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.CaptureAct;
import com.example.attendancetrackernew.Employee.EmployeeDashboard;
import com.example.attendancetrackernew.Employee.EmployeeSharedPreferences;
import com.example.attendancetrackernew.R;
import com.example.attendancetrackernew.SelectRole;
import com.example.attendancetrackernew.Utils.DateAndTimeUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AdminDashboard extends AppCompatActivity {
    CardView attendanceBtn,
            scanBtn,
            reportsBtn,
            employeePayRollBtn,
            listOfEmployeeBtn,
            logoutBtn;
    AdminSharedPreferences adminDetails;
    TextView adminName;
    Dialog scanResultDialog;
    private int duration =25;

    private boolean isTimerRunning = false;
    private String second = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initWidgets();
        setUpAdminName();
        setUpCardClickListeners();



    }
    private void setUpAdminName() {
        adminDetails = new AdminSharedPreferences(AdminDashboard.this);

        adminName.setText(adminDetails.getFullName());


    }

    private void setUpCardClickListeners() {
        attendanceBtn.setOnClickListener(v->{

        });
        listOfEmployeeBtn.setOnClickListener(v1->{startActivity(new Intent(getApplicationContext(), AdminListOfEmployees.class));});

        logoutBtn.setOnClickListener(v->{showLogoutDialog();});

        scanBtn.setOnClickListener(v->{

            if (!isTimerRunning){
                isTimerRunning = true;

                scanCode();
                new CountDownTimer(duration * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String time = String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)-
                                                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)-
                                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)), Locale.getDefault());

                                final String[] hourMinSec = time.split(":");

                                second = hourMinSec[2];
                            }
                        });
                    }

                    @Override
                    public void onFinish() {
                        duration =25;
                        isTimerRunning = false;
                    }
                }.start();
            }
            else {
                int secondInt = Integer.parseInt(second);


                Toast.makeText(getApplicationContext(), "Please wait for " + ((secondInt < 2) ? secondInt + " second": secondInt + " seconds"), Toast.LENGTH_LONG).show();
            }
            });

        attendanceBtn.setOnClickListener(v->{startActivity(new Intent(getApplicationContext(), AdminAttendance.class));});

        employeePayRollBtn.setOnClickListener(v->{startActivity(new Intent(getApplicationContext(), AdminEmployeePayroll.class));});

        reportsBtn.setOnClickListener(v->{startActivity(new Intent(getApplicationContext(), AdminReports.class));});
    }

    private void showLogoutDialog() {

        Dialog logoutDialog = new Dialog(this);
        logoutDialog.setContentView(R.layout.logout_dialog);
        logoutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        logoutDialog.show();

        AppCompatButton logoutBtn, cancelBtn;
        cancelBtn = logoutDialog.findViewById(R.id.cancel_Button);
        logoutBtn = logoutDialog.findViewById(R.id.logout_Button);

        logoutBtn.setOnClickListener(v->{
            FirebaseAuth.getInstance().signOut();
            AdminSharedPreferences userDetails = new AdminSharedPreferences(AdminDashboard.this);
            userDetails.logout();
            Toast.makeText(getApplicationContext(), "Successfully log out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), SelectRole.class));
            logoutDialog.dismiss();
        });

        cancelBtn.setOnClickListener(v1->{
            logoutDialog.dismiss();
        });
    }

    private void scanCode() {



        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up for flash down for off");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);


    }

    private void initWidgets() {
        attendanceBtn = findViewById(R.id.attendanceSummary_CardView);
        scanBtn = findViewById(R.id.scan_CardView);
        reportsBtn = findViewById(R.id.reports_Cardview);
        employeePayRollBtn = findViewById(R.id.employeePayroll_CardView);
        listOfEmployeeBtn = findViewById(R.id.listOfEmployee_CardView);
        logoutBtn = findViewById(R.id.logout_CardView);

        adminName = findViewById(R.id.adminName_Textview);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {

            //Get value of qrcode
            String itemChild = result.getContents();
            showResultDialog(itemChild);
//            setScannedHistory(itemChild);

        }
    });

    private void showResultDialog(String itemChild) {
        Toast.makeText(AdminDashboard.this, itemChild, Toast.LENGTH_SHORT).show();
        scanResultDialog = new Dialog(this);
        scanResultDialog.setContentView(R.layout.scan_result_dialog);
        scanResultDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scanResultDialog.setCancelable(false);
        scanResultDialog.show();

        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        // Format the date and time
        String dateId = currentDateTime.format(formatter);

        String year = DateAndTimeUtils.getYear(dateId);
        String month = DateAndTimeUtils.getMonth(dateId);
        String currentTimeWithAmAndPM = DateAndTimeUtils.getTimeWithAMAndPM();

        TextView nameTV, positionTV, timeInOurTimeOutTV, timeTV;

        AppCompatButton timeInBtn, timeOutBtn;
        ImageView cancelBtn;


        nameTV = scanResultDialog.findViewById(R.id.name_Textview);
        positionTV = scanResultDialog.findViewById(R.id.position_Textview);
        timeInOurTimeOutTV =scanResultDialog.findViewById(R.id.timeInOrTimeOut_TextView);
        timeTV = scanResultDialog.findViewById(R.id.time_TextView);


        timeInBtn = scanResultDialog.findViewById(R.id.timeIn_Button);
        timeOutBtn = scanResultDialog.findViewById(R.id.timeOut_Button);
        cancelBtn = scanResultDialog.findViewById(R.id.cancel_ImageView);

        HashMap<String , Object> employeeDetailsTimeIn = new HashMap<>();
        employeeDetailsTimeIn.put("employeeNumber", itemChild);
        employeeDetailsTimeIn.put("timeIn", currentTimeWithAmAndPM);
        employeeDetailsTimeIn.put("timeOut", "");
        employeeDetailsTimeIn.put("dateId", dateId);

        if (!itemChild.contains(".") &&
                !itemChild.contains("#") &&
                !itemChild.contains("$") &&
                !itemChild.contains("[") &&
                !itemChild.contains("]")){

            FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();

                                if (documentSnapshot.exists()){
                                    String fullName = documentSnapshot.getString("fullName");
                                    String position = documentSnapshot.getString("position");
                                    nameTV.setText(fullName);
                                    positionTV.setText(position);
                                    employeeDetailsTimeIn.put("fullName", fullName);
                                    employeeDetailsTimeIn.put("position", position);

                                    isEmployeeAlreadyTimeIn(itemChild, new EmployeeAlreadyTimeIn() {
                                        @Override
                                        public void onCallBack(boolean isEmployeeAlreadyTimeIn, String timeIn) {
                                            if (isEmployeeAlreadyTimeIn){
                                                // Employee already time in
//                    timeInBtn.setEnabled(false);
//                    timeInBtn.setBackgroundColor(Color.LTGRAY);
//
//                    timeOutBtn.setEnabled(true);
//                    timeOutBtn.setBackgroundColor(Color.RED);

                                                //REVISION!!!!!!!!!!!


                                                isEmployeeAlreadyTimeOut(itemChild, new EmployeeAlreadyTimeOut() {
                                                    @SuppressLint("SetTextI18n")
                                                    @Override
                                                    public void onCallBack(boolean isEmployeeAlreadyTimeOut, String timeOut) {
                                                        if (isEmployeeAlreadyTimeOut){
                                                            // Employee already time out

//                                timeOutBtn.setEnabled(false);
//                                timeOutBtn.setBackgroundColor(Color.LTGRAY);
//
//                                if (isEmployeeAlreadyTimeOut && isEmployeeAlreadyTimeIn){
//                                    if (timeOut.equals(dateId)){
//                                        timeOutBtn.setEnabled(false);
//                                        timeOutBtn.setBackgroundColor(Color.LTGRAY);
//
//                                        timeInBtn.setEnabled(false);
//                                        timeInBtn.setBackgroundColor(Color.LTGRAY);
//                                    }
//                                    else{
//                                        timeOutBtn.setEnabled(true);
//                                        timeOutBtn.setBackgroundColor(Color.RED);
//
//                                        timeInBtn.setEnabled(true);
//                                        timeInBtn.setBackgroundColor(ContextCompat.getColor(AdminDashboard.this, R.color.primaryColor));
//                                    }
//                                }

                                                            //REVISION!!!!!

                                                            timeTV.setVisibility(View.GONE);
                                                            timeInOurTimeOutTV.setText("Employee is present, Please view the details of attendance in the Employee Attendance");
                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    scanResultDialog.dismiss();
                                                                }
                                                            },3500);
                                                        }
                                                        else{
                                                            //Employee not yet time out

//                                timeOutBtn.setEnabled(true);
//                                timeOutBtn.setBackgroundColor(Color.RED);


                                                            //REVISION!!!!!!!

                                                            FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                                    .collection("attendance_year" + year).document(month+year)
                                                                    .collection(dateId)
                                                                    .document(itemChild)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            if (task.isSuccessful()){
                                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                                if (documentSnapshot.exists()){
                                                                                    String timeIn = documentSnapshot.getString("timeIn");
                                                                                    String timeOut = currentTimeWithAmAndPM;

                                                                                    String timeInSchedule = "08:00"; //8AM
                                                                                    String timeOutSchedule = "17:00"; //5PM

                                                                                    String formattedTimeIn = DateAndTimeUtils.convertAMAndPMFormatInto24HrsFormat(timeIn);
                                                                                    String formattedTimeOut = DateAndTimeUtils.convertAMAndPMFormatInto24HrsFormat(timeOut);

                                                                                    String late = DateAndTimeUtils.getMinutes(timeInSchedule, formattedTimeIn);
                                                                                    String leaveEarly = DateAndTimeUtils.getMinutes(formattedTimeOut, timeOutSchedule);
                                                                                    String overTime = DateAndTimeUtils.getMinutes(timeOutSchedule, formattedTimeOut);
                                                                                    HashMap<String , Object>  employeeDetailsTimeOut = new HashMap<>();

                                                                                    if (LocalTime.parse(formattedTimeOut).isBefore(LocalTime.parse(timeOutSchedule))) {
                                                                                        Log.d("TAG", "BEFORE");
                                                                                        overTime = "0";
                                                                                    }
                                                                                    else {
                                                                                        leaveEarly = "0";
                                                                                        Log.d("TAG", "after");
                                                                                    }


                                                                                    employeeDetailsTimeOut.put("timeOut", currentTimeWithAmAndPM);
                                                                                    employeeDetailsTimeOut.put("late", late);
                                                                                    employeeDetailsTimeOut.put("leaveEarly", leaveEarly);
                                                                                    employeeDetailsTimeOut.put("overTime", overTime);

                                                                                    FirebaseFirestore.getInstance().collection("attendance_year" + year).document(month+year)
                                                                                            .collection(dateId)
                                                                                            .document(itemChild)
                                                                                            .update(employeeDetailsTimeOut)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
//                                                                        Toast.makeText(AdminDashboard.this,
//                                                                                "Employee " + nameTV.getText().toString() + " time out " + currentTimeWithAmAndPM
//                                                                                ,Toast.LENGTH_LONG).show();
//                                                                        scanResultDialog.dismiss();

                                                                                                    //REVISION !!!!
                                                                                                    timeTV.setVisibility(View.VISIBLE);
                                                                                                    timeInOurTimeOutTV.setText("Timeout:");
                                                                                                    timeTV.setText(currentTimeWithAmAndPM);
                                                                                                    new Handler().postDelayed(new Runnable() {
                                                                                                        @Override
                                                                                                        public void run() {
                                                                                                            scanResultDialog.dismiss();
                                                                                                        }
                                                                                                    },3500);
                                                                                                }
                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Toast.makeText(AdminDashboard.this, "Failed to time out: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                                                    scanResultDialog.dismiss();
                                                                                                }
                                                                                            });

                                                                                    FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                                                            .collection("attendance_year" + year).document(month+year)
                                                                                            .collection(dateId)
                                                                                            .document(itemChild)
                                                                                            .update(employeeDetailsTimeOut)
                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                @Override
                                                                                                public void onSuccess(Void unused) {
                                                                                                    Log.d("TAG", "Successfully upload late, leaveEarly and Overtime");
                                                                                                }
                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                @Override
                                                                                                public void onFailure(@NonNull Exception e) {
                                                                                                    Log.d("TAG", "Failed to upload late, leaveEarly and Overtime");
                                                                                                }
                                                                                            });



                                                                                    //Payroll

                                                                                    String dayOfMonth = DateAndTimeUtils.getDay(dateId);
                                                                                    int dayOfMonthInt = Integer.parseInt(dayOfMonth);
                                                                                    Log.d("TAG", String.valueOf(dayOfMonthInt));


                                                                                    String semiMonthName = "";
                                                                                    String year = DateAndTimeUtils.getYear(dateId);
                                                                                    String payslipDate = "";

                                                                                    if (dayOfMonthInt > 25 && dayOfMonthInt < 32){

                                                                                        String firstMonth = DateAndTimeUtils.getMonth(dateId);
                                                                                        String secondMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getNextMonthDateId(dateId));

                                                                                        Log.d("TAG", "First Month:" + firstMonth);
                                                                                        Log.d("TAG", "Second Month:" + secondMonth);
                                                                                        Log.d("TAG", "1");

                                                                                        semiMonthName = firstMonth + "26To" + secondMonth + "10_" + year;

                                                                                        String upperCaseFirstLetterOfFirstMonth = firstMonth.substring(0,1).toUpperCase() + firstMonth.substring(1);
                                                                                        String upperCaseFirstLetterOfSecondMonth = secondMonth.substring(0,1).toUpperCase() + secondMonth.substring(1);

                                                                                        payslipDate = upperCaseFirstLetterOfFirstMonth + " 26 to " + upperCaseFirstLetterOfSecondMonth + " 10 " + year;



                                                                                    }

                                                                                    else if (dayOfMonthInt > 0 && dayOfMonthInt < 11){
                                                                                        String firstMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getPreviousMonthDateId(dateId));
                                                                                        String secondMonth = DateAndTimeUtils.getMonth(dateId);

                                                                                        Log.d("TAG", "First Month:" + firstMonth);
                                                                                        Log.d("TAG", "Second Month:" + secondMonth);
                                                                                        Log.d("TAG", "2");

                                                                                        semiMonthName = firstMonth + "26To" + secondMonth + "10_" + year;

                                                                                        String upperCaseFirstLetterOfFirstMonth = firstMonth.substring(0,1).toUpperCase() + firstMonth.substring(1);
                                                                                        String upperCaseFirstLetterOfSecondMonth = secondMonth.substring(0,1).toUpperCase() + secondMonth.substring(1);

                                                                                        payslipDate = upperCaseFirstLetterOfFirstMonth + " 26 to " + upperCaseFirstLetterOfSecondMonth + " 10 " + year;
                                                                                    }
                                                                                    else if (dayOfMonthInt > 10 && dayOfMonthInt < 26){
                                                                                        String month = DateAndTimeUtils.getMonth(dateId);

                                                                                        semiMonthName = month + "11To25_" + year;

                                                                                        String upperCaseMonth = month.substring(0,1).toUpperCase() + month.substring(1);

                                                                                        payslipDate = month + " 11 to 25 " + year;
                                                                                    }

                                                                                    Log.d("TAG", semiMonthName);

                                                                                    HashMap<String, Object> employeePaySlip = new HashMap<>();
                                                                                    HashMap<String, Object> employeePayRoll= new HashMap<>();

                                                                                    String finalSemiMonthName = semiMonthName;
                                                                                    String finalPayslipDate = payslipDate;


                                                                                    FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                                                            .collection("payslip").document(semiMonthName).get()
                                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                    if (task.isSuccessful()){
                                                                                                        DocumentSnapshot documentSnapshot1 = task.getResult();

                                                                                                        if (documentSnapshot1.exists()){

                                                                                                            final int[] summaryOfWork = {Integer.parseInt(documentSnapshot1.getString("totalOvertime")),
                                                                                                                    Integer.parseInt(documentSnapshot1.getString("totalWorkedHours")),
                                                                                                                    Integer.parseInt(documentSnapshot1.getString("totalLeaveEarly")),
                                                                                                                    Integer.parseInt(documentSnapshot1.getString("totalLate"))};


                                                                                                            FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                                                                                    .collection("attendance_year" + year).document(month+year)
                                                                                                                    .collection(dateId)
                                                                                                                    .document(itemChild).get()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                            if (task.isSuccessful()){
                                                                                                                                DocumentSnapshot documentSnapshot2 = task.getResult();
                                                                                                                                if(documentSnapshot2.exists()){
                                                                                                                                    String timeIn = documentSnapshot2.getString("timeIn");
                                                                                                                                    String timeOut = documentSnapshot2.getString("timeOut");

                                                                                                                                    String timeInSchedule = "08:00"; //8AM
                                                                                                                                    String timeOutSchedule = "17:00"; //5PM

                                                                                                                                    String formattedTimeIn = DateAndTimeUtils.convertAMAndPMFormatInto24HrsFormat(timeIn);
                                                                                                                                    String formattedTimeOut = DateAndTimeUtils.convertAMAndPMFormatInto24HrsFormat(timeOut);

                                                                                                                                    String late = DateAndTimeUtils.getMinutes(timeInSchedule, formattedTimeIn);
                                                                                                                                    String leaveEarly = DateAndTimeUtils.getMinutes(formattedTimeOut, timeOutSchedule);
                                                                                                                                    String overTime = DateAndTimeUtils.getMinutes(timeOutSchedule, formattedTimeOut);

                                                                                                                                    String totalHoursWorked = DateAndTimeUtils.getMinutes(formattedTimeIn, formattedTimeOut);
                                                                                                                                    if (LocalTime.parse(formattedTimeOut).isBefore(LocalTime.parse(timeOutSchedule))) {
                                                                                                                                        Log.d("TAG", "BEFORE");
                                                                                                                                        overTime = "0";
                                                                                                                                    }
                                                                                                                                    else {
                                                                                                                                        leaveEarly = "0";
                                                                                                                                        Log.d("TAG", "after");
                                                                                                                                    }


                                                                                                                                    summaryOfWork[0] += Integer.parseInt(overTime);
                                                                                                                                    summaryOfWork[1] += Integer.parseInt(totalHoursWorked);
                                                                                                                                    summaryOfWork[2] += Integer.parseInt(leaveEarly);
                                                                                                                                    summaryOfWork[3] += Integer.parseInt(late);

                                                                                                                                    employeePaySlip.put("totalLate", String.valueOf(summaryOfWork[3]));
                                                                                                                                    employeePaySlip.put("totalLeaveEarly", String.valueOf(summaryOfWork[2]));
                                                                                                                                    employeePaySlip.put("totalOvertime", String.valueOf(summaryOfWork[0]));
                                                                                                                                    employeePaySlip.put("totalWorkedHours", String.valueOf(summaryOfWork[1]));
                                                                                                                                    employeePaySlip.put("payslipID", finalSemiMonthName);
                                                                                                                                    employeePaySlip.put("payslipDate", finalPayslipDate);

                                                                                                                                    employeePayRoll.put("totalLate", String.valueOf(summaryOfWork[3]));
                                                                                                                                    employeePayRoll.put("totalLeaveEarly", String.valueOf(summaryOfWork[2]));
                                                                                                                                    employeePayRoll.put("totalOvertime", String.valueOf(summaryOfWork[0]));
                                                                                                                                    employeePayRoll.put("totalWorkedHours", String.valueOf(summaryOfWork[1]));
                                                                                                                                    employeePayRoll.put("payslipID", finalSemiMonthName);
                                                                                                                                    employeePayRoll.put("payslipDate", finalPayslipDate);
                                                                                                                                    employeePayRoll.put("employeeNumber", itemChild);

                                                                                                                                    FirebaseFirestore.getInstance().collection("employeesPayroll").document(finalSemiMonthName)
                                                                                                                                            .collection(itemChild).document(finalSemiMonthName)
                                                                                                                                            .update(employeePayRoll).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                    if (task.isSuccessful()){
                                                                                                                                                        Log.d("TAG", "Successfully saved employee payslip data");
                                                                                                                                                    }
                                                                                                                                                    else{
                                                                                                                                                        Log.d("TAG", "Failed to saved employee payslip data");
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });

                                                                                                                                    FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                                                                                                            .collection("payslip").document(finalSemiMonthName)
                                                                                                                                            .update(employeePaySlip).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                    if (task.isSuccessful()){
                                                                                                                                                        Log.d("TAG", "Successfully saved employee payslip data");
                                                                                                                                                    }
                                                                                                                                                    else{
                                                                                                                                                        Log.d("TAG", "Failed to saved employee payslip data");
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });

                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });


                                                                                                        }
                                                                                                        else{
                                                                                                            FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                                                                                    .collection("attendance_year" + year).document(month+year)
                                                                                                                    .collection(dateId)
                                                                                                                    .document(itemChild).get()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                                            if (task.isSuccessful()){
                                                                                                                                DocumentSnapshot documentSnapshot2 = task.getResult();
                                                                                                                                if(documentSnapshot2.exists()){
                                                                                                                                    String timeIn = documentSnapshot2.getString("timeIn");
                                                                                                                                    String timeOut = documentSnapshot2.getString("timeOut");

                                                                                                                                    String timeInSchedule = "08:00"; //8AM
                                                                                                                                    String timeOutSchedule = "17:00"; //5PM

                                                                                                                                    String formattedTimeIn = DateAndTimeUtils.convertAMAndPMFormatInto24HrsFormat(timeIn);
                                                                                                                                    String formattedTimeOut = DateAndTimeUtils.convertAMAndPMFormatInto24HrsFormat(timeOut);

                                                                                                                                    String late = DateAndTimeUtils.getMinutes(timeInSchedule, formattedTimeIn);
                                                                                                                                    String leaveEarly = DateAndTimeUtils.getMinutes(formattedTimeOut, timeOutSchedule);
                                                                                                                                    String overTime = DateAndTimeUtils.getMinutes(timeOutSchedule, formattedTimeOut);

                                                                                                                                    String totalHoursWorked = DateAndTimeUtils.getMinutes(formattedTimeIn, formattedTimeOut);

                                                                                                                                    if (LocalTime.parse(formattedTimeOut).isBefore(LocalTime.parse(timeOutSchedule))) {
                                                                                                                                        Log.d("TAG", "BEFORE");
                                                                                                                                        overTime = "0";
                                                                                                                                    }
                                                                                                                                    else {
                                                                                                                                        leaveEarly = "0";
                                                                                                                                        Log.d("TAG", "after");
                                                                                                                                    }

                                                                                                                                    employeePaySlip.put("totalLate", late);
                                                                                                                                    employeePaySlip.put("totalLeaveEarly", leaveEarly);
                                                                                                                                    employeePaySlip.put("totalOvertime", overTime);
                                                                                                                                    employeePaySlip.put("totalWorkedHours", totalHoursWorked);
                                                                                                                                    employeePaySlip.put("payslipID", finalSemiMonthName);
                                                                                                                                    employeePaySlip.put("payslipDate", finalPayslipDate);

                                                                                                                                    employeePayRoll.put("totalLate", late);
                                                                                                                                    employeePayRoll.put("totalLeaveEarly", leaveEarly);
                                                                                                                                    employeePayRoll.put("totalOvertime", overTime);
                                                                                                                                    employeePayRoll.put("totalWorkedHours", totalHoursWorked);
                                                                                                                                    employeePayRoll.put("payslipID", finalSemiMonthName);
                                                                                                                                    employeePayRoll.put("payslipDate", finalPayslipDate);
                                                                                                                                    employeePayRoll.put("employeeNumber", itemChild);

                                                                                                                                    FirebaseFirestore.getInstance().collection("employeesPayroll").document(finalSemiMonthName)
                                                                                                                                            .collection(itemChild).document(finalSemiMonthName)
                                                                                                                                            .set(employeePayRoll).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                    if (task.isSuccessful()){
                                                                                                                                                        Log.d("TAG", "Successfully saved employee payslip data");
                                                                                                                                                    }
                                                                                                                                                    else{
                                                                                                                                                        Log.d("TAG", "Failed to saved employee payslip data");
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });

                                                                                                                                    FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                                                                                                            .collection("payslip").document(finalSemiMonthName)
                                                                                                                                            .set(employeePaySlip).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                    if (task.isSuccessful()){
                                                                                                                                                        Log.d("TAG", "Successfully saved employee payslip data");
                                                                                                                                                    }
                                                                                                                                                    else{
                                                                                                                                                        Log.d("TAG", "Failed to saved employee payslip data");
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });



                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            });

                                                                                }
                                                                            }
                                                                        }
                                                                    });

                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    calculateTotalLateLeaveEarlyAndOverTime(itemChild, month, year, dateId);

                                                                }
                                                            },3000);
                                                        }
                                                    }
                                                });
                                            }
                                            else{
                                                //Employee not yet time in
//                    timeInBtn.setEnabled(true);
//                    timeInBtn.setBackgroundColor(ContextCompat.getColor(AdminDashboard.this, R.color.primaryColor));
//
//                    timeOutBtn.setEnabled(false);
//                    timeOutBtn.setBackgroundColor(Color.LTGRAY);

                                                //REVISION!!!!!!!

                                                FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                        .collection("attendance_year" + year).document(month+year)
                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    DocumentSnapshot documentSnapshot = task.getResult();

                                                                    if (!documentSnapshot.exists()){
                                                                        HashMap<String , Object> monthDetails = new HashMap<>();
                                                                        monthDetails.put("monthId", month+year);

                                                                        FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                                                .collection("attendance_year" + year).document(month+year)
                                                                                .update(monthDetails)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {

                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            }
                                                        });

                                                FirebaseFirestore.getInstance().collection("attendance_year" + year).document(month+year)
                                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if (task.isSuccessful()){
                                                                    DocumentSnapshot documentSnapshot = task.getResult();

                                                                    if (!documentSnapshot.exists()){
                                                                        HashMap<String , Object> monthDetails = new HashMap<>();
                                                                        monthDetails.put("monthId", month+year);

                                                                        FirebaseFirestore.getInstance().collection("attendance_year" + year).document(month+year)
                                                                                .update(monthDetails)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {

                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {

                                                                                    }
                                                                                });
                                                                    }
                                                                }
                                                            }
                                                        });
                                                FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                                        .collection("attendance_year" + year).document(month+year)
                                                        .collection(dateId)
                                                        .document(itemChild)
                                                        .set(employeeDetailsTimeIn)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {

                                                            }
                                                        });

                                                FirebaseFirestore.getInstance().collection("attendance_year" + year).document(month+year)
                                                        .collection(dateId)
                                                        .document(itemChild)
                                                        .set(employeeDetailsTimeIn)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

//                                    Toast.makeText(AdminDashboard.this,
//                                            "Employee " + nameTV.getText().toString() + " time in " + currentTimeWithAmAndPM
//                                            ,Toast.LENGTH_LONG).show();
//                                    scanResultDialog.dismiss();

                                                                //REVISION !!!!
                                                                timeTV.setVisibility(View.VISIBLE);
                                                                timeInOurTimeOutTV.setText("Time in:");
                                                                timeTV.setText(currentTimeWithAmAndPM);
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        scanResultDialog.dismiss();
                                                                    }
                                                                },3500);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(AdminDashboard.this, "Failed to in: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                                                scanResultDialog.dismiss();
                                                            }
                                                        });
                                            }
                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
                                    scanResultDialog.dismiss();
                                }
                            }
                        }
                    });
        }
        else{
            Toast.makeText(getApplicationContext(), "No data found", Toast.LENGTH_LONG).show();
            scanResultDialog.dismiss();
        }




        cancelBtn.setOnClickListener(v->{
            scanResultDialog.dismiss();
        });
        timeInBtn.setOnClickListener(v->{
            FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                    .collection("attendance_year" + year).document(month+year)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();

                                if (!documentSnapshot.exists()){
                                    HashMap<String , Object> monthDetails = new HashMap<>();
                                    monthDetails.put("monthId", month+year);

                                    FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                                            .collection("attendance_year" + year).document(month+year)
                                            .update(monthDetails)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }
                            }
                        }
                    });

            FirebaseFirestore.getInstance().collection("attendance_year" + year).document(month+year)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();

                                if (!documentSnapshot.exists()){
                                    HashMap<String , Object> monthDetails = new HashMap<>();
                                    monthDetails.put("monthId", month+year);

                                    FirebaseFirestore.getInstance().collection("attendance_year" + year).document(month+year)
                                            .update(monthDetails)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });
                                }
                            }
                        }
                    });
            FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                    .collection("attendance_year" + year).document(month+year)
                    .collection(dateId)
                    .document(itemChild)
                    .set(employeeDetailsTimeIn)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });

            FirebaseFirestore.getInstance().collection("attendance_year" + year).document(month+year)
                    .collection(dateId)
                    .document(itemChild)
                    .set(employeeDetailsTimeIn)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AdminDashboard.this,
                                    "Employee " + nameTV.getText().toString() + " time in " + currentTimeWithAmAndPM
                                    ,Toast.LENGTH_LONG).show();
                            scanResultDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminDashboard.this, "Failed to in: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            scanResultDialog.dismiss();
                        }
                    });
        });


        timeOutBtn.setOnClickListener(v->{



        });

    }

    private void calculateTotalLateLeaveEarlyAndOverTime(String itemChild, String documentId, String year, String dateId) {
        int totalLate = 0;
        int totalLeaveEarly = 0;
        int totalOverTime = 0;
        int[] totalValue = {totalLate, totalLeaveEarly, totalOverTime};



        for (int i = 1; i <= 12; i++){
            String month = "";
            String iString = String.valueOf(i);


            if (i < 10){

                month = "0" + iString;
            }
            else {
                month = iString;
            }

            for (int j = 1; j <= 31; j++){
                String day = "";
                String jString = String.valueOf(j);

                day = (j < 10) ? "0" +  jString: jString;

                String collectionName = day + month + year;
//                Log.d("TAG", collectionName);

                FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                        .collection("attendance_year"+ year).document(documentId+year)
                        .collection(collectionName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();

                                    if (!querySnapshot.isEmpty() && querySnapshot != null){
                                        for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                            if (documentSnapshot.exists()){

                                                if (documentSnapshot.contains("late")){
                                                    int late = Integer.parseInt(documentSnapshot.getString("late"));
                                                    totalValue[0] += late;
                                                }
                                                if (documentSnapshot.contains("leaveEarly")){
                                                    int leaveEarly = Integer.parseInt(documentSnapshot.getString("leaveEarly"));
                                                    totalValue[1] += leaveEarly;
                                                }
                                                if (documentSnapshot.contains("overTime")){
                                                    int overtime = Integer.parseInt(documentSnapshot.getString("overTime"));
                                                    totalValue[2] += overtime;

                                                }


                                                Log.d("TAG", "Calculating Data for Monthly Attendance");
                                            }
                                        }
                                    }
                                }
                            }
                        });


            }
        }

        HashMap<String , Object> totalValueDetails = new HashMap<>();

        FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                .collection("attendance_year" + year).document(documentId+year)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot1 = task.getResult();

                            if (documentSnapshot1.exists()){

                                if(documentSnapshot1.contains("presentDays")){
                                    String presentDay = documentSnapshot1.getString("presentDays");
                                    int presentDayInt = Integer.parseInt(presentDay);
                                    int totalPresentDay = presentDayInt + 1;
                                    totalValueDetails.put("presentDays", String.valueOf(totalPresentDay));
                                    Log.d("TAG", "Present days exist and put");

                                }
                                else{
                                    totalValueDetails.put("presentDays", "1");
                                    Log.d("TAG", "Present days does not exist");
                                }

                            }
                            else{
                                totalValueDetails.put("presentDays", "1");
                                Log.d("TAG", "Present days document does not exist");
                            }
                        }
                        else{
                            Log.d("TAG", "Task unsuccessfull for present days" + task.getException().getMessage());
                        }
                    }
                });

       
       new Handler().postDelayed(new Runnable() {
           @Override
           public void run() {

               totalValueDetails.put("late", String.valueOf(totalValue[0]));
               totalValueDetails.put("leaveEarly", String.valueOf(totalValue[1]));
               totalValueDetails.put("overTime", String.valueOf(totalValue[2]));
               totalValueDetails.put("monthId", documentId+year);

               FirebaseFirestore.getInstance().collection("employees").document(itemChild)
                       .collection("attendance_year"+ year).document(documentId+year)
                       .set(totalValueDetails)
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               Log.d("TAG", "Success upload total details of Monthly Attendance");
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Log.d("TAG", "Failed to upload total details of Monthly Attendance");
                           }
                       });
           }
       },8000);
    }

    public interface EmployeeAlreadyTimeIn{
        void onCallBack(boolean isEmployeeAlreadyTimeIn, String dateTimeIn);
    }
    public interface EmployeeAlreadyTimeOut{
        void onCallBack(boolean isEmployeeAlreadyTimeOut, String dateTimeOut);
    }
    private void isEmployeeAlreadyTimeOut(String employeeNumber, EmployeeAlreadyTimeOut callback){
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        // Format the date and time
        String dateId = currentDateTime.format(formatter);

        String year = DateAndTimeUtils.getYear(dateId);
        String month = DateAndTimeUtils.getMonth(dateId);

        FirebaseFirestore.getInstance().collection("attendance_year" + year).document(month+year)
                .collection(dateId)
                .document(employeeNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                if (documentSnapshot.contains("timeOut")){
                                    String date = documentSnapshot.getString("dateId");
                                    String timeOut = documentSnapshot.getString("timeOut");
                                    if (timeOut.equals(""))
                                        callback.onCallBack(false, "");
                                    else
                                        callback.onCallBack(true, date);
                                }
                                else{
                                    callback.onCallBack(false,"");
                                }
                            }
                            else{
                                callback.onCallBack(false,"");
                            }
                        }
                    }
                });

    }

    private void isEmployeeAlreadyTimeIn(String employeeNumber, EmployeeAlreadyTimeIn callback){
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Define the format pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");

        // Format the date and time
        String dateId = currentDateTime.format(formatter);

        String year = DateAndTimeUtils.getYear(dateId);
        String month = DateAndTimeUtils.getMonth(dateId);

        FirebaseFirestore.getInstance().collection("attendance_year" + year).document(month+year)
                .collection(dateId)
                .document(employeeNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()){
                                if (documentSnapshot.contains("timeIn")){
                                    String date = documentSnapshot.getString("dateId");
                                    String timeIn = documentSnapshot.getString("timeIn");
                                    if (timeIn.isEmpty() && timeIn == null)
                                        callback.onCallBack(false, "");
                                    else
                                        callback.onCallBack(true, date);
                                }
                                else{
                                    callback.onCallBack(false, "");
                                }
                            }
                            else{
                                callback.onCallBack(false, "");
                            }
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();
    }
}

