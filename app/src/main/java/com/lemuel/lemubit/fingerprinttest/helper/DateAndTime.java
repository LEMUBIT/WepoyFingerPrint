package com.lemuel.lemubit.fingerprinttest.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateAndTime {
    public static String getCurrentTime()
    {
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        return currentTime.format(new Date());
    }

    public static String getCurrentDate()
    {
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM/yyyy");
      return currentDate.format(new Date());
    }
}
