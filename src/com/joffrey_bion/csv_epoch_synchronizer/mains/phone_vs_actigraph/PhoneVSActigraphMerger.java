package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.phone.RawToEpConverter;
import com.joffrey_bion.file_processor_window.JFileProcessorWindow;
import com.joffrey_bion.file_processor_window.file_picker.FilePicker;
import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;
import com.joffrey_bion.utils.dates.DateHelper;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

/**
 * This program is meant to create a Weka-ready dataset based on the given raw phone
 * samples and the actigraph's epochs. For more information about the parameters,
 * check the {@link OldPvAParams} class.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class PhoneVSActigraphMerger {

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
                    PvAParams params = new PvAParams(xmlParamsFile);
                    createDataset(params);
                } catch (IOException e) {
                    System.err.println("I/O error: " + e.getMessage());
                } catch (SAXException e) {
                    System.err.println("XML error: " + e.getMessage());
                } catch (SpecificationNotMetException e) {
                    System.err.println("Parameters format error : ");
                    System.err.println(e.getMessage());
                }
                System.out.println();
            }
        }
    }

    /**
     * Starts the GUI.
     */
    private static void openWindow() {
        JFileProcessorWindow.setSystemLookAndFeel();
        // file pickers source and destination
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {
                "Phone raw file", "Actigraph epoch file" }, "Output file");
        for (FilePicker fp : filePickers.getInputFilePickers()) {
            fp.addFileTypeFilter(".csv", "Comma-Separated Values file");
        }
        for (FilePicker fp : filePickers.getOutputFilePickers()) {
            fp.addFileTypeFilter(".csv", "Comma-Separated Values file");
        }
        final PvAArgsPanel pvAArgsPanel = new PvAArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Phone-VS-Actigraph Synchronizer",
                "Process", filePickers, pvAArgsPanel) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                this.clearLog();
                try {
                    PvAParams params = new PvAParams();
                    pvAArgsPanel.getParameters(params);
                    createDataset(params);
                } catch (ParseException e) {
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
     *            The com.joffrey_bion.csv_epoch_synchronizer.config for the instance
     *            to deal with.
     */
    private static void createDataset(PvAParams params) {
        long start = System.currentTimeMillis();
        try {
            System.out.println("Computing phone-to-actigraph delay...");
            System.out.println("> Delay average: " + params.getDelay() / 1000000 + "ms");
            System.out.println("> Actigraph start time: "
                    + DateHelper.toDateTimeMillis(params.startTime / 1000000));
            long phoneStartTime = params.startTime - params.getDelay();
            System.out.println("> Phone start time: "
                    + DateHelper.toDateTimeMillis(phoneStartTime / 1000000));
            System.out.println();
            System.out.println("Accumulating phone raw data into actigraph-timestamped epochs...");
            String phoneEpFilename = params.getPhoneEpochFilePath();
            RawToEpConverter.createEpochsFile(params);
            System.out.println("> Intermediate epoch file created (" + phoneEpFilename + ")");
            System.out.println();
            System.out.println("Merging phone epochs with actigraph labels...");
            PvAMerger merger = new PvAMerger(phoneEpFilename, params.actigraphEpFilename,
                    params.outputFilename, params.actigraphFileFormat);
            merger.createLabeledFile(params);
            System.out.println("> Dataset file created (" + params.outputFilename + ")");
            if (Config.get().deleteIntermediateFile) {
                if (new File(phoneEpFilename).delete()) {
                    System.out.println("> Temporary epoch file deleted (" + phoneEpFilename + ")");
                } else {
                    System.err.println("> Temporary file couldn't be deleted.");
                }
            } else {
                System.out.println("> Temporary file kept (" + phoneEpFilename + ")");
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println();
        System.out.println("Processing time: " + (System.currentTimeMillis() - start) + "ms");
    }
}
