package com.ipid.demo.utils;

import static com.ipid.demo.constants.Constants.EEEdMMMyyyy;
import static com.ipid.demo.constants.Constants.EEEdMMMyyyyHHmm;
import static com.ipid.demo.constants.Constants.YYYYMMDDHHmmss;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    public static String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat(YYYYMMDDHHmmss);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getFormattedDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(EEEdMMMyyyy);
        return dateFormat.format(Date.parse(date));
    }

    public static String getFormattedDateTime(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(EEEdMMMyyyyHHmm);
        return dateFormat.format(Date.parse(date));
    }
}