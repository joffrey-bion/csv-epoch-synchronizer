package com_joffrey_bion.csv.csv_epoch_synchronizer.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.joffrey_bion.file_processor_window.FilePicker;
import com.joffrey_bion.file_processor_window.JFilePickersPanel;

import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.Box;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import java.awt.Component;

@SuppressWarnings("serial")
public class ArgsPanel extends JPanel {

    static final int NB_MAX_SPIKES = 6;
    
    private final XmlSaver saver;
    private final JFilePickersPanel filePickers;
    JTextField tfStartTime;
    JTextField tfStopTime;

    JLabel[] tfSpikeLabel;
    JTextField[] tfSpikePhone;
    JTextField[] tfSpikeActig;
    JTextField tfEpochWidth;
    JTextField tfWindowWidth;
    JCheckBox chckbxDeleteTemp;
    
    public class IncompleteSpikeException extends Exception {
        public IncompleteSpikeException(String message) {
            super(message);
        }
    }

    /**
     * Create the panel.
     * @param filePickers 
     */
    public ArgsPanel(JFilePickersPanel filePickers) {
        this.saver = new XmlSaver(this);
        this.filePickers = filePickers;
        
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BorderLayout(0, 0));

        JPanel panel_2 = new JPanel();
        panel.add(panel_2, BorderLayout.NORTH);
        panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.Y_AXIS));

        JPanel panelLimits = new JPanel();
        panel_2.add(panelLimits);
        panelLimits.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.MIN_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC, FormFactory.PREF_COLSPEC, },
                new RowSpec[] { RowSpec.decode("14px"), FormFactory.LINE_GAP_ROWSPEC,
                        RowSpec.decode("20px"), FormFactory.LINE_GAP_ROWSPEC,
                        RowSpec.decode("20px"), FormFactory.LINE_GAP_ROWSPEC,
                        RowSpec.decode("14px"), }));

        JLabel lblNewLabel = new JLabel("Processing Limits (actigraph time)");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelLimits.add(lblNewLabel, "1, 1, 3, 1, center, center");

        JLabel lblStartTime = new JLabel("Start time:");
        panelLimits.add(lblStartTime, "1, 3, left, center");

        tfStartTime = new JTextField();
        tfStartTime.setHorizontalAlignment(SwingConstants.CENTER);
        panelLimits.add(tfStartTime, "3, 3, fill, center");
        tfStartTime.setColumns(18);

        JLabel lblStopTime = new JLabel("Stop time:");
        panelLimits.add(lblStopTime, "1, 5, left, center");

        tfStopTime = new JTextField();
        tfStopTime.setHorizontalAlignment(SwingConstants.CENTER);
        panelLimits.add(tfStopTime, "3, 5, fill, center");
        tfStopTime.setColumns(18);

        JLabel lblFormat = new JLabel("(yyyy-MM-dd HH:mm:ss.SSS)");
        panelLimits.add(lblFormat, "3, 7, center, center");

        panel_2.add(Box.createVerticalStrut(5));
        panel_2.add(new JSeparator());
        panel_2.add(Box.createVerticalStrut(5));

        JPanel panel_4 = new JPanel();
        panel_4.setAlignmentX(Component.RIGHT_ALIGNMENT);
        panel_4.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_2.add(panel_4);
        panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));

        JPanel panel_3 = new JPanel();
        panel_3.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_4.add(panel_3);
        panel_3.setLayout(new FormLayout(new ColumnSpec[] { FormFactory.MIN_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC, FormFactory.PREF_COLSPEC, }, new RowSpec[] {
                FormFactory.LINE_GAP_ROWSPEC, FormFactory.MIN_ROWSPEC,
                FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("23px"), }));

        JLabel lblEpochWidth = new JLabel("Epoch width (sec):");
        panel_3.add(lblEpochWidth, "1, 2, left, center");

        tfEpochWidth = new JTextField();
        tfEpochWidth.setText("1");
        panel_3.add(tfEpochWidth, "3, 2, fill, top");
        tfEpochWidth.setColumns(1);

        JLabel lblWindowWidth = new JLabel("Window width (sec):");
        panel_3.add(lblWindowWidth, "1, 4, left, center");

        tfWindowWidth = new JTextField();
        tfWindowWidth.setText("5");
        panel_3.add(tfWindowWidth, "3, 4, fill, center");
        tfWindowWidth.setColumns(1);

        chckbxDeleteTemp = new JCheckBox("Delete temporary file");
        chckbxDeleteTemp.setSelected(true);
        chckbxDeleteTemp.setHorizontalAlignment(SwingConstants.TRAILING);
        panel_4.add(chckbxDeleteTemp);

        JPanel panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.SOUTH);

        JButton btnSave = new JButton("Save params...");
        @SuppressWarnings("unused")
        FilePicker saveFilePicker = new FilePicker(this, btnSave, FilePicker.MODE_SAVE) {
            @Override
            protected void onSelect() {
                saver.save(getSelectedFilePath(), ArgsPanel.this.filePickers);
            }
        };
        panel_1.add(btnSave);

        JButton btnLoad = new JButton("Load params...");
        @SuppressWarnings("unused")
        FilePicker loadFilePicker = new FilePicker(this, btnLoad, FilePicker.MODE_OPEN) {
            @Override
            protected void onSelect() {
                saver.load(getSelectedFilePath(), ArgsPanel.this.filePickers);
            }
        };
        panel_1.add(btnLoad);

        add(Box.createHorizontalStrut(5));
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        add(separator);
        add(Box.createHorizontalStrut(5));

        JPanel panelSpikes = new JPanel();
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

        JLabel lblSpikes = new JLabel("Spikes (HH:mm:ss.SSS)");
        lblSpikes.setFont(new Font("Tahoma", Font.BOLD, 11));
        panelSpikes.add(lblSpikes, "3, 1, 3, 1, center, center");

        JLabel lblPhone = new JLabel("Phone");
        panelSpikes.add(lblPhone, "3, 3, center, default");

        JLabel lblActigraph = new JLabel("Actigraph");
        panelSpikes.add(lblActigraph, "5, 3, center, default");

        tfSpikeLabel = new JLabel[NB_MAX_SPIKES];
        tfSpikePhone = new JTextField[NB_MAX_SPIKES];
        tfSpikeActig = new JTextField[NB_MAX_SPIKES];
        for (int i = 0; i < NB_MAX_SPIKES; i++) {
            tfSpikeLabel[i] = new JLabel(Integer.toString(i + 1));
            panelSpikes.add(tfSpikeLabel[i], "1, " + (2 * i + 5) + ", right, default");

            tfSpikePhone[i] = new JTextField();
            tfSpikePhone[i].setColumns(10);
            tfSpikePhone[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelSpikes.add(tfSpikePhone[i], "3, " + (2 * i + 5) + ", fill, default");

            tfSpikeActig[i] = new JTextField();
            tfSpikeActig[i].setColumns(10);
            tfSpikeActig[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelSpikes.add(tfSpikeActig[i], "5, " + (2 * i + 5) + ", fill, default");
        }
    }

    public String[] getSpikes() throws IncompleteSpikeException {
        String[] spikes = new String[NB_MAX_SPIKES * 2];
        int nbSpikes = 0;
        for (int i = 0; i < NB_MAX_SPIKES; i++) {
            String phone = tfSpikePhone[i].getText();
            String actig = tfSpikeActig[i].getText();
            if (!phone.equals("") && !actig.equals("")) {
                spikes[2 * nbSpikes] = phone;
                spikes[2 * nbSpikes + 1] = actig;
                nbSpikes++;
            } else if (phone.equals("") && actig.equals("")) {
                continue;
            } else {
                throw new IncompleteSpikeException(
                        "Each phone spike must have a corresponding actigraph spike");
            }
        }
        if (nbSpikes == 0)
            return new String[0];
        return Arrays.copyOfRange(spikes, 0, nbSpikes * 2 - 1); // TODO check the *2
    }

    public String getStartTime() {
        return tfStartTime.getText();
    }

    public String getStopTime() {
        return tfStopTime.getText();
    }
}
