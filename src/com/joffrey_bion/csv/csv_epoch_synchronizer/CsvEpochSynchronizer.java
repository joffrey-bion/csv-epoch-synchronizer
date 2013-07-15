package com.joffrey_bion.csv.csv_epoch_synchronizer;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv.Csv;
import com.joffrey_bion.csv.csv_epoch_synchronizer.parameters.Config;
import com.joffrey_bion.csv.csv_epoch_synchronizer.parameters.Parameters;
import com.joffrey_bion.csv.csv_epoch_synchronizer.parameters.InstanceRawParameters;
import com.joffrey_bion.file_processor_window.JFileProcessorWindow;
import com.joffrey_bion.file_processor_window.file_picker.FilePicker;
import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;
import com.joffrey_bion.utils.dates.DateHelper;

import com.joffrey_bion.csv.csv_epoch_synchronizer.ui.ArgsPanel;

/**
 * This program is meant to create a Weka-ready dataset based on the given raw phone
 * samples and the actigraph's epochs. For more information about the
 * com.joffrey_bion.csv.csv_epoch_synchronizer.parameters, check the
 * {@link Parameters} class.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class CsvEpochSynchronizer {

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
            // console version, process parameter files one by one.
            for (String xmlParamsFile : args) {
                System.out.println("------[ " + xmlParamsFile + " ]---------------------");
                System.out.println();
                try {
                    InstanceRawParameters rawParams = InstanceRawParameters.load(xmlParamsFile);
                    createDataset(new Parameters(rawParams));
                } catch (Parameters.ArgumentFormatException e) {
                    System.err.println(e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                System.out.println();
            }
        }
    }

    /**
     * Starts the GUI.
     */
    private static void openWindow() {
        // windows system look and feel for the window
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
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
                    InstanceRawParameters rawParams = argsPanel.getRawParameters(inPaths[0], inPaths[1],
                            outPaths[0]);
                    createDataset(new Parameters(rawParams));
                } catch (Parameters.ArgumentFormatException e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates a Weka-ready dataset based on the given raw phone samples and the
     * actigraph's epochs.
     * 
     * @param params
     *            The com.joffrey_bion.csv.csv_epoch_synchronizer.parameters for the
     *            instance to deal with.
     */
    private static void createDataset(Parameters params) {
        long timerStart = System.currentTimeMillis();
        try {
            System.out.println("Computing phone-to-actigraph delay...");
            System.out.println("> Phone-to-actigraph delay average: " + params.getDelay() / 1000000 + "ms");
            System.out.println("> Actigraph start time: "
                    + DateHelper.toDateTimeMillis(params.startTime / 1000000));
            long phoneStartTime = params.startTime - params.getDelay();
            System.out.println("> Phone start time: "
                    + DateHelper.toDateTimeMillis(phoneStartTime / 1000000));
            System.out.println("");
            System.out.println("Accumulating phone raw data into actigraph-timestamped epochs...");
            String phoneEpFilename = Csv.removeCsvExtension(params.phoneRawFilename)
                    + "-epochs.csv";
            RawToEpConverter conv = new RawToEpConverter(params.phoneRawFilename, phoneEpFilename);
            conv.createEpochsFile(params);
            System.out.println("> Intermediate epoch file created (" + phoneEpFilename + ")");
            System.out.println("");
            System.out.println("Merging phone epochs with actigraph labels...");
            EpLabelsMerger merger = new EpLabelsMerger(phoneEpFilename, params.actigraphEpFilename,
                    params.outputFilename);
            merger.createLabeledFile(params);
            System.out.println("> Dataset file created (" + params.outputFilename + ").");
            if (Config.get().deleteIntermediateFile) {
                if (new File(phoneEpFilename).delete()) {
                    System.out.println("> Intermediate epoch file deleted (" + phoneEpFilename + ").");
                } else {
                    System.err.println("> Delete operation has failed.");
                }
            } else {
                System.out.println("> Intermediate file kept (" + phoneEpFilename + ").");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println("");
        System.out.println("Total processing time: " + (System.currentTimeMillis() - timerStart) + "ms");
    }
}
