package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import java.io.IOException;


public class K4b2StatsCalculator {

    private K4b2CsvReader reader;

    public K4b2StatsCalculator(String filename) throws IOException {
        reader = new K4b2CsvReader(filename);
    }

    public Results[] getStats(int nbSyncMarkers) throws IOException {
        Results[] results = new Results[4];
        reader.skipHeaders();
        reader.readRow();
        reader.skipMarkers(nbSyncMarkers);
        long cumulatedLength = 0;
        for (int i = 0; i < results.length; i++) {
            results[i] = get5minStats(cumulatedLength);
            cumulatedLength += results[i].getTotalLength();
        }
        reader.close();
        return results;
    }

    public Results get5minStats(long prevCumulatedLength) throws IOException {
        Results results = new Results();
        long cumulatedLength = prevCumulatedLength % Results.TOTAL_LENGTH_MILLIS;
        int nbGroups = 0;
        String[] line;
        while ((line = reader.readRow()) != null) {
            long length = reader.getCurrentEpochLength();
            cumulatedLength += length;
            results.add(line, length);
            if (cumulatedLength >= (nbGroups + 1) * Results.GROUP_LENGTH_MILLIS) {
                results.finishCurrentGroup();
                nbGroups++;
            }
            if (cumulatedLength >= Results.TOTAL_LENGTH_MILLIS || K4b2CsvReader.isMarked(line)) {
                results.finishCurrentGroup();
                break;
            }
        }
        return results;
    }
}
