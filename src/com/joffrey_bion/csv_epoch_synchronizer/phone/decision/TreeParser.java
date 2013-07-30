package com.joffrey_bion.csv_epoch_synchronizer.phone.decision;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.joffrey_bion.utils.xml.XmlHelper;

class TreeParser {

    private static final String TYPE_LEAF = "leaf";
    private static final String TYPE_NODE = "node";
    private static final String ATT_NODE_TYPE = "type";
    private static final String ATT_CLASS = "class";
    private static final String ATT_FEATURE = "feature";
    private static final String ATT_THRESHOLD = "threshold";
    private static final String TAG_LEFT_SON = "left";
    private static final String TAG_RIGHT_SON = "right";

    private Document dom;
    private Element root;

    /**
     * Creates a new {@code TreeParser} for the specified decision tree file.
     * 
     * @param xmlTreeFile
     *            The path to the XML file containing the decision tree to parse.
     * @throws SAXException
     *             If any parse error occurs.
     * @throws IOException
     *             If any IO error occurs.
     */
    private TreeParser(String xmlTreeFile) throws SAXException, IOException {
        dom = XmlHelper.getDomDocumentFromFile(xmlTreeFile);
        root = dom.getDocumentElement();
    }

    /**
     * Returns the {@link DecisionTree} corresponding to the specified decision tree
     * XML file.
     * 
     * @param xmlTreeFile
     *            The path to the XML file containing the decision tree to parse.
     * @throws SAXException
     *             If any parse error occurs.
     * @throws IOException
     *             If any IO error occurs.
     */
    public static DecisionTree parseTree(String xmlTreeFile) throws SAXException, IOException {
        TreeParser parser = new TreeParser(xmlTreeFile);
        return getTree(parser.root);
    }

    /**
     * Returns the {@link DecisionTree} corresponding to the specified XML
     * {@link Element}.
     * 
     * @param elt
     *            The XML {@link Element} to convert into a {@link DecisionTree}
     *            object.
     */
    private static DecisionTree getTree(Element elt) {
        String type = elt.getAttribute(ATT_NODE_TYPE);
        if (type.equals(TYPE_LEAF)) {
            String classStr = elt.getAttribute(ATT_CLASS);
            return new DecisionTree(classStr);
        } else if (type.equals(TYPE_NODE)) {
            String featStr = elt.getAttribute(ATT_FEATURE);
            String thresholdStr = elt.getAttribute(ATT_THRESHOLD);
            DecisionTree decisionTree = new DecisionTree(featStr, Double.parseDouble(thresholdStr),
                    getTree(XmlHelper.getFirstDirectChild(elt, TAG_LEFT_SON)),
                    getTree(XmlHelper.getFirstDirectChild(elt, TAG_RIGHT_SON)));
            return decisionTree;
        } else {
            throw new IllegalArgumentException("expected '" + TYPE_NODE + "' or '" + TYPE_LEAF
                    + "' element");
        }
    }
}
