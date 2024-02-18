package com.example.attendancetrackernew.Employee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmployeeSignup extends AppCompatActivity {

    String [] items = {"Office Staff", "Draftsman", "Engineer", "Labor"};
    ArrayAdapter<String> itemAdapter;
    AutoCompleteTextView positionACT;
    TextInputLayout birthdayTIL;
    TextInputEditText birthdayET;
    TextInputEditText fullNameET;
    TextInputEditText employeeNumET;
    TextInputEditText positionET;
    TextInputEditText passwordET;
    TextInputEditText confirmPasswordET;
    TextInputEditText emailET;
    TextInputEditText phoneNumberET;
    Toolbar toolbar;
    TextInputLayout fullNameTIL;
    TextInputLayout employeeNumTIL;
    TextInputLayout positionTIL;

    TextInputLayout emailTIL;
    TextInputLayout passwordTIL;
    TextInputLayout confirmPasswordTIL;
    TextInputLayout phoneNumberTIL;
    AppCompatButton registerBtn;
    TextView alreadyHaveAccBtn;
    ProgressBar progressBar;
    boolean noError = false;
    Bitmap scaledPngImage;

    String employeeNumFromDataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_signup);

        initWidgets();
        setUpToolbar();
        setPositionSelector();
        setBirthdaySelector();
        setUpUserAlreadyHaveAccount();
        invalidateEditText();

        progressBar.setVisibility(View.GONE);
        setUpRegisterProcess();




    }

    private void invalidateEditText() {

        fullNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String fullName = s.toString();

                if (fullName.isEmpty()){
                    fullNameTIL.setError("Enter full name");
                    noError = false;
                }
                else {
                    fullNameTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phoneNumberET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phoneNumber = s.toString();

                if (phoneNumber.isEmpty()){
                    phoneNumberTIL.setError("Enter Phone Number");
                    noError = false;
                }
                else if (phoneNumber.length() < 11){
                    phoneNumberTIL.setError("Invalid Phone Number");
                    noError = false;
                }
                else {
                    phoneNumberTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        employeeNumET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String employeeNum = s.toString();
                String regexPattern = "^[0-9]{4}-[0-9]{4}";
                Pattern pattern = Pattern.compile(regexPattern);

                Matcher matcher = pattern.matcher(employeeNum);

                if (employeeNum.isEmpty()){
                    employeeNumTIL.setError("Enter Employee Number");
                    noError = false;
                }
                else if (!matcher.matches()){
                    employeeNumTIL.setError("Invalid number. Ex. 2102-1323");
                    noError = false;
                }

                else {
                    employeeNumTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        positionACT.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String position = s.toString();

                if (position.isEmpty()){
                    positionTIL.setError("Enter position");
                    noError = false;
                }
                else {
                    positionTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        birthdayET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String birthday = s.toString();

                if (birthday.isEmpty()){
                    birthdayTIL.setError("Enter your Birthdate");
                    noError = false;
                }
                else {
                    birthdayTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email = s.toString();

                if (email.isEmpty()){
                    emailTIL.setError("Enter email");
                    noError = false;
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailTIL.setError("Enter valid Email");
                    noError = false;
                }
                else {
                    emailTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();

                if (password.isEmpty()){
                    passwordTIL.setError("Enter password");
                    noError = false;
                }
                else if (password.length() < 8){
                    passwordTIL.setError("Password must consists of 8 characters");
                    noError = false;
                }
                else {
                    passwordTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirmPasswordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String confirmPassword = s.toString();

                if (confirmPassword.isEmpty()){
                    confirmPasswordTIL.setError("Enter password");
                    noError = false;
                }
                else if (confirmPassword.length() < 8){
                    confirmPasswordTIL.setError("Password must consists of 8 characters");
                    noError = false;
                }
                else {
                    confirmPasswordTIL.setErrorEnabled(false);
                    noError = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    private void disableError(){
        fullNameTIL.setErrorEnabled(false);
        employeeNumTIL.setErrorEnabled(false);
        positionTIL.setErrorEnabled(false);
        birthdayTIL.setErrorEnabled(false);
        emailTIL.setErrorEnabled(false);
        passwordTIL.setErrorEnabled(false);
        confirmPasswordTIL.setErrorEnabled(false);
        phoneNumberTIL.setErrorEnabled(false);
    }

    private void checkIfExist(String employeeNum, final ExistenceCallback callback) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("employees");

        if (collectionReference != null) {
            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                                String employeeNumFromDataBase = documentSnapshot.get("employeeNumber").toString();

                                if (employeeNumFromDataBase.equals(employeeNum)) {
                                    callback.onExistenceChecked(true);
                                    return; // No need to continue checking
                                }
                            }
                        }
                        callback.onExistenceChecked(false);
                    } else {
                        callback.onExistenceChecked(false);
                    }
                }
            });
        } else {
            callback.onExistenceChecked(false);
        }
    }

    // Define a callback interface
    interface ExistenceCallback {
        void onExistenceChecked(boolean exists);
    }



    private void setUpRegisterProcess() {
        registerBtn.setOnClickListener(v->{
            disableError();


            progressBar.setVisibility(View.VISIBLE);
            registerBtn.setVisibility(View.GONE);
            String fullName = fullNameET.getText().toString();
            String employeeNum = employeeNumET.getText().toString();
            String position = positionACT.getText().toString();
            String birthDate = birthdayET.getText().toString();
            String email = emailET.getText().toString();
            String password = passwordET.getText().toString();
            String confirmPassword = confirmPasswordET.getText().toString();
            String phoneNumber = phoneNumberET.getText().toString();

            if (noError){

                if (fullName.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    fullNameTIL.setError("Enter Full name");
                }
                else if (phoneNumber.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    phoneNumberTIL.setError("Enter Phone Number");
                }
                else if (employeeNum.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    employeeNumTIL.setError("Enter Employee Number");
                }
                else if(!employeeNum.isEmpty()){
                    checkIfExist(employeeNum, new ExistenceCallback() {
                        @Override
                        public void onExistenceChecked(boolean exists) {
                            // Use the 'exists' value here or pass it to another method
                            if (exists) {
                                progressBar.setVisibility(View.GONE);
                                registerBtn.setVisibility(View.VISIBLE);
                                employeeNumTIL.setError("Already Exist");
                            }
                        }
                    });

                }
                else if (position.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    positionTIL.setError("Select Position");
                }
                else if (birthDate.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    birthdayTIL.setError("Select Birthdate");
                }
                else if (email.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    emailTIL.setError("Enter Email");
                }
                else if (password.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    passwordTIL.setError("Enter Password");
                }
                else if (confirmPassword.isEmpty()){
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    confirmPasswordTIL.setError("Enter Email");
                }
                else if (!password.equals(confirmPassword) && !confirmPassword.equals(password)){
                    progressBar.setVisibility(View.GONE);
                    registerBtn.setVisibility(View.VISIBLE);
                    passwordTIL.setError("Password not match");
                    confirmPasswordTIL.setError("Password not match");
                }
                else{
                    progressBar.setVisibility(View.VISIBLE);
                    registerBtn.setVisibility(View.GONE);

                    registerUser(fullName, employeeNum, position, birthDate, email, confirmPassword, phoneNumber);
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "Fill the empty fields", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                registerBtn.setVisibility(View.VISIBLE);
            }




        });
    }



    private void registerUser(String fullName, String employeeNum, String position,
                              String birthDate, String email, String password, String phoneNumber) {


        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        generateEmployeeQRCode(employeeNum);
                        saveUserDetails(fullName, employeeNum, position, birthDate, email, phoneNumber);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        registerBtn.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Failed to create account: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserDetails(String fullName, String employeeNum, String position, String birthDate, String email,String phoneNumber) {

        HashMap<String, Object> employeeDetails = new HashMap<>();

        employeeDetails.put("fullName", fullName);
        employeeDetails.put("employeeNumber", employeeNum);
        employeeDetails.put("position", position);
        employeeDetails.put("birthDate", birthDate);
        employeeDetails.put("email", email);
        employeeDetails.put("phoneNumber", phoneNumber);

        FirebaseFirestore.getInstance().collection("employees").document(employeeNum)
                .set(employeeDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Successfully Register", Toast.LENGTH_LONG).show();
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getApplicationContext(), EmployeeLogin.class));
                                }
                            }, 3000);
                        }
                        else{
                            Log.d("TAG", "Employee Details failed to upload");
                        }
                    }
                });

        HashMap<String , Object> userDetails = new HashMap<>();

        userDetails.put("accountType", "employeeAccount");
        userDetails.put("fullName", fullName);
        userDetails.put("email", email);
        userDetails.put("employeeNumber", employeeNum);
        userDetails.put("userId", FirebaseAuth.getInstance().getUid());

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid())
                .set(userDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("TAG", "User details successfully uploaded");
                        }
                        else{
                            Log.d("TAG", "User details failed to upload");
                        }
                    }
                });

    }

    private Bitmap loadAndOverlayPngImage(Bitmap qrCodeBitmap) {
        // Load the PNG image to be placed in the middle of the QR code
        Bitmap pngImage = BitmapFactory.decodeResource(getResources(), R.drawable.ic_icon_attendance_tracker_2);
        int qrCodeWidth = qrCodeBitmap.getWidth();
        int qrCodeHeight = qrCodeBitmap.getHeight();
        int desiredWidthInPixels = 125; // Adjust the size as desired
        int desiredHeightInPixels = 125; // Adjust the size as desired
        // Scale the PNG image to the desired dimensions
        scaledPngImage = Bitmap.createScaledBitmap(pngImage, desiredWidthInPixels, desiredHeightInPixels, true);
        // Create a new bitmap of the same size as the QR code
        Bitmap mergedBitmap = Bitmap.createBitmap(qrCodeWidth, qrCodeHeight, qrCodeBitmap.getConfig());
        // Draw the QR code bitmap onto the canvas of the merged bitmap
        Canvas canvas = new Canvas(mergedBitmap);
        canvas.drawBitmap(qrCodeBitmap, 0, 0, null);
        // Calculate the position to place the PNG image in the middle of the QR code
        int x = (qrCodeWidth - scaledPngImage.getWidth()) / 2;
        int y = (qrCodeHeight - scaledPngImage.getHeight()) / 2;
        // Draw the scaled PNG image onto the canvas of the merged bitmap
        canvas.drawBitmap(scaledPngImage, x, y, null);
        // Set the merged bitmap with the PNG image to the ImageView

        return  mergedBitmap;
    }

    private void generateEmployeeQRCode(String employeeNum) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(employeeNum, BarcodeFormat.QR_CODE, 600,  600);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);

            Bitmap qrCodeBitmap = loadAndOverlayPngImage(bitmap);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            byte[] imageData = bytes.toByteArray();

            //Upload QRCODE Image to Firebase storage
            StorageReference qrCodeStorageRef = FirebaseStorage.getInstance().getReference("images/employeesQRCODE/" + employeeNum);

            qrCodeStorageRef.putBytes(imageData)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Log.d("TAG", "QR CODE UPLOADED TO FIREBASE");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "QR CODE FAILED TO UPLOAD TO FIREBASE");
                        }
                    });

        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private void setUpUserAlreadyHaveAccount() {
        alreadyHaveAccBtn.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), EmployeeLogin.class));
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeActionContentDescription("Back");
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setBirthdaySelector() {
        birthdayTIL.setEndIconOnClickListener(v -> showDatePickerDialog());
        birthdayET.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePickerDialog();
            }
            return true;
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

    private int calculateAge(Calendar birthDate) {
        // Calculate the age based on the provided date of birth
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
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

    private void setPositionSelector() {
        itemAdapter = new ArrayAdapter<>(this, R.layout.position_list_item, items);
        positionACT.setAdapter(itemAdapter);

        positionACT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void initWidgets() {
        toolbar = findViewById(R.id.toolbar);
        
        positionACT = findViewById(R.id.position_AutoCompleteTextView);
        birthdayET = findViewById(R.id.birthday_EditText);
        fullNameET = findViewById(R.id.fullName_EditText);
        employeeNumET = findViewById(R.id.empNumber_EditText);
        emailET = findViewById(R.id.email_EditText);
        passwordET = findViewById(R.id.password_EditText);
        confirmPasswordET = findViewById(R.id.confirmPassword_EditText);
        phoneNumberET = findViewById(R.id.phoneNumber_EditText);
                
        birthdayTIL = findViewById(R.id.birthday_TextInputLayout);
        fullNameTIL = findViewById(R.id.fullName_TextInputLayout);
        positionTIL = findViewById(R.id.position_TextInputLayout);
        emailTIL = findViewById(R.id.email_TextInputLayout);
        passwordTIL = findViewById(R.id.password_TextInputLayout);
        confirmPasswordTIL = findViewById(R.id.confirmPassword_TextInputLayout);
        employeeNumTIL = findViewById(R.id.empNumber_TextInputLayout);
        phoneNumberTIL = findViewById(R.id.phoneNumber_TextInputLayout);

        registerBtn = findViewById(R.id.register_Button);

        alreadyHaveAccBtn = findViewById(R.id.alreadyHaveAccount_Textview);

        progressBar = findViewById(R.id.progressbar);
        
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}