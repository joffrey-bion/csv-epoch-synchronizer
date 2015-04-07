package org.hildan.waterloo.ces.k4b2.stats;

import java.util.LinkedList;

import org.hildan.utils.dates.DurationHelper;
import org.hildan.utils.stats.FlowStats;
import org.hildan.waterloo.ces.k4b2.Sample;

class StatsWindowsGroup {

    private final long maxDuration;

    private final long subwindowsDuration;

    private long compensation;

    private long duration;

    private final LinkedList<StatsWindow> subwindows;

    public StatsWindowsGroup(long maxDuration, long subwindowsDuration) {
        this.maxDuration = maxDuration;
        this.subwindowsDuration = subwindowsDuration;
        this.compensation = 0;
        this.duration = 0;
        subwindows = new LinkedList<>();
    }

    /**
     * Sets the duration compensation of this window. This allows this window to have a longer
     * duration than the maximum if the previous windows are shorter, and vice-versa.
     *
     * @param prevCumulatedDuration
     *            The cumulated duration of the previous windows.
     * @param nbPrevious
     *            The number of previous windows.
     */
    public void setCompensation(long prevCumulatedDuration, int nbPrevious) {
        compensation = prevCumulatedDuration - nbPrevious * maxDuration;
    }

    /**
     * Adds the specified lines of values to this group.
     *
     * @param linesToAdd
     *            The {@link Sample}s to add, in the order they should be added to this window. This
     *            list will be cleared and receive the oldest lines in this group that do not fit
     *            anymore in the maximum length. The oldest of these lines is added first in the
     *            list, the newest is added last.
     */
    public void add(LinkedList<Sample> linesToAdd) {
        for (final Sample line : linesToAdd) {
            duration += line.duration;
        }
        long cumulatedDuration = 0;
        int nbWindows = 0;
        // fill first subwindow, and move extra old lines to next subwindow
        for (final StatsWindow win : subwindows) {
            win.setCompensation(cumulatedDuration, nbWindows);
            win.add(linesToAdd);
            cumulatedDuration += win.getDuration();
            nbWindows++;
        }
        // create new subwindows for the extra old lines that remain
        while (!linesToAdd.isEmpty()) {
            final StatsWindow win = new StatsWindow(subwindowsDuration);
            subwindows.add(win);
            win.setCompensation(cumulatedDuration, nbWindows);
            win.add(linesToAdd);
            cumulatedDuration += win.getDuration();
            nbWindows++;
        }
        // fill oldies with the global extra old lines exceeding the duration
        while (duration + compensation > maxDuration) {
            linesToAdd.add(removeOldest());
        }
    }

    private Sample removeOldest() {
        final StatsWindow win = subwindows.getLast();
        final Sample oldest = win.removeOldest();
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
        return getStats(StatsColumn.VO2KG).mean();
    }

    private static String formatPercent(double d) {
        return String.format("%2.2f", d * 100) + "%";
    }

    private FlowStats getStats(StatsColumn c) {
        final FlowStats globalStats = new FlowStats();
        for (final StatsWindow win : subwindows) {
            globalStats.add(win.getStats().getStats(c).mean());
        }
        return globalStats;
    }

    private String getCV(StatsColumn c) {
        return formatPercent(getStats(c).coeffOfVariation());
    }

    private String getStartTime() {
        final long time = subwindows.getLast().getStartTime();
        return DurationHelper.toTime(time);
    }

    private String getEndTime() {
        final long time = subwindows.getFirst().getEndTime();
        return DurationHelper.toTime(time);
    }

    @Override
    public String toString() {
        final String newLine = System.getProperty("line.separator");
        String res = getStartTime() + " to " + getEndTime();
        res += " (" + DurationHelper.toTime(duration) + ")" + newLine;
        res += " VO2 CV = " + getCV(StatsColumn.VO2) + newLine;
        res += "VCO2 CV = " + getCV(StatsColumn.VCO2) + newLine;
        res += "   R CV = " + getCV(StatsColumn.R) + newLine;
        res += "Resting VO2/kg avg = ";
        res += String.format("%2.2f", getStats(StatsColumn.VO2KG).mean());
        res += newLine;
        return res;
    }
}
