package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import java.io.EOFException;
import java.io.IOException;
import java.text.ParseException;

import com.joffrey_bion.csv.CsvReader;

public class K4b2CsvReader extends CsvReader {

    private static final int NB_HEADER_LINES = 3;

    private K4b2Sample lastSampleRead;

    public K4b2CsvReader(String filename) throws IOException {
        super(filename);
    }

    /**
     * Skips the headers of the CSV file, so that the next row to read is a data row.
     * 
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public void skipHeaders() throws IOException {
        for (int i = 0; i < NB_HEADER_LINES; i++) {
            super.readRow();
        }
    }

    /**
     * Reads a row as a {@link K4b2Sample} object.
     * 
     * @return A {@link K4b2Sample} object representing the CSV row.
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public K4b2Sample readK4b2Sample() throws IOException {
        String[] row = readRow();
        if (row == null) {
            return null;
        }
        try {
            long previousTime = lastSampleRead != null ? lastSampleRead.endTime : 0;
            lastSampleRead = new K4b2Sample(row, previousTime);
        } catch (ParseException e) {
            throw new IOException(e.getMessage());
        }
        return lastSampleRead;
    }

    /**
     * Skips the next {@code nbToSkip} markers (including the current one if the
     * current row is marked) and goes to the next marked row.
     * 
     * @param nbToSkip
     *            The number of markers that have to be skipped.
     * @return The next marked row after the {@code nbToSkip} skipped markers.
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public K4b2Sample skipMarkers(int nbToSkip) throws IOException {
        if (lastSampleRead == null || !lastSampleRead.isMarked()) {
            goToNextMarker();
        }
        // at this point the current row is marked
        for (int i = 0; i < nbToSkip; i++) {
            goToNextMarker();
        }
        return lastSampleRead;
    }

    /**
     * Reads as many rows as necessary to reach the next row with a marker. If the
     * current row is marked, it is not taken into account and the next row with a
     * marker is returned. Postcondition: {@link #isMarked()} will return true.
     * 
     * @return The next marked sample.
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public K4b2Sample goToNextMarker() throws IOException {
        int count = 0;
        while (readK4b2Sample() != null) {
            count++;
            if (lastSampleRead.isMarked()) {
                return lastSampleRead;
            }
        }
        throw new EOFException(
                "The end of file has been reached while looking for the next marker (" + count
                        + " rows read).");
    }

    /**
     * Returns the last sample that was read by this reader.
     * 
     * @return the last sample that was read by this reader.
     */
    public K4b2Sample getLastSample() {
        return lastSampleRead;
    }
}
