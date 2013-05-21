package com_joffrey_bion.csv.csv_epoch_synchronizer.ui;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.GridLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.SwingConstants;
import javax.swing.JSeparator;
import java.awt.Font;
import java.awt.Component;
import javax.swing.Box;

public class ArgsPanel extends JPanel {
    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;
    private JTextField textField_3;
    private JTextField textField_4;
    private JTextField textField_5;
    private JTextField textField_6;
    private JTextField textField_7;
    private JTextField textField_8;
    private JTextField textField_9;
    private JTextField textField_10;
    private JTextField textField_11;

    /**
     * Create the panel.
     */
    public ArgsPanel() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        
        JPanel panel = new JPanel();
        add(panel);
        panel.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.MIN_COLSPEC,
                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                FormFactory.PREF_COLSPEC,},
            new RowSpec[] {
                RowSpec.decode("14px"),
                FormFactory.LINE_GAP_ROWSPEC,
                RowSpec.decode("20px"),
                FormFactory.LINE_GAP_ROWSPEC,
                RowSpec.decode("20px"),
                FormFactory.LINE_GAP_ROWSPEC,
                RowSpec.decode("14px"),}));
        
        JLabel lblNewLabel = new JLabel("Processing Limits (actigraph time)");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel.add(lblNewLabel, "1, 1, 3, 1, center, center");
        
        JLabel lblStartTimeactigraph = new JLabel("Start time:");
        panel.add(lblStartTimeactigraph, "1, 3, left, center");
        
        textField = new JTextField();
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(textField, "3, 3, fill, center");
        textField.setColumns(23);
        
        JLabel lblStopTimeactigraph = new JLabel("Stop time:");
        panel.add(lblStopTimeactigraph, "1, 5, left, center");
        
        textField_1 = new JTextField();
        textField_1.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(textField_1, "3, 5, fill, center");
        textField_1.setColumns(23);
        
        JLabel lblyyyymmddHhmmss = new JLabel("(yyyy-MM-dd HH:mm:ss.SSS)");
        panel.add(lblyyyymmddHhmmss, "3, 7, center, center");
        
        Component horizontalStrut_1 = Box.createHorizontalStrut(5);
        add(horizontalStrut_1);
        
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        add(separator);
        
        Component horizontalStrut = Box.createHorizontalStrut(5);
        add(horizontalStrut);
        
        JPanel panel_1 = new JPanel();
        add(panel_1);
        panel_1.setLayout(new FormLayout(new ColumnSpec[] {
                FormFactory.PREF_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.PREF_COLSPEC,
                FormFactory.RELATED_GAP_COLSPEC,
                FormFactory.PREF_COLSPEC,},
            new RowSpec[] {
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                RowSpec.decode("14px"),
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,
                FormFactory.RELATED_GAP_ROWSPEC,
                FormFactory.DEFAULT_ROWSPEC,}));
        
        JLabel lblSpikes = new JLabel("Spikes (HH:mm:ss.SSS)");
        lblSpikes.setFont(new Font("Tahoma", Font.BOLD, 11));
        panel_1.add(lblSpikes, "3, 1, 3, 1, center, center");
        
        JLabel lblPhone = new JLabel("Phone");
        panel_1.add(lblPhone, "3, 3, center, default");
        
        JLabel lblActigraph = new JLabel("Actigraph");
        panel_1.add(lblActigraph, "5, 3, center, default");
        
        JLabel label_1 = new JLabel("1");
        panel_1.add(label_1, "1, 5, right, default");
        
        textField_2 = new JTextField();
        textField_2.setHorizontalAlignment(SwingConstants.CENTER);
        panel_1.add(textField_2, "3, 5, fill, default");
        textField_2.setColumns(12);
        
        textField_3 = new JTextField();
        textField_3.setColumns(12);
        panel_1.add(textField_3, "5, 5, fill, default");
        
        JLabel label_2 = new JLabel("2");
        panel_1.add(label_2, "1, 7, right, default");
        
        textField_4 = new JTextField();
        textField_4.setColumns(12);
        panel_1.add(textField_4, "3, 7, fill, default");
        
        textField_5 = new JTextField();
        textField_5.setColumns(12);
        panel_1.add(textField_5, "5, 7, fill, default");
        
        JLabel label_3 = new JLabel("3");
        panel_1.add(label_3, "1, 9, right, default");
        
        textField_6 = new JTextField();
        textField_6.setColumns(12);
        panel_1.add(textField_6, "3, 9, fill, default");
        
        textField_7 = new JTextField();
        textField_7.setColumns(12);
        panel_1.add(textField_7, "5, 9, fill, default");
        
        JLabel label_4 = new JLabel("4");
        panel_1.add(label_4, "1, 11, right, default");
        
        textField_8 = new JTextField();
        textField_8.setColumns(12);
        panel_1.add(textField_8, "3, 11, fill, default");
        
        textField_9 = new JTextField();
        textField_9.setColumns(12);
        panel_1.add(textField_9, "5, 11, fill, default");
        
        JLabel label_5 = new JLabel("5");
        panel_1.add(label_5, "1, 13, right, default");
        
        textField_10 = new JTextField();
        textField_10.setColumns(12);
        panel_1.add(textField_10, "3, 13, fill, default");
        
        textField_11 = new JTextField();
        textField_11.setColumns(12);
        panel_1.add(textField_11, "5, 13, fill, default");

    }

}
