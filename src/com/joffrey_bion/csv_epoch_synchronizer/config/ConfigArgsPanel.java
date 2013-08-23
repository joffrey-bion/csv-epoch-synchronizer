package com.joffrey_bion.csv_epoch_synchronizer.config;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.joffrey_bion.csv_epoch_synchronizer.actigraph.CutPointsSet;
import com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2.PhoneType;
import com.joffrey_bion.generic_guis.file_picker.FilePicker;
import com.joffrey_bion.generic_guis.file_picker.JFilePickersPanel;

@SuppressWarnings("serial")
public class ConfigArgsPanel extends JPanel {

    private final JFilePickersPanel filePickers;
    private JComboBox<CutPointsSet> cbCutPoints;
    private JTextField tfEpochWidth;
    private JTextField tfWindowWidth;
    private JCheckBox chckbxDeleteTemp;

    /**
     * Create the panel.
     * 
     * @param filePickers
     */
    public ConfigArgsPanel(JFilePickersPanel filePickers) {
        this.filePickers = filePickers;
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[] { 0, 0, 0, 0 };
        gbl_panel_2.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0 };
        gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
        setLayout(gbl_panel_2);

        JLabel lblNbSyncMarkers = new JLabel("Cut points set for the Actigraph:");
        GridBagConstraints gbc_lblNbSyncMarkers = new GridBagConstraints();
        gbc_lblNbSyncMarkers.anchor = GridBagConstraints.WEST;
        gbc_lblNbSyncMarkers.insets = new Insets(0, 0, 5, 5);
        gbc_lblNbSyncMarkers.gridx = 0;
        gbc_lblNbSyncMarkers.gridy = 0;
        add(lblNbSyncMarkers, gbc_lblNbSyncMarkers);

        cbCutPoints = new JComboBox<>();
        cbCutPoints.setModel(new DefaultComboBoxModel<>(CutPointsSet.values()));
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.gridwidth = 2;
        gbc_comboBox.anchor = GridBagConstraints.WEST;
        gbc_comboBox.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox.gridx = 1;
        gbc_comboBox.gridy = 0;
        add(cbCutPoints, gbc_comboBox);

        JLabel lblWindowLength = new JLabel("Phones windows length (sec):");
        GridBagConstraints gbc_lblWindowLength = new GridBagConstraints();
        gbc_lblWindowLength.anchor = GridBagConstraints.WEST;
        gbc_lblWindowLength.insets = new Insets(0, 0, 5, 5);
        gbc_lblWindowLength.gridx = 0;
        gbc_lblWindowLength.gridy = 1;
        add(lblWindowLength, gbc_lblWindowLength);

        tfWindowWidth = new JTextField();
        tfWindowWidth.setColumns(1);
        GridBagConstraints gbc_tfWindowLength = new GridBagConstraints();
        gbc_tfWindowLength.insets = new Insets(0, 0, 5, 5);
        gbc_tfWindowLength.anchor = GridBagConstraints.WEST;
        gbc_tfWindowLength.gridx = 1;
        gbc_tfWindowLength.gridy = 1;
        add(tfWindowWidth, gbc_tfWindowLength);

        JLabel lblEpochLength = new JLabel("Phones epochs length (sec):");
        GridBagConstraints gbc_lblEpochLength = new GridBagConstraints();
        gbc_lblEpochLength.anchor = GridBagConstraints.WEST;
        gbc_lblEpochLength.insets = new Insets(0, 0, 5, 5);
        gbc_lblEpochLength.gridx = 0;
        gbc_lblEpochLength.gridy = 2;
        add(lblEpochLength, gbc_lblEpochLength);

        tfEpochWidth = new JTextField();
        tfEpochWidth.setColumns(1);
        GridBagConstraints gbc_tfEpochLength = new GridBagConstraints();
        gbc_tfEpochLength.insets = new Insets(0, 0, 5, 5);
        gbc_tfEpochLength.anchor = GridBagConstraints.WEST;
        gbc_tfEpochLength.gridx = 1;
        gbc_tfEpochLength.gridy = 2;
        add(tfEpochWidth, gbc_tfEpochLength);

