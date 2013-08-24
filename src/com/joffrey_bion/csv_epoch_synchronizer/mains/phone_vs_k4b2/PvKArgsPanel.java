package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.JComboBox;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.config.Profile;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneLocation;
import com.joffrey_bion.csv_epoch_synchronizer.phone.PhoneType;
import com.joffrey_bion.generic_guis.file_picker.FilePicker;
import com.joffrey_bion.generic_guis.file_picker.JFilePickersPanel;
import com.joffrey_bion.generic_guis.parameters.SaveLoadPanel;

import java.text.ParseException;
import java.util.Arrays;
import javax.swing.JSeparator;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.BorderLayout;

@SuppressWarnings("serial")
class PvKArgsPanel extends JPanel {

    private static final int NB_MAX_SPIKES = 6;
    private static final int DATE_TEXTFIELD_WIDTH = 17;
    private static final int TIME_TEXTFIELD_WIDTH = 10;
    private static final Integer[] NB_SYNC_MARKERS_LIST = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    private final JFilePickersPanel filePickers;
    private JComboBox<Integer> nbSyncMarkersBox;
    private JLabel[] tfSpikeLabel;
    private JTextField[] tfSpikePhone;
    private JTextField[] tfSpikeK4b2;
    private JComboBox<PhoneType> cbGyro;
    private JComboBox<Profile> profileComboBox;
    private JComboBox<PhoneLocation> cbLocation;

    /**
     * Create the panel.
     * 
     * @param filePickers
     */
    public PvKArgsPanel(JFilePickersPanel filePickers) {
        this.filePickers = filePickers;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel panelLeft = new JPanel();
        panelLeft.setAlignmentY(Component.TOP_ALIGNMENT);
        panelLeft.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panelLeft);
        panelLeft.setLayout(new BorderLayout(0, 0));

