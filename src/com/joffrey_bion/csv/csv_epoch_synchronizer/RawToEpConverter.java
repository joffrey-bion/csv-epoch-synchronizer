package com.joffrey_bion.csv.csv_epoch_synchronizer;

import java.io.IOException;
import java.util.LinkedList;

import com.joffrey_bion.csv.Csv.NotACsvFileException;
import com.joffrey_bion.csv.csv_epoch_synchronizer.csv_manipulation.DateHelper;
import com.joffrey_bion.csv.csv_epoch_synchronizer.csv_manipulation.PhoneCsvReader;
import com.joffrey_bion.csv.csv_epoch_synchronizer.row_statistics.StatsLineSkipSome;
import com.joffrey_bion.csv.CsvWriter;

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

    public RawToEpConverter(String sourceName, String destName) throws IOException, NotACsvFileException {
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
        String[] line;
        while ((line = reader.readRow()) != null) {
            long timestamp = reader.extractTimestamp(line);
            if (timestamp > beginning + InstanceProperties.EPOCH_WIDTH_NANO) {
                writer.writeRow(stats.getEpochLine(beginning + props.delay));
                stats.clear();
                beginning += InstanceProperties.EPOCH_WIDTH_NANO;
                if (beginning >= phoneStopTime)
                    break;
            }
            stats.add(line);
        }
        reader.close();
        writer.close();
    }

    private void writeSmoothEpochs(int nbOfColumns, InstanceProperties props) throws IOException {
        final long phoneStartTime = props.startTime - props.delay;
        final long phoneStopTime = props.stopTime - props.delay;
        final Window win = new Window(phoneStartTime, nbOfColumns);
        reader.skipToReachTimestamp(phoneStartTime - InstanceProperties.WINBEGIN_TO_EPBEGIN);
        String[] line;
        while ((line = reader.readRow()) != null) {
            long timestamp = reader.extractTimestamp(line);
            win.add(timestamp, line);
            if (win.hasMovedEnough()) {
                writer.writeRow(win.accumulate(props.delay));
                if (win.getLastEpEnd() >= phoneStopTime) {
                    break;
                }
            }
        }
        reader.close();
        writer.close();
    }

    private class Window {
        private long winBeginning;
        private long lastEpBeginning;
        private boolean full;

        private LinkedList<Long> timestamps;
        private LinkedList<String[]> samples;
        private EpochStatsLine stats;

        public Window(long firstEpBeginningTime, int nbOfColumns) {
            stats = new EpochStatsLine(nbOfColumns);
            timestamps = new LinkedList<>();
            samples = new LinkedList<>();
            winBeginning = 0;
            full = false;
            lastEpBeginning = firstEpBeginningTime - InstanceProperties.EPOCH_WIDTH_NANO;
        }

        public void add(long timestamp, String[] line) {
            if (samples.isEmpty()) {
                winBeginning = timestamp;
            }
            timestamps.add(timestamp);
            samples.add(line);
            stats.add(line);
            //System.out.println("add " + timestamp);
            while (timestamp > winBeginning + InstanceProperties.WINDOW_WIDTH_NANO) {
                full = true;
                removeFirst();
            }
        }

        private String[] removeFirst() {
            //System.out.println("remove " + timestamps.getFirst());
            timestamps.removeFirst();
            String[] line = samples.removeFirst();
            stats.remove(line);
            winBeginning = timestamps.getFirst();
            return line;
        }

        public String[] accumulate(long phoneToActigDelay) {
            lastEpBeginning += InstanceProperties.EPOCH_WIDTH_NANO;
            String[] accLine = stats.getEpochLine(lastEpBeginning + phoneToActigDelay);
            return accLine;
        }
        
        public boolean hasMovedEnough() {
            return full && (winBeginning + InstanceProperties.WINBEGIN_TO_EPBEGIN - lastEpBeginning >= InstanceProperties.EPOCH_WIDTH_NANO);
        }
        
        public long getLastEpEnd() {
            return lastEpBeginning + InstanceProperties.EPOCH_WIDTH_NANO;
        }
    }
}
