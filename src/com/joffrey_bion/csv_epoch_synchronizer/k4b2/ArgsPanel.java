package com.joffrey_bion.csv_epoch_synchronizer.k4b2;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;
import javax.swing.Box;
import javax.swing.JComboBox;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;

@SuppressWarnings("serial")
class ArgsPanel extends JPanel {

    private static final Integer[] NB_SYNC_MARKERS_LIST = { 0, 1, 2, 3, 4, 5, 6, 7 };
    private final JFilePickersPanel filePickers;
    private JCheckBox chckbxOutput;
    private JComboBox<Integer> nbSyncMarkersBox;

    /**
     * Create the panel.
     * 
     * @param filePickers
     */
    public ArgsPanel(JFilePickersPanel filePickers) {
        this.filePickers = filePickers;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        chckbxOutput = new JCheckBox("Write the output to a file");
        panel.add(chckbxOutput);
        chckbxOutput.setSelected(false);
        chckbxOutput.setHorizontalAlignment(SwingConstants.TRAILING);
        chckbxOutput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArgsPanel.this.filePickers.setOutputFilePickerEnabled(0, chckbxOutput.isSelected());
            }
        });

        JPanel panel_3 = new JPanel();
        panel.add(panel_3);
        panel_3.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.X_AXIS));

        JLabel lblNbSyncMarkers = new JLabel("Number of sync markers:");
        panel_3.add(lblNbSyncMarkers);

        Component horizontalStrut = Box.createHorizontalStrut(5);
        panel_3.add(horizontalStrut);

        nbSyncMarkersBox = new JComboBox<>();
        nbSyncMarkersBox.setModel(new DefaultComboBoxModel<>(NB_SYNC_MARKERS_LIST));
        panel_3.add(nbSyncMarkersBox);

        Component horizontalGlue_2 = Box.createHorizontalGlue();
        panel_3.add(horizontalGlue_2);

        Component horizontalGlue_1 = Box.createHorizontalGlue();
        panel_3.add(horizontalGlue_1);

        Component horizontalGlue = Box.createHorizontalGlue();
        panel_3.add(horizontalGlue);

        this.filePickers.setOutputFilePickerEnabled(0, false);
    }

    public Integer getNbSyncMarkers() {
        return (Integer) nbSyncMarkersBox.getSelectedItem();
    }

    public boolean shouldWriteOutput() {
        return chckbxOutput.isSelected();
    }
}
