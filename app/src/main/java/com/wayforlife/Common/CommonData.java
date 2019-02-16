package com.wayforlife.Common;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonData {
    public static final int LOCATION_PERMISSION_REQUEST_CODE=99;
    public static final int PICK_IMAGE_REQUEST=71;
    public static final int USER_PICK_IMAGE_REQUEST=81;
    public static String firebaseCurrentUserUid;
    public static String firstPatternWord="~@$&";
    public static String secondPatternWord="&$@~";
    public static boolean isAdmin=false;
    public static final String[] months={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    public static String donateUrl="https://milaap.org/fundraisers/wayforlife?utm_source=shorturl";
    public static String requestBloodUrl="https://google.com";

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm a");
        return mdformat.format(calendar.getTime());
    }

    public static String getTodayDate(){
        Calendar calendar=Calendar.getInstance();
//        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        Date date=calendar.getTime();
        String day= (String) DateFormat.format("dd",date);
        String monthString=(String) DateFormat.format("MMM",date);
        String year=(String) DateFormat.format("yyyy",date);
        return day+" "+monthString+" "+year;
    }

}
