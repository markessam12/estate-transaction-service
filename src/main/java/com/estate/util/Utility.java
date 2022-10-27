package com.estate.util;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A static class that provides utilities.
 */
public class Utility {
    private Utility(){}

    /**
     * Gets the current date in string format.
     *
     * @return the date in string type
     */
    public static @NotNull String getCurrentDate(){
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return dateFormat.format(date);
    }
}
