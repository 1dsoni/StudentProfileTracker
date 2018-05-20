package com.projects.diwanshusoni.studentprofiletracker.helpers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Diwanshu Soni on 19-09-2017.
 */

public class TimeFormat {
    private static DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    public static String correctTime(long milliSeconds){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return ""+ (formatter.format(calendar.getTime()));
    }
}
