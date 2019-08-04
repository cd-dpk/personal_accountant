package com.dpk.pa.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class TimeHandler {


    public static String now(){
        String now="";
        Calendar localCalendar = Calendar.getInstance();
        Date date = localCalendar.getTime();
        Timestamp timestamp = new Timestamp(date.getTime());
        return timestamp.toString();
    }
}
