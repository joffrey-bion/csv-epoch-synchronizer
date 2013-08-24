package com.joffrey_bion.csv_epoch_synchronizer.mains.validation;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph.PvAParams;
import com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2.PvKParams;
import com.joffrey_bion.utils.paths.Paths;
import com.joffrey_bion.utils.xml.serializers.SimpleSerializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PVParams extends Parameters {

    static final String PVA_FILE_PATH = "pva-params-file";
    static final String PVK_FILE_PATH = "pvk-params-file";
    static final String PARTICIPANT_FILE_PATH = "participant-file";
    static final String OUTPUT_FILE_PATH = "output-file";

    private static final ParamsSchema SCHEMA = new ParamsSchema(1);
    static {
        SCHEMA.addParam(PVA_FILE_PATH, SimpleSerializer.STRING);
        SCHEMA.addParam(PVK_FILE_PATH, SimpleSerializer.STRING);
        SCHEMA.addParam(PARTICIPANT_FILE_PATH, SimpleSerializer.STRING);
        SCHEMA.addParam(OUTPUT_FILE_PATH, SimpleSerializer.STRING);
    }

    public PvAParams pvaParams;
    public PvKParams pvkParams;
    public String participantFile;
    public String outputFile;

    /**
     * Creates a new {@link PVParams} object from the specified XML file.
     * 
     * @param paramsFilePath
     *            The path to the XML file to read the parameters from.
     * @throws IOException
     *             If an error occurs while reading the file.
     * @throws SAXException
     *             If any XML parse error occurs.
     * @throws SpecificationNotMetException
     *             If the XML file does not meet the schema's requirements.
     */
    public PVParams(String xmlFilePath) throws IOException, SAXException,
            SpecificationNotMetException {
        super(SCHEMA);
        loadFromXml(xmlFilePath);
        populatePublicFields();
    }

    /**
     * Creates a new empty {@link PVParams} object that has to be filled via
     * {@link Parameters}' methods. When all parameters are set, the method
     * {@link #populatePublicFields()} has to be called to make the public fields of
     * this class usable.
     */
    public PVParams() {
        super(SCHEMA);
    }

    /**
     * Pull the data from this {@link Parameters} object and make it available more
     * efficiently through the public fields and getters.
     * 
     * @throws SpecificationNotMetException
     * @throws SAXException
     * @throws IOException
     */
    public void populatePublicFields() throws IOException, SAXException,
            SpecificationNotMetException {
        this.pvaParams = new PvAParams(getString(PVA_FILE_PATH));
        this.pvkParams = new PvKParams(getString(PVK_FILE_PATH));
        this.participantFile = getString(PARTICIPANT_FILE_PATH);
        this.outputFile = getString(OUTPUT_FILE_PATH);
    }

    @Override
    public void loadFromXml(String xmlFilePath) throws IOException, SAXException,
            SpecificationNotMetException {
        super.loadFromXml(xmlFilePath);
        resolve(PVA_FILE_PATH, xmlFilePath);
        resolve(PVK_FILE_PATH, xmlFilePath);
        resolve(PARTICIPANT_FILE_PATH, xmlFilePath);
        resolve(OUTPUT_FILE_PATH, xmlFilePath);
    }

    @Override
    public void saveToXml(String xmlFilePath) throws IOException, SpecificationNotMetException {
        relativize(PVA_FILE_PATH, xmlFilePath);
        relativize(PVK_FILE_PATH, xmlFilePath);
        relativize(PARTICIPANT_FILE_PATH, xmlFilePath);
        relativize(OUTPUT_FILE_PATH, xmlFilePath);
        super.saveToXml(xmlFilePath);
    }

    /**
     * Replace an absolute path parameter by a path relative to the specified
     * {@code basePath}.
     * 
     * @param key
     *            The key of the parameter to relativize.
     * @param basePath
     *            The base path to use.
     */
    private void relativize(String key, String basePath) {
        set(key, Paths.relativizeSibling(basePath, getString(key)));
    }

    /**
     * Replace a relative path parameter by an absolute path.
     * 
     * @param key
     *            The key of the parameter to relativize.
     * @param basePath
     *            The base path to use to resolve the relative path.
     */
    private void resolve(String key, String basePath) {
        set(key, Paths.resolveSibling(basePath, getString(key)));
    }
}
