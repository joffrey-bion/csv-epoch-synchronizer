package org.hildan.waterloo.ces.phone.decision;

import java.io.IOException;

import org.hildan.utils.xml.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

class TreeParser {

    private static final String TYPE_LEAF = "leaf";

    private static final String TYPE_NODE = "node";

    private static final String ATT_NODE_TYPE = "type";

    private static final String ATT_CLASS = "class";

    private static final String ATT_FEATURE = "feature";

    private static final String ATT_THRESHOLD = "threshold";

    private static final String TAG_LEFT_SON = "left";

    private static final String TAG_RIGHT_SON = "right";

    private final Document dom;

    private final Element root;

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
     * Returns the {@link DecisionTree} corresponding to the specified decision tree XML file.
     *
     * @param classifierFile
     *            The path to the XML file containing the decision tree to parse.
     * @return the {@link DecisionTree} corresponding to the specified XML file.
     * @throws SAXException
     *             If any parse error occurs.
     * @throws IOException
     *             If any IO error occurs.
     */
    public static DecisionTree parseTree(String classifierFile) throws SAXException, IOException {
        final TreeParser parser = new TreeParser(classifierFile);
        return getTree(parser.root);
    }

    /**
     * Returns the {@link DecisionTree} corresponding to the specified XML {@link Element}.
     *
     * @param elt
     *            The XML {@link Element} to convert into a {@link DecisionTree} object.
     */
    private static DecisionTree getTree(Element elt) {
        final String type = elt.getAttribute(ATT_NODE_TYPE);
        if (type.equals(TYPE_LEAF)) {
            final String classStr = elt.getAttribute(ATT_CLASS);
            return new DecisionTree(classStr);
        } else if (type.equals(TYPE_NODE)) {
            final String featStr = elt.getAttribute(ATT_FEATURE);
            final String thresholdStr = elt.getAttribute(ATT_THRESHOLD);
            final DecisionTree decisionTree = new DecisionTree(featStr, Double.parseDouble(thresholdStr),
                    getTree(XmlHelper.getFirstDirectChild(elt, TAG_LEFT_SON)), getTree(XmlHelper.getFirstDirectChild(
                            elt, TAG_RIGHT_SON)));
            return decisionTree;
        } else {
            throw new IllegalArgumentException("expected '" + TYPE_NODE + "' or '" + TYPE_LEAF + "' element");
        }
    }
}
