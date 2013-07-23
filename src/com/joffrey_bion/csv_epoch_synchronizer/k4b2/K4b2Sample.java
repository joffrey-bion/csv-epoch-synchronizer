package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import java.text.ParseException;

import com.joffrey_bion.utils.dates.DurationHelper;

public class K4b2Sample {

    public static final int COL_VO2 = 13;
    public static final int COL_VCO2 = 14;
    public static final int COL_VO2KG = 19;
    public static final int COL_R = 20;
    public static final int COL_METS = 107;

    private static final int COL_TIME = 9;
    private static final int COL_MARK = 29;

    private static final String MARK = "mark";
    private static final String TIME_FORMAT = "HH:mm:ss";

    public String[] cols;
    public long startTime;
    public long endTime;
    public double duration;

    public K4b2Sample(String[] cols, long previousTime) throws ParseException {
        if (cols == null) {
            throw new IllegalArgumentException("The given row cannot be null");
        }
        this.cols = cols;
        startTime = previousTime;
        endTime = DurationHelper.strToMillis(cols[COL_TIME], TIME_FORMAT);
        duration = endTime - startTime;
    }

    /**
     * Returns whether there is a marker on this row.
     * 
     * @return whether there is a marker on this row.
     */
    public boolean isMarked() {
        return cols[COL_MARK].equals(MARK);
    }

    /**
     * Returns the value in this row at the specified column.
     * @param column The index of the column.
     * @return the value in this row at the given column.
     */
    public double getValue(int column) {
        return Double.parseDouble(cols[column]);
    }
}
