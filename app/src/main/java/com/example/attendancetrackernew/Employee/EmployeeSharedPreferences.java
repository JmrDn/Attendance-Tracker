package com.example.attendancetrackernew.Employee;

import android.content.Context;
import android.content.SharedPreferences;

public class EmployeeSharedPreferences {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public EmployeeSharedPreferences(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("employeeDetails", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

    }
    public void setImageURL(String imageURL){
        editor.putString("imageURL", imageURL);
        editor.apply();
    }
    public String getImageURL(){
        return sharedPreferences.getString("imageURL", null);
    }

    public void setFullName(String fullName){
        editor.putString("fullName", fullName);
        editor.apply();
    }

    public String getFullName(){
        return sharedPreferences.getString("fullName", null);
    }
    public void setEmail(String email){
        editor.putString("email", email);
        editor.apply();
    }


    public String getEmail(){
        return sharedPreferences.getString("email", null);
    }

    public void setBirthday(String birthday){
        editor.putString("birthday", birthday);
        editor.apply();
    }


    public String getBirthday(){
        return sharedPreferences.getString("birthday", null);
    }

    public void setEmployeeNumber(String employeeNumber){
        editor.putString("employeeNumber", employeeNumber);
        editor.apply();
    }


    public String getEmployeeNumber(){
        return sharedPreferences.getString("employeeNumber", null);
    }

    public void setPosition(String position){
        editor.putString("position", position);
        editor.apply();
    }


    public String getPosition(){
        return sharedPreferences.getString("position", null);
    }

    public void setPhoneNumber(String phoneNumber){
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
    }


    public String getPhoneNumber(){
        return sharedPreferences.getString("phoneNumber", null);
    }

    public void logout(){
        editor.clear();
        editor.apply();
        editor.commit();
    }



}
