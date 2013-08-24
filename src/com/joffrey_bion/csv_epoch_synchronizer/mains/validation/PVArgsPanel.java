package com.joffrey_bion.csv_epoch_synchronizer.mains.validation;

import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.xml.sax.SAXException;

import com.joffrey_bion.generic_guis.file_picker.FilePicker;
import com.joffrey_bion.generic_guis.file_picker.JFilePickersPanel;
import com.joffrey_bion.generic_guis.parameters.SaveLoadPanel;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

@SuppressWarnings("serial")
public class PVArgsPanel extends JPanel {

    private final JFilePickersPanel filePickers;

    /**
     * Create the panel.
     * 
     * @param filePickers
     */
    public PVArgsPanel(JFilePickersPanel filePickers) {
        this.filePickers = filePickers;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        SaveLoadPanel psp = new SaveLoadPanel("Save params...", "Load params...") {
            @Override
            public void saveToFile(String paramFilePath) {
                try {
                    PVParams params = new PVParams();
                    getParameters(params);
                    params.saveToXml(paramFilePath);
                    System.out.println("Parameters saved to '" + paramFilePath + "'.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void loadFromFile(String paramFilePath) {
                try {
                    PVParams params = new PVParams(paramFilePath);
                    setParameters(params);
                    System.out.println("Parameters loaded from '" + paramFilePath + "'.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        psp.addFileTypeFilter(".xml", "XML Parameter File");
        add(psp);
    }

    /**
     * Populates the fields of this panel (including files paths) with the given
     * parameters.
     * 
     * @param params
     *            The parameters to use to populate this panel.
     */
    private void setParameters(PVParams params) {
        FilePicker[] inputs = filePickers.getInputFilePickers();
        inputs[PhoneValidation.INPUT_PVA].setSelectedFilePath(params
                .getString(PVParams.PVA_FILE_PATH));
        inputs[PhoneValidation.INPUT_PVK].setSelectedFilePath(params
                .getString(PVParams.PVK_FILE_PATH));
        inputs[PhoneValidation.INPUT_PARTICIPANT].setSelectedFilePath(params
                .getString(PVParams.PARTICIPANT_FILE_PATH));
        FilePicker[] outputs = filePickers.getOutputFilePickers();
        outputs[PhoneValidation.OUTPUT].setSelectedFilePath(params
                .getString(PVParams.OUTPUT_FILE_PATH));
    }

    /**
     * Fills the specified parameters object with the current fields of this panel
     * (including the files paths).
     * 
     * @param params
     *            The params object to populate.
     * @throws SpecificationNotMetException
     * @throws SAXException
     * @throws IOException
     */
    public void getParameters(PVParams params) throws IOException, SAXException,
            SpecificationNotMetException {
        getUnpopulatedParameters(params);
        params.populatePublicFields();
    }

    public void getUnpopulatedParameters(PVParams params) {
        setFileParam(params, PVParams.PVA_FILE_PATH,
                filePickers.getInputFilePaths()[PhoneValidation.INPUT_PVA]);
        setFileParam(params, PVParams.PVK_FILE_PATH,
                filePickers.getInputFilePaths()[PhoneValidation.INPUT_PVK]);
        setFileParam(params, PVParams.PARTICIPANT_FILE_PATH,
                filePickers.getInputFilePaths()[PhoneValidation.INPUT_PARTICIPANT]);
        setFileParam(params, PVParams.OUTPUT_FILE_PATH,
                filePickers.getOutputFilePaths()[PhoneValidation.OUTPUT]);
    }

    private static void setFileParam(PVParams params, String key, String filePath) {
        if (filePath != null && !filePath.equals("")) {
            params.set(key, filePath);
        }
    }
}
