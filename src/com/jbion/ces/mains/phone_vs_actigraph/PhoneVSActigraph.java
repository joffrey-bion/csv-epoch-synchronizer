package com.jbion.ces.mains.phone_vs_actigraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.jbion.ces.config.Config;
import com.jbion.ces.phone.RawToEpConverter;
import com.jbion.ces.phone.decision.LabelAppender;
import com.jbion.utils.classification.ConfusionMatrix;
import com.jbion.utils.dates.DateHelper;
import com.jbion.utils.fpgui.LookAndFeel;
import com.jbion.utils.fpgui.fpickers.FilePicker;
import com.jbion.utils.fpgui.fpickers.JFilePickersPanel;
import com.jbion.utils.fpgui.gwindows.JFileProcessorWindow;
import com.jbion.utils.xml.serialization.parameters.SpecificationNotMetException;
import com.jbion.utils.xml.serialization.parameters.Parameters.MissingParameterException;

/**
 * This program is meant to create a Weka-ready dataset based on the given raw phone
 * samples and the actigraph's epochs. For more information about the parameters,
 * check the {@link PvAParams} class.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class PhoneVSActigraph {

    private static final String VALIDATE_SWITCH = "-v";
    private static final String TRAIN_SWITCH = "-t";

    static final int INPUT_PHONE = 0;
    static final int INPUT_ACTIGRAPH = 1;
    static final int INPUT_PARTICIPANT = 2;
    static final int OUTPUT_TRAINING_SET = 0;
    static final int OUTPUT_VALIDATION = 1;

    private static enum Mode {
        VALIDATION,
        TRAINING,
        NONE;
    }

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
            Mode mode = getMode(args);
            if (mode == Mode.NONE) {
                printUsage();
                return;
            }
            // console version, process parameter files one by one.
            for (String xmlParamsFile : args) {
                if (xmlParamsFile.equals(VALIDATE_SWITCH) || xmlParamsFile.equals(TRAIN_SWITCH)) {
                    continue; // skip the switch
                }
                System.out.println("------[ " + xmlParamsFile + " ]---------------------");
                System.out.println();
                try {
                    PvAParams params = new PvAParams(xmlParamsFile);
                    createDataset(params);
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
    
    private static void printUsage() {
        System.out.println("Usage: java -jar PhoneVSActigraph.jar {-t|-v} <params.xml> [other-params.xml]*");
        System.out.println("-t\tTraining mode");
        System.out.println("-v\tValidation mode");
    }

    private static Mode getMode(String[] args) {
        for (String arg : args) {
            if (arg.equals(VALIDATE_SWITCH)) {
                return Mode.VALIDATION;
            } else if (arg.equals(TRAIN_SWITCH)) {
                return Mode.TRAINING;
            }
        }
        return Mode.NONE;
    }

    /**
     * Starts the GUI.
     */
    private static void openWindow() {
        LookAndFeel.setSystemLookAndFeel();
        // file pickers source and destination
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {
                "Phone raw file", "Actigraph epoch file", "Participant file" }, new String[] {
                "Training set output file", "Validation output file" });
        FilePicker[] ifps = filePickers.getInputFilePickers();
        ifps[INPUT_PHONE].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ifps[INPUT_ACTIGRAPH].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ifps[INPUT_PARTICIPANT].addFileTypeFilter(".xml", "XML Participant file");
        FilePicker[] ofps = filePickers.getInputFilePickers();
        ofps[OUTPUT_TRAINING_SET].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ofps[OUTPUT_VALIDATION].addFileTypeFilter(".csv", "Comma-Separated Values file");
        final PvAArgsPanel pvAArgsPanel = new PvAArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Phone-VS-Actigraph", filePickers,
                pvAArgsPanel, "Create dataset", "Validate") {
            @Override
            public void process(String[] inPaths, String[] outPaths, int processBtnIndex) {
                this.clearLog();
                try {
                    PvAParams params = new PvAParams();
                    pvAArgsPanel.getParameters(params);
                    if (processBtnIndex == 0) {
                        createDataset(params);
                    } else if (processBtnIndex == 1) {
                        analyze(params);
                    }
                } catch (ParseException e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                } catch (MissingParameterException e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                } catch (Exception e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
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
    private static void createDataset(PvAParams params) {
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
            System.out.println("> Intermediate epoch file created (" + phoneEpFilename + ")");
            System.out.println();
            System.out.println("Merging phone epochs with actigraph labels...");
            PvAMerger merger = new PvAMerger(phoneEpFilename, params.actigraphEpFile,
                    params.outputTrainingSetFile, params.actigraphFileFormat);
            merger.createLabeledFile(params);
            System.out.println("> Dataset file created (" + params.outputTrainingSetFile + ")");
            if (Config.get().deleteTempFiles) {
                delete("Temp epoch file", phoneEpFilename);
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

    /**
     * Creates a Weka-ready dataset based on the given raw phone samples and the
     * actigraph's epochs.
     * 
     * @param params
     *            The parameters for the instance to deal with.
     * @return The confusion matrix describing the results.
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
            try {
                LabelAppender.appendLabels(params);
            } catch (SAXException e) {
                System.err.println("Incorrect classifier file: " + e.getMessage() + " ("
                        + e.getClass().getSimpleName() + ")");
            }
            System.out
                    .println("> Temp labeled epoch file created (" + phoneEpLabeledFilename + ")");
            System.out.println();
            System.out.println("Merging phone labeled epochs with actigraph labels...");
            String twoLabeledFilename = params.getTwoLabeledFile();
            PvAMerger merger = new PvAMerger(phoneEpLabeledFilename, params.actigraphEpFile,
                    twoLabeledFilename, params.actigraphFileFormat);
            merger.createLabeledFile(params);
            System.out.println("> Temp 2-labeled file created (" + twoLabeledFilename + ")");
            ConfusionMatrix<String> cm = PvAAnalyzer.analyzeClassification(params);
            if (Config.get().deleteTempFiles) {
                delete("Temp epoch file", phoneEpFilename);
                delete("Temp labeled epoch file", phoneEpLabeledFilename);
                delete("Temp double labels file", twoLabeledFilename);
            } else {
                System.out.println("> Temporary files kept");
            }
            System.out.println();
            System.out.println("Writing results...");
            try {
                new PrintableConfusionMatrix(cm).writeResults(params.participantFile,
                        params.outputValidationFile);
            } catch (SAXException | SpecificationNotMetException e) {
                System.err.println("Incorrect participant file: " + e.getMessage() + " ("
                        + e.getClass().getSimpleName() + ")");
            }
            System.out.println("Results written in " + params.outputValidationFile);
            System.out.println();
            System.out.println("Processing time: " + (System.currentTimeMillis() - start) + "ms");
            return cm;
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private static void delete(String description, String filename) {
        File toDelete = new File(filename);
        if (toDelete.delete()) {
            System.out.println("> " + description + " deleted (" + filename + ")");
        } else {
            System.err.println("> " + description + " couldn't be deleted.");
        }
    }

    @SuppressWarnings("unused")
    private static void deleteNIO(String description, String filename) {
        try {
            java.nio.file.Files.delete(Paths.get(filename));
            System.out.println("> " + description + " deleted (" + filename + ")");
        } catch (IOException e) {
            System.err
                    .println("> " + description + " couldn't be deleted (" + e.getMessage() + ")");
        }
    }
}
