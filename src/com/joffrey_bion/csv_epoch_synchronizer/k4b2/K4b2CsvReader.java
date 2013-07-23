package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import java.io.EOFException;
import java.io.IOException;
import java.text.ParseException;

import com.joffrey_bion.csv.CsvReader;

public class K4b2CsvReader extends CsvReader {

    private static final int NB_HEADER_LINES = 3;

    private K4b2Sample lastSample;

    public K4b2CsvReader(String filename) throws IOException {
        super(filename);
    }

    public void skipHeaders() throws IOException {
        for (int i = 0; i < NB_HEADER_LINES; i++) {
            super.readRow();
        }
    }

    public K4b2Sample readK4b2Sample() throws IOException {
        String[] row = readRow();
        if (row == null) {
            return null;
        }
        try {
            long previousTime = lastSample != null ? lastSample.endTime : 0;
            lastSample = new K4b2Sample(row, previousTime);
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
        return lastSample;
    }

    /**
     * Skips the next {@code nbToSkip} markers (including the current one if the
     * current row is marked) and goes to the next marked row.
     * 
     * @param nbToSkip
     * @return The next marked row after the {@code nbToSkip} skipped markers.
     * @throws IOException
     */
    public K4b2Sample skipMarkers(int nbToSkip) throws IOException {
        if (lastSample == null || !lastSample.isMarked()) {
            goToNextMarker();
        }
        // at this point the current row is marked
        for (int i = 0; i < nbToSkip; i++) {
            goToNextMarker();
        }
        return lastSample;
    }

    /**
     * Reads as many rows as necessary to reach the next row with a marker. If the
     * current row is marked, it is not taken into account and the next row with a
     * marker is returned. Postcondition: {@link #isMarked()} will return true.
     * 
     * @return The next marked sample.
     * @throws IOException
     */
    public K4b2Sample goToNextMarker() throws IOException {
        int count = 0;
        while (readK4b2Sample() != null) {
            count++;
            if (lastSample.isMarked()) {
                return lastSample;
            }
        }
        throw new EOFException(
                "The end of file has been reached while looking for the next marker (" + count
                        + " rows read).");
    }
}
