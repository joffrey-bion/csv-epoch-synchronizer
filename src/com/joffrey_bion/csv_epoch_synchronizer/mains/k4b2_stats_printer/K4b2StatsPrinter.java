package com.joffrey_bion.csv_epoch_synchronizer.mains.k4b2_stats_printer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.SwingUtilities;

import com.joffrey_bion.csv.Csv.NotACsvFileException;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Results;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2StatsCalculator;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Phase;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats.RestingResults;
import com.joffrey_bion.file_processor_window.JFileProcessorWindow;
import com.joffrey_bion.file_processor_window.file_picker.FilePicker;
import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;

public class K4b2StatsPrinter {
    
    private static final int NB_ARGS = 2;
    private static final int ARG_SOURCE = 0;
    private static final int ARG_NB_MARKERS = 1;
    
    /**
     * Choose between GUI or console version according to the number of arguments.
     * 
     * @param args
     *            No arguments will start the GUI, otherwise XML parameter filenames
     *            have to be specified. Each one will be processed at a time.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    openWindow();
                }
            });
        } else if (args.length == NB_ARGS){
            try {
                calculateStats(args[ARG_SOURCE], null, Integer.parseInt(args[ARG_NB_MARKERS]), false);
            } catch (NumberFormatException e) {
                System.err.println("The number of synchronization markers must be an integer.");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.err.println("2 parameters needed: <source file> <nb of sync markers>");
        }
    }

    /**
     * Starts the GUI.
     */
    private static void openWindow() {
        JFileProcessorWindow.setSystemLookAndFeel();
        // file pickers source and destination
        final JFilePickersPanel filePickers = new JFilePickersPanel("K4b2 CSV file", "Output file");
        for (FilePicker fp : filePickers.getInputFilePickers()) {
            fp.addFileTypeFilter(".csv", "Comma-Separated Values file");
        }
        for (FilePicker fp : filePickers.getOutputFilePickers()) {
            fp.addFileTypeFilter(".txt", "Text file");
        }
        final KSPArgsPanel kSPArgsPanel = new KSPArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Resting Stats Calculator",
                "Calculate", filePickers, kSPArgsPanel) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                this.clearLog();
                try {
                    calculateStats(inPaths[0], outPaths[0], kSPArgsPanel.getNbSyncMarkers(),
                            kSPArgsPanel.shouldWriteOutput());
                } catch (NotACsvFileException e) {
                    System.err.println(e.getMessage());
                    System.err.println("Please open the file in Excel and save it as a CSV file.");
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }

    private static void calculateStats(String inputFilename, String outputFilename,
            Integer nbSyncMarkers, boolean output) throws IOException {
        System.out.println("Opening file...");
        K4b2StatsCalculator k4 = new K4b2StatsCalculator(inputFilename);
        BufferedWriter writer = output ? new BufferedWriter(new FileWriter(outputFilename)) : null;
        System.out.println("Computing stats...");
        Results stats = k4.getStats(nbSyncMarkers);
        System.out.println("Done.\n");
        System.out.println("*** RESTING STABILITY ***");
        System.out.println(((RestingResults) stats.get(Phase.RESTING)).allToString());
        System.out.println("*** STATISTICS PER PHASE ***");
        System.out.println(stats.toString());
        if (output) {
            writer.write(stats.toString());
            writer.close();
        }
    }
}
