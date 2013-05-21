package com.joffrey_bion.csv_epoch_synchronizer;

import java.io.IOException;
import java.util.Arrays;

import javax.swing.UIManager;

import com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation.Csv;
import com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation.CsvMerger;
import com.joffrey_bion.csv_epoch_synchronizer.csv_manipulation.DateHelper;
import com.joffrey_bion.csv_epoch_synchronizer.row_statistics.FlowStats;
import com.joffrey_bion.file_processor_window.JFilePickersPanel;
import com.joffrey_bion.file_processor_window.JFileProcessorWindow;

public class Main {

    private static final int ARG_MODE = 0;
    private static final int ARG_E_PHONE_RAW_FILE = 1;
    private static final int ARG_E_ACTIGRAPH_EPOCH_FILE = 2;
    private static final int ARG_E_START_TIME = 3;
    private static final int ARG_E_STOP_TIME = 4;
    private static final int ARG_M_DESTINATION_FILE = 1;

    private static final int NB_ARGS_BEFORE_SPIKES = 5;

    private static final String START_STOP_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String SPIKE_TIMESTAMP_FORMAT = "HH:mm:ss.SSS";

    private static void printUsage() {
        System.out
                .println("usage: java Main <phone-raw-filename> <actig-ep-filename> <actig-start-time> <actig-stop-time> [phone-spike actig-spike]+");
        System.out.println("spikes time format: " + SPIKE_TIMESTAMP_FORMAT);
        System.out.println("start/stop time format: " + START_STOP_TIMESTAMP_FORMAT);
        System.out.println("modes: merge | epc");
    }

    /**
     * @param args
     * @param labeledFilename
     */
    public static void main(String[] args) {
        long timerStart = System.currentTimeMillis();
        String destFilename = null;
        if (args.length == 0) {
            openWindow();
            System.err.println("Too few arguments.");
            printUsage();
            return;
        } else if (args[ARG_MODE].equals("merge")) {
            destFilename = merge(args);
        } else if (args[ARG_MODE].equals("epc")) {
            destFilename = createDataset(args);
        } else {
            System.err.println("Invalid mode.");
            printUsage();
            return;
        }
        if (destFilename == null) {
            System.out.println("Cannot proceed due to previous errors.");
            return;
        }
        System.out.println();
        // weka.core.converters.CSVLoader.main(new String[]{destFilename});
        System.out.println("Total processing time: " + (System.currentTimeMillis() - timerStart)
                + "ms");
        System.out.println("Output file: " + destFilename);
    }
    
    private static void openWindow() {
     // windows system look and feel for the window
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // file pickers source and destination
        JFilePickersPanel filePickers = new JFilePickersPanel("Input file", "Output file");
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Pseq File Processor", "Process",
                filePickers, null) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                // TODO
            }
        };
        frame.setVisible(true);
    }

    private static String merge(String[] args) {
        String destFilename = args[ARG_M_DESTINATION_FILE];
        String[] sources = Arrays.copyOfRange(args, 2, args.length);
        try {
            CsvMerger merger = new CsvMerger(destFilename);
            System.out.println("Merging files:");
            for (String source : sources) {
                System.out.println("> " + source);
            }
            merger.merge(sources);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
        System.out.println("Success.");
        return destFilename;
    }

    private static String createDataset(String[] args) {
        if (args.length < NB_ARGS_BEFORE_SPIKES) {
            System.err.println("Too few arguments.");
            printUsage();
            return null;
        } else if (args.length == NB_ARGS_BEFORE_SPIKES) {
            System.err.println("Spikes time are necessary to compute clocks delay.");
            printUsage();
            return null;
        }
        String[] spikeArgs = Arrays.copyOfRange(args, NB_ARGS_BEFORE_SPIKES, args.length);
        if (spikeArgs.length % 2 != 0) {
            System.err.println("Wrong number of spikes times: it has to be even.");
            printUsage();
            return null;
        }
        printSpikes(spikeArgs);
        String phoneRawFilename = args[ARG_E_PHONE_RAW_FILE];
        String actigraphEpFilename = args[ARG_E_ACTIGRAPH_EPOCH_FILE];
        String phoneEpFilename = Csv.removeCsvExtension(phoneRawFilename) + "-epochs.csv";
        String destFilename = "dataset.csv";
        InstanceProperties props = new InstanceProperties();
        try {
            props.delay = getTimeDelayAverage(spikeArgs);
            System.out.println("> Phone-to-actigraph delay average: " + props.delay / 1000000
                    + "ms");
            System.out.println();
            props.startTime = Csv.timestampStrToNanos(args[ARG_E_START_TIME],
                    START_STOP_TIMESTAMP_FORMAT);
            props.stopTime = Csv.timestampStrToNanos(args[ARG_E_STOP_TIME],
                    START_STOP_TIMESTAMP_FORMAT);
            DateHelper.displayTimestamp("Actigraph start time", props.startTime);
            System.out.println("Accumulating phone raw data into actigraph-timestamped epochs...");
            RawToEpConverter conv = new RawToEpConverter(phoneRawFilename, phoneEpFilename);
            conv.createEpochsFile(props);
            System.out.println("> Intermediate file " + phoneEpFilename + " successfully created.");
            System.out.println();
            System.out.println("Merging phone epochs with actigraph labels...");
            EpLabelsMerger merger = new EpLabelsMerger(phoneEpFilename, actigraphEpFilename,
                    destFilename);
            merger.createLabeledFile(props);
            System.out.println("> Dataset file " + destFilename + " successfully created.");
            /*
             * if (new File(phoneEpFilename).delete()) {
             * System.out.println("> Intermediate file " + file.getName() +
             * " was deleted!"); } else {
             * System.out.println("> Delete operation has failed."); }
             */
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return null;
        }
        return destFilename;
    }

    private static void printSpikes(String[] spikes) {
        System.out.println("spikes (phone - actigraph): ");
        for (int i = 0; i < spikes.length; i++) {
            System.out.println(spikes[i++] + " - " + spikes[i]);
        }
    }

    private static long getTimeDelayAverage(String[] strSpikes) throws IOException {
        FlowStats delayStats = new FlowStats();
        long phoneTime;
        long actigraphTime;
        for (int i = 0; i < strSpikes.length / 2; i++) {
            phoneTime = Csv.timestampStrToNanos(strSpikes[2 * i], SPIKE_TIMESTAMP_FORMAT);
            actigraphTime = Csv.timestampStrToNanos(strSpikes[2 * i + 1], SPIKE_TIMESTAMP_FORMAT);
            delayStats.add(actigraphTime - phoneTime);
        }
        return Double.valueOf(delayStats.mean()).longValue();
    }

}
