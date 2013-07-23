package com.joffrey_bion.csv_epoch_synchronizer;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.joffrey_bion.csv_epoch_synchronizer.actigraph.ActigraphFileFormat;
import com.joffrey_bion.csv_epoch_synchronizer.parameters.Config;
import com.joffrey_bion.csv_epoch_synchronizer.parameters.InstanceRawParameters;
import com.joffrey_bion.file_processor_window.file_picker.FilePicker;
import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;

import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import java.awt.Component;
import javax.swing.JComboBox;
import java.awt.FlowLayout;

@SuppressWarnings("serial")
class ArgsPanelCES extends JPanel {

    private final JFilePickersPanel filePickers;

    private JTextField tfStartTime;
    private JTextField tfStopTime;

    private JLabel[] tfSpikeLabel;
    private JTextField[] tfSpikePhone;
    private JTextField[] tfSpikeActig;
    private JTextField tfEpochWidth;
    private JTextField tfWindowWidth;
    private JCheckBox chckbxDeleteTemp;
    private JComboBox<ActigraphFileFormat> cBoxActigraphFileFormat;

    /**
     * Create the panel.
     * 
     * @param filePickers
     */
    public ArgsPanelCES(JFilePickersPanel filePickers) {
        this.filePickers = filePickers;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        JPanel panelArgsLeft = new JPanel();
        panel.add(panelArgsLeft);
        panelArgsLeft.setLayout(new BoxLayout(panelArgsLeft, BoxLayout.Y_AXIS));

        JPanel panelLimits = new JPanel();
        panelLimits.setAlignmentY(Component.TOP_ALIGNMENT);
        panelLimits.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelArgsLeft.add(panelLimits);
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

        panelArgsLeft.add(Box.createVerticalStrut(5));
        panelArgsLeft.add(new JSeparator());
        panelArgsLeft.add(Box.createVerticalStrut(5));

        JPanel panel_4 = new JPanel();
        panelArgsLeft.add(panel_4);
        panel_4.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_4.setAlignmentY(Component.TOP_ALIGNMENT);
        panel_4.setLayout(new BoxLayout(panel_4, BoxLayout.Y_AXIS));

        JPanel panel_3 = new JPanel();
        panel_3.setAlignmentY(Component.TOP_ALIGNMENT);
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

        JPanel panel_1 = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
        flowLayout.setHgap(0);
        flowLayout.setAlignment(FlowLayout.LEFT);
        panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel_4.add(panel_1);

        JLabel lblActigraphEpochFile = new JLabel("Actigraph file format:");
        lblActigraphEpochFile.setHorizontalAlignment(SwingConstants.TRAILING);
        panel_1.add(lblActigraphEpochFile);

        Component horizontalStrut = Box.createHorizontalStrut(10);
        panel_1.add(horizontalStrut);

        cBoxActigraphFileFormat = new JComboBox<>(ActigraphFileFormat.values());
        panel_1.add(cBoxActigraphFileFormat);

        chckbxDeleteTemp = new JCheckBox("Delete temporary file");
        panel_4.add(chckbxDeleteTemp);
        chckbxDeleteTemp.setSelected(Config.get().deleteIntermediateFile);
        chckbxDeleteTemp.setHorizontalAlignment(SwingConstants.TRAILING);

        Component verticalGlue = Box.createVerticalGlue();
        panelArgsLeft.add(verticalGlue);

        Component horizontalStrut_1 = Box.createHorizontalStrut(5);
        panel.add(horizontalStrut_1);
        JSeparator separator = new JSeparator();
        panel.add(separator);
        separator.setOrientation(SwingConstants.VERTICAL);
        Component horizontalStrut_2 = Box.createHorizontalStrut(5);
        panel.add(horizontalStrut_2);

        JPanel panelSpikes = new JPanel();
        panel.add(panelSpikes);
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

        Component verticalStrut = Box.createVerticalStrut(5);
        add(verticalStrut);

        JSeparator separator_1 = new JSeparator();
        add(separator_1);

        Component verticalStrut_1 = Box.createVerticalStrut(5);
        add(verticalStrut_1);

        JPanel savePanel = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) savePanel.getLayout();
        flowLayout_1.setHgap(0);
        flowLayout_1.setVgap(0);
        add(savePanel);

        JButton btnSave = new JButton("Save params...");

        FilePicker saveFilePicker = new FilePicker(this, btnSave, FilePicker.MODE_SAVE) {
            @Override
            protected void onSelect() {
                try {
                    String[] inFiles = ArgsPanelCES.this.filePickers.getInputFilePaths();
                    String[] outFiles = ArgsPanelCES.this.filePickers.getOutputFilePaths();
                    InstanceRawParameters rawParams = getRawParameters(inFiles[0], inFiles[1],
                            outFiles[0]);
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
                    FilePicker[] inFp = ArgsPanelCES.this.filePickers.getInputFilePickers();
                    inFp[0].setSelectedFilePath(raw.phoneRawFile);
                    inFp[1].setSelectedFilePath(raw.actigEpFile);
                    FilePicker[] outFp = ArgsPanelCES.this.filePickers.getOutputFilePickers();
                    outFp[0].setSelectedFilePath(raw.outputFile);
                    System.out.println("Parameters loaded from '" + paramFilePath + "'.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        loadFilePicker.addFileTypeFilter(".xml", "XML Parameter File");
        savePanel.add(btnLoad);

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
        if (raw.actigraphFileFormat != null) {
            cBoxActigraphFileFormat.setSelectedItem(ActigraphFileFormat
                    .valueOf(raw.actigraphFileFormat));
        }
        for (int i = 0; i < raw.phoneSpikes.length; i++) {
            tfSpikePhone[i].setText(raw.phoneSpikes[i]);
            tfSpikeActig[i].setText(raw.actigraphSpikes[i]);
        }
    }

    public InstanceRawParameters getRawParameters(String phoneRawFile, String actigEpFile,
            String outputFile) {
        InstanceRawParameters params = new InstanceRawParameters();
        params.phoneRawFile = phoneRawFile;
        params.actigEpFile = actigEpFile;
        params.outputFile = outputFile;
        params.startTime = tfStartTime.getText();
        params.stopTime = tfStopTime.getText();
        params.actigraphFileFormat = ((ActigraphFileFormat) cBoxActigraphFileFormat
                .getSelectedItem()).toString();
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
            params.phoneSpikes = Arrays.copyOfRange(phoneSpikes, 0, nbSpikes);
            params.actigraphSpikes = Arrays.copyOfRange(actigraphSpikes, 0, nbSpikes);
        } else {
            params.phoneSpikes = new String[0];
            params.actigraphSpikes = new String[0];
        }
        return params;
    }
}
