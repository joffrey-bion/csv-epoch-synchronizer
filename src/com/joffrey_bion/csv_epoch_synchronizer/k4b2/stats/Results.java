package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.util.Iterator;
import java.util.LinkedList;

public class Results {

    public static final long TOTAL_LENGTH_MILLIS = 5 * 60 * 1000;
    public static final long GROUP_LENGTH_MILLIS = 30 * 1000;

    private LinkedList<StatsWindowsGroup> windows;

    public Results() {
        windows = new LinkedList<>();
        windows.add(new StatsWindowsGroup(TOTAL_LENGTH_MILLIS, GROUP_LENGTH_MILLIS));
    }

    /**
     * Adds the specified line of values to the results calculation.
     * 
     * @param line
     *            The line to add.
     * @param length
     *            The duration of the sample corresponding to the specified line.
     */
    public void add(String[] line, double length) {
        LinkedList<K4b2Line> toMove = new LinkedList<>();
        toMove.add(new K4b2Line(line, length));
        LinkedList<K4b2Line> toMoveNext = new LinkedList<>();
        double cumulatedDuration = 0;
        int nbWindows = 0;
        // fill first window, and move extra old lines to next window
        for (StatsWindowsGroup win : windows) {
            win.setCompensation(cumulatedDuration, nbWindows);
            toMoveNext.clear();
            win.add(toMove, toMoveNext);
            LinkedList<K4b2Line> tempForInversion = toMoveNext;
            toMoveNext = toMove;
            toMove = tempForInversion;
            cumulatedDuration += win.getDuration();
            nbWindows++;
        }
        // create new windows for the extra old lines that remain
        while (!toMove.isEmpty()) {
            StatsWindowsGroup win = new StatsWindowsGroup(TOTAL_LENGTH_MILLIS, GROUP_LENGTH_MILLIS);
            windows.add(win);
            win.setCompensation(cumulatedDuration, nbWindows);
            toMoveNext.clear();
            win.add(toMove, toMoveNext);
            LinkedList<K4b2Line> tempForInversion = toMoveNext;
            toMoveNext = toMove;
            toMove = tempForInversion;
            cumulatedDuration += win.getDuration();
            nbWindows++;
        }
    }

    public double getVO2kgAvg() {
        return windows.getFirst().getVO2kgAvg();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<StatsWindowsGroup> it = windows.descendingIterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
