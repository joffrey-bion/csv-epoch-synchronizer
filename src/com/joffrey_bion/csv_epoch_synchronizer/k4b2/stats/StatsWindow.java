package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.util.LinkedList;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2Sample;

/**
 * A {@code StatsWindow} sets a duration limit for a group of values. When adding new
 * values, it fills the time window until the maximum duration is reached, and then
 * returns the oldest value for each new value inserted.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
class StatsWindow {

    private final double maxDuration;
    private double compensation;
    private double duration;
    private K4b2SampleStats stats;
    private LinkedList<K4b2Sample> samples;

    /**
     * Creates a new window without maximum duration.
     */
    public StatsWindow() {
        this(Double.POSITIVE_INFINITY);
    }

    /**
     * Creates a new window with the specified maximum duration.
     */
    public StatsWindow(double maxDuration) {
        this.maxDuration = maxDuration;
        this.duration = 0;
        this.compensation = 0;
        this.stats = new K4b2SampleStats();
        this.samples = new LinkedList<>();
    }
    
    public void setCompensation(double prevCumulatedDuration, int nbPrevious) {
        compensation = prevCumulatedDuration - nbPrevious * maxDuration;
    }

    /**
     * Adds the specified line of values to this window.
     * 
     * @param line The {@link K4b2Sample} to add.
     */
    public void add(K4b2Sample line) {
        LinkedList<K4b2Sample> linesToAdd = new LinkedList<>();
        linesToAdd.add(line);
        add(linesToAdd, null);
    }

    /**
     * Adds the specified samples of values to this group.
     * 
     * @param linesToAdd
     *            The {@link K4b2Sample}s to add, in the order they should be added to
     *            this window. This list is not modified.
     * @param oldies
     *            A list to receive the oldest samples in this group that do not fit
     *            anymore in the maximum length. The oldest of these samples is added
     *            first in the list, the newest is added last. This argument may be
     *            {@code null} if the removed old samples are not needed.
     */
    public void add(LinkedList<K4b2Sample> linesToAdd, LinkedList<K4b2Sample> oldies) {
        for (K4b2Sample line : linesToAdd) {
            stats.add(line);
            samples.add(line);
            duration += line.duration;
        }
        if (oldies != null) {
            while (duration + compensation > maxDuration) {
                oldies.add(removeOldest());
            }
        }
    }

    public K4b2Sample removeOldest() {
        K4b2Sample oldest = samples.pollFirst();
        stats.remove(oldest);
        duration -= oldest.duration;
        return oldest;
    }

    public K4b2Sample removeNewest() {
        K4b2Sample oldest = samples.pollLast();
        stats.remove(oldest);
        duration -= oldest.duration;
        return oldest;
    }

    public boolean isEmpty() {
        return samples.isEmpty();
    }

    public K4b2SampleStats getStats() {
        return stats;
    }

    public double getDuration() {
        return duration;
    }

    public void trimBeginning(double millisToSkip) {
        double target = duration - millisToSkip;
        while (duration > target) {
            removeOldest();
        }
    }

    public void trimEnd(double millisToSkip) {
        double target = duration - millisToSkip;
        while (duration > target) {
            removeNewest();
        }
    }
}
