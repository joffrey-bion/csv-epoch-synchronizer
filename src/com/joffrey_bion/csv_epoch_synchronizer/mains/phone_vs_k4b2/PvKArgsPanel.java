package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.Box;
import javax.swing.JComboBox;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.csv_epoch_synchronizer.config.Profile;
import com.joffrey_bion.file_processor_window.file_picker.FilePicker;
import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;
import com.joffrey_bion.file_processor_window.parameters.ParamsSaverPanel;

import java.text.ParseException;
import java.util.Arrays;
import javax.swing.JSeparator;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
class PvKArgsPanel extends JPanel {

    private static final int NB_MAX_SPIKES = 6;
    private static final int DATE_TEXTFIELD_WIDTH = 17;
    private static final int TIME_TEXTFIELD_WIDTH = 10;
    private static final Integer[] NB_SYNC_MARKERS_LIST = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

    private final JFilePickersPanel filePickers;
    private JCheckBox chckbxOutput;
    private JComboBox<Integer> nbSyncMarkersBox;
    private JComboBox<Profile> profileComboBox;
    private JLabel[] tfSpikeLabel;
    private JTextField[] tfSpikePhone;
    private JTextField[] tfSpikeK4b2;

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
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));

        JPanel panel_2 = new JPanel();
        panelLeft.add(panel_2);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[] { 0, 0, 0 };
        gbl_panel_2.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_panel_2.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel_2.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        panel_2.setLayout(gbl_panel_2);

        chckbxOutput = new JCheckBox("Write the output to a file");
        GridBagConstraints gbc_chckbxOutput = new GridBagConstraints();
        gbc_chckbxOutput.anchor = GridBagConstraints.WEST;
        gbc_chckbxOutput.gridwidth = 2;
        gbc_chckbxOutput.insets = new Insets(0, 0, 5, 0);
        gbc_chckbxOutput.gridx = 0;
        gbc_chckbxOutput.gridy = 0;
        panel_2.add(chckbxOutput, gbc_chckbxOutput);
        chckbxOutput.setSelected(false);
        chckbxOutput.setHorizontalAlignment(SwingConstants.TRAILING);
        chckbxOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PvKArgsPanel.this.filePickers.setOutputFilePickerEnabled(0, chckbxOutput
                        .isSelected());
            }
        });
        filePickers.setOutputFilePickerEnabled(0, chckbxOutput.isSelected());

        JLabel lblNbSyncMarkers = new JLabel("Number of sync markers to skip:");
        GridBagConstraints gbc_lblNbSyncMarkers = new GridBagConstraints();
        gbc_lblNbSyncMarkers.anchor = GridBagConstraints.WEST;
        gbc_lblNbSyncMarkers.insets = new Insets(0, 0, 5, 5);
        gbc_lblNbSyncMarkers.gridx = 0;
        gbc_lblNbSyncMarkers.gridy = 1;
        panel_2.add(lblNbSyncMarkers, gbc_lblNbSyncMarkers);

        nbSyncMarkersBox = new JComboBox<>();
        GridBagConstraints gbc_nbSyncMarkersBox = new GridBagConstraints();
        gbc_nbSyncMarkersBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_nbSyncMarkersBox.insets = new Insets(0, 0, 5, 0);
        gbc_nbSyncMarkersBox.gridx = 1;
        gbc_nbSyncMarkersBox.gridy = 1;
        panel_2.add(nbSyncMarkersBox, gbc_nbSyncMarkersBox);
        nbSyncMarkersBox.setModel(new DefaultComboBoxModel<>(NB_SYNC_MARKERS_LIST));

        JLabel lblProfile = new JLabel("Phones' location:");
        GridBagConstraints gbc_lblProfile = new GridBagConstraints();
        gbc_lblProfile.anchor = GridBagConstraints.WEST;
        gbc_lblProfile.insets = new Insets(0, 0, 0, 5);
        gbc_lblProfile.gridx = 0;
        gbc_lblProfile.gridy = 2;
        panel_2.add(lblProfile, gbc_lblProfile);

        profileComboBox = new JComboBox<>();
        GridBagConstraints gbc_profileComboBox = new GridBagConstraints();
        gbc_profileComboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_profileComboBox.gridx = 1;
        gbc_profileComboBox.gridy = 2;
        panel_2.add(profileComboBox, gbc_profileComboBox);
        profileComboBox.setModel(new DefaultComboBoxModel<>(Profile.values()));
        profileComboBox.setSelectedIndex(0);
        profileComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FilePicker[] inputs = PvKArgsPanel.this.filePickers.getInputFilePickers();
                inputs[2].setSelectedFilePath(Config.get().getClassifier(
                        (Profile) profileComboBox.getSelectedItem()));
            }
        });
        filePickers.getInputFilePickers()[2].setSelectedFilePath(Config.get().getClassifier(
                (Profile) profileComboBox.getSelectedItem()));

        Component verticalStrut_1 = Box.createVerticalStrut(5);
        add(verticalStrut_1);

        ParamsSaverPanel psp = new ParamsSaverPanel() {
            @Override
            public void saveParamsToFile(String paramFilePath) {
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
            public void loadParamsFromFile(String paramFilePath) {
                try {
                    PvKParams params = new PvKParams(paramFilePath);
                    setParameters(params);
                    System.out.println("Parameters loaded from '" + paramFilePath + "'.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        panelLeft.add(psp);

        add(Box.createHorizontalStrut(5));
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        add(separator);
        add(Box.createHorizontalStrut(5));

        JPanel panelSpikes = new JPanel();
        panelSpikes.setAlignmentY(Component.TOP_ALIGNMENT);
        panelSpikes.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(panelSpikes);
        panelSpikes.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.PREF_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC, FormFactory.PREF_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC, FormFactory.PREF_COLSPEC, }, new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("14px"), FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC, }));

        JLabel lblSpikes = new JLabel("Acceleration Spikes Times");
        lblSpikes.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelSpikes.add(lblSpikes, "3, 1, 3, 1, center, center");

        JLabel lblPhoneSpikes = new JLabel("Phone (" + PvKParams.PHONE_FORMAT + ")");
        panelSpikes.add(lblPhoneSpikes, "3, 3, center, default");

        JLabel lblK4b2Spikes = new JLabel("K4b2 (" + PvKParams.K4B2_FORMAT + ")");
        panelSpikes.add(lblK4b2Spikes, "5, 3, center, default");

        tfSpikeLabel = new JLabel[NB_MAX_SPIKES];
        tfSpikePhone = new JTextField[NB_MAX_SPIKES];
        tfSpikeK4b2 = new JTextField[NB_MAX_SPIKES];
        for (int i = 0; i < NB_MAX_SPIKES; i++) {
            tfSpikeLabel[i] = new JLabel(Integer.toString(i + 1));
            panelSpikes.add(tfSpikeLabel[i], "1, " + (2 * i + 5) + ", right, default");

            tfSpikePhone[i] = new JTextField();
            tfSpikePhone[i].setColumns(DATE_TEXTFIELD_WIDTH);
            tfSpikePhone[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelSpikes.add(tfSpikePhone[i], "3, " + (2 * i + 5) + ", fill, default");

            tfSpikeK4b2[i] = new JTextField();
            tfSpikeK4b2[i].setColumns(TIME_TEXTFIELD_WIDTH);
            tfSpikeK4b2[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelSpikes.add(tfSpikeK4b2[i], "5, " + (2 * i + 5) + ", fill, default");
        }

        filePickers.setOutputFilePickerEnabled(0, false);
        filePickers.setInputFilePickerEditable(2, false);

    }

    public Integer getNbSyncMarkers() {
        return (Integer) nbSyncMarkersBox.getSelectedItem();
    }

    public boolean shouldWriteOutput() {
        return chckbxOutput.isSelected();
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
        inputs[0].setSelectedFilePath(params.getString(PvKParams.PHONE_FILE_PATH));
        inputs[1].setSelectedFilePath(params.getString(PvKParams.K4B2_FILE_PATH));
        inputs[2].setSelectedFilePath(params.classifierFile);
        FilePicker[] outputs = filePickers.getOutputFilePickers();
        outputs[0].setSelectedFilePath(params.getString(PvKParams.OUTPUT_FILE_PATH));
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
        setFileParam(params, PvKParams.PHONE_FILE_PATH, filePickers.getInputFilePaths()[0]);
        setFileParam(params, PvKParams.K4B2_FILE_PATH, filePickers.getInputFilePaths()[1]);
        setFileParam(params, PvKParams.OUTPUT_FILE_PATH, filePickers.getOutputFilePaths()[0]);
        params.set(PvKParams.PROFILE, profileComboBox.getSelectedItem());
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
    }

    private static void setFileParam(PvKParams params, String key, String filePath) {
        if (filePath != null && !filePath.equals("")) {
            params.set(key, filePath);
        }
    }
}
