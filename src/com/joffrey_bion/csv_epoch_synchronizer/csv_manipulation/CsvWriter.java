package com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CsvWriter extends Csv {

    private BufferedWriter out;

    public CsvWriter(String filename) throws IOException {
        super(filename);
        out = new BufferedWriter(new FileWriter(filename));
    }

    public void writeRow(String[] cols) throws IOException {
        for (String c : cols) {
            out.write(c);
            if (c != cols[cols.length - 1]) {
                out.write(Csv.CSV_COL_SEP);
            }
        }
        out.write("\n");
    }

    public void close() {
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
