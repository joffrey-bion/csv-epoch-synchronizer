package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import com.joffrey_bion.csv_epoch_synchronizer.actigraph.ActigraphFileFormat;
import com.joffrey_bion.csv_epoch_synchronizer.config.Profile;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneLocation;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneType;
import com.joffrey_bion.generic_guis.file_picker.FilePicker;
import com.joffrey_bion.generic_guis.file_picker.JFilePickersPanel;
import com.joffrey_bion.generic_guis.parameters.SaveLoadPanel;
import com.joffrey_bion.xml_parameters_serializer.Parameters.MissingParameterException;

import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import java.awt.Font;
import java.text.ParseException;
import java.util.Arrays;

import javax.swing.Box;

import java.awt.Component;
import javax.swing.JComboBox;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
class PvAArgsPanel extends JPanel {

    private static final int NB_MAX_SPIKES = 6;
    private static final int DATE_TEXTFIELD_WIDTH = 17;
    private final JFilePickersPanel filePickers;

    private JTextField tfStartTime;
    private JTextField tfStopTime;

    private JLabel[] tfSpikeLabel;
    private JTextField[] tfSpikePhone;
    private JTextField[] tfSpikeActig;
    private JTextField tfEpochWidth;
    private JComboBox<ActigraphFileFormat> cBoxActigraphFileFormat;
    private JComboBox<PhoneType> cbGyro;
    private JComboBox<Profile> profileComboBox;
    private JComboBox<PhoneLocation> cbLocation;

    /**
     * Create the panel.
     * 
     * @param filePickers
     *            The file pickers of the parent window.
     */
    public PvAArgsPanel(JFilePickersPanel filePickers) {
        this.filePickers = filePickers;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JPanel panelArgsLeft = new JPanel();
        panelArgsLeft.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.add(panelArgsLeft);
        panelArgsLeft.setLayout(new BoxLayout(panelArgsLeft, BoxLayout.Y_AXIS));

        JPanel panelLimits = new JPanel();
        panelLimits.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelArgsLeft.add(panelLimits);
        GridBagLayout gbl_panelLimits = new GridBagLayout();
        gbl_panelLimits.columnWidths = new int[] { 0, 0, 0 };
        gbl_panelLimits.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_panelLimits.columnWeights = new double[] { 0.0, 0.0, 1.0 };
        gbl_panelLimits.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
        panelLimits.setLayout(gbl_panelLimits);

        JLabel lblNewLabel = new JLabel("Processing Limits (actigraph time)");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
        gbc_lblNewLabel.anchor = GridBagConstraints.CENTER;
        gbc_lblNewLabel.gridwidth = 3;
        gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
        gbc_lblNewLabel.gridx = 0;
        gbc_lblNewLabel.gridy = 0;
        panelLimits.add(lblNewLabel, gbc_lblNewLabel);

        JLabel lblStartTime = new JLabel("Start time:");
        GridBagConstraints gbc_lblStartTime = new GridBagConstraints();
        gbc_lblStartTime.insets = new Insets(0, 0, 5, 5);
        gbc_lblStartTime.gridx = 0;
        gbc_lblStartTime.gridy = 1;
        panelLimits.add(lblStartTime, gbc_lblStartTime);

        tfStartTime = new JTextField();
        tfStartTime.setHorizontalAlignment(SwingConstants.CENTER);
        tfStartTime.setColumns(DATE_TEXTFIELD_WIDTH);
        GridBagConstraints gbc_tfStartTime = new GridBagConstraints();
        gbc_tfStartTime.insets = new Insets(0, 0, 5, 5);
        gbc_tfStartTime.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfStartTime.gridx = 1;
        gbc_tfStartTime.gridy = 1;
        panelLimits.add(tfStartTime, gbc_tfStartTime);

        JLabel lblStopTime = new JLabel("Stop time:");
        GridBagConstraints gbc_lblStopTime = new GridBagConstraints();
        gbc_lblStopTime.anchor = GridBagConstraints.WEST;
        gbc_lblStopTime.insets = new Insets(0, 0, 5, 5);
        gbc_lblStopTime.gridx = 0;
        gbc_lblStopTime.gridy = 2;
        panelLimits.add(lblStopTime, gbc_lblStopTime);

        tfStopTime = new JTextField();
        tfStopTime.setHorizontalAlignment(SwingConstants.CENTER);
        tfStopTime.setColumns(DATE_TEXTFIELD_WIDTH);
        GridBagConstraints gbc_tfStopTime = new GridBagConstraints();
        gbc_tfStopTime.insets = new Insets(0, 0, 5, 5);
        gbc_tfStopTime.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfStopTime.gridx = 1;
        gbc_tfStopTime.gridy = 2;
        panelLimits.add(tfStopTime, gbc_tfStopTime);

        JLabel lblFormat = new JLabel("(" + PvAParams.TIMESTAMP_FORMAT + ")");
        GridBagConstraints gbc_lblFormat = new GridBagConstraints();
        gbc_lblFormat.insets = new Insets(0, 0, 5, 5);
        gbc_lblFormat.gridx = 1;
        gbc_lblFormat.gridy = 3;
        panelLimits.add(lblFormat, gbc_lblFormat);

        panelArgsLeft.add(Box.createVerticalStrut(5));
        panelArgsLeft.add(new JSeparator());
        panelArgsLeft.add(Box.createVerticalStrut(5));

        JPanel panelSettings = new JPanel();
        panelSettings.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelArgsLeft.add(panelSettings);
        GridBagLayout gbl_panelSettings = new GridBagLayout();
        gbl_panelSettings.columnWidths = new int[] { 0, 0, 0 };
        gbl_panelSettings.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_panelSettings.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        gbl_panelSettings.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0 };
        panelSettings.setLayout(gbl_panelSettings);

