package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.util.Iterator;
import java.util.LinkedList;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Sample;

public class RestingResults extends PhaseResults {
    
    private static final long GROUP_LENGTH_MILLIS = 5 * 60 * 1000;
    private static final long SUBGROUP_LENGTH_MILLIS = 30 * 1000;
    
    private static final long END_TRIM_MILLIS = 15 * 1000;

    private LinkedList<StatsWindowsGroup> windows;

    public RestingResults() {
        windows = new LinkedList<>();
        windows.add(new StatsWindowsGroup(GROUP_LENGTH_MILLIS, SUBGROUP_LENGTH_MILLIS));
    }

    /**
     * Adds the specified line of values to the results calculation.
     * 
     * @param line
     *            The line to add.
     * @param length
     *            The duration of the sample corresponding to the specified line.
     */
    @Override
    public void add(Sample sample) {
        super.add(sample);
        LinkedList<Sample> toMove = new LinkedList<>();
        toMove.add(sample);
        long cumulatedDuration = 0;
        int nbWindows = 0;
        // fill first window, and move extra old lines to next window
        for (StatsWindowsGroup win : windows) {
            win.setCompensation(cumulatedDuration, nbWindows);
            win.add(toMove);
            cumulatedDuration += win.getDuration();
            nbWindows++;
        }
        // create new windows for the extra old lines that remain
        while (!toMove.isEmpty()) {
            StatsWindowsGroup win = new StatsWindowsGroup(GROUP_LENGTH_MILLIS, SUBGROUP_LENGTH_MILLIS);
            windows.add(win);
            win.setCompensation(cumulatedDuration, nbWindows);
            win.add(toMove);
            cumulatedDuration += win.getDuration();
            nbWindows++;
        }
    }

    @Override
    public void trim() {
        trimBeginningToReach(GROUP_LENGTH_MILLIS + END_TRIM_MILLIS);
        trimEnd(END_TRIM_MILLIS);
    }

    public double getVO2kgAvg() {
        return windows.getFirst().getVO2kgAvg();
    }

    public String allToString() {
        StringBuilder sb = new StringBuilder();
        Iterator<StatsWindowsGroup> it = windows.descendingIterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    public String lastToString() {
        return windows.getFirst().toString();
    }
}
