package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.io.IOException;
import java.util.LinkedList;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2CsvReader;

public class K4b2StatsCalculator {

    private K4b2CsvReader reader;

    public K4b2StatsCalculator(String filename) throws IOException {
        reader = new K4b2CsvReader(filename);
    }

    public Results getStats(int nbSyncMarkers) throws IOException {
        reader.skipHeaders();
        //reader.readRow();
        reader.skipMarkers(nbSyncMarkers);
        Results res = new Results();
        String[] line;
        while ((line = reader.readRow()) != null && !reader.isMarked()) {
            res.add(line, reader.getCurrentEpochLength());
        }
        return res;
    }

    public LinkedList<OldResults> getStatsOldWay(int nbSyncMarkers) throws IOException {
        LinkedList<OldResults> results = new LinkedList<>();
        reader.skipHeaders();
        reader.readRow();
        reader.skipMarkers(nbSyncMarkers);
        long cumulatedLength = 0;
        while (true) {
            OldResults res5min = new OldResults();
            cumulatedLength = cumulatedLength % Results.TOTAL_LENGTH_MILLIS;
            int nbGroups = 0;
            String[] line;
            while ((line = reader.readRow()) != null) {
                long length = reader.getCurrentEpochLength();
                cumulatedLength += length;
                res5min.add(line, length);
                if (cumulatedLength >= (nbGroups + 1) * Results.GROUP_LENGTH_MILLIS) {
                    res5min.finishCurrentGroup();
                    nbGroups++;
                }
                if (cumulatedLength >= Results.TOTAL_LENGTH_MILLIS || reader.isMarked()) {
                    res5min.finishCurrentGroup();
                    break;
                }
            }
            results.add(res5min);
            if (reader.isMarked()) {
                break;
            }
        }
        reader.close();
        return results;
    }
}
