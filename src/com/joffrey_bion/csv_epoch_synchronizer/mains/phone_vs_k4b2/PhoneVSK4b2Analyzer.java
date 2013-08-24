package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2StatsCalculator;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Phase;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Results;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.stats.PhaseResults;
import com.joffrey_bion.csv_epoch_synchronizer.phone.RawToEpConverter;
import com.joffrey_bion.csv_epoch_synchronizer.phone.decision.LabelAppender;
import com.joffrey_bion.generic_guis.LookAndFeel;
import com.joffrey_bion.generic_guis.file_picker.FilePicker;
import com.joffrey_bion.generic_guis.file_picker.JFilePickersPanel;
import com.joffrey_bion.generic_guis.file_processor.JFileProcessorWindow;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PhoneVSK4b2Analyzer {
    
    static final int INPUT_PHONE = 0;
    static final int INPUT_K4B2 = 1;
    static final int INPUT_XML_TREE = 2;
    
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
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {
                "Phone raw file", "K4b2 test file", "XML Tree file" }, new String[]{});
        FilePicker[] ifps = filePickers.getInputFilePickers();
        ifps[INPUT_PHONE].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ifps[INPUT_K4B2].addFileTypeFilter(".csv", "Comma-Separated Values file");
        ifps[INPUT_XML_TREE].addFileTypeFilter(".xml", "XML Classifier file");
        final PvKArgsPanel pvKArgsPanel = new PvKArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Phone-VS-K4b2 Analyzer", "Process",
                filePickers, pvKArgsPanel) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                this.clearLog();
                try {
                    PvKParams params = new PvKParams();
                    pvKArgsPanel.getParameters(params);
                    analyze(params);
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                } catch (Exception e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }

    public static void processSeveralParams(String[] paramsFiles) {
        for (String xmlParamsFile : paramsFiles) {
            System.out.println("------[ " + xmlParamsFile + " ]---------------------");
            System.out.println();
            try {
                PvKParams params = new PvKParams(xmlParamsFile);
                HashMap<Phase, Accuracy> accuracies = analyze(params);
                for (Phase p : accuracies.keySet()) {
                    System.out.println(p);
                    System.out.println(accuracies.get(p));
                }
            } catch (IOException e) {
                System.err.println("I/O error: " + e.getMessage());
            } catch (SAXException e) {
                System.err.println("Incorrect XML file: " + e.getMessage());
            } catch (SpecificationNotMetException e) {
                System.err.println("Incorrect parameter file: " + e.getMessage());
            }
            System.out.println();
        }
    }

    public static HashMap<Phase, Accuracy> analyze(PvKParams params) {
        try {
            K4b2StatsCalculator k4 = new K4b2StatsCalculator(params.k4b2File);
            Results res = k4.getAllStats(params.nbSyncMarkers);
            PhasePhoneParams ppp = new PhasePhoneParams(params);
            HashMap<Phase, Accuracy> accuracies = new HashMap<>();
            for (Phase p : Phase.values()) {
                PhaseResults pr = res.get(p);
                ppp.setPhaseResults(p, pr);
                RawToEpConverter.createEpochsFile(ppp);
                HashMap<String, Integer> lvlsDistrib = LabelAppender.appendLabels(ppp);
                if (Config.get().deleteTempFiles) {
                    if (new File(ppp.getUnlabeledDatasetFilePath()).delete()) {
                        System.out.println("Temp file deleted (" + ppp.getUnlabeledDatasetFilePath() + ")");
                    }
                    if (new File(ppp.getLabeledDatasetFilePath()).delete()) {
                        System.out.println("Temp file deleted (" + ppp.getUnlabeledDatasetFilePath() + ")");
                    }
                }
                HashMap<String, Double> lvlsTimeDistrib = new HashMap<>();
                for (String level : lvlsDistrib.keySet()) {
                    lvlsTimeDistrib.put(level,
                            (double) (lvlsDistrib.get(level) * Config.get().epochWidthVsK4b2));
                }
                System.out.println("K4b2 levels distribution:");
                System.out.println(pr.getLevelsDistribution());
                System.out.println("Phone levels distribution:");
                System.out.println(lvlsTimeDistrib);
                accuracies.put(p, new Accuracy(lvlsTimeDistrib, pr.getLevelsDistribution()));
            }
            return accuracies;
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (SAXException e) {
            System.err.println("Incorrect XML file: " + e.getMessage());
        }
        return null;
    }
}
