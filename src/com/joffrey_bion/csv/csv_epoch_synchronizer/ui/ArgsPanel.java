package com.joffrey_bion.csv.csv_epoch_synchronizer.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.joffrey_bion.csv.csv_epoch_synchronizer.parameters.Config;
import com.joffrey_bion.csv.csv_epoch_synchronizer.parameters.InstanceRawParameters;
import com.joffrey_bion.file_processor_window.file_picker.FilePicker;
import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;

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

    private final JFilePickersPanel filePickers;

    private JTextField tfStartTime;
    private JTextField tfStopTime;

    private JLabel[] tfSpikeLabel;
    private JTextField[] tfSpikePhone;
    private JTextField[] tfSpikeActig;
    private JTextField tfEpochWidth;
    private JTextField tfWindowWidth;
    private JCheckBox chckbxDeleteTemp;

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
        tfWindowWidth.setText(Integer.toString(Config.get().windowWidthSec));
        panel_3.add(tfWindowWidth, "3, 4, fill, center");
        tfWindowWidth.setColumns(1);

        chckbxDeleteTemp = new JCheckBox("Delete temporary file");
        chckbxDeleteTemp.setSelected(Config.get().deleteIntermediateFile);
        chckbxDeleteTemp.setHorizontalAlignment(SwingConstants.TRAILING);
        panel_4.add(chckbxDeleteTemp);

        JPanel savePanel = new JPanel();
        panel.add(savePanel, BorderLayout.SOUTH);

        JButton btnSave = new JButton("Save params...");

        FilePicker saveFilePicker = new FilePicker(this, btnSave, FilePicker.MODE_SAVE) {
            @Override
            protected void onSelect() {
                try {
                    String[] inFiles = ArgsPanel.this.filePickers.getInputFilePaths();
                    String[] outFiles = ArgsPanel.this.filePickers.getOutputFilePaths();
                    InstanceRawParameters rawParams = getRawParameters(inFiles[0], inFiles[1], outFiles[0]);
                    String paramFilePath = getSelectedFilePath();
                    rawParams.save(paramFilePath);
                    System.out.println("Parameters saved to '" + paramFilePath + "'.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        saveFilePicker.addFileTypeFilter(".xml", "XML Parameter File");
        savePanel.add(btnSave);

        JButton btnLoad = new JButton("Load params...");

        FilePicker loadFilePicker = new FilePicker(this, btnLoad, FilePicker.MODE_OPEN) {
            @Override
            protected void onSelect() {
                try {
                    String paramFilePath = getSelectedFilePath();
                    InstanceRawParameters raw = InstanceRawParameters.load(paramFilePath);
                    setParameters(raw);
                    FilePicker[] inFp = ArgsPanel.this.filePickers.getInputFilePickers();
                    inFp[0].setSelectedFilePath(raw.phoneRawFile);
                    inFp[1].setSelectedFilePath(raw.actigEpFile);
                    FilePicker[] outFp = ArgsPanel.this.filePickers.getOutputFilePickers();
                    outFp[0].setSelectedFilePath(raw.outputFile);
                    System.out.println("Parameters loaded from '" + paramFilePath + "'.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        loadFilePicker.addFileTypeFilter(".xml", "XML Parameter File");
        savePanel.add(btnLoad);

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

        tfSpikeLabel = new JLabel[InstanceRawParameters.NB_MAX_SPIKES];
        tfSpikePhone = new JTextField[InstanceRawParameters.NB_MAX_SPIKES];
        tfSpikeActig = new JTextField[InstanceRawParameters.NB_MAX_SPIKES];
        for (int i = 0; i < InstanceRawParameters.NB_MAX_SPIKES; i++) {
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

    public void setParameters(InstanceRawParameters raw) {
        tfStartTime.setText(raw.startTime);
        tfStopTime.setText(raw.stopTime);
        tfEpochWidth.setText(raw.epochWidthSec);
        for (int i = 0; i < raw.phoneSpikes.length; i++) {
            tfSpikePhone[i].setText(raw.phoneSpikes[i]);
            tfSpikeActig[i].setText(raw.actigraphSpikes[i]);
        }
    }

    public InstanceRawParameters getRawParameters(String phoneRawFile, String actigEpFile, String outputFile) {
        InstanceRawParameters params = new InstanceRawParameters();
        params.phoneRawFile = phoneRawFile;
        params.actigEpFile = actigEpFile;
        params.outputFile = outputFile;
        params.startTime = tfStartTime.getText();
        params.stopTime = tfStopTime.getText();
        params.epochWidthSec = tfEpochWidth.getText();
        String[] phoneSpikes = new String[InstanceRawParameters.NB_MAX_SPIKES];
        String[] actigraphSpikes = new String[InstanceRawParameters.NB_MAX_SPIKES];
        int nbSpikes = 0;
        for (int i = 0; i < InstanceRawParameters.NB_MAX_SPIKES; i++) {
            phoneSpikes[nbSpikes] = tfSpikePhone[i].getText();
            actigraphSpikes[nbSpikes] = tfSpikeActig[i].getText();
            if (!phoneSpikes[nbSpikes].equals("") && !actigraphSpikes[nbSpikes].equals("")) {
                nbSpikes++;
            }
        }
        if (nbSpikes > 0) {
            params.phoneSpikes = Arrays.copyOfRange(phoneSpikes, 0, nbSpikes - 1);
            params.actigraphSpikes = Arrays.copyOfRange(actigraphSpikes, 0, nbSpikes - 1);
        } else {
            params.phoneSpikes = new String[0];
            params.actigraphSpikes = new String[0];
        }
        return params;
    }
}
