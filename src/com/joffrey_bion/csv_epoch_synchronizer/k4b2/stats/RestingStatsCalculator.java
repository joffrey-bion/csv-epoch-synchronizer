package com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats;

import java.io.IOException;

import javax.swing.SwingUtilities;

import com.joffrey_bion.csv.Csv.NotACsvFileException;
import com.joffrey_bion.csv.CsvWriter;
import com.joffrey_bion.file_processor_window.JFileProcessorWindow;
import com.joffrey_bion.file_processor_window.file_picker.FilePicker;
import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;

public class RestingStatsCalculator {
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
        } else {
            // TODO command line arguments (XML? raw arguments?)
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
        final ArgsPanel argsPanel = new ArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Resting Stats Calculator",
                "Calculate", filePickers, argsPanel) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                this.clearLog();
                try {
                    calculateStats(inPaths[0], outPaths[0], argsPanel.getNbSyncMarkers(),
                            argsPanel.shouldWriteOutput());
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
        Results[] results = k4.getStats(nbSyncMarkers);
        for (Results res : results) {
            System.out.println(res.toString());
        }
        if (output) {
            writer.close();
        }
    }
}
