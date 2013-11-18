package com.jbion.ces.phone;

import java.util.LinkedList;

import com.jbion.utils.dates.DateHelper;

class TimeWindow {
    
    private final long WINDOW_WIDTH_NANO;
    private final long EPOCH_WIDTH_NANO;
    private final long WIN_BEGIN_TO_EP_BEGIN;

    private long winBeginning;
    private long lastEpBeginning;
    private boolean full;

    private LinkedList<Long> timestamps;
    private LinkedList<String[]> samples;
    private EpochStatsLine stats;

    private static class EpochStatsLine extends StatsLineSkipSome {
        public EpochStatsLine(int nbOfColumns) {
            super(nbOfColumns, 1);
        }

        public String[] getEpochLine(long firstTimestamp) {
            String[] timestamp = { DateHelper.toDateTimeMillis(firstTimestamp / 1000000) };
            return toStringArray(timestamp);
        }
    }

    public TimeWindow(long firstEpBeginningTime, int nbOfColumns, PhoneRawToEpParams props) {
        WINDOW_WIDTH_NANO = props.getWindowWidthNano();
        EPOCH_WIDTH_NANO = props.getEpochWidthNano();
        WIN_BEGIN_TO_EP_BEGIN = props.getWinBeginToEpBegin();
        stats = new EpochStatsLine(nbOfColumns);
        timestamps = new LinkedList<>();
        samples = new LinkedList<>();
        winBeginning = 0;
        full = false;
        lastEpBeginning = firstEpBeginningTime - props.getEpochWidthNano();
    }

    public void add(long timestamp, String[] line) {
        if (samples.isEmpty()) {
            winBeginning = timestamp;
        }
        timestamps.add(timestamp);
        samples.add(line);
        stats.add(line);
        while (timestamp > winBeginning + WINDOW_WIDTH_NANO) {
            full = true;
            removeFirst();
        }
    }

    private String[] removeFirst() {
        timestamps.removeFirst();
        String[] line = samples.removeFirst();
        stats.remove(line);
        winBeginning = timestamps.getFirst();
        return line;
    }

    public String[] accumulate(long phoneToActigDelay) {
        lastEpBeginning += EPOCH_WIDTH_NANO;
        String[] accLine = stats.getEpochLine(lastEpBeginning + phoneToActigDelay);
        return accLine;
    }

    public boolean hasMovedEnough() {
        return full && (winBeginning + WIN_BEGIN_TO_EP_BEGIN - lastEpBeginning >= EPOCH_WIDTH_NANO);
    }

    public long getLastEpEnd() {
        return lastEpBeginning + EPOCH_WIDTH_NANO;
    }
}