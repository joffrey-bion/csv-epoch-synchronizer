package com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation;

import java.io.IOException;

public class CsvMerger {

    private CsvWriter writer;

    public CsvMerger(String destination) throws IOException {
        writer = new CsvWriter(destination);
    }

    public void merge(String[] sources) throws IOException {
        String[] header = null;
        CsvReader reader;
        for (String source : sources) {
            reader = new CsvReader(source);
            if (header == null) {
                header = reader.readRow();
                writer.writeRow(header);
            } else {
                if (!equals(header, reader.readRow()))
                    throw new RuntimeException("some source files dont have the same headers");
            }
            String[] line;
            while ((line = reader.readRow()) != null) {
                writer.writeRow(line);
            }
            reader.close();
        }
        writer.close();
    }

    private static boolean equals(String[] h1, String[] h2) {
        if (h1.length != h2.length)
            return false;
        for (int i = 0; i < h1.length; i++) {
            if (!h1[i].equals(h2[i]))
                return false;
        }
        return true;
    }
}
