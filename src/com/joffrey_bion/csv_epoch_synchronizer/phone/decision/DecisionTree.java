package com.joffrey_bion.csv_epoch_synchronizer.phone.decision;

import java.util.HashMap;

/**
 * A {@code DecisionTree} object can be viewed as a subtree of a decision tree. It is
 * either an internal node or a leaf.
 * <p>
 * If it is a leaf, then it represents a class. If it is an internal node, then it
 * represents a feature that has to be compared to a threshold.
 * </p>
 * <p>
 * On a given sample, if the specified feature of the sample is lower than or equal
 * to the threshold, then we move to the left (low) son. Otherwise, we move to the
 * right (high) son. Anyway, we carry on until a leaf is reached, giving the class of
 * the tested sample.
 * </p>
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
class DecisionTree {

    private boolean isLeaf;
    private String classAttribute;

    private DecisionTree lowSon;
    private DecisionTree highSon;
    private String feature;
    private Double threshold;

    /**
     * Creates a new leaf.
     * 
     * @param classAttribute
     *            The class associated with this leaf.
     */
    public DecisionTree(String classAttribute) {
        this.isLeaf = true;
        this.classAttribute = classAttribute;
    }

    /**
     * Creates a new internal node.
     * 
     * @param feature
     *            The String representing the feature that has to be tested.
     * @param threshold
     *            The threshold to choose between lowSon or highSon. If the feature
     *            is less than or equal to the threshold, then the lowSon has to be
     *            considered, otherwise the highSon branch is followed.
     * @param lowSon
     *            The left (low) child of this node.
     * @param highSon
     *            The right (high) child of this node.
     */
    public DecisionTree(String feature, double threshold, DecisionTree lowSon, DecisionTree highSon) {
        this.isLeaf = false;
        this.feature = feature;
        this.threshold = threshold;
        this.lowSon = lowSon;
        this.highSon = highSon;
    }

    /**
     * Classifies the given set of values for the features.
     * 
     * @param attributes
     *            The values of each feature.
     * @param features
     *            A map between the name of each feature and its index in the
     *            {@code attributes} array.
     * @return The class of this set of values according to this decision tree.
     */
    public String classify(String[] attributes, HashMap<String, Integer> features) {
        if (isLeaf) {
            return classAttribute;
        }
        Integer featureIndex = features.get(feature);
        if (featureIndex == null) {
            throw new IllegalArgumentException("This node feature '" + feature + "' is missing.");
        }
        if (Double.parseDouble(attributes[featureIndex]) <= threshold) {
            return lowSon.classify(attributes, features);
        } else {
            return highSon.classify(attributes, features);
        }
    }

    /*
     * Fancy printing methods
     */

    /** Vertical line UTF-8 symbol. */
    private static final char vline = Character.toChars(Integer.parseInt("2502", 16))[0];
    /** Horizontal line UTF-8 symbol. */
    private static final char hline = Character.toChars(Integer.parseInt("2500", 16))[0];
    /** Vertical line with right branch UTF-8 symbol. */
    private static final char midBranch = Character.toChars(Integer.parseInt("251c", 16))[0];
    /** End branch (top and right line) UTF-8 symbol. */
    private static final char endBranch = Character.toChars(Integer.parseInt("2514", 16))[0];

    /**
     * Returns a {@code String} representation of this tree, with fancy UTF-8 lines.
     * 
     * @param indent
     *            The indentation to append before each line. May contain vertical
     *            lines, or branches. Middle branches will be turned into vertical
     *            lines (to continue the other branches), end branches will be
     *            replaced by blanks (because the branch is finished).
     * @return a {@code String} representation of this tree, with fancy UTF-8 lines.
     */
    public String toString(String indent) {
        String newIndent = indent.replace(endBranch, ' ').replace(hline, ' ')
                .replace(midBranch, vline);
        String indL = newIndent + " " + midBranch + hline;
        String indR = newIndent + " " + endBranch + hline;
        String res = indent;
        if (isLeaf) {
            res += "<" + classAttribute + ">";
        } else {
            res += "[" + feature + "][" + threshold + "]\n";
            if (lowSon == null)
                res += indL + "null";
            else
                res += lowSon.toString(indL);
            res += "\n";
            if (highSon == null)
                res += indR + "null";
            else
                res += highSon.toString(indR);
        }
        return res;
    }

    @Override
    public String toString() {
        return toString("");
    }
}
