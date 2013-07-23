package com.joffrey_bion.csv_epoch_synchronizer;

import java.io.IOException;

import javax.swing.SwingUtilities;

import com.joffrey_bion.csv.Csv.NotACsvFileException;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats.K4b2StatsCalculator;
import com.joffrey_bion.csv.CsvWriter;
import com.joffrey_bion.file_processor_window.JFileProcessorWindow;
import com.joffrey_bion.file_processor_window.file_picker.FilePicker;
import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;

public class RestingStatsCalculator {
    
    private static final int NB_ARGS = 2;
    private static final int SOURCE = 0;
    private static final int NB_MARKERS = 1;
    
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
                calculateStats(args[SOURCE], null, Integer.parseInt(args[NB_MARKERS]), false);
            } catch (NumberFormatException e) {
                System.err.println("The number of synchornization markers must be an integer.");
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
        final ArgsPanelRSC argsPanelRSC = new ArgsPanelRSC(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Resting Stats Calculator",
                "Calculate", filePickers, argsPanelRSC) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                this.clearLog();
                try {
                    calculateStats(inPaths[0], outPaths[0], argsPanelRSC.getNbSyncMarkers(),
                            argsPanelRSC.shouldWriteOutput());
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
        CsvWriter writer = output ? new CsvWriter(outputFilename) : null;
        System.out.println("Computing stats...");
        System.out.println(k4.getStats(nbSyncMarkers));
        if (output) {
            writer.close();
        }
    }
}
