package com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CsvReader extends Csv {

    protected BufferedReader in;

    public CsvReader(String filename) throws FileNotFoundException {
        super(filename);
        in = new BufferedReader(new FileReader(filename));
    }

    public String[] readRows(int nbRows) throws IOException {
        String[] line = null;
        int n = nbRows;
        while (n > 0 && (line = readRow()) != null)
            n--;
        return line;
    }

    public String[] readRow() throws IOException {
        String line = in.readLine();
        if (line == null)
            return null;
        else
            return line.split(CSV_COL_SEP);
    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
