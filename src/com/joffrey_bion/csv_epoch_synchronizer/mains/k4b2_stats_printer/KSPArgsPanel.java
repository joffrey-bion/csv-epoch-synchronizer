package com.joffrey_bion.csv_epoch_synchronizer.mains.k4b2_stats_printer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.joffrey_bion.generic_guis.file_picker.JFilePickersPanel;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
class KSPArgsPanel extends JPanel {

    private static final Integer[] NB_SYNC_MARKERS_LIST = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
    private final JFilePickersPanel filePickers;
    private JCheckBox chckbxOutput;
    private JComboBox<Integer> nbSyncMarkersBox;
    private JCheckBox chckbxRestingPhaseOnly;

    /**
     * Create the panel.
     * 
     * @param filePickers
     */
    public KSPArgsPanel(JFilePickersPanel filePickers) {
        this.filePickers = filePickers;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel panel_1 = new JPanel();
        add(panel_1);
        GridBagLayout gbl_panel_1 = new GridBagLayout();
        gbl_panel_1.columnWidths = new int[] { 0, 0, 0 };
        gbl_panel_1.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_panel_1.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
        gbl_panel_1.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        panel_1.setLayout(gbl_panel_1);

        chckbxOutput = new JCheckBox("Write the output to a file");
        GridBagConstraints gbc_chckbxOutput = new GridBagConstraints();
        gbc_chckbxOutput.anchor = GridBagConstraints.WEST;
        gbc_chckbxOutput.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxOutput.gridx = 0;
        gbc_chckbxOutput.gridy = 0;
        panel_1.add(chckbxOutput, gbc_chckbxOutput);
        chckbxOutput.setSelected(false);
        chckbxOutput.setHorizontalAlignment(SwingConstants.TRAILING);

        chckbxRestingPhaseOnly = new JCheckBox("Resting phase only");
        GridBagConstraints gbc_chckbxRestingPhaseOnly = new GridBagConstraints();
        gbc_chckbxRestingPhaseOnly.anchor = GridBagConstraints.WEST;
        gbc_chckbxRestingPhaseOnly.insets = new Insets(0, 0, 5, 5);
        gbc_chckbxRestingPhaseOnly.gridx = 0;
        gbc_chckbxRestingPhaseOnly.gridy = 1;
        panel_1.add(chckbxRestingPhaseOnly, gbc_chckbxRestingPhaseOnly);

        JLabel lblNbSyncMarkers = new JLabel("Number of sync markers to skip:");
        GridBagConstraints gbc_lblNbSyncMarkers = new GridBagConstraints();
        gbc_lblNbSyncMarkers.anchor = GridBagConstraints.WEST;
        gbc_lblNbSyncMarkers.insets = new Insets(0, 0, 0, 5);
        gbc_lblNbSyncMarkers.gridx = 0;
        gbc_lblNbSyncMarkers.gridy = 2;
        panel_1.add(lblNbSyncMarkers, gbc_lblNbSyncMarkers);

        nbSyncMarkersBox = new JComboBox<>();
        GridBagConstraints gbc_nbSyncMarkersBox = new GridBagConstraints();
        gbc_nbSyncMarkersBox.gridx = 1;
        gbc_nbSyncMarkersBox.gridy = 2;
        panel_1.add(nbSyncMarkersBox, gbc_nbSyncMarkersBox);
        nbSyncMarkersBox.setModel(new DefaultComboBoxModel<>(NB_SYNC_MARKERS_LIST));
        chckbxOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KSPArgsPanel.this.filePickers.setOutputFilePickerEnabled(0, chckbxOutput
                        .isSelected());
            }
        });

        this.filePickers.setOutputFilePickerEnabled(0, false);
    }

    public Integer getNbSyncMarkers() {
        return (Integer) nbSyncMarkersBox.getSelectedItem();
    }

    public boolean shouldWriteOutput() {
        return chckbxOutput.isSelected();
    }

    public boolean isRestingOnly() {
        return chckbxRestingPhaseOnly.isSelected();
    }
}
