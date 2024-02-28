package com.example.attendancetrackernew.Admin.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.attendancetrackernew.Admin.Model.ListOfEmployeeModel;
import com.example.attendancetrackernew.Employee.EmployeeSharedPreferences;
import com.example.attendancetrackernew.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListOfEmployeeAdapter extends RecyclerView.Adapter<ListOfEmployeeAdapter.MyViewHolder> {
    Context context;
    ArrayList<ListOfEmployeeModel> list;

    private DialogPlus dialogPlus;
    private ImageView imageView;
    private TextView textViewFullname;
    private static final int CALL_PERMISSION_REQUEST_CODE = 100;
    private static final int SEND_PERMISSION_REQUEST_CODE = 200;
    Bitmap bitmap;
    public ListOfEmployeeAdapter(Context context, ArrayList<ListOfEmployeeModel> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public ListOfEmployeeAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_of_employees_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListOfEmployeeAdapter.MyViewHolder holder, int position) {
        ListOfEmployeeModel model = list.get(position);
        holder.fullNameTV.setText(model.getFullName());
        holder.employeeNumTV.setText(model.getEmployeeNum());
        holder.positionTV.setText(model.getPosition());
        holder.phoneNumTV.setText(model.getPhoneNum());
        holder.birthdayTV.setText(model.getBirthday());
        holder.emailTV.setText(model.getEmail());

        holder.qrCodeImageView.setOnClickListener(v->{
            displayQRCode(v.getContext(), model);
        });
        final String fileNameQRCODE = model.getFileNameOfQRCode();
        retrieveQrCode(fileNameQRCODE, holder.qrCodeImageView);
        holder.itemView.setOnClickListener(v -> {
            final DialogPlus dialogPlus = createDialogPlus(v.getContext(), position, model, holder);
            dialogPlus.show();
        });

    }


    private void displayQRCode(Context context, ListOfEmployeeModel model) {
        Dialog dialog = new Dialog(context);

        dialog.setContentView(R.layout.employee_highlight_qrcode_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.primary_border));
        dialog.setCancelable(true);
        dialog.show();

        ImageView qrCodeImageview1;
        AppCompatButton downloadBtn;
        TextView nameTV;

        nameTV = dialog.findViewById(R.id.name_Textview);
        qrCodeImageview1 = dialog.findViewById(R.id.qrCode_ImageView);
        downloadBtn = dialog.findViewById(R.id.download_Button);

        nameTV.setText(model.getFullName());
        retrieveQrCode(model.getFileNameOfQRCode(), qrCodeImageview1);

        downloadBtn.setOnClickListener(v->{
            FileOutputStream fileOutputStream = null;

            //Download file
            File sdCard = Environment.getExternalStorageDirectory();
            File directory = new File(sdCard.getAbsolutePath() + "/Download");
            directory.mkdir();

            String fileName = String.format(model.getFullName() + "%d.jpg", System.currentTimeMillis());
            File outfile = new File(directory, fileName);

            Toast.makeText(context, "Image saved", Toast.LENGTH_SHORT).show();

            try {
                fileOutputStream = new FileOutputStream(outfile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(outfile));
                context.sendBroadcast(intent);

            } catch(FileNotFoundException e){
                e.printStackTrace();

            } catch(IOException e){
                e.printStackTrace();
            }
        });

    }

    private void showDeleteConfirmation(Context context, ListOfEmployeeModel model) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Delete");
        alertDialogBuilder.setMessage("Are you sure you want to delete this? \nYou cannot undo this action.");
        alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
            FirebaseFirestore.getInstance().collection("users").document(model.getEmployeeNum())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @SuppressLint("ResourceType")
                        @Override
                        public void onSuccess(Void unused) {
                            Toast toast = Toast.makeText(context, "Employee " + model.getFullName() + " deleted", Toast.LENGTH_SHORT);
                            toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                            toast.show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "Failed to delete employee details");
                        }
                    });
        });

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        alertDialogBuilder.create();
        alertDialogBuilder.show();
    }

    @SuppressLint("ResourceType")
    private DialogPlus createDialogPlus(Context context, int position, ListOfEmployeeModel model, MyViewHolder holder) {
        dialogPlus = DialogPlus.newDialog(context)
                .setContentHolder(new ViewHolder(R.layout.dialog_list_of_employee))
                .setExpanded(true, ViewGroup.LayoutParams.WRAP_CONTENT)
                .create();

        View view = dialogPlus.getHolderView();
        ImageView img = view.findViewById(R.id.qrcode_img);
        ImageView toolbarMoreOptions = view.findViewById(R.id.more_options_icon);
        LinearLayout callButton = view.findViewById(R.id.menu_call);
        LinearLayout smsButton = view.findViewById(R.id.menu_sms);
        LinearLayout emailButton = view.findViewById(R.id.menu_email);
        LinearLayout contactButton = view.findViewById(R.id.menu_contact);
        TextView fullNameTextView = view.findViewById(R.id.textView_show_fullname);
        TextView employeeNumberTextView = view.findViewById(R.id.textView_show_employee_number);
        TextView employeePositionTextView = view.findViewById(R.id.textView_show_position);
        TextView phoneNumberTextView = view.findViewById(R.id.textView_show_phone_number);
        TextView birthdayTextView = view.findViewById(R.id.textView_show_birthday);
        TextView emailTextView = view.findViewById(R.id.textView_show_email);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        retrieveQrCode(model.getFileNameOfQRCode(), img);
        fullNameTextView.setText(model.getFullName());
        employeeNumberTextView.setText(model.getEmployeeNum());
        employeePositionTextView.setText(model.getPosition());
        phoneNumberTextView.setText(model.getPhoneNum());
        birthdayTextView.setText(model.getBirthday());
        emailTextView.setText(model.getEmail());

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(view1 -> dialogPlus.dismiss());
        toolbarMoreOptions.setOnClickListener(view2 -> {
            PopupMenu popupMenu = new PopupMenu(context, view2);
            popupMenu.getMenuInflater().inflate(R.menu.menu_admin_list_of_employee_view, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(menuItem -> {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.menu_edit) {
                    showEditDialog(context, model, position);
                    return true;
                } else if (itemId == R.id.menu_delete) {
                    showDeleteConfirmation(context, model);
                    return true;
                }
                return false;
            });
            popupMenu.show();
        });


        callButton.setOnClickListener(view4 -> {
            if (ContextCompat.checkSelfPermission(view4.getContext(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions((Activity) view4.getContext(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        CALL_PERMISSION_REQUEST_CODE);
            } else {
                String phoneNumber = phoneNumberTextView.getText().toString();

                // Copy phone number to clipboard
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("phone number", phoneNumber);
                clipboard.setPrimaryClip(clip);

                // Create intent for dialing the phone number
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber));

                // Start the dialer activity
                view4.getContext().startActivity(dialIntent);
            }
        });

        smsButton.setOnClickListener(view5 -> {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                ActivityCompat.requestPermissions((Activity) view.getContext(),
                        new String[]{Manifest.permission.SEND_SMS},
                        SEND_PERMISSION_REQUEST_CODE);
            } else {
                String fullName = fullNameTextView.getText().toString();
                String phoneNumber = phoneNumberTextView.getText().toString();
                String message = "Hello, " + fullName;

                // Copy phone number to clipboard
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("phone number", phoneNumber);
                clipboard.setPrimaryClip(clip);

                // Send SMS
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("smsto:" + phoneNumber));
                intent.putExtra("sms_body", message);
                context.startActivity(intent);
            }
        });

        emailButton.setOnClickListener(view6 -> {
            String emailAddress = emailTextView.getText().toString();
            String fullName = fullNameTextView.getText().toString();

            // Copy the email address to the clipboard
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("email", emailAddress);
            clipboard.setPrimaryClip(clip);

            // Compose the email
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello " + fullName + ", ");

            PackageManager packageManager = view6.getContext().getPackageManager();
            ComponentName emailApp = emailIntent.resolveActivity(packageManager);

            if (emailApp != null) {
                view6.getContext().startActivity(emailIntent);
            } else {

                Toast toast = Toast.makeText(context,  "Unable to open Gmail app", Toast.LENGTH_SHORT);
                toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                toast.show();
            }
        });

        contactButton.setOnClickListener(view7 -> {
            String fullName = fullNameTextView.getText().toString();
            String phoneNumber = phoneNumberTextView.getText().toString();
            String emailAdd = emailTextView.getText().toString();

            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            intent.putExtra(ContactsContract.Intents.Insert.NAME, fullName);
            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
            intent.putExtra(ContactsContract.Intents.Insert.EMAIL, emailAdd);
            view7.getContext().startActivity(intent);
        });

        return dialogPlus;
    }

    public void showPopupMenu(TextInputEditText editText, TextInputLayout textInputLayoutPosition) {
        // Show a pop-up menu for selecting an item from a dropdown
        PopupMenu popupMenu = new PopupMenu(editText.getContext(), editText, Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.menu_drop_down_position, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            // Set the selected item text to the corresponding EditText
            String selectedItemText = menuItem.getTitle().toString();
            editText.setText(selectedItemText);
            textInputLayoutPosition.setEndIconDrawable(R.drawable.ic_arrow_drop_down);
            return true;
        });
        popupMenu.setOnDismissListener((menuItem) -> textInputLayoutPosition.setEndIconDrawable(R.drawable.ic_arrow_drop_down));
        textInputLayoutPosition.setEndIconDrawable(R.drawable.ic_arrow_drop_up);
        popupMenu.show();
    }

    public void showDatePickerDialog(TextInputEditText editText) {
        // Show a date picker dialog to select a date
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a DatePickerDialog
        @SuppressLint("SetTextI18n") DatePickerDialog datePickerDialog = new DatePickerDialog(editText.getContext(), (view, year1, monthOfYear, dayOfMonth) -> {
            // Set the selected date to the EditText
            editText.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year1);
            // Call the validateDOB() method to perform date validation
            validateDOB(year1, monthOfYear, dayOfMonth, editText);
        }, year, month, day);

        // Display the date picker dialog
        datePickerDialog.create();
        datePickerDialog.show();
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
    private void showInvalidDOBAlertDialog(TextInputEditText birthdayEditText, String message) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(context.getString(R.string.invalid_dob_title))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> showDatePickerDialog(birthdayEditText))
                .show();
    }

    private void validateDOB(int year, int month, int day, TextInputEditText birthdayEditText) {
        // Validate the selected date of birth to ensure it meets specific criteria
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);

        Calendar today = Calendar.getInstance();

        if (selectedDate.after(today)) {
            // Show an alert for an invalid future date of birth
            showInvalidDOBAlertDialog(birthdayEditText, context.getString(R.string.future_dob_message));
            birthdayEditText.requestFocus();
            birthdayEditText.setText(null);

        } else if (selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) && selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            // Show an alert for an invalid current date of birth
            showInvalidDOBAlertDialog(birthdayEditText, context.getString(R.string.current_dob_message));
            birthdayEditText.requestFocus();
            birthdayEditText.setText(null);

        } else if (calculateAge(selectedDate) < 15) {
            // Show an alert for an invalid age (below 15 years old)
            showInvalidDOBAlertDialog(birthdayEditText, context.getString(R.string.min_age_dob_message));
            birthdayEditText.requestFocus();
            birthdayEditText.setText(null);

        } else if (selectedDate.get(Calendar.YEAR) < (today.get(Calendar.YEAR) - 100)) {
            // Show an alert for an invalid date of birth too far in the past
            showInvalidDOBAlertDialog(birthdayEditText, context.getString(R.string.past_dob_message));
            birthdayEditText.requestFocus();
            birthdayEditText.setText(null);
        }
    }
    public boolean isValidEmployeeNumber(String employeeNumber) {
        // Check if the employee number is valid based on a specific format
        boolean isValid = false;
        // Split employee number by dash separator
        String[] parts = employeeNumber.split("-");
        // Employee number should have exactly two parts
        if (parts.length != 2) {
            return false;
        }
        String firstPart = parts[0];
        String secondPart = parts[1];
        if (firstPart.matches("\\d{4}") && secondPart.matches("\\d{1,4}")) {
            // First part should be 4 digits and second part should be between 1 and 4 digits
            isValid = true;
        }
        return isValid;
    }
    private boolean isValidPhilippinesPhoneNumber(String phoneNumber) {
        // Validate if the phone number follows the Philippines phone number format
        String regexPattern = "^09\\d{9}$";
        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }


    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    private void showEditDialog(Context context, ListOfEmployeeModel model, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View editView = LayoutInflater.from(context).inflate(R.layout.dialog_list_of_employee_update , null);
        builder.setCancelable(false);
        builder.setView(editView);

        TextInputEditText fullNameEditText = editView.findViewById(R.id.textFullname);
        TextInputEditText employeeNumberEditText = editView.findViewById(R.id.textEmployeeNumber);
        TextInputEditText employeePositionEditText = editView.findViewById(R.id.textPosition);
        TextInputLayout textInputLayoutPosition = editView.findViewById(R.id.textInputLayoutPosition);
        TextInputEditText phoneNumberEditText = editView.findViewById(R.id.textPhoneNumber);
        TextInputEditText birthdayEditText = editView.findViewById(R.id.textDateofBirth);
        TextInputLayout textInputLayoutBirthday = editView.findViewById(R.id.textInputLayoutBirthday);

        fullNameEditText.setText(model.getFullName());
        employeeNumberEditText.setText(model.getEmployeeNum());
        employeePositionEditText.setText(model.getPosition());
        phoneNumberEditText.setText(model.getPhoneNum());
        birthdayEditText.setText(model.getBirthday());

        textInputLayoutPosition.setEndIconOnClickListener(view -> showPopupMenu(employeePositionEditText, textInputLayoutPosition));
        employeePositionEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showPopupMenu(employeePositionEditText, textInputLayoutPosition);
            }
            return true;
        });

        textInputLayoutBirthday.setEndIconOnClickListener(v -> showDatePickerDialog(birthdayEditText));
        birthdayEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                showDatePickerDialog(birthdayEditText);
            }
            return true;
        });

        AlertDialog alertDialog = builder.create();

        // Positive Button (Save Changes Button)
        builder.setPositiveButton("Save Changes", (dialog, which) -> {
            String fullName = Objects.requireNonNull(fullNameEditText.getText()).toString();
            String employeeNumber = Objects.requireNonNull(employeeNumberEditText.getText()).toString();
            String employeePosition = Objects.requireNonNull(employeePositionEditText.getText()).toString();
            String phoneNumber = Objects.requireNonNull(phoneNumberEditText.getText()).toString();
            String birthday = Objects.requireNonNull(birthdayEditText.getText()).toString();

            if (fullName.isEmpty() && employeeNumber.isEmpty() && employeePosition.isEmpty()
                    && phoneNumber.isEmpty() && birthday.isEmpty()) {
                Toast toast = Toast.makeText(context, "All fields are required.", Toast.LENGTH_SHORT);
                toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                toast.show();
            }

            if (fullName.length() > 50) {
                fullNameEditText.requestFocus();
                Toast toast = Toast.makeText(context, "Fullname should be less than or equal to 50 characters", Toast.LENGTH_SHORT);
                toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                toast.show();
            } else if (!isValidEmployeeNumber(employeeNumber)) {
                employeeNumberEditText.requestFocus();
                Toast toast = Toast.makeText(context, "Please enter a valid Employee number", Toast.LENGTH_SHORT);
                toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                toast.show();
            } else if (!isValidPhilippinesPhoneNumber(phoneNumber)) {
                phoneNumberEditText.requestFocus();
                Toast toast = Toast.makeText(context, "Please enter a valid Phone number", Toast.LENGTH_SHORT);
                toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                toast.show();
            } else if (phoneNumber.length() != 11) {
                phoneNumberEditText.requestFocus();
                Toast toast = Toast.makeText(context, "Phone number should be equal to 11 digits", Toast.LENGTH_SHORT);
                toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                toast.show();
            } else {
                // All validation passed, proceed with updating the data
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("fullname", fullName);
                userMap.put("employee_number", employeeNumber);
                userMap.put("employee_position", employeePosition);
                userMap.put("phone_number", phoneNumber);
                userMap.put("birthday", birthday);

                FirebaseFirestore.getInstance().collection("users").document(employeeNumber)
                        .update(userMap)
                        .addOnSuccessListener(aVoid -> {
                            Toast toast = Toast.makeText(context, "Data Updated Successfully", Toast.LENGTH_SHORT);
                            toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                            toast.show();
                            // Dismiss the dialogPlus using the class member variable
                            if (dialogPlus != null) {
                                dialogPlus.dismiss();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast toast = Toast.makeText(context, "Error While Updating", Toast.LENGTH_SHORT);
                            toast.getView().setBackgroundResource(R.style.BaseToastMessage);
                            toast.show();
                            // Dismiss the dialogPlus using the class member variable
                            if (dialogPlus != null) {
                                dialogPlus.dismiss();
                            }
                        });

//                // Retrieve existing payrollData and include it in the update
//                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
//                        .child(Objects.requireNonNull(getRef(position).getKey()));
////                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@org.checkerframework.checker.nullness.qual.NonNull DataSnapshot dataSnapshot) {
////                        if (dataSnapshot.exists()) {
////                            Object payrollData = dataSnapshot.child("payrollData").getValue();
////                            if (payrollData != null) {
////                                userMap.put("payrollData", payrollData);
////                            }
////                        }
//
//                         Update user details in the Firebase Realtime Database
//
//
//
//                        userRef.updateChildren(userMap)
//                                .addOnSuccessListener(aVoid -> {
//                                    StyleableToast.makeText(context, "Data Updated Successfully", R.style.ToastMessageLong).show();
//                                    // Dismiss the dialogPlus using the class member variable
//                                    if (dialogPlus != null) {
//                                        dialogPlus.dismiss();
//                                    }
//                                })
//                                .addOnFailureListener(e -> {
//                                    StyleableToast.makeText(context, "Error While Updating", R.style.ToastMessageLong).show();
//                                    // Dismiss the dialogPlus using the class member variable
//                                    if (dialogPlus != null) {
//                                        dialogPlus.dismiss();
//                                    }
//                                });
//                    }
//
//            @Override
//            public void onCancelled(@org.checkerframework.checker.nullness.qual.NonNull DatabaseError databaseError) {
//                StyleableToast.makeText(context, "Error Retrieving Data", R.style.ToastMessageLong).show();
//                alertDialog.dismiss();
//
//            }
            }
        });

                // Negative Button (Cancel Button)
                builder.setNeutralButton("Cancel", (dialog1, which1) -> alertDialog.dismiss());
                builder.show();
    }


    private void retrieveQrCode(String imageURL, ImageView qrCodeImageView) {
//        StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/employeesQRCODE/"+ fileNameQRCODE);

//        try {
//            File localFile = File.createTempFile("tempfile", "jpg");
//            storageReference.getFile(localFile)
//                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//
//                             bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                            qrCodeImageView.setImageBitmap(bitmap);
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d("TAG", "Failed to retrieve Profile Picture: " + e.getCause());
//
//                        }
//                    });
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        Glide.with(context).load(imageURL).into(qrCodeImageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fullNameTV,
                employeeNumTV,
                positionTV,
                phoneNumTV,
                birthdayTV,
                emailTV;
        ImageView qrCodeImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullNameTV = itemView.findViewById(R.id.fullName_TextView);
            employeeNumTV = itemView.findViewById(R.id.employeeNumber_TextView);
            positionTV = itemView.findViewById(R.id.position_TextView);
            phoneNumTV = itemView.findViewById(R.id.phoneNumber_TextView);
            birthdayTV = itemView.findViewById(R.id.birthday_TextView);
            emailTV = itemView.findViewById(R.id.email_TextView);
            qrCodeImageView = itemView.findViewById(R.id.qrCode_ImageView);
        }
    }
}