        JLabel lblEpochWidth = new JLabel("Actigraph epochs length (sec):");
        GridBagConstraints gbc_lblEpochWidth = new GridBagConstraints();
        gbc_lblEpochWidth.anchor = GridBagConstraints.WEST;
        gbc_lblEpochWidth.insets = new Insets(0, 0, 5, 5);
        gbc_lblEpochWidth.gridx = 0;
        gbc_lblEpochWidth.gridy = 0;
        panelSettings.add(lblEpochWidth, gbc_lblEpochWidth);

        tfEpochWidth = new JTextField();
        tfEpochWidth.setText("1");
        tfEpochWidth.setColumns(1);
        GridBagConstraints gbc_tfEpochWidth = new GridBagConstraints();
        gbc_tfEpochWidth.anchor = GridBagConstraints.WEST;
        gbc_tfEpochWidth.insets = new Insets(0, 0, 5, 0);
        gbc_tfEpochWidth.gridx = 1;
        gbc_tfEpochWidth.gridy = 0;
        panelSettings.add(tfEpochWidth, gbc_tfEpochWidth);

        JLabel lblActigraphEpochFile = new JLabel("Actigraph file format:");
        GridBagConstraints gbc_lblActigraphEpochFile = new GridBagConstraints();
        gbc_lblActigraphEpochFile.anchor = GridBagConstraints.WEST;
        gbc_lblActigraphEpochFile.insets = new Insets(0, 0, 5, 5);
        gbc_lblActigraphEpochFile.gridx = 0;
        gbc_lblActigraphEpochFile.gridy = 1;
        panelSettings.add(lblActigraphEpochFile, gbc_lblActigraphEpochFile);
        lblActigraphEpochFile.setHorizontalAlignment(SwingConstants.TRAILING);

        cBoxActigraphFileFormat = new JComboBox<>();
        cBoxActigraphFileFormat.setModel(new DefaultComboBoxModel<>(ActigraphFileFormat.values()));
        GridBagConstraints gbc_cBoxActigraphFileFormat = new GridBagConstraints();
        gbc_cBoxActigraphFileFormat.fill = GridBagConstraints.HORIZONTAL;
        gbc_cBoxActigraphFileFormat.insets = new Insets(0, 0, 5, 0);
        gbc_cBoxActigraphFileFormat.gridx = 1;
        gbc_cBoxActigraphFileFormat.gridy = 1;
        panelSettings.add(cBoxActigraphFileFormat, gbc_cBoxActigraphFileFormat);
        
                JLabel lblPhonesType = new JLabel("Phone's type:");
                GridBagConstraints gbc_lblPhonesType = new GridBagConstraints();
                gbc_lblPhonesType.anchor = GridBagConstraints.WEST;
                gbc_lblPhonesType.insets = new Insets(0, 0, 5, 5);
                gbc_lblPhonesType.gridx = 0;
                gbc_lblPhonesType.gridy = 2;
                panelSettings.add(lblPhonesType, gbc_lblPhonesType);
        
