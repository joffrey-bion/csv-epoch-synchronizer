package parameters;

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

public class XmlSaver {

    public static void save(String xmlFilePath, RawParameters params) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();
            Element root = doc.createElement("instance");
            doc.appendChild(root);
            Element files = doc.createElement("files");
            root.appendChild(files);
            appendFilePickers(doc, files, new String[]{params.phoneRawFile, params.actigEpFile}, "input-file");
            appendFilePickers(doc, files, new String[]{params.outputFile}, "output-file");
            appendField(doc, root, "start", params.startTime);
            appendField(doc, root, "stop", params.stopTime);
            appendField(doc, root, "epoch-width", params.epochWidthSec);
            appendField(doc, root, "window-width", params.windowWidthSec);
            appendField(doc, root, "delete-temp", "" + params.deleteIntermediateFile);
            Element spikes = doc.createElement("spikes");
            root.appendChild(spikes);
            for (int i = 0; i < RawParameters.NB_MAX_SPIKES; i++) {
                Element spike = doc.createElement("spike");
                spike.setAttribute("phone", params.phoneSpikes[i]);
                spike.setAttribute("actig", params.actigraphSpikes[i]);
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

    private static void writeXml(String filePath, Document doc) {
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
            System.out.println("Parameters saved.");
        } catch (TransformerException te) {
            System.out.println(te.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    public static RawParameters load(String xmlFilePath) {
        RawParameters raw = new RawParameters();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the
            // XML file
            Document dom = db.parse(fixURI(xmlFilePath));
            Element root = dom.getDocumentElement();
            Element files = (Element) root.getElementsByTagName("files").item(0);
            String[] inFiles = getFilesPaths(files, "input-file");
            raw.phoneRawFile = inFiles[0];
            raw.actigEpFile = inFiles[1];
            String[] outFiles = getFilesPaths(files, "output-file");
            raw.outputFile = outFiles[0];
            raw.startTime = getField(root, "start");
            raw.stopTime = getField(root, "stop");
            raw.epochWidthSec = getField(root, "epoch-width");
            raw.windowWidthSec = getField(root, "window-width");
            raw.deleteIntermediateFile = Boolean.parseBoolean(getField(root, "delete-temp"));
            Element spikes = (Element) root.getElementsByTagName("spikes").item(0);
            NodeList spikesList = spikes.getElementsByTagName("spike");
            int nbSpikes = spikesList.getLength();
            raw.phoneSpikes = new String[nbSpikes];
            raw.actigraphSpikes = new String[nbSpikes];
            for (int i = 0; i < nbSpikes; i++) {
                Element spike = (Element) spikesList.item(i);
                raw.phoneSpikes[i] = spike.getAttribute("phone");
                raw.actigraphSpikes[i] = spike.getAttribute("actig");
            }
        } catch (ParserConfigurationException pce) {
            System.err.println(pce.getMessage());
            pce.printStackTrace();
        } catch (SAXException se) {
            System.err.println(se.getMessage());
            se.printStackTrace();
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
            ioe.printStackTrace();
        }
        return raw;
    }

    private static String getField(Element root, String tag) {
        NodeList children = root.getElementsByTagName(tag);
        if (children.getLength() == 0)
            return null;
        return children.item(0).getFirstChild().getNodeValue();
    }

    private static void appendFilePickers(Document doc, Element parent, String[] files,
            String tag) {
        for (int i = 0; i < files.length; i++) {
            Element file = appendField(doc, parent, tag, files[i]);
            file.setAttribute("index", i + "");
        }
    }

    private static String[] getFilesPaths(Element files, String tag) {
        NodeList childrenFiles = files.getElementsByTagName(tag);
        String[] filesPaths = new String[childrenFiles.getLength()];
        for (int i = 0; i < childrenFiles.getLength(); i++) {
            Element file = (Element) childrenFiles.item(i);
            int index = Integer.parseInt(file.getAttribute("index"));
            filesPaths[index] = file.getFirstChild().getNodeValue();
        }
        return filesPaths;
    }

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
