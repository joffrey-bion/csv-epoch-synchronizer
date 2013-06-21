package com.joffrey_bion.csv.csv_epoch_synchronizer.parameters;

import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * A {@code RawParameters} object contains all the raw information read from an XML
 * parameter file or from the GUI. Therefore, most fields are just {@code String}s
 * because they have not been parsed yet.
 * <p>
 * This class provides serialization methods {@link #save(String)} and
 * {@link #load(String)}, to write/read raw parameters to/from an XML file.
 * </p>
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class RawParameters {

    private static final String ROOT = "parameters";
    private static final String FILES_LIST = "files";
    private static final String INPUT_FILE = "input-file";
    private static final String OUTPUT_FILE = "output-file";
    private static final String FILE_INDEX_ATT = "index";
    private static final String START_TIME = "start";
    private static final String STOP_TIME = "stop";
    private static final String EPOCH_WIDTH_SEC = "epoch-width";
    private static final String WINDOW_WIDTH_SEC = "window-width";
    private static final String DELETE_TEMP_FILE = "delete-temp";
    private static final String SPIKES_LIST = "spikes";
    private static final String SPIKE = "spike";
    private static final String SPIKE_PHONE_ATT = "phone";
    private static final String SPIKE_ACTIGRAPH_ATT = "actig";

    public static final int NB_MAX_SPIKES = 6;

    public String phoneRawFile;
    public String actigEpFile;
    public String outputFile;
    public String windowWidthSec;
    public String epochWidthSec;
    public String startTime;
    public String stopTime;
    public boolean deleteIntermediateFile;
    public String[] phoneSpikes;
    public String[] actigraphSpikes;

    /**
     * Save this {@code RawParameters} object to the specified XML file.
     * 
     * @param xmlFilePath
     *            The path to the XML output file.
     * @throws IOException
     *             If an error occurs while writing to the file.
     */
    public void save(String xmlFilePath) throws IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element root = doc.createElement(ROOT);
            doc.appendChild(root);
            Element files = doc.createElement(FILES_LIST);
            root.appendChild(files);
            appendField(doc, files, INPUT_FILE, phoneRawFile).setAttribute(FILE_INDEX_ATT, "0");
            appendField(doc, files, INPUT_FILE, actigEpFile).setAttribute(FILE_INDEX_ATT, "1");
            appendField(doc, files, OUTPUT_FILE, outputFile).setAttribute(FILE_INDEX_ATT, "0");
            appendField(doc, root, START_TIME, startTime);
            appendField(doc, root, STOP_TIME, stopTime);
            appendField(doc, root, EPOCH_WIDTH_SEC, epochWidthSec);
            appendField(doc, root, WINDOW_WIDTH_SEC, windowWidthSec);
            appendField(doc, root, DELETE_TEMP_FILE, Boolean.toString(deleteIntermediateFile));
            Element spikes = doc.createElement(SPIKES_LIST);
            root.appendChild(spikes);
            for (int i = 0; i < phoneSpikes.length; i++) {
                Element spike = doc.createElement(SPIKE);
                spike.setAttribute(SPIKE_PHONE_ATT, phoneSpikes[i]);
                spike.setAttribute(SPIKE_ACTIGRAPH_ATT, actigraphSpikes[i]);
                spikes.appendChild(spike);
            }
            writeXml(xmlFilePath, doc);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an element representing {@code <tag>text</tag>} and appends it to
     * {@code root}.
     * 
     * @param doc
     *            The {@link Document} containing the {@link Element}.
     * @param root
     *            The {@link Node} to append the created {@link Element} to.
     * @param tag
     *            The tag name of the created {@link Element}.
     * @param text
     *            The content of the created {@link Element}.
     * @return the {@link Element} created.
     */
    private static Element appendField(Document doc, Element root, String tag, String text) {
        Element elem = doc.createElement(tag);
        elem.appendChild(doc.createTextNode(text));
        root.appendChild(elem);
        return elem;
    }

    /**
     * Writes the specified DOM {@link Document} to the specified XML file.
     * 
     * @param filePath
     *            The path to the XML output file.
     * @param doc
     *            The {@code Document} to write.
     * @throws IOException
     *             If an error occurs while writing to the file.
     */
    private static void writeXml(String filePath, Document doc) throws IOException {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            // tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // send DOM to file
            FileOutputStream fos = new FileOutputStream(filePath);
            tr.transform(new DOMSource(doc), new StreamResult(fos));
            fos.close();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a {@code RawParameters} object from the specified XML file.
     * 
     * @param xmlFilePath
     *            The path to the XML output file.
     * @throws IOException
     *             If an error occurs while reading the file.
     * @throws SAXException
     *             If any parse error occurs.
     */
    public static RawParameters load(String xmlFilePath) throws IOException, SAXException {
        RawParameters raw = new RawParameters();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the
            // XML file
            Document dom = db.parse(fixURI(xmlFilePath));
            Element root = dom.getDocumentElement();
            Element files = (Element) root.getElementsByTagName(FILES_LIST).item(0);
            String[] inFiles = getFilesPaths(files, INPUT_FILE);
            raw.phoneRawFile = inFiles[0];
            raw.actigEpFile = inFiles[1];
            String[] outFiles = getFilesPaths(files, OUTPUT_FILE);
            raw.outputFile = outFiles[0];
            raw.startTime = getField(root, START_TIME);
            raw.stopTime = getField(root, STOP_TIME);
            raw.epochWidthSec = getField(root, EPOCH_WIDTH_SEC);
            raw.windowWidthSec = getField(root, WINDOW_WIDTH_SEC);
            raw.deleteIntermediateFile = Boolean.parseBoolean(getField(root, DELETE_TEMP_FILE));
            Element spikes = (Element) root.getElementsByTagName(SPIKES_LIST).item(0);
            NodeList spikesList = spikes.getElementsByTagName(SPIKE);
            int nbSpikes = spikesList.getLength();
            raw.phoneSpikes = new String[nbSpikes];
            raw.actigraphSpikes = new String[nbSpikes];
            for (int i = 0; i < nbSpikes; i++) {
                Element spike = (Element) spikesList.item(i);
                raw.phoneSpikes[i] = spike.getAttribute(SPIKE_PHONE_ATT);
                raw.actigraphSpikes[i] = spike.getAttribute(SPIKE_ACTIGRAPH_ATT);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return raw;
    }

    /**
     * Returns the content of the first descendant node matching the specified tag.
     * <p>
     * Example:
     * 
     * <pre>
     * {@code <root>
     *     <name>
     *         <first>John</first>
     *         <last>Smith</last>
     *     </name>
     * </root>
     * 
     * getField(root, "last"); // returns "Smith"}
     * </pre>
     * 
     * </p>
     * 
     * @param root
     *            The starting point in the XML tree to look for descendants.
     * @param tag
     *            The tag of the desired descendant.
     * @return The content of the first descendant matching the tag.
     */
    private static String getField(Element root, String tag) {
        NodeList children = root.getElementsByTagName(tag);
        if (children.getLength() == 0) {
            return null;
        }
        Node fieldNode = children.item(0).getFirstChild();
        if (fieldNode == null) {
            return null;
        }
        return fieldNode.getNodeValue();
    }

    /**
     * Retrieves an array of file paths from the files list.
     * 
     * @param filesList
     *            The XML {@link Element} containing all the files nodes.
     * @param tag
     *            The tag name for the desired files ({@link #INPUT_FILE} or
     *            {@link #OUTPUT_FILE}).
     * @return An array containing the file paths as {@code String}s. Each file
     *         path's index in the returned array is the index given by the index
     *         attribute {@link #FILE_INDEX_ATT} in the XML Element.
     */
    private static String[] getFilesPaths(Element filesList, String tag) {
        NodeList childrenFiles = filesList.getElementsByTagName(tag);
        String[] filesPaths = new String[childrenFiles.getLength()];
        for (int i = 0; i < childrenFiles.getLength(); i++) {
            Element file = (Element) childrenFiles.item(i);
            int index = Integer.parseInt(file.getAttribute(FILE_INDEX_ATT));
            Node filePathNode = file.getFirstChild();
            if (filePathNode == null) {
                filesPaths[index] = null;
            } else {
                filesPaths[index] = filePathNode.getNodeValue();
            }
        }
        return filesPaths;
    }

    /**
     * Fix problems in the URIs (spaces for instance).
     * 
     * @param uri
     *            The original URI.
     * @return The corrected URI.
     */
    private static String fixURI(String uri) {
        // handle platform dependent strings
        String path = uri.replace(java.io.File.separatorChar, '/');
        // Windows fix
        if (path.length() >= 2) {
            char ch1 = path.charAt(1);
            // change "C:blah" to "/C:blah"
            if (ch1 == ':') {
                char ch0 = Character.toUpperCase(path.charAt(0));
                if (ch0 >= 'A' && ch0 <= 'Z') {
                    path = "/" + path;
                }
            }
            // change "//blah" to "file://blah"
            else if (ch1 == '/' && path.charAt(0) == '/') {
                path = "file:" + path;
            }
        }
        // replace spaces in file names with %20.
        // Original comment from JDK5: the following algorithm might not be
        // very performant, but people who want to use invalid URI's have to
        // pay the price.
        int pos = path.indexOf(' ');
        if (pos >= 0) {
            StringBuilder sb = new StringBuilder(path.length());
            // put characters before ' ' into the string builder
            for (int i = 0; i < pos; i++)
                sb.append(path.charAt(i));
            // and %20 for the space
            sb.append("%20");
            // for the remaining part, also convert ' ' to "%20".
            for (int i = pos + 1; i < path.length(); i++) {
                if (path.charAt(i) == ' ')
                    sb.append("%20");
                else
                    sb.append(path.charAt(i));
            }
            return sb.toString();
        }
        return path;
    }
}
