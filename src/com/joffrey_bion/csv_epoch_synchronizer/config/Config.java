package com.joffrey_bion.csv_epoch_synchronizer.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.actigraph.CutPointsSet;
import com.joffrey_bion.utils.xml.XmlHelper;

/**
 * A singleton containing the values of the configuration file. If no such file
 * exists, it is created the first time this class is accessed.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class Config {

    private static final String CONFIG_FILENAME = "config.xml";

    private static final String ROOT = "config";
    private static final String TAG_DELETE_TEMP_FILE = "delete-temp-file";
    private static final String TAG_CUT_POINTS = "cut-points";
    private static final String TAG_WINDOW_WIDTH = "window-width";
    private static final boolean DEFAULT_DELETE_TEMP = true;
    private static final CutPointsSet DEFAULT_CUT_POINTS = CutPointsSet.CUSTOM;
    private static final int DEFAULT_WINDOW_WIDTH = 5;

    private static Config instance = null;

    /**
     * Returns the current configuration.
     * 
     * @return the configuration as a {@link Config} object.
     */
    public static Config get() {
        if (instance == null) {
            instance = new Config(getConfigFilePath());
        }
        return instance;
    }

    /**
     * Whether or not the intermediate file (containing phone epochs) must be
     * deleted.
     */
    public boolean deleteIntermediateFile;
    /** The cut points set to use to level the actigraph's CPM. */
    public CutPointsSet cutPointsSet;
    /** Window width in seconds */
    public int windowWidthSec;

    private Config(String configFileName) {
        deleteIntermediateFile = DEFAULT_DELETE_TEMP;
        cutPointsSet = DEFAULT_CUT_POINTS;
        windowWidthSec = DEFAULT_WINDOW_WIDTH;
        if (configFileName == null) {
            System.out.println("Default config loaded (no config file created).");
            return;
        }
        if (!load(configFileName)) {
            System.out.println("Creating config file '" + CONFIG_FILENAME + "' with defaults...");
            save(configFileName);
            System.out.println("Complete.");
        } else {
            System.out.println("Config file loaded.");
        }
        System.out.println();
    }

    /**
     * Save this {@code Config} object to the specified XML file.
     * 
     * @param xmlFilePath
     *            The path to the XML output file.
     */
    private void save(String xmlFilePath) {
        Document doc = XmlHelper.createEmptyDomDocument();
        Element root = doc.createElement(ROOT);
        doc.appendChild(root);
        root.appendChild(doc
                .createComment("The cut points set must be one of CutPointsSet enum constant names"));
        XmlHelper.appendField(doc, root, TAG_CUT_POINTS, cutPointsSet.toString());
        XmlHelper.appendField(doc, root, TAG_WINDOW_WIDTH, Integer.toString(windowWidthSec));
        XmlHelper.appendField(doc, root, TAG_DELETE_TEMP_FILE,
                Boolean.toString(deleteIntermediateFile));
        try {
            XmlHelper.writeXml(xmlFilePath, doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads this {@code Config} from the specified XML configuration file.
     * 
     * @param xmlFilePath
     *            The path to the XML output file.
     * @return Whether the configuration file has been read.
     */
    private boolean load(String xmlFilePath) {
        Document dom;
        try {
            dom = XmlHelper.getDomDocumentFromFile(xmlFilePath);
        } catch (FileNotFoundException e) {
            System.out.println("Config file not found.");
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        Element root = dom.getDocumentElement();

        try {
            cutPointsSet = CutPointsSet.valueOf(XmlHelper.getField(root, TAG_CUT_POINTS));
        } catch (IllegalArgumentException e) {
            System.err.println("Unknown cut points set. Using default...");
            cutPointsSet = DEFAULT_CUT_POINTS;
        }
        try {
            windowWidthSec = Integer.parseInt(XmlHelper.getField(root, TAG_WINDOW_WIDTH));
        } catch (NumberFormatException e) {
            System.err.println("The window width must be an integer in seconds. Using default...");
            windowWidthSec = DEFAULT_WINDOW_WIDTH;
        }
        deleteIntermediateFile = Boolean.parseBoolean(XmlHelper
                .getField(root, TAG_DELETE_TEMP_FILE));
        return true;
    }

    /**
     * Returns the path to the configuration file.
     */
    private static String getConfigFilePath() {
        try {
            String urlString = ClassLoader
                    .getSystemClassLoader()
                    .getResource(
                            "com/joffrey_bion/csv_epoch_synchronizer/mains/phone_vs_actigraph/PhoneVSActigraphMerger.class")
                    .toString();
            urlString = urlString.substring(urlString.indexOf("file:"), urlString.indexOf('!'));
            URL url = new URL(urlString);
            File file = new File(url.toURI());
            return file.getParent() + "\\" + CONFIG_FILENAME;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (StringIndexOutOfBoundsException e) {
            System.err
                    .println("error while parsing config file path (normal if executed from eclipse)");
        }
        return null;
    }
}
