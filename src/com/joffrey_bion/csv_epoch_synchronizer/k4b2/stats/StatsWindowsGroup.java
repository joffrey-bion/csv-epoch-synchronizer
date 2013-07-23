package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.util.LinkedList;

import com.joffrey_bion.utils.stats.FlowStats;

public class StatsWindowsGroup {

    private final double maxDuration;
    private final double subwindowsDuration;
    private double compensation;
    private double duration;
    private LinkedList<StatsWindow> subwindows;

    public StatsWindowsGroup(double maxDuration, double subwindowsDuration) {
        this.maxDuration = maxDuration;
        this.subwindowsDuration = subwindowsDuration;
        this.compensation = 0;
        this.duration = 0;
        subwindows = new LinkedList<>();
    }
    
    public void setCompensation(double prevCumulatedDuration, int nbPrevious) {
        compensation = prevCumulatedDuration - nbPrevious * maxDuration;
    }

    /**
     * Adds the specified lines of values to this group.
     * 
     * @param lines
     *            The {@link K4b2Line}s to add, in the order they should be added to
     *            this window. This list is not modified.
     * @param oldies
     *            A list to receive the oldest lines in this group that do not fit
     *            anymore in the maximum length. The oldest of these lines is added
     *            first in the list, the newest is added last.
     */
    public void add(LinkedList<K4b2Line> lines, LinkedList<K4b2Line> oldies) {
        LinkedList<K4b2Line> toMove = lines;
        for (K4b2Line line : lines) {
            duration += line.duration;
        }
        LinkedList<K4b2Line> toMoveNext = new LinkedList<>();
        double cumulatedDuration = 0;
        int nbWindows = 0;
        // fill first subwindow, and move extra old lines to next subwindow
        for (StatsWindow win : subwindows) {
            win.setCompensation(cumulatedDuration, nbWindows);
            toMoveNext.clear();
            win.add(toMove, toMoveNext);
            LinkedList<K4b2Line> tempForInversion = toMoveNext;
            toMoveNext = toMove;
            toMove = tempForInversion;
            cumulatedDuration += win.getDuration();
            nbWindows++;
        }
        // create new subwindows for the extra old lines that remain
        while (!toMove.isEmpty()) {
            StatsWindow win = new StatsWindow(subwindowsDuration);
            subwindows.add(win);
            win.setCompensation(cumulatedDuration, nbWindows);
            toMoveNext.clear();
            win.add(toMove, toMoveNext);
            LinkedList<K4b2Line> tempForInversion = toMoveNext;
            toMoveNext = toMove;
            toMove = tempForInversion;
            cumulatedDuration += win.getDuration();
            nbWindows++;
        }
        // fill oldies with the global extra old lines exceeding the duration
        while (duration + compensation > maxDuration) {
            oldies.add(removeOldest());
        }
    }

    private K4b2Line removeOldest() {
        StatsWindow win = subwindows.getLast();
        K4b2Line oldest = win.removeOldest();
        duration -= oldest.duration;
        if (win.isEmpty()) {
            subwindows.remove(win);
        }
        return oldest;
    }

    public double getDuration() {
        return duration;
    }
    
    public double getVO2kgAvg() {
        return getStats(Column.VO2KG).mean();
    }

    private static String formatPercent(double d) {
        return String.format("%2.2f", d * 100) + "%";
    }

    private FlowStats getStats(Column c) {
        FlowStats globalStats = new FlowStats();
        for (StatsWindow win : subwindows) {
            globalStats.add(win.getStats().getStats(c).mean());
        }
        return globalStats;
    }
    
    @Override
    public String toString() {
        String res = "Total length: " + duration / 1000 + "s\n";
        res += "VO2  CV = " + formatPercent(getStats(Column.VO2).coeffOfVariation()) + "\n";
        res += "VCO2 CV = " + formatPercent(getStats(Column.VCO2).coeffOfVariation()) + "\n";
        res += "R    CV = " + formatPercent(getStats(Column.R).coeffOfVariation()) + "\n";
        res += "VO2/kg average = " + String.format("%2.2f", getStats(Column.VO2KG).mean()) + "\n";
        return res;
    }
}
