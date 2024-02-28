package com.example.attendancetrackernew.Admin;

import android.content.Context;
import android.content.SharedPreferences;

public class AdminSharedPreferences {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public AdminSharedPreferences(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences("ADMIN_USER_DETAILS", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setEmail(String email){
        editor.putString("email", email);
        editor.apply();
    }

    public String getEmail(){
        return  sharedPreferences.getString("email", null);
    }

    public void setFullName(String fullName){
        editor.putString("fullName", fullName);
        editor.apply();
    }

    public String getFullName(){
        return  sharedPreferences.getString("fullName", null);
    }
    public void logout(){
        editor.clear();
        editor.commit();
        editor.apply();
    }

}
