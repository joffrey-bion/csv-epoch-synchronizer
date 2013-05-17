package com.joffrey_bion.csv_epoch_synchronizer;
import java.io.IOException;
import java.util.LinkedList;

import com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation.CsvWriter;
import com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation.DateHelper;
import com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation.PhoneCsvReader;
import com.joffrey_bion.csv_epoch_synchronizer.row_statistics.StatsLineSkipSome;



public class RawToEpConverter {
    private static final int NB_STATS_PER_COL = 2;

    private PhoneCsvReader reader;
    private CsvWriter writer;

    private class EpochStatsLine extends StatsLineSkipSome {

        public EpochStatsLine(int nbOfColumns) {
            super(nbOfColumns, 1);
        }

        public String[] getEpochLine(long firstTimestamp) {
            String[] timestamp = { DateHelper.toDateTimeMillis(firstTimestamp / 1000000) };
            return toStringArray(timestamp);
        }
    }

    public RawToEpConverter(String sourceName, String destName) throws IOException {
        reader = new PhoneCsvReader(sourceName);
        writer = new CsvWriter(destName);
    }

    public int createEpochsFile(InstanceProperties props) throws IOException {
        // read the columns headers
        String[] line;
        if ((line = reader.readRow()) != null) {
            writer.writeRow(getNewHeaders(line));
        } else {
            System.out.println("Empty file, nothing done.");
            reader.close();
            writer.close();
            return -1;
        }
        // accumulate the samples in epochs and write them
        writeSmoothEpochs(line.length, props);
        reader.close();
        writer.close();
        return 0;
    }

    private static String[] getNewHeaders(String[] headers) {
        String[] newHeaders = new String[headers.length * NB_STATS_PER_COL - 1];
        newHeaders[0] = headers[0];
        int j = 1;
        for (int i = 1; i < headers.length; i++) {
            newHeaders[j++] = headers[i] + "Avg";
            newHeaders[j++] = headers[i] + "StdDev";
        }
        return newHeaders;
    }

    @SuppressWarnings("unused")
    private void writeEpochs(int nbOfColumns, InstanceProperties props) throws IOException {
        EpochStatsLine stats = new EpochStatsLine(nbOfColumns);
        long phoneStartTime = props.startTime - props.delay;
        long phoneStopTime = props.stopTime - props.delay;
        reader.skipToReachTimestamp(phoneStartTime);
        long beginning = phoneStartTime;
        long timestamp;
        String[] line;
        while ((line = reader.readRow()) != null) {
            timestamp = reader.extractTimestamp(line);
            if (timestamp > beginning + InstanceProperties.EPOCH_WIDTH_NANO) {
                writer.writeRow(stats.getEpochLine(beginning + props.delay));
                stats.clear();
                if (beginning + InstanceProperties.EPOCH_WIDTH_NANO >= phoneStopTime)
                    break;
                beginning += InstanceProperties.EPOCH_WIDTH_NANO;
            }
            stats.add(line);
        }
        reader.close();
        writer.close();
    }

    private void writeSmoothEpochs(int nbOfColumns, InstanceProperties props) throws IOException {
        long phoneStartTime = props.startTime - props.delay;
        long phoneStopTime = props.stopTime - props.delay;
        long beginning = phoneStartTime - InstanceProperties.WINBEGIN_TO_EPBEGIN;
        reader.skipToReachTimestamp(beginning);
        long timestamp;
        String[] line;
        Window win = new Window(phoneStartTime, nbOfColumns);
        while ((line = reader.readRow()) != null) {
            timestamp = reader.extractTimestamp(line);
            win.add(timestamp, line);
            if (win.hasMovedEnough()) {
                writer.writeRow(win.accumulate(props.delay));
                if (win.getEpEndAfterAccumulation() >= phoneStopTime)
                    break;
            }
        }
        reader.close();
        writer.close();
    }

    private class Window {
        private long winBeginning;
        private long lastEpBeginning;

        private LinkedList<Long> timestamps;
        private LinkedList<String[]> samples;
        private EpochStatsLine stats;

        public Window(long firstEpBeginningTime, int nbOfColumns) {
            timestamps = new LinkedList<>();
            samples = new LinkedList<>();
            winBeginning = firstEpBeginningTime - InstanceProperties.WINBEGIN_TO_EPBEGIN;
            lastEpBeginning = firstEpBeginningTime;
            stats = new EpochStatsLine(nbOfColumns);
        }

        public void add(long timestamp, String[] line) {
            timestamps.add(timestamp);
            samples.add(line);
            stats.add(line);
            while (timestamp > winBeginning + InstanceProperties.WINDOW_WIDTH_NANO) {
                removeFirst();
            }
        }

        private String[] removeFirst() {
            Long timestamp = timestamps.removeFirst();
            if (timestamp == null)
                return null;
            String[] line = samples.removeFirst();
            stats.remove(line);
            winBeginning = timestamps.getFirst();
            return line;
        }

        public String[] accumulate(long phoneToActigDelay) {
            String[] accLine = stats.getEpochLine(lastEpBeginning + phoneToActigDelay);
            lastEpBeginning += InstanceProperties.EPOCH_WIDTH_NANO;
            return accLine;
        }
        
        public boolean hasMovedEnough() {
            return winBeginning + InstanceProperties.WINBEGIN_TO_EPBEGIN - lastEpBeginning >= InstanceProperties.EPOCH_WIDTH_NANO;
        }
        
        public long getEpEndAfterAccumulation() {
            return lastEpBeginning;
        }
    }
}