                cbGyro = new JComboBox<>();
                cbGyro.setModel(new DefaultComboBoxModel<>(PhoneType.values()));
                cbGyro.setSelectedIndex(0);
                GridBagConstraints gbc_cbGyro = new GridBagConstraints();
                gbc_cbGyro.insets = new Insets(0, 0, 5, 0);
                gbc_cbGyro.fill = GridBagConstraints.HORIZONTAL;
                gbc_cbGyro.gridx = 1;
                gbc_cbGyro.gridy = 2;
                panelSettings.add(cbGyro, gbc_cbGyro);

        JLabel lblLocation = new JLabel("Phone's location:");
        GridBagConstraints gbc_lblLocation = new GridBagConstraints();
        gbc_lblLocation.anchor = GridBagConstraints.WEST;
        gbc_lblLocation.insets = new Insets(0, 0, 5, 5);
        gbc_lblLocation.gridx = 0;
        gbc_lblLocation.gridy = 3;
        panelSettings.add(lblLocation, gbc_lblLocation);

        profileComboBox = new JComboBox<>();
        profileComboBox.setModel(new DefaultComboBoxModel<>(Profile.values()));
        profileComboBox.setSelectedIndex(0);
        GridBagConstraints gbc_profileComboBox = new GridBagConstraints();
        gbc_profileComboBox.insets = new Insets(0, 0, 5, 0);
        gbc_profileComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_profileComboBox.gridx = 1;
        gbc_profileComboBox.gridy = 3;
        panelSettings.add(profileComboBox, gbc_profileComboBox);
        
        
        cbLocation = new JComboBox<>();
        cbLocation.setModel(new DefaultComboBoxModel<>(PhoneLocation.values()));
        GridBagConstraints gbc_cbLocation = new GridBagConstraints();
        gbc_cbLocation.insets = new Insets(0, 0, 5, 0);
        gbc_cbLocation.fill = GridBagConstraints.HORIZONTAL;
        gbc_cbLocation.gridx = 1;
        gbc_cbLocation.gridy = 4;
        panelSettings.add(cbLocation, gbc_cbLocation);
        
        panel.add(Box.createHorizontalStrut(5));
        JSeparator separator = new JSeparator();
        separator.setAlignmentY(Component.TOP_ALIGNMENT);
        separator.setOrientation(SwingConstants.VERTICAL);
        panel.add(separator);
        panel.add(Box.createHorizontalStrut(5));

        JPanel panelSpikes = new JPanel();
        panelSpikes.setAlignmentY(Component.TOP_ALIGNMENT);
        panel.add(panelSpikes);
        GridBagLayout gbl_panelSpikes = new GridBagLayout();
        gbl_panelSpikes.columnWidths = new int[] { 0, 0, 0 };
        gbl_panelSpikes.rowHeights = new int[] { 0, 0 };
        gbl_panelSpikes.columnWeights = new double[] { 0.0, 1.0, 1.0 };
        gbl_panelSpikes.rowWeights = new double[] { 0.0, 0.0 };
        panelSpikes.setLayout(gbl_panelSpikes);

        JLabel lblSpikesTitle = new JLabel("Spikes (" + PvAParams.TIMESTAMP_FORMAT + ")");
        lblSpikesTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.anchor = GridBagConstraints.CENTER;
        gbc_lblTitle.gridwidth = 2;
        gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
        gbc_lblTitle.gridx = 1;
        gbc_lblTitle.gridy = 0;
        panelSpikes.add(lblSpikesTitle, gbc_lblTitle);

        JLabel lblPhone = new JLabel("Phone time");
        GridBagConstraints gbc_lblPhone = new GridBagConstraints();
        gbc_lblPhone.anchor = GridBagConstraints.CENTER;
        gbc_lblPhone.insets = new Insets(0, 0, 5, 5);
        gbc_lblPhone.gridx = 1;
        gbc_lblPhone.gridy = 1;
        panelSpikes.add(lblPhone, gbc_lblPhone);

        JLabel lblActigraph = new JLabel("Actigraph time");
        GridBagConstraints gbc_lblActigraph = new GridBagConstraints();
        gbc_lblActigraph.anchor = GridBagConstraints.CENTER;
        gbc_lblActigraph.insets = new Insets(0, 0, 5, 0);
        gbc_lblActigraph.gridx = 2;
        gbc_lblActigraph.gridy = 1;
        panelSpikes.add(lblActigraph, gbc_lblActigraph);

