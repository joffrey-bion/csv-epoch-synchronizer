package org.hildan.waterloo.ces.mains.phone_vs_k4b2;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.hildan.utils.fpgui.LookAndFeel;
import org.hildan.utils.fpgui.fpickers.FilePicker;
import org.hildan.utils.fpgui.fpickers.JFilePickersPanel;
import org.hildan.utils.fpgui.gwindows.JFileProcessorWindow;
import org.hildan.utils.xml.serialization.parameters.SpecificationNotMetException;
import org.hildan.waterloo.ces.config.Config;
import org.hildan.waterloo.ces.k4b2.K4b2StatsCalculator;
import org.hildan.waterloo.ces.k4b2.Phase;
import org.hildan.waterloo.ces.k4b2.Results;
import org.hildan.waterloo.ces.k4b2.stats.PhaseResults;
import org.hildan.waterloo.ces.phone.RawToEpConverter;
import org.hildan.waterloo.ces.phone.decision.LabelAppender;
import org.xml.sax.SAXException;

public class PhoneVSK4b2 {

    static final int INPUT_PHONE = 0;

    static final int INPUT_K4B2 = 1;

    static final int INPUT_PARTICIPANT = 2;

    static final int INPUT_XML_TREE = 3;

    static final int OUTPUT_VALIDATION = 0;

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
            processSeveralParams(args);
        }
    }

    /**
     * Starts the GUI.
     */
    private static void openWindow() {
        LookAndFeel.setSystemLookAndFeel();
        // file pickers source and destination
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {"Phone raw file", "K4b2 test file",
                "Participant file", "XML Tree file"}, "Validation output file");
        final FilePicker[] ifps = filePickers.getInputFilePickers();
        ifps[INPUT_PHONE].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ifps[INPUT_K4B2].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ifps[INPUT_XML_TREE].addFileTypeFilter(".xml", "XML Classifier file");
        ifps[INPUT_PARTICIPANT].addFileTypeFilter(".xml", "XML Participant file");
        final FilePicker[] ofps = filePickers.getInputFilePickers();
        ofps[OUTPUT_VALIDATION].addFileTypeFilter(".csv", "Comma-Separated Values file");
        final PvKArgsPanel pvKArgsPanel = new PvKArgsPanel(filePickers);
        @SuppressWarnings("serial")
        final JFileProcessorWindow frame = new JFileProcessorWindow("Phone-VS-K4b2", filePickers, pvKArgsPanel,
                "Validate") {

            @Override
            public void process(String[] inPaths, String[] outPaths, int processBtnIndex) {
                clearLog();
                try {
                    final PvKParams params = new PvKParams();
                    pvKArgsPanel.getParameters(params);
                    analyze(params);
                } catch (final ParseException e) {
                    System.err.println(e.getMessage());
                } catch (final Exception e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                    e.printStackTrace();
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }

    public static void processSeveralParams(String[] paramsFiles) {
        for (final String xmlParamsFile : paramsFiles) {
            System.out.println("------[ " + xmlParamsFile + " ]---------------------");
            System.out.println();
            try {
                final PvKParams params = new PvKParams(xmlParamsFile);
                final HashMap<Phase, Accuracy> accuracies = analyze(params);
                for (final Phase p : accuracies.keySet()) {
                    System.out.println(p);
                    System.out.println(accuracies.get(p));
                }
            } catch (final IOException e) {
                System.err.println("I/O error: " + e.getMessage());
                e.printStackTrace();
            } catch (final SAXException e) {
                System.err.println("Incorrect XML file: " + e.getMessage());
            } catch (final SpecificationNotMetException e) {
                System.err.println("Incorrect parameter file: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static HashMap<Phase, Accuracy> analyze(PvKParams params) throws IOException, SAXException,
    SpecificationNotMetException {
        final K4b2StatsCalculator k4 = new K4b2StatsCalculator(params.k4b2File);
        final Results res = k4.getAllStats(params.nbSyncMarkers);
        final PhasePhoneParams ppp = new PhasePhoneParams(params);
        final HashMap<Phase, Accuracy> accuracies = new HashMap<>();
        for (final Phase p : Phase.values()) {
            final PhaseResults pr = res.get(p);
            ppp.setPhaseResults(p, pr);
            RawToEpConverter.createEpochsFile(ppp);
            final HashMap<String, Integer> lvlsDistrib = LabelAppender.appendLabels(ppp);
            if (Config.get().deleteTempFiles) {
                if (new File(ppp.getUnlabeledDatasetFilePath()).delete()) {
                    System.out.println("Temp file deleted (" + ppp.getUnlabeledDatasetFilePath() + ")");
                }
                if (new File(ppp.getLabeledDatasetFilePath()).delete()) {
                    System.out.println("Temp file deleted (" + ppp.getLabeledDatasetFilePath() + ")");
                }
            }
            final HashMap<String, Double> lvlsTimeDistrib = new HashMap<>();
            for (final String level : lvlsDistrib.keySet()) {
                lvlsTimeDistrib.put(level, (double) (lvlsDistrib.get(level) * Config.get().epochWidthVsK4b2));
            }
            System.out.println("K4b2 levels distribution:");
            System.out.println(pr.getLevelsDistribution());
            System.out.println("Phone levels distribution:");
            System.out.println(lvlsTimeDistrib);
            accuracies.put(p, new Accuracy(lvlsTimeDistrib, pr.getLevelsDistribution()));
        }
        System.out.println();
        System.out.println("Writing results...");
        new PrintableAccuracies(accuracies).writeResults(params.participantFile, params.outputValidationFile);
        System.out.println("Results written in " + params.outputValidationFile);
        return accuracies;
    }
}
