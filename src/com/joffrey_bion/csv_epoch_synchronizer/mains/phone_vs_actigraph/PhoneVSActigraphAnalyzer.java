package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.phone.RawToEpConverter;
import com.joffrey_bion.csv_epoch_synchronizer.phone.decision.LabelAppender;
import com.joffrey_bion.generic_guis.LookAndFeel;
import com.joffrey_bion.generic_guis.file_picker.FilePicker;
import com.joffrey_bion.generic_guis.file_picker.JFilePickersPanel;
import com.joffrey_bion.generic_guis.file_processor.JFileProcessorWindow;
import com.joffrey_bion.utils.classification.ConfusionMatrix;
import com.joffrey_bion.utils.dates.DateHelper;
import com.joffrey_bion.xml_parameters_serializer.Parameters.MissingParameterException;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PhoneVSActigraphAnalyzer {
    
    static final int INPUT_PHONE = 0;
    static final int INPUT_ACTIGRAPH = 1;
    static final int OUTPUT = 0;
    
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
                    analyze(params);
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
        LookAndFeel.setSystemLookAndFeel();
        // file pickers source and destination
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {
                "Phone raw file", "Actigraph epoch file" }, new String[] {});
        for (FilePicker fp : filePickers.getInputFilePickers()) {
            fp.addFileTypeFilter(".csv", "Comma-Separated Values file");
        }
        final PvAArgsPanel pvAArgsPanel = new PvAArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Phone-VS-Actigraph Analyzer",
                "Process", filePickers, pvAArgsPanel) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                this.clearLog();
                try {
                    PvAParams params = new PvAParams();
                    pvAArgsPanel.getParameters(params);
                    analyze(params);
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                } catch (MissingParameterException e) {
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
     *            The parameters for the instance to deal with.
     */
    public static ConfusionMatrix<String> analyze(PvAParams params) {
        long start = System.currentTimeMillis();
        try {
            System.out.println("Computing phone-to-actigraph delay...");
            System.out.println("> Delay average: " + params.getDelayNano() / 1000000 + "ms");
            System.out.println("> Actigraph start time: "
                    + DateHelper.toDateTimeMillis(params.startTime / 1000000));
            long phoneStartTime = params.startTime - params.getDelayNano();
            System.out.println("> Phone start time: "
                    + DateHelper.toDateTimeMillis(phoneStartTime / 1000000));
            System.out.println();
            System.out.println("Accumulating phone raw data into actigraph-timestamped epochs...");
            String phoneEpFilename = params.getPhoneEpochFilePath();
            RawToEpConverter.createEpochsFile(params);
            System.out.println("> Temp epoch file created (" + phoneEpFilename + ")");
            System.out.println();
            System.out.println("Labeling phone data with the classifier...");
            String phoneEpLabeledFilename = params.getLabeledDatasetFilePath();
            LabelAppender.appendLabels(params);
            System.out
                    .println("> Temp labeled epoch file created (" + phoneEpLabeledFilename + ")");
            System.out.println();
            System.out.println("Merging phone labeled epochs with actigraph labels...");
            String twoLabeledFilename = params.getTwoLabeledFile();
            PvAMerger merger = new PvAMerger(phoneEpLabeledFilename, params.actigraphEpFilename,
                    twoLabeledFilename, params.actigraphFileFormat);
            merger.createLabeledFile(params);
            System.out.println("> Temp 2-labeled file created (" + twoLabeledFilename + ")");
            ConfusionMatrix<String> cm = PvAAnalyzer.analyzeClassification(params);
            if (Config.get().deleteTempFiles) {
                if (new File(phoneEpFilename).delete()) {
                    System.out.println("> Temp epoch file deleted (" + phoneEpFilename + ")");
                } else {
                    System.err.println("> Temp epoch file couldn't be deleted.");
                }
                if (new File(phoneEpLabeledFilename).delete()) {
                    System.out.println("> Temp labeled epoch file file deleted (" + phoneEpFilename + ")");
                } else {
                    System.err.println("> Temp labeled epoch file couldn't be deleted.");
                }
                if (new File(twoLabeledFilename).delete()) {
                    System.out.println("> Temp 2-labeled file deleted (" + twoLabeledFilename + ")");
                } else {
                    System.err.println("> Temp 2-labeled file couldn't be deleted.");
                }
            } else {
                System.out.println("> Temporary files kept");
            }
            System.out.println();
            System.out.println("Processing time: " + (System.currentTimeMillis() - start) + "ms");
            return cm;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (SAXException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }
}