        tfSpikeLabel = new JLabel[NB_MAX_SPIKES];
        tfSpikePhone = new JTextField[NB_MAX_SPIKES];
        tfSpikeActig = new JTextField[NB_MAX_SPIKES];

        for (int i = 0; i < NB_MAX_SPIKES; i++) {
            tfSpikeLabel[i] = new JLabel(Integer.toString(i + 1));
            GridBagConstraints gbc_label = new GridBagConstraints();
            gbc_label.anchor = GridBagConstraints.EAST;
            gbc_label.insets = new Insets(0, 0, 5, 5);
            gbc_label.gridx = 0;
            gbc_label.gridy = i + 3;
            panelSpikes.add(tfSpikeLabel[i], gbc_label);

            tfSpikePhone[i] = new JTextField();
            tfSpikePhone[i].setColumns(DATE_TEXTFIELD_WIDTH);
            tfSpikePhone[i].setHorizontalAlignment(SwingConstants.CENTER);
            GridBagConstraints gbc_tfSpikePhone = new GridBagConstraints();
            gbc_tfSpikePhone.anchor = GridBagConstraints.CENTER;
            gbc_tfSpikePhone.insets = new Insets(0, 0, 5, 5);
            gbc_tfSpikePhone.fill = GridBagConstraints.HORIZONTAL;
            gbc_tfSpikePhone.gridx = 1;
            gbc_tfSpikePhone.gridy = i + 3;
            panelSpikes.add(tfSpikePhone[i], gbc_tfSpikePhone);

            tfSpikeActig[i] = new JTextField();
            tfSpikeActig[i].setColumns(DATE_TEXTFIELD_WIDTH);
            tfSpikeActig[i].setHorizontalAlignment(SwingConstants.CENTER);
            GridBagConstraints gbc_tfSpikeActig = new GridBagConstraints();
            gbc_tfSpikeActig.anchor = GridBagConstraints.CENTER;
            gbc_tfSpikeActig.insets = new Insets(0, 0, 5, 0);
            gbc_tfSpikeActig.fill = GridBagConstraints.HORIZONTAL;
            gbc_tfSpikeActig.gridx = 2;
            gbc_tfSpikeActig.gridy = i + 3;
            panelSpikes.add(tfSpikeActig[i], gbc_tfSpikeActig);
        }

        Component verticalStrut = Box.createVerticalStrut(5);
        add(verticalStrut);

        JSeparator separator_1 = new JSeparator();
        add(separator_1);

        Component verticalStrut_1 = Box.createVerticalStrut(5);
        add(verticalStrut_1);