        JPanel panelSettings = new JPanel();
        panelLeft.add(panelSettings, BorderLayout.CENTER);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[] { 0, 0, 0 };
        gbl_panel_2.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, 1.0 };
        gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0 };
        panelSettings.setLayout(gbl_panel_2);

        JLabel lblNbSyncMarkers = new JLabel("Number of sync markers to skip:");
        GridBagConstraints gbc_lblNbSyncMarkers = new GridBagConstraints();
        gbc_lblNbSyncMarkers.anchor = GridBagConstraints.WEST;
        gbc_lblNbSyncMarkers.insets = new Insets(0, 0, 5, 5);
        gbc_lblNbSyncMarkers.gridx = 0;
        gbc_lblNbSyncMarkers.gridy = 0;
        panelSettings.add(lblNbSyncMarkers, gbc_lblNbSyncMarkers);

        nbSyncMarkersBox = new JComboBox<>();
        nbSyncMarkersBox.setModel(new DefaultComboBoxModel<>(NB_SYNC_MARKERS_LIST));
        nbSyncMarkersBox.setSelectedIndex(0);
        GridBagConstraints gbc_nbSyncMarkersBox = new GridBagConstraints();
        gbc_nbSyncMarkersBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_nbSyncMarkersBox.insets = new Insets(0, 0, 5, 5);
        gbc_nbSyncMarkersBox.gridx = 1;
        gbc_nbSyncMarkersBox.gridy = 0;
        panelSettings.add(nbSyncMarkersBox, gbc_nbSyncMarkersBox);

        JLabel lblPhonesType = new JLabel("Phone's type:");
        GridBagConstraints gbc_lblPhonesType = new GridBagConstraints();
        gbc_lblPhonesType.anchor = GridBagConstraints.WEST;
        gbc_lblPhonesType.insets = new Insets(0, 0, 5, 5);
        gbc_lblPhonesType.gridx = 0;
        gbc_lblPhonesType.gridy = 1;
        panelSettings.add(lblPhonesType, gbc_lblPhonesType);

        cbGyro = new JComboBox<>();
        cbGyro.setModel(new DefaultComboBoxModel<>(PhoneType.values()));
        cbGyro.setSelectedIndex(0);
        GridBagConstraints gbc_cbGyro = new GridBagConstraints();
        gbc_cbGyro.insets = new Insets(0, 0, 5, 5);
        gbc_cbGyro.fill = GridBagConstraints.HORIZONTAL;
        gbc_cbGyro.gridx = 1;
        gbc_cbGyro.gridy = 1;
        panelSettings.add(cbGyro, gbc_cbGyro);
        cbGyro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClassifierPath();
            }
        });

        JLabel lblLocation = new JLabel("Phone's location:");
        GridBagConstraints gbc_lblLocation = new GridBagConstraints();
        gbc_lblLocation.anchor = GridBagConstraints.WEST;
        gbc_lblLocation.insets = new Insets(0, 0, 5, 5);
        gbc_lblLocation.gridx = 0;
        gbc_lblLocation.gridy = 2;
        panelSettings.add(lblLocation, gbc_lblLocation);

        profileComboBox = new JComboBox<>();
        profileComboBox.setModel(new DefaultComboBoxModel<>(Profile.values()));
        profileComboBox.setSelectedIndex(0);
        GridBagConstraints gbc_profileComboBox = new GridBagConstraints();
        gbc_profileComboBox.insets = new Insets(0, 0, 5, 5);
        gbc_profileComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_profileComboBox.gridx = 1;
        gbc_profileComboBox.gridy = 2;
        panelSettings.add(profileComboBox, gbc_profileComboBox);
        profileComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClassifierPath();
            }
        });

        cbLocation = new JComboBox<>();
        cbLocation.setModel(new DefaultComboBoxModel<>(PhoneLocation.values()));
        cbLocation.setSelectedIndex(0);
        GridBagConstraints gbc_cbLocation = new GridBagConstraints();
        gbc_cbLocation.insets = new Insets(0, 0, 5, 5);
        gbc_cbLocation.fill = GridBagConstraints.HORIZONTAL;
        gbc_cbLocation.gridx = 1;
        gbc_cbLocation.gridy = 3;
        panelSettings.add(cbLocation, gbc_cbLocation);

        Component verticalStrut_1 = Box.createVerticalStrut(5);
        add(verticalStrut_1);

        SaveLoadPanel psp = new SaveLoadPanel("Save params...", "Load params...") {
            @Override
            public void saveToFile(String paramFilePath) {
                try {
                    PvKParams params = new PvKParams();
                    getParameters(params);
                    params.saveToXml(paramFilePath);
                    System.out.println("Parameters saved to '" + paramFilePath + "'.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }

            @Override
            public void loadFromFile(String paramFilePath) {
                try {
                    PvKParams params = new PvKParams(paramFilePath);
                    setParameters(params);
                    System.out.println("Parameters loaded from '" + paramFilePath + "'.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        psp.addFileTypeFilter(".xml", "XML Parameter File");
        panelLeft.add(psp, BorderLayout.SOUTH);

        add(Box.createHorizontalStrut(5));
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        add(separator);
        add(Box.createHorizontalStrut(5));

        JPanel panelSpikes = new JPanel();
        panelSpikes.setAlignmentY(Component.TOP_ALIGNMENT);
        add(panelSpikes);
        GridBagLayout gbl_panelSpikes = new GridBagLayout();
        gbl_panelSpikes.columnWidths = new int[] { 0, 0, 0 };
        gbl_panelSpikes.rowHeights = new int[] { 0, 0 };
        gbl_panelSpikes.columnWeights = new double[] { 0.0, 1.0, 1.0 };
        gbl_panelSpikes.rowWeights = new double[] { 0.0, 0.0 };
        panelSpikes.setLayout(gbl_panelSpikes);

        JLabel lblSpikesTitle = new JLabel("Acceleration Spikes Times");
        lblSpikesTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
        GridBagConstraints gbc_lblTitle = new GridBagConstraints();
        gbc_lblTitle.anchor = GridBagConstraints.CENTER;
        gbc_lblTitle.gridwidth = 2;
        gbc_lblTitle.insets = new Insets(0, 0, 5, 5);
        gbc_lblTitle.gridx = 1;
        gbc_lblTitle.gridy = 0;
        panelSpikes.add(lblSpikesTitle, gbc_lblTitle);

        JLabel lblPhoneSpikes = new JLabel("Phone (" + PvKParams.PHONE_FORMAT + ")");
        GridBagConstraints gbc_lblPhone = new GridBagConstraints();
        gbc_lblPhone.insets = new Insets(0, 0, 5, 5);
        gbc_lblPhone.gridx = 1;
        gbc_lblPhone.gridy = 1;
        panelSpikes.add(lblPhoneSpikes, gbc_lblPhone);

        JLabel lblK4b2Spikes = new JLabel("K4b2 (" + PvKParams.K4B2_FORMAT + ")");
        GridBagConstraints gbc_lblK4b2 = new GridBagConstraints();
        gbc_lblK4b2.insets = new Insets(0, 0, 5, 0);
        gbc_lblK4b2.gridx = 2;
        gbc_lblK4b2.gridy = 1;
        panelSpikes.add(lblK4b2Spikes, gbc_lblK4b2);

        tfSpikeLabel = new JLabel[NB_MAX_SPIKES];
        tfSpikePhone = new JTextField[NB_MAX_SPIKES];
        tfSpikeK4b2 = new JTextField[NB_MAX_SPIKES];

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
            tfSpikePhone[i].setToolTipText(PvKParams.PHONE_FORMAT);
            tfSpikePhone[i].setHorizontalAlignment(SwingConstants.CENTER);
            GridBagConstraints gbc_tfSpikePhone = new GridBagConstraints();
            gbc_tfSpikePhone.insets = new Insets(0, 0, 5, 5);
            gbc_tfSpikePhone.fill = GridBagConstraints.HORIZONTAL;
            gbc_tfSpikePhone.gridx = 1;
            gbc_tfSpikePhone.gridy = i + 3;
            panelSpikes.add(tfSpikePhone[i], gbc_tfSpikePhone);

            tfSpikeK4b2[i] = new JTextField();
            tfSpikeK4b2[i].setColumns(TIME_TEXTFIELD_WIDTH);
            tfSpikeK4b2[i].setToolTipText(PvKParams.K4B2_FORMAT);
            tfSpikeK4b2[i].setHorizontalAlignment(SwingConstants.CENTER);
            GridBagConstraints gbc_tfSpikeK4b2 = new GridBagConstraints();
            gbc_tfSpikeK4b2.insets = new Insets(0, 0, 5, 0);
            gbc_tfSpikeK4b2.fill = GridBagConstraints.HORIZONTAL;
            gbc_tfSpikeK4b2.gridx = 2;
            gbc_tfSpikeK4b2.gridy = i + 3;
            panelSpikes.add(tfSpikeK4b2[i], gbc_tfSpikeK4b2);
        }

        filePickers.setInputFilePickerEditable(PhoneVSK4b2Analyzer.INPUT_XML_TREE, false);
        updateClassifierPath();
    }

    /**
     * Populates the fields of this panel (including files paths) with the given
     * parameters.
     * 
     * @param params
     *            The parameters to use to populate this panel.
     */
    private void setParameters(PvKParams params) {
        FilePicker[] inputs = filePickers.getInputFilePickers();
        inputs[PhoneVSK4b2Analyzer.INPUT_PHONE].setSelectedFilePath(params.getString(PvKParams.PHONE_FILE_PATH));
        inputs[PhoneVSK4b2Analyzer.INPUT_K4B2].setSelectedFilePath(params.getString(PvKParams.K4B2_FILE_PATH));
        inputs[PhoneVSK4b2Analyzer.INPUT_XML_TREE].setSelectedFilePath(params.classifierFile);
        nbSyncMarkersBox.setSelectedItem(params.get(PvKParams.NB_SYNC_MARKERS));
        profileComboBox.setSelectedItem(params.get(PvKParams.PROFILE));
        params.set(PvKParams.PROFILE, profileComboBox.getSelectedItem());
        String[] phoneSpikes = params.getSerializedArray(PvKParams.PHONE_SPIKES_LIST);
        for (int i = 0; i < phoneSpikes.length; i++) {
            tfSpikePhone[i].setText(phoneSpikes[i]);
        }
        String[] actigraphSpikes = params.getSerializedArray(PvKParams.K4B2_SPIKES_LIST);
        for (int i = 0; i < actigraphSpikes.length; i++) {
            tfSpikeK4b2[i].setText(actigraphSpikes[i]);
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
    public void getParameters(PvKParams params) throws ParseException {
        setFileParam(params, PvKParams.PHONE_FILE_PATH, filePickers.getInputFilePaths()[PhoneVSK4b2Analyzer.INPUT_PHONE]);
        setFileParam(params, PvKParams.K4B2_FILE_PATH, filePickers.getInputFilePaths()[PhoneVSK4b2Analyzer.INPUT_K4B2]);
        params.set(PvKParams.NB_SYNC_MARKERS, nbSyncMarkersBox.getSelectedItem());
        params.set(PvKParams.PROFILE, profileComboBox.getSelectedItem());
        params.set(PvKParams.PHONE_TYPE, cbGyro.getSelectedItem());
        params.set(PvKParams.PHONE_LOCATION, cbLocation.getSelectedItem());
        String[] phoneSpikes = new String[NB_MAX_SPIKES];
        String[] actigraphSpikes = new String[NB_MAX_SPIKES];
        int nbSpikes = 0;
        for (int i = 0; i < NB_MAX_SPIKES; i++) {
            phoneSpikes[nbSpikes] = tfSpikePhone[i].getText();
            actigraphSpikes[nbSpikes] = tfSpikeK4b2[i].getText();
            if (!phoneSpikes[nbSpikes].equals("") && !actigraphSpikes[nbSpikes].equals("")) {
                nbSpikes++;
            }
        }
        if (nbSpikes > 0) {
            params.deserializeAndSet(PvKParams.PHONE_SPIKES_LIST, Arrays.copyOfRange(phoneSpikes,
                    0, nbSpikes));
            params.deserializeAndSet(PvKParams.K4B2_SPIKES_LIST, Arrays.copyOfRange(
                    actigraphSpikes, 0, nbSpikes));
        } else {
            params.deserializeAndSet(PvKParams.PHONE_SPIKES_LIST, new String[0]);
            params.deserializeAndSet(PvKParams.K4B2_SPIKES_LIST, new String[0]);
        }
        params.populatePublicFields();
    }

    private void updateClassifierPath() {
        FilePicker[] inputs = PvKArgsPanel.this.filePickers.getInputFilePickers();
        inputs[PhoneVSK4b2Analyzer.INPUT_XML_TREE].setSelectedFilePath(Config.get().getClassifier(
                (Profile) profileComboBox.getSelectedItem(), (PhoneType) cbGyro.getSelectedItem()));
    }

    private static void setFileParam(PvKParams params, String key, String filePath) {
        if (filePath != null && !filePath.equals("")) {
            params.set(key, filePath);
        }
    }
}