        JLabel lblwhenComparedTo = new JLabel("(when compared to K4b2 only)");
        GridBagConstraints gbc_lblwhenComparedTo = new GridBagConstraints();
        gbc_lblwhenComparedTo.anchor = GridBagConstraints.WEST;
        gbc_lblwhenComparedTo.insets = new Insets(0, 0, 5, 5);
        gbc_lblwhenComparedTo.gridx = 2;
        gbc_lblwhenComparedTo.gridy = 2;
        add(lblwhenComparedTo, gbc_lblwhenComparedTo);

        chckbxDeleteTemp = new JCheckBox("Delete temporary files");
        GridBagConstraints gbc_chckbxDeleteTemp = new GridBagConstraints();
        gbc_chckbxDeleteTemp.insets = new Insets(0, 0, 0, 5);
        gbc_chckbxDeleteTemp.gridwidth = 3;
        gbc_chckbxDeleteTemp.anchor = GridBagConstraints.WEST;
        gbc_chckbxDeleteTemp.gridx = 0;
        gbc_chckbxDeleteTemp.gridy = 3;
        add(chckbxDeleteTemp, gbc_chckbxDeleteTemp);
        chckbxDeleteTemp.setSelected(Config.get().deleteIntermediateFile);
        chckbxDeleteTemp.setHorizontalAlignment(SwingConstants.TRAILING);

        fillWithConfig();
    }

    /**
     * Populates the fields of this panel (including files paths) with the current
     * configuration.
     */
    private void fillWithConfig() {
        Config config = Config.get();
        FilePicker[] inputs = filePickers.getInputFilePickers();
        inputs[0].setSelectedFilePath(config.getClassifier(Profile.POCKET, PhoneType.GYRO));
        inputs[1].setSelectedFilePath(config.getClassifier(Profile.HOLSTER, PhoneType.GYRO));
        inputs[2].setSelectedFilePath(config.getClassifier(Profile.POCKET, PhoneType.NO_GYRO));
        inputs[3].setSelectedFilePath(config.getClassifier(Profile.HOLSTER, PhoneType.NO_GYRO));
        cbCutPoints.setSelectedItem(config.cutPointsSet);
        tfEpochWidth.setText(Integer.toString(config.epochWidthVsK4b2));
        tfWindowWidth.setText(Integer.toString(config.windowWidthSec));
        chckbxDeleteTemp.setSelected(config.deleteIntermediateFile);
    }

    /**
     * Fills the specified {@link PvAParams} object with the current fields of this
     * panel (including the files paths).
     * 
     * @param config
     *            The params object to populate.
     * @throws ParseException
     *             If one field could not be properly parsed.
     */
    public void updateConfig() throws ParseException {
        Config config = Config.get();
        config.setClassifier(Profile.POCKET, PhoneType.GYRO, filePickers.getInputFilePaths()[0]);
        config.setClassifier(Profile.HOLSTER, PhoneType.GYRO, filePickers.getInputFilePaths()[1]);
        config.setClassifier(Profile.POCKET, PhoneType.NO_GYRO, filePickers.getInputFilePaths()[0]);
        config.setClassifier(Profile.HOLSTER, PhoneType.NO_GYRO, filePickers.getInputFilePaths()[1]);
        config.cutPointsSet = (CutPointsSet) cbCutPoints.getSelectedItem();
        config.deleteIntermediateFile = chckbxDeleteTemp.isSelected();
        try {
            config.epochWidthVsK4b2 = Integer.valueOf(tfEpochWidth.getText());
        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Incorrect epoch length value, integer expected (in seconds).", 1);
        }
        try {
            config.windowWidthSec = Integer.valueOf(tfWindowWidth.getText());
        } catch (NumberFormatException e) {
            throw new ParseException(
                    "Incorrect window length value, integer expected (in seconds).", 1);
        }
    }
}
