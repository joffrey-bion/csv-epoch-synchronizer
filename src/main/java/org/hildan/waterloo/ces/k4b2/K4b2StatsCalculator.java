package org.hildan.waterloo.ces.k4b2;

import java.io.IOException;

import org.hildan.waterloo.ces.k4b2.stats.PhaseResults;
import org.hildan.waterloo.ces.k4b2.stats.RestingResults;

/**
 * A program able to compute the statistics of each phase of a K4b2 test.
 *
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class K4b2StatsCalculator {

    private final K4b2CsvReader reader;

    private double restingVO2kg;

    /**
     * Creates a new {@code K4b2StatsCalculator} for the specified file.
     *
     * @param filename
     *            The path to the file to read.
     * @throws IOException
     *             If any IO error occurs.
     */
    public K4b2StatsCalculator(String filename) throws IOException {
        reader = new K4b2CsvReader(filename);
    }

    /**
     * Skips the headers and the given number of sync markers, and calculates statistics on each
     * phase of the K4b2 file. Do not use this method in addition to {@link #getRestingStats(int)}.
     * Use one or the other.
     *
     * @param nbSyncMarkers
     *            The number of markers that were used for synchronization.
     * @return The stats of each phase in a {@link Results} object.
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public Results getAllStats(int nbSyncMarkers) throws IOException {
        final Results fullResults = new Results();
        reader.skipHeaders();
        reader.skipMarkers(nbSyncMarkers);
        for (final Phase p : Phase.values()) {
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
            fullResults.put(p, pr);
        }
        return fullResults;
    }

    /**
     * Skips the headers and the given number of sync markers, and calculates statistics on the
     * first (resting) phase of the K4b2 file. Do not use this method in addition to
     * {@link #getAllStats(int)}. Use one or the other.
     *
     * @param nbSyncMarkers
     *            The number of markers that were used for synchronization.
     * @return The stats of the resting phase.
     * @throws IOException
     *             If an error occurs while reading the file.
     */
    public RestingResults getRestingStats(int nbSyncMarkers) throws IOException {
        reader.skipHeaders();
        reader.skipMarkers(nbSyncMarkers);
        final RestingResults rr = new RestingResults();
        getPhaseStats(rr);
        return rr;
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
        Sample line;
        while ((line = reader.readK4b2Sample()) != null && !line.isMarked()) {
            results.add(line);
        }
        results.trim();
    }
}
