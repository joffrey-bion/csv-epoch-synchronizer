package com.joffrey_bion.csv.csv_epoch_synchronizer;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import parameters.Parameters;
import parameters.RawParameters;
import parameters.XmlSaver;

import com.joffrey_bion.csv.Csv;
import com.joffrey_bion.csv.csv_epoch_synchronizer.csv_manipulation.DateHelper;
import com.joffrey_bion.file_processor_window.ConsoleLogger;
import com.joffrey_bion.file_processor_window.FilePicker;
import com.joffrey_bion.file_processor_window.JFilePickersPanel;
import com.joffrey_bion.file_processor_window.JFileProcessorWindow;
import com.joffrey_bion.file_processor_window.Logger;

import com_joffrey_bion.csv.csv_epoch_synchronizer.ui.ArgsPanel;

public class CsvEpochSynchronizer {

    public static void main(String[] args) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    openWindow();
                }
            });
        } else {
            for (String xmlParamsFile : args) {
                try {
                    RawParameters rawParams = XmlSaver.load(xmlParamsFile);
                    createDataset(new Parameters(rawParams), new ConsoleLogger());
                } catch (Parameters.ArgumentFormatException e) {
                    System.err.println(e.getMessage());
                }
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
        // file pickers source and destination
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {
                "Phone raw file", "Actigraph epoch file" }, "Output file");
        for (FilePicker fp : filePickers.getInputFilePickers()) {
            fp.addFileTypeFilter(".csv", "Comma-Separated Values file");
        }
        for (FilePicker fp : filePickers.getOutputFilePickers()) {
            fp.addFileTypeFilter(".csv", "Comma-Separated Values file");
        }
        final ArgsPanel argsPanel = new ArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Epoch Synchronizer", "Process",
                filePickers, argsPanel) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                this.clearLog();
                try {
                    RawParameters rawParams = argsPanel.getRawParameters(inPaths[0], inPaths[1], outPaths[0]);
                    createDataset(new Parameters(rawParams), this);
                } catch (Parameters.ArgumentFormatException e) {
                    this.printErr(e.getMessage());
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }
    
    private static void createDataset(Parameters props, Logger log) {
        long timerStart = System.currentTimeMillis();
        try {
            log.println("> Phone-to-actigraph delay average: " + props.getDelay() / 1000000 + "ms");
            log.println("");
            log.println("Actigraph start time: "
                    + DateHelper.toDateTimeMillis(props.startTime / 1000000));
            log.println("Accumulating phone raw data into actigraph-timestamped epochs...");
            String phoneEpFilename = Csv.removeCsvExtension(props.phoneRawFilename) + "-epochs.csv";
            RawToEpConverter conv = new RawToEpConverter(props.phoneRawFilename, phoneEpFilename);
            conv.createEpochsFile(props);
            log.println("> Intermediate file successfully created (" + phoneEpFilename + ")");
            log.println("");
            log.println("Merging phone epochs with actigraph labels...");
            EpLabelsMerger merger = new EpLabelsMerger(phoneEpFilename, props.actigraphEpFilename,
                    props.outputFilename);
            merger.createLabeledFile(props);
            log.println("> Dataset file successfully created (" + props.outputFilename + ").");
            if (props.deleteIntermediateFile) {
                if (new File(phoneEpFilename).delete()) {
                    log.println("> Intermediate file deleted (" + phoneEpFilename + ").");
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
}
