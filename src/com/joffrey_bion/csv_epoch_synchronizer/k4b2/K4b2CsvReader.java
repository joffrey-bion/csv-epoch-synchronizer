package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import java.io.EOFException;
import java.io.IOException;
import java.text.ParseException;

import com.joffrey_bion.csv.TimestampedCsvReader;
import com.joffrey_bion.utils.dates.DateHelper;

public class K4b2CsvReader extends TimestampedCsvReader {

    private static final int NB_HEADER_LINES = 3;

    public static final int COL_TIME = 9;
    public static final int COL_VO2 = 13;
    public static final int COL_VCO2 = 14;
    public static final int COL_VO2KG = 19;
    public static final int COL_R = 20;
    public static final int COL_MARK = 29;
    public static final int COL_METS = 107;

    private static final String MARK = "mark";

    private static final String DATE_FORMAT = "M/d/yyyy";
    private static final String TIME_FORMAT = "hh:mm:ss";

    private long previousTime;

    public K4b2CsvReader(String filename) throws IOException {
        super(filename);
        super.readRow();
    }

    @Override
    public long extractTimestamp(String[] line) throws IOException {
        String timestamp = line[COL_TIME]; // TODO figure something out
        String format = DATE_FORMAT + " " + TIME_FORMAT;
        try {
            return DateHelper.timestampStrToNanos(timestamp, format);
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void skipHeaders() throws IOException {
        for (int i = 0; i < NB_HEADER_LINES; i++) {
            super.readRow();
        }
    }

    @Override
    public String[] readRow() throws IOException {
        previousTime = getTimeMillis();
        return super.readRow();
    }

    /**
     * Skips the next {@code nbToSkip} markers (including the current one if the
     * current row is marked) and goes to the next marked row.
     * 
     * @param nbToSkip
     * @return The next marked row after the {@code nbToSkip} skipped markers.
     * @throws IOException
     */
    public String[] skipMarkers(int nbToSkip) throws IOException {
        if (!currentRow[COL_MARK].equals(MARK)) {
            goToNextMarker();
        }
        for (int i = 0; i < nbToSkip; i++) {
            currentRow = goToNextMarker();
        }
        return currentRow;
    }

    /**
     * Reads as many rows as necessary to reach the next row with a marker. If the
     * current row is marked, it is not taken into account and the next row with a
     * marker is returned.
     * 
     * @return The next marked row.
     * @throws IOException
     */
    public String[] goToNextMarker() throws IOException {
        int count = 0;
        while (readRow() != null) {
            count++;
            if (currentRow[COL_MARK].equals(MARK)) {
                return currentRow;
            }
        }
        throw new EOFException(
                "The end of file has been reached while looking for the next marker (" + count
                        + " rows read).");
    }

    public long getTimeMillis() {
        if (currentRow == null) {
            return 0;
        }
        return getTimeMillis(currentRow);
    }

    public long getCurrentEpochLength() {
        return getTimeMillis(currentRow) - previousTime;
    }

    public static long getTimeMillis(String[] line) {
        String[] hhmmss = line[COL_TIME].split(":");
        long h = Long.parseLong(hhmmss[0]);
        long m = Long.parseLong(hhmmss[1]);
        long s = Long.parseLong(hhmmss[2]);
        return (((h * 60) + m) * 60 + s) * 1000;
    }

    public static boolean isMarked(String[] line) {
        return line[COL_MARK].equals(MARK);
    }

    public static double getNumber(int column, String[] line) {
        return Double.parseDouble(line[column]);
    }

    public static double getVO2(String[] line) {
        return getNumber(COL_VO2, line);
    }

    public static double getVCO2(String[] line) {
        return getNumber(COL_VCO2, line);
    }

    public static double getR(String[] line) {
        return getNumber(COL_R, line);
    }

    public static double getVO2kg(String[] line) {
        return getNumber(COL_VO2KG, line);
    }

}
