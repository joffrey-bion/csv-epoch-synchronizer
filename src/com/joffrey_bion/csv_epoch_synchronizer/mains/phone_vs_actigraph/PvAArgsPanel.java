package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.joffrey_bion.csv_epoch_synchronizer.actigraph.ActigraphFileFormat;
import com.joffrey_bion.csv_epoch_synchronizer.config.Config;
import com.joffrey_bion.file_processor_window.file_picker.FilePicker;
import com.joffrey_bion.file_processor_window.file_picker.JFilePickersPanel;
import com.joffrey_bion.file_processor_window.parameters.ParamsSaverPanel;

import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import java.awt.Font;
import java.text.ParseException;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.JCheckBox;

import java.awt.Component;
import javax.swing.JComboBox;
import java.awt.FlowLayout;

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
    private JTextField tfWindowWidth;
    private JCheckBox chckbxDeleteTemp;
    private JComboBox<ActigraphFileFormat> cBoxActigraphFileFormat;

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
        tfStartTime.setColumns(DATE_TEXTFIELD_WIDTH);

        JLabel lblStopTime = new JLabel("Stop time:");
        panelLimits.add(lblStopTime, "1, 5, left, center");

        tfStopTime = new JTextField();
        tfStopTime.setHorizontalAlignment(SwingConstants.CENTER);
        panelLimits.add(tfStopTime, "3, 5, fill, center");
        tfStopTime.setColumns(DATE_TEXTFIELD_WIDTH);

        JLabel lblFormat = new JLabel("(" + PvAParams.TIMESTAMP_FORMAT + ")");
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

        panel.add(Box.createHorizontalStrut(5));
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        panel.add(separator);
        panel.add(Box.createHorizontalStrut(5));

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

        JLabel lblSpikes = new JLabel("Spikes (" + PvAParams.TIMESTAMP_FORMAT + ")");
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

        ParamsSaverPanel psp = new ParamsSaverPanel() {
            @Override
            public void saveParamsToFile(String paramFilePath) {
                try {
                    PvAParams params = new PvAParams();
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
                    PvAParams params = new PvAParams(paramFilePath);
                    setParameters(params);
                    System.out.println("Parameters loaded from '" + paramFilePath + "'.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        add(psp);

        tfSpikeLabel = new JLabel[NB_MAX_SPIKES];
        tfSpikePhone = new JTextField[NB_MAX_SPIKES];
        tfSpikeActig = new JTextField[NB_MAX_SPIKES];
        for (int i = 0; i < NB_MAX_SPIKES; i++) {
            tfSpikeLabel[i] = new JLabel(Integer.toString(i + 1));
            panelSpikes.add(tfSpikeLabel[i], "1, " + (2 * i + 5) + ", right, default");

            tfSpikePhone[i] = new JTextField();
            tfSpikePhone[i].setColumns(DATE_TEXTFIELD_WIDTH);
            tfSpikePhone[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelSpikes.add(tfSpikePhone[i], "3, " + (2 * i + 5) + ", fill, default");

            tfSpikeActig[i] = new JTextField();
            tfSpikeActig[i].setColumns(DATE_TEXTFIELD_WIDTH);
            tfSpikeActig[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelSpikes.add(tfSpikeActig[i], "5, " + (2 * i + 5) + ", fill, default");
        }
    }

    /**
     * Populates the fields of this panel (including files paths) with the given
     * parameters.
     * 
     * @param params
     *            The parameters to use to populate this panel.
     */
    private void setParameters(PvAParams params) {
        FilePicker[] inputs = filePickers.getInputFilePickers();
        inputs[0].setSelectedFilePath(params.getString(PvAParams.PHONE_FILE_PATH));
        inputs[1].setSelectedFilePath(params.getString(PvAParams.ACTIG_FILE_PATH));
        FilePicker[] outputs = filePickers.getOutputFilePickers();
        outputs[0].setSelectedFilePath(params.getString(PvAParams.OUTPUT_FILE_PATH));
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
        setFileParam(params, PvAParams.PHONE_FILE_PATH, filePickers.getInputFilePaths()[0]);
        setFileParam(params, PvAParams.ACTIG_FILE_PATH, filePickers.getInputFilePaths()[1]);
        setFileParam(params, PvAParams.OUTPUT_FILE_PATH, filePickers.getOutputFilePaths()[0]);
        params.deserializeAndSet(PvAParams.START_TIME, tfStartTime.getText());
        params.deserializeAndSet(PvAParams.STOP_TIME, tfStopTime.getText());
        params.set(PvAParams.ACTIG_FILE_FORMAT, cBoxActigraphFileFormat.getSelectedItem());
        params.deserializeAndSet(PvAParams.EPOCH_WIDTH_SEC, tfEpochWidth.getText());
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
    }

    private static void setFileParam(PvAParams params, String key, String filePath) {
        if (filePath != null && !filePath.equals("")) {
            params.set(key, filePath);
        }
    }
}
