package com_joffrey_bion.csv.csv_epoch_synchronizer.ui;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import java.awt.Font;
import java.util.Arrays;

import javax.swing.Box;

@SuppressWarnings("serial")
public class ArgsPanel extends JPanel {

    private static final int NB_MAX_SPIKES = 5;

    private JTextField tfStartTime;
    private JTextField tfStopTime;

    private JLabel[] tfSpikeLabel;
    private JTextField[] tfSpikePhone;
    private JTextField[] tfSpikeActig;

    public class IncompleteSpikeException extends Exception {
        public IncompleteSpikeException(String message) {
            super(message);
        }
    }

    /**
     * Create the panel.
     */
    public ArgsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JPanel panelLimits = new JPanel();
        add(panelLimits);
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
        return Arrays.copyOfRange(spikes, 0, nbSpikes - 1);
    }

    public String getStartTime() {
        return tfStartTime.getText();
    }

    public String getStopTime() {
        return tfStopTime.getText();
    }
}
