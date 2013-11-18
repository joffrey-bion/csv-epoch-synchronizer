package com.jbion.ces.k4b2.stats;

import java.util.HashMap;

import com.jbion.ces.k4b2.METsToLevel;
import com.jbion.ces.k4b2.Sample;
import com.jbion.utils.dates.DurationHelper;

/**
 * Contains the results of a phase in the K4b2 file.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class PhaseResults extends StatsWindow {

    private static final long LENGTH_TRIM_MILLIS = (2 * 60 + 45) * 1000;
    private static final long END_TRIM_MILLIS = 15 * 1000;

    private double restingVO2kg;
    private HashMap<String, Double> lvlDistrib;
    private METsToLevel mtl;

    public PhaseResults() {
        super();
        lvlDistrib = new HashMap<>();
        mtl = METsToLevel.STANDARD;
    }

    @Override
    public void add(Sample line) {
        super.add(line);
        double METs = line.getValue(Sample.COL_METS);
        String level = mtl.metsToLevel(METs);
        double oldWeight = lvlDistrib.containsKey(level) ? lvlDistrib.get(level) : 0;
        lvlDistrib.put(level, oldWeight + line.duration / 1000);
    }

    @Override
    public boolean remove(Sample line) {
        boolean removed = super.remove(line);
        if (removed) {
            double METs = line.getValue(Sample.COL_METS);
            String level = mtl.metsToLevel(METs);
            double oldWeight = lvlDistrib.containsKey(level) ? lvlDistrib.get(level) : 0;
            lvlDistrib.put(level, oldWeight - line.duration / 1000);
            if (lvlDistrib.get(level) == 0) {
                lvlDistrib.remove(level);
            }
        }
        return removed;
    }

    /**
     * Removes the beginning and the end of this phase as specified by the protocol.
     */
    public void trim() {
        trimBeginningToReach(LENGTH_TRIM_MILLIS + END_TRIM_MILLIS);
        trimEnd(END_TRIM_MILLIS);
    }

    /**
     * Returns the time spent in each level in seconds.
     * 
     * @return A map between the level names and the associated cumulated duration in
     *         seconds.
     */
    public HashMap<String, Double> getLevelsDistribution() {
        return lvlDistrib;
    }

    /**
     * Set the resting VO2/kg determined by the resting phase.
     * 
     * @param restingVO2kg
     *            The resting VO2/kg, in ml/min/kg.
     */
    public void setRestingVO2kg(double restingVO2kg) {
        this.restingVO2kg = restingVO2kg;
    }

    /**
     * Returns the average METs for this phase. This method uses the value of the
     * resting VO2/kg set via {@link #setRestingVO2kg(double)} to compute individual
     * METs values.
     * 
     * @return the average METs for this phase.
     */
    public double getPersonalizedMETs() {
        return getMean(StatsColumn.VO2KG) / restingVO2kg;
    }

    /**
     * Returns the mean (average) for the specified value.
     * 
     * @param c
     *            The value to get the mean for.
     * @return the mean (average) for the specified value.
     */
    public double getMean(StatsColumn c) {
        return getStats().getStats(c).mean();
    }

    private static String format(double value) {
        return String.format("%2.2f", value);
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "This phase does not contain any sample.\n";
        }
        String newLine = System.getProperty("line.separator");
        String res = DurationHelper.toTime(getStartTime()) + " to "
                + DurationHelper.toTime(getEndTime()) + " (" + DurationHelper.toTime(getDuration())
                + ")\n";
        res += "        R avg = " + format(getMean(StatsColumn.R)) + newLine;
        res += "   VO2/kg avg = " + format(getMean(StatsColumn.VO2KG)) + newLine;
        res += " std METs avg = " + format(getMean(StatsColumn.METS)) + newLine;
        res += "pers METs avg = " + format(getPersonalizedMETs()) + newLine;
        res += "Levels distribution: " + lvlDistrib.toString();
        res += newLine;
        return res;
    }
}
