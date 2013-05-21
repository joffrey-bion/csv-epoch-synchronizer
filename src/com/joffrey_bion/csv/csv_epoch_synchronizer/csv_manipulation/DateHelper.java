package com.joffrey_bion.csv.csv_epoch_synchronizer.csv_manipulation;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateHelper {

    private static final String DATE = "yyyy-MM-dd";
    private static final String TIME = "HH:mm:ss";
    private static final String MILLIS = ".SSS";
    private static final String DATE_TIME_SEP = " ";

    public static String toDate(long timestamp) {
        return format(timestamp, DATE);
    }

    public static String toTime(long timestamp) {
        return format(timestamp, TIME);
    }

    public static String toTimeMillis(long timestamp) {
        return format(timestamp, TIME + MILLIS);
    }

    public static String toDateTime(long timestamp) {
        return format(timestamp, DATE + DATE_TIME_SEP + TIME);
    }

    public static String toDateTimeMillis(long timestamp) {
        return format(timestamp, DATE + DATE_TIME_SEP + TIME + MILLIS);
    }

    public static String toFileNameDateTime(long timestamp) {
        return format(timestamp, "yyyy.MM.dd-HHmmss");
    }

    private static String format(long timestamp, String pattern) {
        return new SimpleDateFormat(pattern, Locale.US).format(new Date(timestamp));
    }

    public static void displayTimestamp(String name, long timestampNanos) {
        System.out.println(name + ": " + toDateTimeMillis(timestampNanos / 1000000));
    }
}
