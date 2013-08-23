package com.joffrey_bion.csv_epoch_synchronizer.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.actigraph.CutPointsSet;
import com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2.PhoneType;
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

    private static final String CONFIG_DIR = getConfigDirectory() + "\\";
    private static final String CONFIG_FILENAME = "config.xml";
    private static final String CLASSIFIERS_DIR = CONFIG_DIR + "classifiers\\";

    private static final String CUT_POINTS = "cut-points";
    private static final String PKT_GYR_CLASSIFIER_PATH = "classifier-pocket-gyro";
    private static final String HOL_GYR_CLASSIFIER_PATH = "classifier-holster-gyro";
    private static final String PKT_NGYR_CLASSIFIER_PATH = "classifier-pocket-no-gyro";
    private static final String HOL_NGYR_CLASSIFIER_PATH = "classifier-holster-no-gyro";
    private static final String WINDOW_WIDTH = "window-width";
    private static final String PHONE_EP_WIDTH_VS_K4B2 = "epoch-width-VS-K4b2";
    private static final String DELETE_TEMP_FILE = "delete-temp-files";

    private static final CutPointsSet DEFAULT_CUT_POINTS = CutPointsSet.CUSTOM;
    private static final int DEFAULT_WINDOW_WIDTH = 5;
    private static final int DEFAULT_EP_WIDTH = 1;
    private static final boolean DEFAULT_DELETE_TEMP = true;

    private static final EnumSerializer<CutPointsSet> CUT_POINTS_SER = new EnumSerializer<>(
            CutPointsSet.class);

    private static final ParamsSchema SCHEMA = new ParamsSchema("config", 4);
    static {
        SCHEMA.addParam(CUT_POINTS, CUT_POINTS_SER, false, DEFAULT_CUT_POINTS,
                "The cut points set must be one of CutPointsSet enum constant names");
        SCHEMA.addParam(PKT_GYR_CLASSIFIER_PATH, SimpleSerializer.STRING, false, CLASSIFIERS_DIR
                + "pocket_gyro.xml",
                "The path to the XML classifier to use for the simulated phone decisions (for different profiles)");
        SCHEMA.addParam(HOL_GYR_CLASSIFIER_PATH, SimpleSerializer.STRING, false, CLASSIFIERS_DIR
                + "holster_gyro.xml");
        SCHEMA.addParam(PKT_NGYR_CLASSIFIER_PATH, SimpleSerializer.STRING, false, CLASSIFIERS_DIR
                + "pocket_no_gyro.xml");
        SCHEMA.addParam(HOL_NGYR_CLASSIFIER_PATH, SimpleSerializer.STRING, false, CLASSIFIERS_DIR
                + "holster_no_gyro.xml");
        SCHEMA.addParam(WINDOW_WIDTH, SimpleSerializer.INTEGER, false, DEFAULT_WINDOW_WIDTH,
                "The time window's length to use to smooth the data, in seconds.");
        SCHEMA.addParam(PHONE_EP_WIDTH_VS_K4B2, SimpleSerializer.INTEGER, false, DEFAULT_EP_WIDTH,
                "The epoch length (in seconds) to use for the phone when comparing "
                        + "with the K4b2 (when comparing to the actigraph, the actigraph's "
                        + "epoch width is used for the phone as well)");
        SCHEMA.addParam(DELETE_TEMP_FILE, SimpleSerializer.BOOLEAN, false, DEFAULT_DELETE_TEMP,
                "Indicates if the temporary files should be deleted");
    }

    /** The only instance of this singleton. */
    private static Config instance = null;
    /** The path to the XML configuration file. */
    private String configFilePath = null;

    /**
     * Returns the current configuration.
     * 
     * @return the configuration as a {@link Config} object.
     */
    public static Config get() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

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
     * Whether or not the intermediate file (containing phone epochs) must be
     * deleted.
     */
    public boolean deleteIntermediateFile;
    /**
     * The path to the XML classifiers for the different profiles.
     */
    private String[][] classifiersPaths;

    /**
     * Creates a {@link Config} object from the specified configuration file.
     */
    private Config() {
        classifiersPaths = new String[Profile.values().length][PhoneType.values().length];
        configFilePath = CONFIG_DIR + CONFIG_FILENAME;
        if (!loadFromConfigFile()) {
            System.out.println("Creating config file '" + CONFIG_FILENAME + "' with defaults...");
            createDefaultConfigFile(configFilePath);
            System.out.println("Complete.");
            loadFromConfigFile();
        }
        System.out.println("Config file loaded.");
        System.out.println();
    }

    /**
     * Creates a default XML config file, pointing to a default classifier.
     * 
     * @param xmlFilePath
     *            The path to the XML output file.
     */
    private static void createDefaultConfigFile(String xmlFilePath) {
        try {
            new Parameters(SCHEMA).saveToXml(xmlFilePath);
            // createDefaultClassifier();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SpecificationNotMetException e) {
            // should not happen
            e.printStackTrace();
        }
    }

    /**
     * Save this {@code Config} object to the XML configuration file.
     */
    public void saveToConfigFile() {
        try {
            Parameters p = new Parameters(SCHEMA);
            p.set(CUT_POINTS, cutPointsSet);
            p.set(DELETE_TEMP_FILE, deleteIntermediateFile);
            p.set(WINDOW_WIDTH, windowWidthSec);
            p.set(PHONE_EP_WIDTH_VS_K4B2, epochWidthVsK4b2);
            p.set(PKT_GYR_CLASSIFIER_PATH, getClassifier(Profile.POCKET, PhoneType.GYRO));
            p.set(HOL_GYR_CLASSIFIER_PATH, getClassifier(Profile.HOLSTER, PhoneType.GYRO));
            p.set(PKT_NGYR_CLASSIFIER_PATH, getClassifier(Profile.POCKET, PhoneType.NO_GYRO));
            p.set(HOL_NGYR_CLASSIFIER_PATH, getClassifier(Profile.HOLSTER, PhoneType.NO_GYRO));
            p.saveToXml(configFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SpecificationNotMetException e) {
            // should not happen
            e.printStackTrace();
        }
    }

    /**
     * Loads this {@code Config} from the XML configuration file.
     * 
     * @return {@code true} if the configuration file has been found and loaded
     *         properly.
     */
    private boolean loadFromConfigFile() {
        try {
            Parameters p = Parameters.loadFromXml(configFilePath, SCHEMA);
            loadFromParameters(p);
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("The config file does not exist (" + configFilePath + ")");
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
        setClassifier(Profile.POCKET, PhoneType.GYRO, p.getString(PKT_GYR_CLASSIFIER_PATH));
        setClassifier(Profile.HOLSTER, PhoneType.GYRO, p.getString(HOL_GYR_CLASSIFIER_PATH));
        setClassifier(Profile.POCKET, PhoneType.NO_GYRO, p.getString(PKT_NGYR_CLASSIFIER_PATH));
        setClassifier(Profile.HOLSTER, PhoneType.NO_GYRO, p.getString(HOL_NGYR_CLASSIFIER_PATH));
    }

    /**
     * Returns the path to the configuration file.
     */
    private static String getConfigDirectory() {
        try {
            String jarPath = Paths.getJarLocation(Config.class);
            if (jarPath == null) {
                jarPath = System.getProperty("user.home");
            }
            return jarPath;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns the path to the XML file containing the classifier for the given
     * profile.
     * 
     * @param profile
     *            The profile to get the classifier for.
     * @param phoneType
     *            The type of phone to get the classifier for.
     * @return the path to the XML file containing the classifier for the given
     *         profile.
     */
    public String getClassifier(Profile profile, PhoneType phoneType) {
        return classifiersPaths[profile.ordinal()][phoneType.ordinal()];
    }

    /**
     * Sets the classifier path for the given profile.
     * 
     * @param profile
     *            Holster or Pocket.
     * @param phoneType
     *            Gyro or not.
     * @param classifierPath
     *            The path to the XML classifier to assign to this profile.
     */
    public void setClassifier(Profile profile, PhoneType phoneType, String classifierPath) {
        String nonEmptyPath = "".equals(classifierPath) ? null : classifierPath;
        classifiersPaths[profile.ordinal()][phoneType.ordinal()] = nonEmptyPath;
    }
}
