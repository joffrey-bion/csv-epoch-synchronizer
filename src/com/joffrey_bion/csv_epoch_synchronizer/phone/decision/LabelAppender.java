package com.joffrey_bion.csv_epoch_synchronizer.phone.decision;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv.CsvReader;
import com.joffrey_bion.csv.CsvWriter;

/**
 * A module that appends the level decided by a decision tree to each row of a CSV
 * file according to the values for each feature (column).
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class LabelAppender {

    private final DecisionTree tree;
    private HashMap<String, Integer> features;
    private HashMap<String, Integer> lvlsDistrib;

    /**
     * Creates a new {@code LabelAppender} for the specified decision tree.
     * 
     * @param decisionTreeFile
     *            The path to the XML file containing the decision tree.
     * @throws SAXException
     *             If any parse error occurs.
     * @throws IOException
     *             If any IO error occurs.
     */
    public LabelAppender(String decisionTreeFile) throws SAXException, IOException {
        System.out.println("Parsing tree file '" + decisionTreeFile + "'...");
        tree = TreeParser.parseTree(decisionTreeFile);
        System.out.println("Tree read successfully.");
        lvlsDistrib = new HashMap<>();
    }

    /**
     * Determines the level of each row of the dataset file according to the decision
     * tree, and appends it as a new column in the destination file.
     * 
     * @param datasetFile
     *            The CSV dataset file to calculate the labels for.
     * @param outputFile
     *            The destination file, which will contain the dataset as well as a
     *            new column with the decided labels.
     * @throws IOException
     *             If any IO error occurs.
     */
    public HashMap<String, Integer> appendLabels(String datasetFile, String outputFile) throws IOException {
        System.out.println("Opening dataset file '" + datasetFile + "'...");
        CsvReader reader = new CsvReader(datasetFile);
        String[] row = reader.readRow();
        indexFeatures(row);
        System.out.println("Classifying...");
        CsvWriter writer = new CsvWriter(outputFile);
        writer.writeRow(LabelAppender.append(row, "Classified As"));
        while ((row = reader.readRow()) != null) {
            writer.writeRow(appendLabel(row));
        }
        reader.close();
        writer.close();
        return lvlsDistrib;
    }

    /**
     * Saves the correspondence between each feature name and its column number.
     * 
     * @param headers
     *            The header line of the CSV dataset containing the names of the
     *            features.
     */
    private void indexFeatures(String[] headers) {
        if (headers == null) {
            throw new IllegalArgumentException("No headers specified.");
        }
        features = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            features.put(headers[i], i);
        }
    }

    /**
     * Determines the level of the sepcified row and appends it at the end of this
     * row.
     * 
     * @param row
     *            The row to append the level to.
     * @return The source row with the appended level.
     */
    private String[] appendLabel(String[] row) {
        String label = tree.classify(row, features);
        int oldNb = lvlsDistrib.containsKey(label) ? lvlsDistrib.get(label) : 0;
        lvlsDistrib.put(label, oldNb + 1);
        return append(row, label);
    }

    /**
     * Appends a {@code String} at the end of an array of {@code String}s.
     * 
     * @param tab
     *            The original array to append a value to.
     * @param element
     *            The element to append at the end of the original array.
     * @return The array with the appended element.
     */
    private static String[] append(String[] tab, String element) {
        String[] res = Arrays.copyOfRange(tab, 1, tab.length + 1);
        res[res.length - 1] = element;
        return res;
    }
}
