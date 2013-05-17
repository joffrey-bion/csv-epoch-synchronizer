package com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Csv {

    private static final String CSV_EXTENSION = ".csv";
    protected static final String CSV_COL_SEP = ",";

    public Csv(String filename) {
        if (!filename.endsWith(CSV_EXTENSION)) {
            throw new RuntimeException("'" + filename + "' is not a CSV file.");
        }
    }
    
    public static String removeCsvExtension(String filename) {
        return filename.substring(0, filename.length() - CSV_EXTENSION.length());
    }

    public static long timestampStrToNanos(String timestamp, String formatPattern) throws IOException {
        DateFormat df = new SimpleDateFormat(formatPattern);
        Date date;
        try {
            date = df.parse(timestamp);
        } catch (ParseException e) {
            throw new IOException("Cannot parse timestamp '" + timestamp + "', expected format: " + formatPattern);
        }
        long nanos = date.getTime() * 1000000;
        return nanos;
    }

    public static void printCsvRow(String[] cols) {
        for (String str : cols) {
            System.out.print(str);
            if (str != cols[cols.length - 1]) {
                System.out.print(CSV_COL_SEP + " ");
            }
        }
        System.out.println();
    }

}
