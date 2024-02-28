package com.example.attendancetrackernew.Employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@SuppressLint("ClickableViewAccessibility")
public class EmployeeUpdateProfile extends AppCompatActivity {

    Toolbar toolbar;
    AppCompatEditText fullNameET;
    AppCompatEditText employeeNumET;
    AppCompatEditText positionET;
    AppCompatEditText birthdayET;
    AppCompatEditText phoneNumberET;
    AppCompatButton updateProfileBtn;
    String [] items = {"Office Staff", "Draftsman", "Engineer", "Labor"};
    ArrayAdapter<String> itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_update_profile);

        initWidgets();
        setUpToolbar();
        setUpEditTextValue();
        setPositionSelector();
        setUpDatePicker();
        setUpEnablingButton();
        setUpUpdatingProfile();
    }
    private void setPositionSelector() {
        positionET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showPositionDropDownMenu();
                return true;
            }
        });
    }

    public void showPositionDropDownMenu() {
        // Show a pop-up menu for selecting an item from a dropdown
        PopupMenu popupMenu = new PopupMenu(EmployeeUpdateProfile.this, Objects.requireNonNull(positionET), Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.menu_drop_down_position, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            // Set the selected item text to the corresponding EditText
            String selectedItemText = menuItem.getTitle().toString();
            positionET.setText(selectedItemText);
            new Handler().post(() -> popupMenu.dismiss());
            return true;
        });
        // Show the pop-up menu
        popupMenu.show();
    }

    private void setUpEnablingButton() {
        EmployeeSharedPreferences userDetails = new EmployeeSharedPreferences(this);


        String fullName = fullNameET.getText().toString();
        String employeeNum = employeeNumET.getText().toString();
        String position = positionET.getText().toString();
        String birthday = birthdayET.getText().toString();
        String phoneNum = phoneNumberET.getText().toString();

        if (!fullName.equals(userDetails.getFullName()) ||
            !employeeNum.equals(userDetails.getEmployeeNumber()) ||
            !position.equals(userDetails.getPosition()) ||
            !birthday.equals(userDetails.getBirthday()) ||
            !phoneNum.equals(userDetails.getPhoneNumber())){

            updateProfileBtn.setEnabled(true);
            updateProfileBtn.setBackgroundColor(ContextCompat.getColor(this, R.color.primaryColor));
        }
        else{
            updateProfileBtn.setEnabled(false);
            updateProfileBtn.setBackgroundColor(Color.LTGRAY);
        }

        refresh(1000);

    }

    private void refresh(int i) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setUpEnablingButton();
            }
        },i);
    }

    private void setUpDatePicker() {

        birthdayET.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    showDatePickerDialog();
                }
                return  true;
            }
        });
    }

    private void setUpEditTextValue() {
        EmployeeSharedPreferences userDetails = new EmployeeSharedPreferences(this);
        fullNameET.setText(userDetails.getFullName());
        employeeNumET.setText(userDetails.getEmployeeNumber());
        positionET.setText(userDetails.getPosition());
        birthdayET.setText(userDetails.getBirthday());
        phoneNumberET.setText(userDetails.getPhoneNumber());

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
            // Set the selected date to the EditText
            birthdayET.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year1);
            // Call the validateDOB() method to perform date validation
            validateDOB(year1, monthOfYear, dayOfMonth);
        }, year, month, day);

        // Hide the soft keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(birthdayET.getWindowToken(), 0);
        // Display the date picker dialog
        datePickerDialog.show();

    }

    private void showInvalidDOBAlertDialog(String message) {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.invalid_dob_title))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> showDatePickerDialog())
                .show();
    }

    private int calculateAge(Calendar birthDate) {
        // Calculate the age based on the provided date of birth
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    private void validateDOB(int year, int month, int day) {
        // Validate the selected date of birth to ensure it meets specific criteria
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);

        Calendar today = Calendar.getInstance();

        if (selectedDate.after(today)) {
            // Show an alert for an invalid future date of birth
            showInvalidDOBAlertDialog(getString(R.string.future_dob_message));
            birthdayET.requestFocus();
            birthdayET.setText(null);

        } else if (selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) && selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            // Show an alert for an invalid current date of birth
            showInvalidDOBAlertDialog(getString(R.string.current_dob_message));
            birthdayET.requestFocus();
            birthdayET.setText(null);

        } else if (calculateAge(selectedDate) < 15) {
            // Show an alert for an invalid age (below 15 years old)
            showInvalidDOBAlertDialog(getString(R.string.min_age_dob_message));
            birthdayET.requestFocus();
            birthdayET.setText(null);

        } else if (selectedDate.get(Calendar.YEAR) < (today.get(Calendar.YEAR) - 100)) {
            // Show an alert for an invalid date of birth too far in the past
            showInvalidDOBAlertDialog(getString(R.string.past_dob_message));
            birthdayET.requestFocus();
            birthdayET.setText(null);
        }
    }


    private void setUpUpdatingProfile() {


        updateProfileBtn.setOnClickListener(v->{
            String fullName = fullNameET.getText().toString();
            String employeeNum = employeeNumET.getText().toString();
            String position = positionET.getText().toString();
            String birthday = birthdayET.getText().toString();
            String phoneNum = phoneNumberET.getText().toString();

            String employeeNumRegexPattern = "^[0-9]{4}-[0-9]{4}";
            Pattern pattern = Pattern.compile(employeeNumRegexPattern);

            if (fullName.isEmpty()){
                fullNameET.setError("Full name is empty");
            }
            else if (employeeNum.isEmpty()){
                employeeNumET.setError("Employee Number is empty");
            }
            else if (!pattern.matcher(employeeNum).matches()){
                employeeNumET.setError("Enter valid Employee Number Ex. 2020-1333");
            }
            else if (position.isEmpty()){
                positionET.setError("Position is empty");
            }
            else if (birthday.isEmpty()){
                birthdayET.setError("Date of birth is empty");
            }
            else if (phoneNum.isEmpty()){
                phoneNumberET.setError("Phone number is empty");
            }
            else{
                updateProfile(fullName, employeeNum, position, birthday, phoneNum);
            }





        });
    }

    private void updateProfile(String fullName, String employeeNum, String position,
                               String birthday, String phoneNum) {

        EmployeeSharedPreferences userDetails = new EmployeeSharedPreferences(this);
        HashMap<String, Object> profileDetails = new HashMap<>();

        if (!fullName.equals(userDetails.getFullName())){
            profileDetails.put("fullName", fullName);
            userDetails.setFullName(fullName);
        }
        if (!employeeNum.equals(userDetails.getEmployeeNumber())){
            profileDetails.put("employeeNumber", employeeNum);
            userDetails.setEmployeeNumber(employeeNum);
        }
        if (!position.equals(userDetails.getPosition())){
            profileDetails.put("position", position);
            userDetails.setPosition(position);
        }
        if (!birthday.equals(userDetails.getBirthday())){
            profileDetails.put("birthDate", birthday);
            userDetails.setBirthday(birthday);
        }
        if(!phoneNum.equals(userDetails.getPhoneNumber())){
            profileDetails.put("phoneNumber", phoneNum);
            userDetails.setPhoneNumber(phoneNum);
        }


        if (profileDetails != null){
            FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                    .update(profileDetails)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Profile details successfully updated", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), EmployeeDashboard.class));
                            }
                            else{
                                Log.d("TAG", "Failed to update profile");
                            }

                        }
                    });
        }





    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

    }

    private void initWidgets() {

        toolbar = findViewById(R.id.toolbar);

        fullNameET = findViewById(R.id.fullName_EditText);
        employeeNumET = findViewById(R.id.employeeNumber_EditText);
        positionET = findViewById(R.id.position_EditText);
        birthdayET = findViewById(R.id.birthday_EditText);
        phoneNumberET = findViewById(R.id.phoneNumber_EditText);

        updateProfileBtn = findViewById(R.id.updateProfile_Button);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}