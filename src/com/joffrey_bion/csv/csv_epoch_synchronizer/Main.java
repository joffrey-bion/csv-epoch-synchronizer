package com.joffrey_bion.csv.csv_epoch_synchronizer;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.joffrey_bion.csv.Csv;
import com.joffrey_bion.csv.csv_epoch_synchronizer.csv_manipulation.DateHelper;
import com.joffrey_bion.csv.csv_epoch_synchronizer.row_statistics.FlowStats;
import com.joffrey_bion.file_processor_window.ConsoleLogger;
import com.joffrey_bion.file_processor_window.JFilePickersPanel;
import com.joffrey_bion.file_processor_window.JFileProcessorWindow;
import com.joffrey_bion.file_processor_window.Logger;

import com_joffrey_bion.csv.csv_epoch_synchronizer.ui.ArgsPanel;
import com_joffrey_bion.csv.csv_epoch_synchronizer.ui.ArgsPanel.IncompleteSpikeException;

public class Main {

    private static final int ARG_PHONE_RAW_FILE = 0;
    private static final int ARG_ACTIGRAPH_EPOCH_FILE = 1;
    private static final int ARG_OUTPUT_FILE = 2;
    private static final int ARG_START_TIME = 3;
    private static final int ARG_STOP_TIME = 4;

    private static final int NB_ARGS_BEFORE_SPIKES = 5;

    private static final String START_STOP_TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final String SPIKE_TIMESTAMP_FORMAT = "HH:mm:ss.SSS";
    private static final boolean DELETE_INTERMEDIATE_FILE = true;

    private static void printUsage() {
        System.out
                .println("usage: java Main <phone-raw-filename> <actig-ep-filename> <output-filename> <actig-start-time> <actig-stop-time> [phone-spike actig-spike]+");
        System.out.println("start/stop time format: " + START_STOP_TIMESTAMP_FORMAT);
        System.out.println("spikes time format: " + SPIKE_TIMESTAMP_FORMAT);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    openWindow();
                }
            });
            return;
        } else {
            InstanceProperties props = parseCommandline(args);
            if (props != null) {
                createDataset(props, new ConsoleLogger());
            }
        }
    }

    private static void openWindow() {
        // windows system look and feel for the window
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
        final ArgsPanel argsPanel = new ArgsPanel();
        // file pickers source and destination
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {
                "Phone raw file", "Actigraph epoch file" }, "Output file");
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Epoch Synchronizer", "Process",
                filePickers, argsPanel) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                this.clearLog();
                InstanceProperties props = getArguments(inPaths[0], inPaths[1], outPaths[0],
                        argsPanel, this);
                if (props != null) {
                    createDataset(props, this);
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }

    private static InstanceProperties getArguments(String phoneRawFile, String actigEpFile,
            String outputFile, ArgsPanel argsPanel, Logger log) {
        InstanceProperties props = new InstanceProperties();
        if (phoneRawFile == null || phoneRawFile.equals("")) {
            log.printErr("Phone raw data file must be specified.");
            return null;
        } else {
            props.phoneRawFilename = phoneRawFile;
        }
        if (actigEpFile == null || actigEpFile.equals("")) {
            log.printErr("Actigraph epoch file must be specified.");
            return null;
        } else {
            props.actigraphEpFilename = actigEpFile;
        }
        if (outputFile != null && !outputFile.equals("")) {
            props.outputFilename = outputFile;
        }
        try {
            props.startTime = Csv.timestampStrToNanos(argsPanel.getStartTime(),
                    START_STOP_TIMESTAMP_FORMAT);
        } catch (ParseException e) {
            log.printErr("Incorrect format for start timestamp.");
            return null;
        }
        try {
            props.stopTime = Csv.timestampStrToNanos(argsPanel.getStopTime(),
                    START_STOP_TIMESTAMP_FORMAT);
        } catch (ParseException e) {
            log.printErr("Incorrect format for stop timestamp.");
            return null;
        }
        try {
            props.delay = getTimeDelayAverage(argsPanel.getSpikes());
        } catch (IncompleteSpikeException e) {
            log.printErr(e.getMessage());
            return null;
        } catch (ParseException e) {
            log.printErr("Incorrect format for spikes timestamp.");
            return null;
        }
        return props;
    }

    private static InstanceProperties parseCommandline(String[] args) {
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
        InstanceProperties props = new InstanceProperties();
        props.phoneRawFilename = args[ARG_PHONE_RAW_FILE];
        props.actigraphEpFilename = args[ARG_ACTIGRAPH_EPOCH_FILE];
        props.outputFilename = args[ARG_OUTPUT_FILE];
        try {
            props.startTime = Csv.timestampStrToNanos(args[ARG_START_TIME],
                    START_STOP_TIMESTAMP_FORMAT);
            props.stopTime = Csv.timestampStrToNanos(args[ARG_STOP_TIME],
                    START_STOP_TIMESTAMP_FORMAT);
            props.delay = getTimeDelayAverage(spikeArgs);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            return null;
        }
        return props;
    }

    private static void createDataset(InstanceProperties props, Logger log) {
        long timerStart = System.currentTimeMillis();
        try {
            log.println("> Phone-to-actigraph delay average: " + props.delay / 1000000 + "ms");
            log.println("");
            log.println("Actigraph start time: "
                    + DateHelper.toDateTimeMillis(props.startTime / 1000000));
            log.println("Accumulating phone raw data into actigraph-timestamped epochs...");
            String phoneEpFilename = Csv.removeCsvExtension(props.phoneRawFilename) + "-epochs.csv";
            RawToEpConverter conv = new RawToEpConverter(props.phoneRawFilename, phoneEpFilename);
            conv.createEpochsFile(props);
            log.println("> Intermediate file " + phoneEpFilename + " successfully created.");
            log.println("");
            log.println("Merging phone epochs with actigraph labels...");
            EpLabelsMerger merger = new EpLabelsMerger(phoneEpFilename, props.actigraphEpFilename,
                    props.outputFilename);
            merger.createLabeledFile(props);
            log.println("> Dataset file " + props.outputFilename + " successfully created.");
            if (DELETE_INTERMEDIATE_FILE) {
                if (new File(phoneEpFilename).delete()) {
                    log.println("> Intermediate file deleted(" + phoneEpFilename + ").");
                } else {
                    log.printErr("> Delete operation has failed.");
                }
            } else {
                log.println("> Intermediate file kept (" + phoneEpFilename + ").");
            }
        } catch (IOException e) {
            log.printErr(e.getMessage());
        }
        log.println("");
        log.println("Total processing time: " + (System.currentTimeMillis() - timerStart) + "ms");
    }

    private static void printSpikes(String[] spikes) {
        System.out.println("Spikes (phone - actigraph): ");
        for (int i = 0; i < spikes.length; i++) {
            System.out.println(spikes[i++] + " - " + spikes[i]);
        }
    }

    private static long getTimeDelayAverage(String[] strSpikes) throws ParseException {
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
