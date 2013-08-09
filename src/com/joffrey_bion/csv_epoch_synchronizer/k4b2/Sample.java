package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import java.text.ParseException;

import com.joffrey_bion.utils.dates.DurationHelper;

/**
 * Contains the statistics on the breathing of the user over a short period of time.
 * These values are contained in one line in the K4b2 file.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class Sample {

    /** The index of the VO2 column. */
    public static final int COL_VO2 = 13;
    /** The index of the VCO2 column. */
    public static final int COL_VCO2 = 14;
    /** The index of the VO2KG column. */
    public static final int COL_VO2KG = 19;
    /** The index of the R column. */
    public static final int COL_R = 20;
    /** The index of the METS column. */
    public static final int COL_METS = 107;

    private static final int COL_TIME = 9;
    private static final int COL_MARK = 29;

    private static final String MARK = "mark";
    private static final String TIME_FORMAT = "HH:mm:ss";

    /** The original row. */
    public String[] cols;
    /**
     * The start time of this {@code Sample}, corresponding to the previous row's
     * timestamp.
     */
    public long startTime;
    /** The end time of this {@code Sample}, corresponding to this row's timestamp. */
    public long endTime;
    /** The duration of this {@code Sample}, in milliseconds. */
    public double duration;

    /**
     * Creates a new {@code Sample} object for the values in the specified row.
     * 
     * @param cols
     *            The row to wrap in a {@code Sample} object.
     * @param previousTime
     *            The timestamp of the previous row (in milliseconds), to be able to know the duration
     *            of this {@code Sample}.
     * @throws ParseException
     *             If any parse error occurs while reading the specified row.
     */
    public Sample(String[] cols, long previousTime) throws ParseException {
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
     * 
     * @param column
     *            The index of the column.
     * @return the value in this row at the given column.
     */
    public double getValue(int column) {
        return Double.parseDouble(cols[column]);
    }
}
