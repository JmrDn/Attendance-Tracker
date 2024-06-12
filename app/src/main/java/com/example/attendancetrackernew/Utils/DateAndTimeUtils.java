package com.example.attendancetrackernew.Utils;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
@RequiresApi(api = Build.VERSION_CODES.O)
public class DateAndTimeUtils {
    public static String getTimeWithAMAndPM(){
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }

    public static String convertTimeToAMAndPM(String time){
        String newDate = "";
        DateFormat outputDateFormat  = new SimpleDateFormat("HH:mm");

        try {
            Date date1 = outputDateFormat.parse(time);
            DateFormat inputDateFormat = new SimpleDateFormat("h:mm a");
            newDate = inputDateFormat.format(date1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return  newDate;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String calculateMinutesAgo(LocalDateTime yourDate) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(yourDate, now);

        long minutes = duration.toMinutes();
        long hours = minutes / 60;
        long day = hours / 24;
        long week = day / 7;


        if (minutes == 0) {
            return "just now";
        } else if (minutes == 1) {
            return "1 minute ago";
        } else if (minutes < 60) {
            return minutes + " minutes ago";
        } else if (minutes < 120) {
            return "1 hour ago";
        } else if (hours > 1 && hours < 25) {
            return hours + " hours ago";
        }
        else if (day == 1){
            return "1 day ago";
        }
        else if (day > 1 && day <= 7){
            return day + " days ago";
        }
        else if (week == 1){
            return "1 week ago";
        }
        else{
            return  week + "weeks ago";
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static LocalDateTime parseDateString(String dateString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateString, formatter);
    }
    public static String convertAMAndPMFormatInto24HrsFormat(String time12Hour){

        String formattedTime = "";
        // Create a SimpleDateFormat for parsing 12-hour time
        SimpleDateFormat inputFormat = new SimpleDateFormat("h:mm a");

        try {
            // Parse the 12-hour time string to Date
            Date date = inputFormat.parse(time12Hour);

            // Create a SimpleDateFormat for formatting to 24-hour time
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm");

            // Format the Date to 24-hour format
            formattedTime = outputFormat.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formattedTime;
    }

    public static String parseDateToDateId(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        return date.format(formatter);
    }


    public static String getMinutes(String start, String end){

        int startHour = Integer.parseInt(start.substring(0, 2));
        int startMinute = Integer.parseInt(start.substring(3,5));

        int endHour = Integer.parseInt(end.substring(0, 2));
        int endMinute = Integer.parseInt(end.substring(3,5));

        // Define the two times
        LocalTime startTime = LocalTime.of(startHour, startMinute);
        LocalTime endTime = LocalTime.of(endHour, endMinute);

        // Calculate the minutes between the two times
        long minutesBetween = ChronoUnit.MINUTES.between(startTime, endTime);

        // Ensure the result is positive (considering it's a time span)
        minutesBetween = Math.abs(minutesBetween);

        return String.valueOf(minutesBetween);
    }

    public static String getDate(){
        return new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
    }
    public static String getDateWithWordFormat(){
        return new SimpleDateFormat("MMM dd yyyy", Locale.getDefault()).format(new Date());
    }

    public static String getDateIdFormat(){
        return new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
    }
    public static String getTime24HrsFormat(){
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }
    public static String getTimeId(){
        return new SimpleDateFormat("HHmmss", Locale.getDefault()).format(new Date());
    }

    public static String getMonth(String date){
        String month = "";
        if(date.charAt(2) == '0' && date.charAt(3) == '1')
            month = "january";
        else if (date.charAt(2) == '0' && date.charAt(3) == '2')
            month = "february";
        else if (date.charAt(2) == '0' && date.charAt(3) == '3')
            month = "march";
        else if (date.charAt(2) == '0' && date.charAt(3) == '4')
            month = "april";
        else if (date.charAt(2) == '0' && date.charAt(3) == '5')
            month = "may";
        else if (date.charAt(2) == '0' && date.charAt(3) == '6')
            month = "june";
        else if (date.charAt(2) == '0' && date.charAt(3) == '7')
            month = "july";
        else if (date.charAt(2) == '0' && date.charAt(3) == '8')
            month = "august";
        else if (date.charAt(2) == '0' && date.charAt(3) == '9')
            month = "september";
        else if (date.charAt(2) == '1' && date.charAt(3) == '0')
            month = "october";
        else if (date.charAt(2) == '1' && date.charAt(3) == '1')
            month = "november";
        else if (date.charAt(2) == '1' && date.charAt(3) == '2')
            month = "december";

        return  month;
    }

    public static String getPreviousMonthDateId(String date) {
        String monthId = date.substring(2, 4);
        int monthIdInt = Integer.parseInt(monthId) - 1;

        // Adjust monthIdInt to handle January (0) and format it to two digits
        if (monthIdInt < 1) {
            monthIdInt = 12;
        }
        String formattedMonthId = String.format("%02d", monthIdInt);

        // Replace the month part in the date string
        String replacedDate = date.substring(0, 2) + formattedMonthId + date.substring(4);

        return replacedDate;
    }

    public static String getNextMonthDateId(String date) {
        String monthId = date.substring(2, 4);
        int monthIdInt = Integer.parseInt(monthId) + 1;

        // Adjust monthIdInt to handle January (0) and format it to two digits
        if (monthIdInt < 1) {
            monthIdInt = 12;
        }
        String formattedMonthId = String.format("%02d", monthIdInt);

        // Replace the month part in the date string
        String replacedDate = date.substring(0, 2) + formattedMonthId + date.substring(4);

        return replacedDate;
    }
    public static String getDay (String date){
        return String.valueOf(date.charAt(0)) + String.valueOf(date.charAt(1));
    }

    public static String getYear(String date){

        String year = String.valueOf(date.charAt(4)) +
                String.valueOf(date.charAt(5))  +
                String.valueOf(date.charAt(6))  +
                String.valueOf(date.charAt(7));

        return year;

    }

    public static String getYear1(String date){
        String year = String.valueOf(date.charAt(6)) +
                String.valueOf(date.charAt(7))  +
                String.valueOf(date.charAt(8))  +
                String.valueOf(date.charAt(9));

        return year;
    }

    public static int getMonthNum(String date){


        if (date.charAt(3) == '0' && date.charAt(4) == '1')
            return 1;
        else if (date.charAt(3) == '0'&& date.charAt(4) == '2')
            return 2;
        else if (date.charAt(3) == '0' && date.charAt(4) == '3')
            return 3;
        else if (date.charAt(3) == '0' && date.charAt(4) == '4')
            return 4;
        else if (date.charAt(3) == '0' && date.charAt(4) == '5')
            return 5;
        else if (date.charAt(3) == '0' && date.charAt(4) == '6')
            return 6;
        else if (date.charAt(3) == '0' && date.charAt(4) == '7')
            return 7;
        else if (date.charAt(3) == '0' && date.charAt(4) == '8')
            return 8;
        else if (date.charAt(3) == '0' && date.charAt(4) == '9')
            return 9;
        else if (date.charAt(3) == '1' && date.charAt(4) == '0')
            return 10;
        else if (date.charAt(3) == '1' && date.charAt(4) == '1')
            return 11;
        else
            return 12;
    }

    public static String getTimeForLineChart(String time){
        String TIME = "";

        if (time.charAt(0) == '0' && time.charAt(1) == '0')
            TIME = "0";
        else if (time.charAt(0) == '0' && time.charAt(1) == '1')
            TIME = "1";
        else if (time.charAt(0) == '0' && time.charAt(1) == '2')
            TIME = "2";
        else if (time.charAt(0) == '0' && time.charAt(1) == '3')
            TIME = "3";
        else if (time.charAt(0) == '0' && time.charAt(1) == '4')
            TIME = "4";
        else if (time.charAt(0) == '0' && time.charAt(1) == '5')
            TIME = "5";
        else if (time.charAt(0) == '0' && time.charAt(1) == '6')
            TIME = "6";
        else if (time.charAt(0) == '0' && time.charAt(1) == '7')
            TIME = "7";
        else if (time.charAt(0) == '0' && time.charAt(1) == '8')
            TIME = "8";
        else if (time.charAt(0) == '0' && time.charAt(1) == '9')
            TIME = "9";
        else if (time.charAt(0) == '1' && time.charAt(1) == '0')
            TIME = "10";
        else if (time.charAt(0) == '1' && time.charAt(1) == '1')
            TIME = "11";
        else if (time.charAt(0) == '1' && time.charAt(1) == '2')
            TIME = "12";
        else if (time.charAt(0) == '1' && time.charAt(1) == '3')
            TIME = "13";
        else if (time.charAt(0) == '1' && time.charAt(1) == '4')
            TIME = "14";
        else if (time.charAt(0) == '1' && time.charAt(1) == '5')
            TIME = "15";
        else if (time.charAt(0) == '1' && time.charAt(1) == '6')
            TIME = "16";
        else if (time.charAt(0) == '1' && time.charAt(1) == '7')
            TIME = "17";
        else if (time.charAt(0) == '1' && time.charAt(1) == '8')
            TIME = "18";
        else if (time.charAt(0) == '1' && time.charAt(1) == '9')
            TIME = "19";
        else if (time.charAt(0) == '2' && time.charAt(1) == '0')
            TIME = "20";
        else if (time.charAt(0) == '2' && time.charAt(1) == '1')
            TIME = "21";
        else if (time.charAt(0) == '2' && time.charAt(1) == '2')
            TIME = "22";
        else if (time.charAt(0) == '2' && time.charAt(1) == '3')
            TIME = "23";
        else if (time.charAt(0) == '2' && time.charAt(1) == '4')
            TIME = "24";

        return  TIME;

    }
    public static String getDateWordFormat (){return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(new Date());}
    public static String getSemiMonthId(){
        String dateId = getDateIdFormat();
        String dayString0 = String.valueOf(dateId.charAt(0));
        String dayString1 = String.valueOf(dateId.charAt(1));
        String dayString = dayString0 + dayString1;
        int day = Integer.parseInt(dayString);

        String semiMonthName = "";
        String year = DateAndTimeUtils.getYear(dateId);


        if (day > 25 && day < 32){

            String month = DateAndTimeUtils.getMonth(dateId);

            return month + "11To25_" + year;

        }

        else if (day > 0 && day < 11){
            String month = DateAndTimeUtils.getPreviousMonthDateId(dateId);

            return month + "11To25_" + year;

        }
        else if (day > 10 && day < 26){

            String firstMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getPreviousMonthDateId(dateId));
            String secondMonth = DateAndTimeUtils.getMonth(dateId);

            return firstMonth + "26To" + secondMonth + "10_" + year;


        } else {
            return  semiMonthName;

        }
    }
    public static String getPeriod(){
        String dateId = getDateIdFormat();
        String dayString0 = String.valueOf(dateId.charAt(0));
        String dayString1 = String.valueOf(dateId.charAt(1));
        String dayString = dayString0 + dayString1;
        int day = Integer.parseInt(dayString);

//        String month = getMonth(dateToday).substring(0,1).toUpperCase() + getMonth(dateToday).substring(1);
//
//        if (day > 10 && day <=25){
//
//            return month + " 11-25, " + getYear(dateToday);
//        }
//        else{
//            return getPreviousMonth() + " 26 - " + month + " 11, 2024";
//        }

        String semiMonthName = "";
        String year = DateAndTimeUtils.getYear(dateId);
        String payslipDate;

        if (day > 25 && day < 32){

            String month = DateAndTimeUtils.getMonth(dateId);

            month = month.substring(0,1).toUpperCase() + month.substring(1);
            payslipDate = month + " 11 to 25 " + year;

            return  payslipDate;



        }

        else if (day > 0 && day < 11){
            String month = DateAndTimeUtils.getMonth(DateAndTimeUtils.getPreviousMonthDateId(dateId));

            payslipDate = month + " 11 to 25 " + year;
            return  payslipDate;
        }
        else if (day > 10 && day < 26){

            String firstMonth = DateAndTimeUtils.getMonth(DateAndTimeUtils.getPreviousMonthDateId(dateId));
            String secondMonth = DateAndTimeUtils.getMonth(dateId);

            String upperCaseFirstLetterOfFirstMonth = firstMonth.substring(0,1).toUpperCase() + firstMonth.substring(1);
            String upperCaseFirstLetterOfSecondMonth = secondMonth.substring(0,1).toUpperCase() + secondMonth.substring(1);

            payslipDate = upperCaseFirstLetterOfFirstMonth + " 26 to " + upperCaseFirstLetterOfSecondMonth + " 10 " + year;

            return  payslipDate;
        } else {
            payslipDate = "";
            return payslipDate;
        }
    }

    public static String convertToDateWordFormat(String date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);

        // Format the date to another format if needed
        return localDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));


    }

    public static String convertToDateWordFormatWithDayName(String date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);

        // Format the date to another format if needed
        return localDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy"));


    }

    public static String getPreviousMonth(){
        LocalDate currentDate = LocalDate.now();

        // Get the next month
        LocalDate previousMonth = currentDate.minusMonths(1);

        // Get the name of the next month
        String previousMonthName = previousMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

        return  previousMonthName;
    }


    public static String getNextMonth(){
        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Get the next month
        LocalDate nextMonth = currentDate.plusMonths(1);

        // Get the name of the next month
        String nextMonthName = nextMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

      return  nextMonthName;
    }
    public static LocalDate getLocalDate(String dateString){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            // Parse the date string into a LocalDate object
            LocalDate localDate = LocalDate.parse(dateString, formatter);

            return localDate;

        } catch (Exception e) {
            // Handle parsing errors
            return  null;
        }
    }
}
