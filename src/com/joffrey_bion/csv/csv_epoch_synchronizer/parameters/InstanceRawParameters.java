package com.joffrey_bion.csv.csv_epoch_synchronizer.parameters;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.joffrey_bion.utils.xml_helper.XmlHelper;

/**
 * A {@code InstanceRawParameters} object contains all the raw information read from
 * an XML parameter file or from the GUI. Therefore, most fields are just
 * {@code String}s because they have not been parsed yet.
 * <p>
 * This class provides serialization methods {@link #save(String)} and
 * {@link #load(String)}, to write/read raw parameters to/from an XML file.
 * </p>
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class InstanceRawParameters {

    private static final String ROOT = "parameters";
    private static final String FILES_LIST = "files";
    private static final String INPUT_FILE = "input-file";
    private static final String OUTPUT_FILE = "output-file";
    private static final String FILE_INDEX_ATT = "index";
    private static final String START_TIME = "start";
    private static final String STOP_TIME = "stop";
    private static final String EPOCH_WIDTH_SEC = "epoch-width";
    private static final String SPIKES_LIST = "spikes";
    private static final String SPIKE = "spike";
    private static final String SPIKE_PHONE_ATT = "phone";
    private static final String SPIKE_ACTIGRAPH_ATT = "actig";

    public static final int NB_MAX_SPIKES = 6;

    public String phoneRawFile;
    public String actigEpFile;
    public String outputFile;
    public String epochWidthSec;
    public String startTime;
    public String stopTime;
    public String[] phoneSpikes;
    public String[] actigraphSpikes;

    /**
     * Save this {@code InstanceRawParameters} object to the specified XML file.
     * 
     * @param xmlFilePath
     *            The path to the XML output file.
     * @throws IOException
     *             If an error occurs while writing to the file.
     */
    public void save(String xmlFilePath) throws IOException {
        try {
            Document doc = XmlHelper.createEmptyDomDocument();
            Element root = doc.createElement(ROOT);
            doc.appendChild(root);
            Element files = doc.createElement(FILES_LIST);
            root.appendChild(files);
            XmlHelper.appendField(doc, files, INPUT_FILE, phoneRawFile).setAttribute(
                    FILE_INDEX_ATT, "0");
            XmlHelper.appendField(doc, files, INPUT_FILE, actigEpFile).setAttribute(FILE_INDEX_ATT,
                    "1");
            XmlHelper.appendField(doc, files, OUTPUT_FILE, outputFile).setAttribute(FILE_INDEX_ATT,
                    "0");
            XmlHelper.appendField(doc, root, START_TIME, startTime);
            XmlHelper.appendField(doc, root, STOP_TIME, stopTime);
            XmlHelper.appendField(doc, root, EPOCH_WIDTH_SEC, epochWidthSec);
            Element spikes = doc.createElement(SPIKES_LIST);
            root.appendChild(spikes);
            for (int i = 0; i < phoneSpikes.length; i++) {
                Element spike = doc.createElement(SPIKE);
                spike.setAttribute(SPIKE_PHONE_ATT, phoneSpikes[i]);
                spike.setAttribute(SPIKE_ACTIGRAPH_ATT, actigraphSpikes[i]);
                spikes.appendChild(spike);
            }
            XmlHelper.writeXml(xmlFilePath, doc);
        } catch (ParserConfigurationException e) {
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
    public static InstanceRawParameters load(String xmlFilePath) throws IOException, SAXException {
        InstanceRawParameters raw = new InstanceRawParameters();
        Document dom;
        try {
            dom = XmlHelper.getDomDocumentFromFile(xmlFilePath);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        Element root = dom.getDocumentElement();
        Element files = (Element) root.getElementsByTagName(FILES_LIST).item(0);
        String[] inFiles = getFilesPaths(files, INPUT_FILE);
        raw.phoneRawFile = inFiles[0];
        raw.actigEpFile = inFiles[1];
        String[] outFiles = getFilesPaths(files, OUTPUT_FILE);
        raw.outputFile = outFiles[0];
        raw.startTime = XmlHelper.getField(root, START_TIME);
        raw.stopTime = XmlHelper.getField(root, STOP_TIME);
        raw.epochWidthSec = XmlHelper.getField(root, EPOCH_WIDTH_SEC);
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
        return raw;
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
}
