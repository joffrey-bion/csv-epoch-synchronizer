package org.hildan.waterloo.ces.mains.phone_decision;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.hildan.utils.fpgui.LookAndFeel;
import org.hildan.utils.fpgui.fpickers.FilePicker;
import org.hildan.utils.fpgui.fpickers.JFilePickersPanel;
import org.hildan.utils.fpgui.gwindows.JFileProcessorWindow;
import org.hildan.waterloo.ces.phone.decision.LabelAppender;
import org.xml.sax.SAXException;

/**
 * A program that uses a decision tree to assign a level to each row of a dataset and appends a new
 * column containing the decided labels.
 *
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class DecisionSimulator {

    private static final int ARG_TREE = 0;

    private static final int ARG_DATASET = 1;

    private static final int ARG_DEST = 2;

    /**
     * Choose between GUI or console version according to the number of arguments.
     *
     * @param args
     *            No arguments will start the GUI, otherwise 3 filenames have to be specified:
     *            decision tree, source dataset, then destination.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    openWindow();
                }
            });
        } else if (args.length == 2) {
            process(args[ARG_TREE], args[ARG_DATASET], args[ARG_DEST]);
        } else {
            System.out.println("Usage: DecisionSimulator.jar <xml-decision-tree-file> <dataset-file> <dest-file>");
        }
    }

    /**
     * Starts the GUI.
     */
    private static void openWindow() {
        LookAndFeel.setSystemLookAndFeel();
        // file pickers source and destination
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {"Decision Tree (XML)",
        "Dataset (CSV)"}, "Output file");
        filePickers.getInputFilePickers()[0].addFileTypeFilter(".xml", "XML Decision Tree file");
        filePickers.getInputFilePickers()[1].addFileTypeFilter(".csv", "Comma Separated Value file");
        for (final FilePicker fp : filePickers.getOutputFilePickers()) {
            fp.addFileTypeFilter(".csv", "Comma Separated Value file");
        }
        @SuppressWarnings("serial")
        final JFileProcessorWindow frame = new JFileProcessorWindow("Decision Simulator", filePickers, null, "Process") {

            @Override
            public void process(String[] inPaths, String[] outPaths, int processBtnIndex) {
                clearLog();
                DecisionSimulator.process(inPaths[0], inPaths[1], outPaths[0]);
            }
        };
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Appends a column to the specified dataset with the level assigned to each row by the
     * specified decision tree.
     *
     * @param xmlTreeFile
     *            The decision tree to use.
     * @param datasetFile
     *            The dataset to append a column to.
     * @param outputPath
     *            The resulting file.
     */
    private static void process(String xmlTreeFile, String datasetFile, String outputPath) {
        if (xmlTreeFile == null || xmlTreeFile.equals("")) {
            System.err.println("No decision tree specified");
            return;
        }
        if (datasetFile == null || datasetFile.equals("")) {
            System.err.println("No dataset specified");
            return;
        }
        if (outputPath == null || outputPath.equals("")) {
            System.err.println("No output file specified");
            return;
        }
        try {
            LabelAppender.appendLabels(xmlTreeFile, datasetFile, outputPath, true);
            System.out.println("Success.");
        } catch (final FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (final IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (final SAXException e) {
            System.err.println("Incorrect decision tree file: " + e.getMessage());
        }
    }
}
