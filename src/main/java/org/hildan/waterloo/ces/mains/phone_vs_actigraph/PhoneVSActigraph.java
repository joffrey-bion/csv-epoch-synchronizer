package org.hildan.waterloo.ces.mains.phone_vs_actigraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.ParseException;

import javax.swing.SwingUtilities;

import org.hildan.utils.ai.classification.ConfusionMatrix;
import org.hildan.utils.dates.DateHelper;
import org.hildan.utils.fpgui.LookAndFeel;
import org.hildan.utils.fpgui.fpickers.FilePicker;
import org.hildan.utils.fpgui.fpickers.JFilePickersPanel;
import org.hildan.utils.fpgui.gwindows.JFileProcessorWindow;
import org.hildan.utils.xml.serialization.parameters.Parameters.MissingParameterException;
import org.hildan.utils.xml.serialization.parameters.SpecificationNotMetException;
import org.hildan.waterloo.ces.config.Config;
import org.hildan.waterloo.ces.phone.RawToEpConverter;
import org.hildan.waterloo.ces.phone.decision.LabelAppender;
import org.xml.sax.SAXException;

/**
 * This program is meant to create a Weka-ready dataset based on the given raw phone samples and the
 * actigraph's epochs. For more information about the parameters, check the {@link PvAParams} class.
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
     *            No arguments will start the GUI, otherwise XML parameter filenames have to be
     *            specified. Each one will be processed at a time.
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
            final Mode mode = getMode(args);
            if (mode == Mode.NONE) {
                printUsage();
                return;
            }
            // console version, process parameter files one by one.
            for (final String xmlParamsFile : args) {
                if (xmlParamsFile.equals(VALIDATE_SWITCH) || xmlParamsFile.equals(TRAIN_SWITCH)) {
                    continue; // skip the switch
                }
                System.out.println("------[ " + xmlParamsFile + " ]---------------------");
                System.out.println();
                try {
                    final PvAParams params = new PvAParams(xmlParamsFile);
                    createDataset(params);
                    analyze(params);
                } catch (final IOException e) {
                    System.err.println("I/O error: " + e.getMessage());
                } catch (final SAXException e) {
                    System.err.println("XML error: " + e.getMessage());
                } catch (final SpecificationNotMetException e) {
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
        for (final String arg : args) {
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
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {"Phone raw file",
                "Actigraph epoch file", "Participant file"}, new String[] {"Training set output file",
                "Validation output file"});
        final FilePicker[] ifps = filePickers.getInputFilePickers();
        ifps[INPUT_PHONE].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ifps[INPUT_ACTIGRAPH].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ifps[INPUT_PARTICIPANT].addFileTypeFilter(".xml", "XML Participant file");
        final FilePicker[] ofps = filePickers.getInputFilePickers();
        ofps[OUTPUT_TRAINING_SET].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ofps[OUTPUT_VALIDATION].addFileTypeFilter(".csv", "Comma-Separated Values file");
        final PvAArgsPanel pvAArgsPanel = new PvAArgsPanel(filePickers);
        @SuppressWarnings("serial")
        final JFileProcessorWindow frame = new JFileProcessorWindow("Phone-VS-Actigraph", filePickers, pvAArgsPanel,
                "Create dataset", "Validate") {

            @Override
            public void process(String[] inPaths, String[] outPaths, int processBtnIndex) {
                clearLog();
                try {
                    final PvAParams params = new PvAParams();
                    pvAArgsPanel.getParameters(params);
                    if (processBtnIndex == 0) {
                        createDataset(params);
                    } else if (processBtnIndex == 1) {
                        analyze(params);
                    }
                } catch (final ParseException e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                } catch (final MissingParameterException e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                } catch (final Exception e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Creates a Weka-ready dataset based on the given raw phone samples and the actigraph's epochs.
     *
     * @param params
     *            The parameters for the instance to deal with.
     */
    private static void createDataset(PvAParams params) {
        final long start = System.currentTimeMillis();
        try {
            System.out.println("Computing phone-to-actigraph delay...");
            System.out.println("> Delay average: " + params.getDelayNano() / 1000000 + "ms");
            System.out.println("> Actigraph start time: " + DateHelper.toDateTimeMillis(params.startTime / 1000000));
            final long phoneStartTime = params.startTime - params.getDelayNano();
            System.out.println("> Phone start time: " + DateHelper.toDateTimeMillis(phoneStartTime / 1000000));
            System.out.println();
            System.out.println("Accumulating phone raw data into actigraph-timestamped epochs...");
            final String phoneEpFilename = params.getPhoneEpochFilePath();
            RawToEpConverter.createEpochsFile(params);
            System.out.println("> Intermediate epoch file created (" + phoneEpFilename + ")");
            System.out.println();
            System.out.println("Merging phone epochs with actigraph labels...");
            final PvAMerger merger = new PvAMerger(phoneEpFilename, params.actigraphEpFile,
                    params.outputTrainingSetFile, params.actigraphFileFormat);
            merger.createLabeledFile(params);
            System.out.println("> Dataset file created (" + params.outputTrainingSetFile + ")");
            if (Config.get().deleteTempFiles) {
                delete("Temp epoch file", phoneEpFilename);
            } else {
                System.out.println("> Temporary file kept (" + phoneEpFilename + ")");
            }
        } catch (final IOException e) {
            System.err.println(e.getMessage());
            return;
        }
        System.out.println();
        System.out.println("Processing time: " + (System.currentTimeMillis() - start) + "ms");
    }

    /**
     * Creates a Weka-ready dataset based on the given raw phone samples and the actigraph's epochs.
     *
     * @param params
     *            The parameters for the instance to deal with.
     * @return The confusion matrix describing the results.
     */
    public static ConfusionMatrix<String> analyze(PvAParams params) {
        final long start = System.currentTimeMillis();
        try {
            System.out.println("Computing phone-to-actigraph delay...");
            System.out.println("> Delay average: " + params.getDelayNano() / 1000000 + "ms");
            System.out.println("> Actigraph start time: " + DateHelper.toDateTimeMillis(params.startTime / 1000000));
            final long phoneStartTime = params.startTime - params.getDelayNano();
            System.out.println("> Phone start time: " + DateHelper.toDateTimeMillis(phoneStartTime / 1000000));
            System.out.println();
            System.out.println("Accumulating phone raw data into actigraph-timestamped epochs...");
            final String phoneEpFilename = params.getPhoneEpochFilePath();
            RawToEpConverter.createEpochsFile(params);
            System.out.println("> Temp epoch file created (" + phoneEpFilename + ")");
            System.out.println();
            System.out.println("Labeling phone data with the classifier...");
            final String phoneEpLabeledFilename = params.getLabeledDatasetFilePath();
            try {
                LabelAppender.appendLabels(params);
            } catch (final SAXException e) {
                System.err.println("Incorrect classifier file: " + e.getMessage() + " (" + e.getClass().getSimpleName()
                        + ")");
            }
            System.out.println("> Temp labeled epoch file created (" + phoneEpLabeledFilename + ")");
            System.out.println();
            System.out.println("Merging phone labeled epochs with actigraph labels...");
            final String twoLabeledFilename = params.getTwoLabeledFile();
            final PvAMerger merger = new PvAMerger(phoneEpLabeledFilename, params.actigraphEpFile, twoLabeledFilename,
                    params.actigraphFileFormat);
            merger.createLabeledFile(params);
            System.out.println("> Temp 2-labeled file created (" + twoLabeledFilename + ")");
            final ConfusionMatrix<String> cm = PvAAnalyzer.analyzeClassification(params);
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
                new PrintableConfusionMatrix(cm).writeResults(params.participantFile, params.outputValidationFile);
            } catch (SAXException | SpecificationNotMetException e) {
                System.err.println("Incorrect participant file: " + e.getMessage() + " ("
                        + e.getClass().getSimpleName() + ")");
            }
            System.out.println("Results written in " + params.outputValidationFile);
            System.out.println();
            System.out.println("Processing time: " + (System.currentTimeMillis() - start) + "ms");
            return cm;
        } catch (final IOException e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    private static void delete(String description, String filename) {
        final File toDelete = new File(filename);
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
        } catch (final IOException e) {
            System.err.println("> " + description + " couldn't be deleted (" + e.getMessage() + ")");
        }
    }
}
