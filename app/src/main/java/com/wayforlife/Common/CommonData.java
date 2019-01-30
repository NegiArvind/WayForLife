package com.wayforlife.Common;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CommonData {
    public static final int LOCATION_PERMISSION_REQUEST_CODE=99;
    public static final int PICK_IMAGE_REQUEST=71;
    public static String firebaseCurrentUserUid;
    public static String firstPatternWord="~@$&";
    public static String secondPatternWord="&$@~";
    public static boolean isAdmin=false;

    public static String getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm a");
        return mdformat.format(calendar.getTime());
    }

    public static String getTodayDate(){
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("dd/MM/yyyy");
        return mdformat.format(calendar.getTime());
    }
}