        SaveLoadPanel psp = new SaveLoadPanel("Save params...", "Load params...") {
            @Override
            public void saveToFile(String paramFilePath) {
                try {
                    PvAParams params = new PvAParams();
                    getParameters(params);
                    params.saveToXml(paramFilePath);
                    System.out.println("Parameters saved to '" + paramFilePath + "'.");
                } catch (Exception e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                }
            }

            @Override
            public void loadFromFile(String paramFilePath) {
                try {
                    PvAParams params = new PvAParams(paramFilePath);
                    setParameters(params);
                    System.out.println("Parameters loaded from '" + paramFilePath + "'.");
                } catch (Exception e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
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
     * @throws MissingParameterException
     *             If a parameter is required and missing.
     */
    private void setParameters(PvAParams params) throws MissingParameterException {
        FilePicker[] inputs = filePickers.getInputFilePickers();
        inputs[PhoneVSActigraph.INPUT_PHONE].setSelectedFilePath(params.getString(PvAParams.INPUT_PHONE_FILE));
        inputs[PhoneVSActigraph.INPUT_ACTIGRAPH].setSelectedFilePath(params.getString(PvAParams.INPUT_ACTIG_EPOCH_FILE));
        inputs[PhoneVSActigraph.INPUT_PARTICIPANT].setSelectedFilePath(params.getString(PvAParams.INPUT_PARTICIPANT_FILE));
        FilePicker[] outputs = filePickers.getOutputFilePickers();
        outputs[PhoneVSActigraph.OUTPUT_TRAINING_SET].setSelectedFilePath(params.getString(PvAParams.OUTPUT_TRAINING_SET_FILE));
        outputs[PhoneVSActigraph.OUTPUT_VALIDATION].setSelectedFilePath(params.getString(PvAParams.OUTPUT_VALIDATION_FILE));
        tfStartTime.setText(params.getSerialized(PvAParams.START_TIME));
        tfStopTime.setText(params.getSerialized(PvAParams.STOP_TIME));
        tfEpochWidth.setText(params.getSerialized(PvAParams.EPOCH_WIDTH_SEC));
        cBoxActigraphFileFormat.setSelectedItem(params.get(PvAParams.ACTIG_FILE_FORMAT));
        String[] phoneSpikes = params.getSerializedArray(PvAParams.PHONE_SPIKES_LIST);
        for (int i = 0; i < phoneSpikes.length; i++) {
            tfSpikePhone[i].setText(phoneSpikes[i]);
        }
        String[] actigraphSpikes = params.getSerializedArray(PvAParams.ACTIG_SPIKES_LIST);
        for (int i = 0; i < actigraphSpikes.length; i++) {
            tfSpikeActig[i].setText(actigraphSpikes[i]);
        }
    }

    /**
     * Fills the specified {@link PvAParams} object with the current fields of this
     * panel (including the files paths).
     * 
     * @param params
     *            The params object to populate.
     * @throws ParseException
     *             If one field could not be properly parsed.
     */
    public void getParameters(PvAParams params) throws ParseException {
        setFileParam(params, PvAParams.INPUT_PHONE_FILE, filePickers.getInputFilePaths()[PhoneVSActigraph.INPUT_PHONE]);
        setFileParam(params, PvAParams.INPUT_PARTICIPANT_FILE, filePickers.getInputFilePaths()[PhoneVSActigraph.INPUT_PARTICIPANT]);
        setFileParam(params, PvAParams.INPUT_ACTIG_EPOCH_FILE, filePickers.getInputFilePaths()[PhoneVSActigraph.INPUT_ACTIGRAPH]);
        setFileParam(params, PvAParams.OUTPUT_VALIDATION_FILE, filePickers.getOutputFilePaths()[PhoneVSActigraph.OUTPUT_VALIDATION]);
        setFileParam(params, PvAParams.OUTPUT_TRAINING_SET_FILE, filePickers.getOutputFilePaths()[PhoneVSActigraph.OUTPUT_TRAINING_SET]);
        setIfNotEmpty(params, PvAParams.START_TIME, tfStartTime.getText());
        setIfNotEmpty(params, PvAParams.STOP_TIME, tfStopTime.getText());
        params.set(PvAParams.ACTIG_FILE_FORMAT, cBoxActigraphFileFormat.getSelectedItem());
        setIfNotEmpty(params, PvAParams.EPOCH_WIDTH_SEC, tfEpochWidth.getText());
        params.set(PvAParams.PROFILE, profileComboBox.getSelectedItem());
        params.set(PvAParams.PHONE_TYPE, cbGyro.getSelectedItem());
        params.set(PvAParams.PHONE_LOCATION, cbLocation.getSelectedItem());
        String[] phoneSpikes = new String[NB_MAX_SPIKES];
        String[] actigraphSpikes = new String[NB_MAX_SPIKES];
        int nbSpikes = 0;
        for (int i = 0; i < NB_MAX_SPIKES; i++) {
            phoneSpikes[nbSpikes] = tfSpikePhone[i].getText();
            actigraphSpikes[nbSpikes] = tfSpikeActig[i].getText();
            if (!phoneSpikes[nbSpikes].equals("") && !actigraphSpikes[nbSpikes].equals("")) {
                nbSpikes++;
            }
        }
        if (nbSpikes > 0) {
            params.deserializeAndSet(PvAParams.PHONE_SPIKES_LIST, Arrays.copyOfRange(phoneSpikes,
                    0, nbSpikes));
            params.deserializeAndSet(PvAParams.ACTIG_SPIKES_LIST, Arrays.copyOfRange(
                    actigraphSpikes, 0, nbSpikes));
        } else {
            params.deserializeAndSet(PvAParams.PHONE_SPIKES_LIST, new String[0]);
            params.deserializeAndSet(PvAParams.ACTIG_SPIKES_LIST, new String[0]);
        }
        params.populatePublicFields();
    }

    private static void setIfNotEmpty(PvAParams params, String key, String value)
            throws ParseException {
        if (!"".equals(value)) {
            params.deserializeAndSet(key, value);
        }
    }

    private static void setFileParam(PvAParams params, String key, String filePath) {
        if (filePath != null && !filePath.equals("")) {
            params.set(key, filePath);
        }
    }
}
