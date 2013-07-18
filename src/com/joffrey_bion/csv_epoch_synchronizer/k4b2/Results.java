package com.joffrey_bion.csv_epoch_synchronizer.k4b2;


public class Results {

    public static final long TOTAL_LENGTH_MILLIS = 5 * 60 * 1000;
    public static final long GROUP_LENGTH_MILLIS = 30 * 1000;

    private NestedStats VO2;
    private NestedStats VCO2;
    private NestedStats R;
    private NestedStats VO2kg;

    public Results() {
        VO2 = new NestedStats();
        VO2kg = new NestedStats();
        VCO2 = new NestedStats();
        R = new NestedStats();
    }

    public void add(String[] line, double length) {
        VO2.add(K4b2CsvReader.getVO2(line), length);
        VO2kg.add(K4b2CsvReader.getVO2kg(line), length);
        VCO2.add(K4b2CsvReader.getVCO2(line), length);
        R.add(K4b2CsvReader.getR(line), length);
    }

    public void finishCurrentGroup() {
        VO2.finishCurrentGroup();
        VO2kg.finishCurrentGroup();
        VCO2.finishCurrentGroup();
        R.finishCurrentGroup();
    }

    public long getTotalLength() {
        return (long) VO2.getGlobalStats().getTotalWeight();
    }

    public long getVO2kgAvg() {
        return (long) VO2kg.getGlobalStats().mean();
    }

    private static String formatPercent(double d) {
        return String.format("%2.2f", d * 100) + "%";
    }

    @Override
    public String toString() {
        String res = "Total length: " + getTotalLength() / 1000 + "s\n";
        res += "VO2  CV = " + formatPercent(VO2.getGlobalStats().coeffOfVariation()) + "\n";
        res += "VCO2 CV = " + formatPercent(VCO2.getGlobalStats().coeffOfVariation()) + "\n";
        res += "R    CV = " + formatPercent(R.getGlobalStats().coeffOfVariation()) + "\n";
        res += "VO2/kg average = " + String.format("%2.2f", VO2kg.getGlobalStats().mean()) + "\n";
        return res;
    }
}
