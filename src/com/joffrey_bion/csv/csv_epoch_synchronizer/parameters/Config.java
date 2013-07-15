package com.joffrey_bion.csv.csv_epoch_synchronizer.parameters;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.joffrey_bion.csv.csv_epoch_synchronizer.CountsLabeler;
import com.joffrey_bion.utils.xml_helper.XmlHelper;

public class Config {

    private static final String CONFIG_FILENAME = "config.xml";
    private static final String ROOT = "config";
    private static final String TAG_DELETE_TEMP_FILE = "delete-temp-file";
    private static final String TAG_CUT_POINTS = "cut-points";
    private static final String TAG_WINDOW_WIDTH = "window-width";
    private static final boolean DEFAULT_DELETE_TEMP = true;
    private static final String DEFAULT_CUT_POINTS = CountsLabeler.CUSTOM_VM3;
    private static final int DEFAULT_WINDOW_WIDTH = 5;

    private static Config instance = null;

    public static Config get() {
        if (instance == null) {
            instance = new Config(CONFIG_FILENAME);
        }
        return instance;
    }

    /**
     * Whether or not the intermediate file (containing phone epochs) must be
     * deleted.
     */
    public boolean deleteIntermediateFile;
    /** The cut points set to use to label the actigraph's CPM. */
    public String cutPointsSet;
    /** Window width in seconds */
    public int windowWidthSec;

    private Config(String configFileName) {
        if (!load(configFileName)) {
            deleteIntermediateFile = DEFAULT_DELETE_TEMP;
            cutPointsSet = DEFAULT_CUT_POINTS;
            windowWidthSec = DEFAULT_WINDOW_WIDTH;
            save(configFileName);
        }
    }

    /**
     * Save this {@code InstanceRawParameters} object to the specified XML file.
     * 
     * @param xmlFilePath
     *            The path to the XML output file.
     */
    private void save(String xmlFilePath) {
        Document doc;
        try {
            doc = XmlHelper.createEmptyDomDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return;
        }
        Element root = doc.createElement(ROOT);
        doc.appendChild(root);
        XmlHelper.appendField(doc, root, TAG_CUT_POINTS, cutPointsSet);
        XmlHelper.appendField(doc, root, TAG_WINDOW_WIDTH, windowWidthSec + "");
        XmlHelper.appendField(doc, root, TAG_DELETE_TEMP_FILE,
                Boolean.toString(deleteIntermediateFile));
        try {
            XmlHelper.writeXml(xmlFilePath, doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a {@code InstanceRawParameters} object from the specified XML file.
     * 
     * @param xmlFilePath
     *            The path to the XML output file.
     * @throws IOException
     *             If an error occurs while reading the file.
     * @throws SAXException
     *             If any parse error occurs.
     */
    public boolean load(String xmlFilePath) {
        Document dom;
        try {
            dom = XmlHelper.getDomDocumentFromFile(xmlFilePath);
            Element root = dom.getDocumentElement();
            cutPointsSet = XmlHelper.getField(root, TAG_CUT_POINTS);
            windowWidthSec = Integer.parseInt(XmlHelper.getField(root, TAG_WINDOW_WIDTH));
            deleteIntermediateFile = Boolean.parseBoolean(XmlHelper.getField(root,
                    TAG_DELETE_TEMP_FILE));
            return true;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }
}
