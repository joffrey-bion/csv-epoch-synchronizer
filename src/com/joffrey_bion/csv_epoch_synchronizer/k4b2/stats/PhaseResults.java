package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import com.joffrey_bion.utils.dates.DurationHelper;

public class PhaseResults extends StatsWindow {

    private static final long LENGTH_TRIM_MILLIS = (2 * 60 + 45) * 1000;
    private static final long END_TRIM_MILLIS = 15 * 1000;

    private double restingVO2kg;

    public void trim() {
        trimBeginningToReach(LENGTH_TRIM_MILLIS + END_TRIM_MILLIS);
        trimEnd(END_TRIM_MILLIS);
    }

    public void setRestingVO2kg(double restingVO2kg) {
        this.restingVO2kg = restingVO2kg;
    }

    public double getPersonalizedMETs() {
        return getMean(StatsColumn.VO2KG) / restingVO2kg;
    }

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
        String res = DurationHelper.toTime(getStartTime()) + " to "
                + DurationHelper.toTime(getEndTime()) + " ("
                + DurationHelper.toTime(getDuration()) + ")\n";
        res += "        R avg = " + format(getMean(StatsColumn.R)) + "\n";
        res += "   VO2/kg avg = " + format(getMean(StatsColumn.VO2KG)) + "\n";
        res += " std METs avg = " + format(getMean(StatsColumn.METS)) + "\n";
        res += "pers METs avg = " + format(getPersonalizedMETs()) + "\n";
        return res;
    }
}
