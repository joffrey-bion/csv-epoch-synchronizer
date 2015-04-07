package org.hildan.waterloo.ces.phone;

import java.io.IOException;

import org.hildan.utils.csv.CsvWriter;
import org.hildan.utils.csv.NotACsvFileException;

public class RawToEpConverter {

    private static final int NB_STATS_PER_COL = 2;

    public static int createEpochsFile(PhoneRawToEpParams props) throws IOException, NotACsvFileException {
        final PhoneCsvReader reader = new PhoneCsvReader(props.getInputFilePath());
        try {
            final CsvWriter writer = new CsvWriter(props.getPhoneEpochFilePath());
            try {
                // read the columns headers
                String[] line;
                if ((line = reader.readRow()) != null) {
                    writer.writeRow(getNewHeaders(line));
                } else {
                    System.out.println("Empty file, nothing done.");
                    return -1;
                }
                // accumulate the samples in epochs and write them
                writeSmoothEpochs(reader, writer, line.length, props);
                return 0;
            } finally {
                writer.close();
            }
        } finally {
            reader.close();
        }
    }

    private static void writeSmoothEpochs(PhoneCsvReader reader, CsvWriter writer, int nbOfColumns,
            PhoneRawToEpParams props) throws IOException {
        final long phoneStartTime = props.getPhoneStartTimeNano();
        final long phoneStopTime = props.getPhoneStopTimeNano();
        final TimeWindow win = new TimeWindow(phoneStartTime, nbOfColumns, props);
        reader.skipToReachTimestamp(phoneStartTime - props.getWinBeginToEpBegin());
        String[] line;
        while ((line = reader.readRow()) != null) {
            final long timestamp = reader.extractTimestamp(line);
            win.add(timestamp, line);
            if (win.hasMovedEnough()) {
                writer.writeRow(win.accumulate(props.getDelayNano()));
                if (win.getLastEpEnd() >= phoneStopTime) {
                    break;
                }
            }
        }
    }

    private static String[] getNewHeaders(String[] headers) {
        final String[] newHeaders = new String[headers.length * NB_STATS_PER_COL - 1];
        newHeaders[0] = headers[0];
        int j = 1;
        for (int i = 1; i < headers.length; i++) {
            newHeaders[j++] = headers[i] + "Avg";
            newHeaders[j++] = headers[i] + "StdDev";
        }
        return newHeaders;
    }
}
