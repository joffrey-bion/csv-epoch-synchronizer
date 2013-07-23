package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.io.IOException;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2CsvReader;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2Sample;

public class K4b2StatsCalculator {

    private K4b2CsvReader reader;

    public K4b2StatsCalculator(String filename) throws IOException {
        reader = new K4b2CsvReader(filename);
    }

    public RestingResults getStats(int nbSyncMarkers) throws IOException {
        reader.skipHeaders();
        //reader.readRow();
        reader.skipMarkers(nbSyncMarkers);
        RestingResults res = new RestingResults();
        K4b2Sample line;
        while ((line = reader.readK4b2Sample()) != null && !line.isMarked()) {
            res.add(line);
        }
        return res;
    }
}
