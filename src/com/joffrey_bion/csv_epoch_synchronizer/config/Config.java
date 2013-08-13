package com.joffrey_bion.csv_epoch_synchronizer.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.actigraph.CutPointsSet;
import com.joffrey_bion.utils.paths.Paths;
import com.joffrey_bion.utils.xml.serializers.EnumSerializer;
import com.joffrey_bion.utils.xml.serializers.SimpleSerializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.Parameters.MissingParameterException;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

/**
 * A singleton containing the values of the configuration file. If no such file
 * exists, it is created with default values the first time this class is accessed.
 * 
 * @author <a href="mailto:joffrey.bion@gmail.com">Joffrey BION</a>
 */
public class Config {

    private static final String CONFIG_FILENAME = "config.xml";

    private static final String CUT_POINTS = "cut-points";
    private static final String WINDOW_WIDTH = "window-width";
    private static final String PHONE_EP_WIDTH_VS_K4B2 = "epoch-width-VS-K4b2";
    private static final String DELETE_TEMP_FILE = "delete-temp-files";

    private static final CutPointsSet DEFAULT_CUT_POINTS = CutPointsSet.CUSTOM;
    private static final int DEFAULT_WINDOW_WIDTH = 5;
    private static final int DEFAULT_EP_WIDTH = 1;
    private static final boolean DEFAULT_DELETE_TEMP = true;

    private static final EnumSerializer<CutPointsSet> CUT_POINTS_SER = new EnumSerializer<>(
            CutPointsSet.class);

    private static final ParamsSchema SCHEMA = new ParamsSchema("config", 2);
    static {
        SCHEMA.addParam(CUT_POINTS, CUT_POINTS_SER, false, DEFAULT_CUT_POINTS,
                "The cut points set must be one of CutPointsSet enum constant names");
        SCHEMA.addParam(WINDOW_WIDTH, SimpleSerializer.INTEGER, false, DEFAULT_WINDOW_WIDTH,
                "The time window's width to use to smooth the data");
        SCHEMA.addParam(PHONE_EP_WIDTH_VS_K4B2, SimpleSerializer.INTEGER, false, DEFAULT_EP_WIDTH,
                "The epoch width to use for the phone when comparing "
                        + "with the K4b2 (when comparing to the actigraph, the actigraph's "
                        + "epoch width is used for the phone as well)");
        SCHEMA.addParam(DELETE_TEMP_FILE, SimpleSerializer.BOOLEAN, false, DEFAULT_DELETE_TEMP,
                "Indicates if the temporary files should be deleted");
    }

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
    /**
     * The epoch width to use for the phone when compared to the K4b2 results, in
     * seconds
     */
    public int epochWidthVsK4b2;

    /**
     * Creates a {@link Config} object from the specified configuration file.
     * 
     * @param configFileName
     *            The path to the XML configuration file.
     */
    private Config(String configFileName) {
        if (configFileName == null) {
            loadFromParameters(new Parameters(SCHEMA));
            System.out.println("Default config loaded (no config file created).");
        } else if (!loadFromFile(configFileName)) {
            System.out.println("Creating config file '" + CONFIG_FILENAME + "' with defaults...");
            createDefaultConfigFile(configFileName);
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
    private static void createDefaultConfigFile(String xmlFilePath) {
        try {
            new Parameters(SCHEMA).saveToXml(xmlFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads this {@code Config} from the specified XML configuration file.
     * 
     * @param xmlFilePath
     *            The path to the XML output file.
     * @return {@code true} if the configuration file has been found and loaded
     *         properly.
     */
    private boolean loadFromFile(String xmlFilePath) {
        try {
            Parameters p = Parameters.loadFromXml(xmlFilePath, SCHEMA);
            loadFromParameters(p);
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("The config file does not exist (" + xmlFilePath + ")");
            return false;
        } catch (IOException | SAXException | SpecificationNotMetException
                | MissingParameterException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Populates the fields of this configuration with the specified
     * {@link Parameters}.
     * 
     * @param p
     *            The {@link Parameters} object to use.
     */
    private void loadFromParameters(Parameters p) {
        cutPointsSet = (CutPointsSet) p.get(CUT_POINTS);
        windowWidthSec = p.getInteger(WINDOW_WIDTH);
        epochWidthVsK4b2 = p.getInteger(PHONE_EP_WIDTH_VS_K4B2);
        deleteIntermediateFile = p.getBoolean(DELETE_TEMP_FILE);
    }

    /**
     * Returns the path to the configuration file.
     */
    private static String getConfigFilePath() {
        try {
            return Paths.getJarLocation(Config.class) + "\\" + CONFIG_FILENAME;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
