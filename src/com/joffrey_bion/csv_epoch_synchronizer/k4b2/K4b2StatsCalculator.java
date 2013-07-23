package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import java.io.IOException;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats.PhaseResults;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats.RestingResults;

public class K4b2StatsCalculator {

    private K4b2CsvReader reader;
    private double restingVO2kg;

    public K4b2StatsCalculator(String filename) throws IOException {
        reader = new K4b2CsvReader(filename);
    }

    /**
     * Skips the headers and the given number of sync markers, and calculates
     * statistics on each phase of the K4b2 file.
     * 
     * @param nbSyncMarkers
     *            The number of markers that were used for synchronization.
     * @return The stats of each phase in a {@link K4b2Stats} object.
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public K4b2Stats getStats(int nbSyncMarkers) throws IOException {
        K4b2Stats result = new K4b2Stats();
        reader.skipHeaders();
        reader.skipMarkers(nbSyncMarkers);
        for (Phase p : Phase.values()) {
            PhaseResults pr;
            if (p == Phase.RESTING) {
                pr = new RestingResults();
                getPhaseStats(pr);
                restingVO2kg = ((RestingResults) pr).getVO2kgAvg();
            } else {
                reader.goToNextMarker();
                pr = new PhaseResults();
                getPhaseStats(pr);
            }
            pr.setRestingVO2kg(restingVO2kg);
            result.put(p, pr);
        }
        return result;
    }

    /**
     * Reads the next phase and put the results of the calculations in the given
     * {@link PhaseResults} object.
     * 
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    private void getPhaseStats(PhaseResults results) throws IOException {
        if (!reader.getLastSample().isMarked()) {
            throw new IllegalStateException("The reader must be on the marker starting the phase.");
        }
        K4b2Sample line;
        while ((line = reader.readK4b2Sample()) != null && !line.isMarked()) {
            results.add(line);
        }
        results.trim();
    }
}
