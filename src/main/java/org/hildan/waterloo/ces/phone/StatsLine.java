package org.hildan.waterloo.ces.phone;

import org.hildan.utils.stats.FlowStats;

class StatsLine {

    private final FlowStats[] columns;

    public StatsLine(int nbOfColumns) {
        columns = new FlowStats[nbOfColumns];
        for (int i = 0; i < nbOfColumns; i++) {
            columns[i] = new FlowStats();
        }
    }

    public void add(String[] line) {
        if (columns.length != line.length) {
            throw new IllegalArgumentException("wrong number of columns in the line");
        }
        for (int i = 0; i < columns.length; i++) {
            columns[i].add(Double.valueOf(line[i]));
        }
    }

    public void remove(String[] line) {
        if (columns.length != line.length) {
            throw new IllegalArgumentException("wrong number of columns in the line");
        }
        for (int i = 0; i < columns.length; i++) {
            columns[i].remove(Double.valueOf(line[i]));
        }
    }

    public void clear() {
        for (final FlowStats column : columns) {
            column.clear();
        }
    }

    public int getNbColumns() {
        return columns.length;
    }

    public String[] toStringArray() {
        final String[] line = new String[columns.length * 2];
        int j = 0;
        for (final FlowStats column : columns) {
            line[j++] = "" + column.mean();
            line[j++] = "" + column.standardDeviation();
        }
        return line;
    }
}
