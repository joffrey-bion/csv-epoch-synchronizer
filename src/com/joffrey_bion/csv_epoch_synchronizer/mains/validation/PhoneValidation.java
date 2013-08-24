package com.joffrey_bion.csv_epoch_synchronizer.mains.validation;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv.CsvWriter;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Phase;
import com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph.PhoneVSActigraphAnalyzer;
import com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2.Accuracy;
import com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2.PhoneVSK4b2Analyzer;
import com.joffrey_bion.csv_epoch_synchronizer.participant.Participant;
import com.joffrey_bion.generic_guis.LookAndFeel;
import com.joffrey_bion.generic_guis.file_picker.FilePicker;
import com.joffrey_bion.generic_guis.file_picker.JFilePickersPanel;
import com.joffrey_bion.generic_guis.file_processor.JFileProcessorWindow;
import com.joffrey_bion.utils.classification.ConfusionMatrix;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PhoneValidation {

    static final int INPUT_PVA = 0;
    static final int INPUT_PVK = 1;
    static final int INPUT_PARTICIPANT = 2;
    static final int OUTPUT = 0;

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
                "Phone vs Actigraph Params", "Phone vs K4b2 Params", "Participant file" },
                "Output file");
        FilePicker[] ifps = filePickers.getInputFilePickers();
        ifps[INPUT_PVA].addFileTypeFilter(".xml", "XML Parameter file");
        ifps[INPUT_PVK].addFileTypeFilter(".xml", "XML Parameter file");
        ifps[INPUT_PARTICIPANT].addFileTypeFilter(".xml", "XML Participant file");
        FilePicker[] ofps = filePickers.getOutputFilePickers();
        ofps[OUTPUT].addFileTypeFilter(".csv", "Comma-Separated Values");
        final PVArgsPanel pvKArgsPanel = new PVArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Phone Validation Row Creator",
                "Process", filePickers, pvKArgsPanel) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
                this.clearLog();
                try {
                    PVParams params = new PVParams();
                    pvKArgsPanel.getParameters(params);
                    computeResults(params);
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
                PVParams params = new PVParams(xmlParamsFile);
                computeResults(params);
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

    public static void computeResults(PVParams params) throws IOException, SAXException, SpecificationNotMetException {
        HashMap<Phase, Accuracy> accuraciesPvK = PhoneVSK4b2Analyzer.analyze(params.pvkParams);
        ConfusionMatrix<String> cmPvA = PhoneVSActigraphAnalyzer.analyze(params.pvaParams);
        writeResults(params, accuraciesPvK, cmPvA);
    }

    private static void writeResults(PVParams params, HashMap<Phase, Accuracy> accuraciesPvK,
            ConfusionMatrix<String> cmPvA) throws IOException, SAXException,
            SpecificationNotMetException {
        Participant participant = new Participant(params.participantFile);
        CsvWriter writer = new CsvWriter(params.outputFile);
        LinkedList<String> headers = participant.getHeaders();
        for (Phase p : Phase.values()) {
            headers.addAll(accuraciesPvK.get(p).getHeaders("PvK-"+ p.toString()+"-"));
        }
        headers.addAll(getConfusionMatrixHeaders("PvA-"));
        writer.writeRow(headers.toArray(new String[headers.size()]));
        LinkedList<String> values = participant.getValues();
        for (Phase p : Phase.values()) {
            values.addAll(accuraciesPvK.get(p).getValues());
        }
        values.addAll(getConfusionMatrixValues(cmPvA));
        writer.writeRow(values.toArray(new String[values.size()]));
        writer.close();
    }
    
    private static LinkedList<String> getConfusionMatrixHeaders(String prefix) {
        LinkedList<String> headers = new LinkedList<>();
        headers.add(prefix + "Overall-Accuracy");
        return headers;
    }
    private static LinkedList<String> getConfusionMatrixValues(ConfusionMatrix<String> cm) {
        LinkedList<String> values = new LinkedList<>();
        values.add(Double.toString(cm.getCorrectRate()));
        return values;
    }
}
