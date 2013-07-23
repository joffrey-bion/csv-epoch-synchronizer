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

    private final long maxDuration;
    private long compensation;
    private long duration;
    private K4b2SampleStats stats;
    private LinkedList<K4b2Sample> samples;

    /**
     * Creates a new window without maximum duration.
     */
    public StatsWindow() {
        this(Long.MAX_VALUE);
    }

    /**
     * Creates a new window with the specified maximum duration.
     */
    public StatsWindow(long maxDuration) {
        this.maxDuration = maxDuration;
        this.duration = 0;
        this.compensation = 0;
        this.stats = new K4b2SampleStats();
        this.samples = new LinkedList<>();
    }

    /**
     * Sets the duration compensation of this window. This allows this window to have
     * a longer duration than the maximum if the previous windows are shorter, and
     * vice-versa.
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
     * Adds the specified line of values to this window.
     * 
     * @param line
     *            The {@link K4b2Sample} to add.
     */
    public void add(K4b2Sample line) {
        LinkedList<K4b2Sample> linesToAdd = new LinkedList<>();
        linesToAdd.add(line);
        add(linesToAdd);
    }

    /**
     * Adds the specified samples of values to this group.
     * 
     * @param linesToAdd
     *            The {@link K4b2Sample}s to add, in the order they should be added
     *            to this window. This list will be cleared and receive the oldest
     *            samples in this group that do not fit anymore in the maximum
     *            length. The oldest of these samples is added first in the list, the
     *            newest is added last.
     */
    public void add(LinkedList<K4b2Sample> linesToAdd) {
        while (!linesToAdd.isEmpty()) {
            K4b2Sample sample = linesToAdd.pollFirst();
            stats.add(sample);
            samples.add(sample);
            duration += sample.duration;
        }
        while (duration + compensation > maxDuration) {
            linesToAdd.add(removeOldest());
        }
    }

    /**
     * Returns the oldest (first) sample's start time.
     * 
     * @return The oldest (first) sample's start time.
     */
    public long getStartTime() {
        return samples.getFirst().startTime;
    }

    /**
     * Returns the newest (last) sample's end time.
     * 
     * @return The newest (last) sample's end time.
     */
    public long getEndTime() {
        return samples.getLast().endTime;
    }

    /**
     * Removes the oldest (first) sample from this window.
     * 
     * @return The removed oldest sample.
     */
    public K4b2Sample removeOldest() {
        K4b2Sample oldest = samples.pollFirst();
        if (oldest == null) {
            return null;
        }
        stats.remove(oldest);
        duration -= oldest.duration;
        return oldest;
    }

    /**
     * Removes the newest (last) sample from this window.
     * 
     * @return The removed newest sample.
     */
    public K4b2Sample removeNewest() {
        K4b2Sample newest = samples.pollLast();
        if (newest == null) {
            return null;
        }
        stats.remove(newest);
        duration -= newest.duration;
        return newest;
    }

    /**
     * Returns whether this window is empty.
     * 
     * @return whether this window is empty.
     */
    public boolean isEmpty() {
        return samples.isEmpty();
    }

    /**
     * Returns the statistics over the samples of this window.
     * 
     * @return the statistics over the samples of this window.
     */
    public K4b2SampleStats getStats() {
        return stats;
    }

    /**
     * Returns the current duration of this window in milliseconds. This corresponds
     * to the actual sum of the durations of all the samples contained in this
     * window, not to the desired duration.
     * 
     * @return the duration of this window.
     */
    public long getDuration() {
        return duration;
    }

    /**
     * Removes the oldest (first) samples from this window. This window's duration is
     * shortened to reach {@code target} milliseconds.
     * 
     * @param target
     *            The target duration for this window.
     */
    public void trimBeginningToReach(long targetDuration) {
        long target = targetDuration < 0 ? 0 : targetDuration;
        while (duration > target) {
            removeOldest();
        }
    }

    /**
     * Removes the newest (last) samples from this window. This window's duration is
     * shortened to reach {@code targetDuration} milliseconds.
     * 
     * @param target
     *            The target duration for this window.
     */
    public void trimEndToReach(long targetDuration) {
        long target = targetDuration < 0 ? 0 : targetDuration;
        while (duration > target) {
            removeNewest();
        }
    }

    /**
     * Removes the oldest (first) samples from this window. This window's duration is
     * shortened by {@code millisToStop} milliseconds.
     * 
     * @param millisToSkip
     *            The amount of time that has to be cut from this window.
     */
    public void trimBeginning(long millisToSkip) {
        trimBeginningToReach(duration - millisToSkip);
    }

    /**
     * Removes the newest (last) samples from this window. This window's duration is
     * shortened by {@code millisToStop} milliseconds.
     * 
     * @param millisToSkip
     *            The amount of time that has to be cut from this window.
     */
    public void trimEnd(long millisToSkip) {
        trimEndToReach(duration - millisToSkip);
    }
}