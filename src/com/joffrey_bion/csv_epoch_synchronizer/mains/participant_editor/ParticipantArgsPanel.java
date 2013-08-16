package com.joffrey_bion.csv_epoch_synchronizer.mains.participant_editor;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;

import com.joffrey_bion.csv_epoch_synchronizer.participant.Participant;
import com.joffrey_bion.xml_parameters_serializer.Parameters.MissingParameterException;

import javax.swing.JComboBox;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Color;
import java.text.ParseException;

@SuppressWarnings("serial")
public class ParticipantArgsPanel extends JPanel {

    private static final int ID_TEXTFIELD_WIDTH = 5;
    private static final int DATE_TEXTFIELD_WIDTH = 8;
    private static final int CM_TEXTFIELD_WIDTH = 4;

    private JTextField tfId;
    private JTextField tfHeight;
    private JTextField tfWeight;
    private JTextField tfWaist;
    private JTextField tfDateOfBirth;
    private JComboBox<Participant.Gender> cbGender;

    public ParticipantArgsPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        setLayout(gridBagLayout);

        JLabel lblParticipantId = new JLabel("ID:");
        GridBagConstraints gbc_lblParticipantId = new GridBagConstraints();
        gbc_lblParticipantId.anchor = GridBagConstraints.WEST;
        gbc_lblParticipantId.insets = new Insets(0, 0, 5, 5);
        gbc_lblParticipantId.gridx = 0;
        gbc_lblParticipantId.gridy = 0;
        add(lblParticipantId, gbc_lblParticipantId);

        tfId = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.gridx = 1;
        gbc_textField.gridy = 0;
        add(tfId, gbc_textField);
        tfId.setColumns(ID_TEXTFIELD_WIDTH);
        
        Component horizontalStrut = Box.createHorizontalStrut(20);
        GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
        gbc_horizontalStrut.gridx = 2;
        gbc_horizontalStrut.gridy = 0;
        add(horizontalStrut, gbc_horizontalStrut);

        JLabel lblHeight = new JLabel("Height:");
        GridBagConstraints gbc_lblHeight = new GridBagConstraints();
        gbc_lblHeight.anchor = GridBagConstraints.WEST;
        gbc_lblHeight.insets = new Insets(0, 0, 5, 5);
        gbc_lblHeight.gridx = 3;
        gbc_lblHeight.gridy = 0;
        add(lblHeight, gbc_lblHeight);

        tfHeight = new JTextField();
        GridBagConstraints gbc_textField_1 = new GridBagConstraints();
        gbc_textField_1.insets = new Insets(0, 0, 5, 5);
        gbc_textField_1.gridx = 4;
        gbc_textField_1.gridy = 0;
        add(tfHeight, gbc_textField_1);
        tfHeight.setColumns(CM_TEXTFIELD_WIDTH);

        JLabel lblHeightUnit = new JLabel("cm");
        GridBagConstraints gbc_lblCm = new GridBagConstraints();
        gbc_lblCm.anchor = GridBagConstraints.WEST;
        gbc_lblCm.insets = new Insets(0, 0, 5, 0);
        gbc_lblCm.gridx = 5;
        gbc_lblCm.gridy = 0;
        add(lblHeightUnit, gbc_lblCm);

        JLabel lblGender = new JLabel("Gender:");
        GridBagConstraints gbc_lblGender = new GridBagConstraints();
        gbc_lblGender.anchor = GridBagConstraints.WEST;
        gbc_lblGender.insets = new Insets(0, 0, 5, 5);
        gbc_lblGender.gridx = 0;
        gbc_lblGender.gridy = 1;
        add(lblGender, gbc_lblGender);

        cbGender = new JComboBox<>();
        cbGender.setModel(new DefaultComboBoxModel<>(Participant.Gender.values()));
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox.gridx = 1;
        gbc_comboBox.gridy = 1;
        add(cbGender, gbc_comboBox);

        JLabel lblWeight = new JLabel("Weight:");
        GridBagConstraints gbc_lblWeight = new GridBagConstraints();
        gbc_lblWeight.insets = new Insets(0, 0, 5, 5);
        gbc_lblWeight.anchor = GridBagConstraints.WEST;
        gbc_lblWeight.gridx = 3;
        gbc_lblWeight.gridy = 1;
        add(lblWeight, gbc_lblWeight);

        tfWeight = new JTextField();
        GridBagConstraints gbc_textField_2 = new GridBagConstraints();
        gbc_textField_2.insets = new Insets(0, 0, 5, 5);
        gbc_textField_2.gridx = 4;
        gbc_textField_2.gridy = 1;
        add(tfWeight, gbc_textField_2);
        tfWeight.setColumns(CM_TEXTFIELD_WIDTH);

        JLabel lblWeightUnit = new JLabel("kg");
        GridBagConstraints gbc_lblCm_1 = new GridBagConstraints();
        gbc_lblCm_1.anchor = GridBagConstraints.WEST;
        gbc_lblCm_1.insets = new Insets(0, 0, 5, 0);
        gbc_lblCm_1.gridx = 5;
        gbc_lblCm_1.gridy = 1;
        add(lblWeightUnit, gbc_lblCm_1);

        JLabel lblDateOfBirth = new JLabel("Date of birth:");
        GridBagConstraints gbc_lblDateOfBirth = new GridBagConstraints();
        gbc_lblDateOfBirth.anchor = GridBagConstraints.WEST;
        gbc_lblDateOfBirth.insets = new Insets(0, 0, 5, 5);
        gbc_lblDateOfBirth.gridx = 0;
        gbc_lblDateOfBirth.gridy = 2;
        add(lblDateOfBirth, gbc_lblDateOfBirth);

        tfDateOfBirth = new JTextField();
        GridBagConstraints gbc_textField_4 = new GridBagConstraints();
        gbc_textField_4.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField_4.insets = new Insets(0, 0, 5, 5);
        gbc_textField_4.gridx = 1;
        gbc_textField_4.gridy = 2;
        add(tfDateOfBirth, gbc_textField_4);
        tfDateOfBirth.setColumns(DATE_TEXTFIELD_WIDTH);

        JLabel lblWaist = new JLabel("Waist circ.:");
        GridBagConstraints gbc_lblWaist = new GridBagConstraints();
        gbc_lblWaist.insets = new Insets(0, 0, 5, 5);
        gbc_lblWaist.anchor = GridBagConstraints.WEST;
        gbc_lblWaist.gridx = 3;
        gbc_lblWaist.gridy = 2;
        add(lblWaist, gbc_lblWaist);

        tfWaist = new JTextField();
        GridBagConstraints gbc_textField_3 = new GridBagConstraints();
        gbc_textField_3.insets = new Insets(0, 0, 5, 5);
        gbc_textField_3.gridx = 4;
        gbc_textField_3.gridy = 2;
        add(tfWaist, gbc_textField_3);
        tfWaist.setColumns(CM_TEXTFIELD_WIDTH);

        JLabel lblCm_2 = new JLabel("cm");
        GridBagConstraints gbc_lblCm_2 = new GridBagConstraints();
        gbc_lblCm_2.anchor = GridBagConstraints.WEST;
        gbc_lblCm_2.insets = new Insets(0, 0, 5, 0);
        gbc_lblCm_2.gridx = 5;
        gbc_lblCm_2.gridy = 2;
        add(lblCm_2, gbc_lblCm_2);

        JLabel lblFormat = new JLabel("(" + Participant.DATE_FORMAT + ")");
        lblFormat.setForeground(Color.GRAY);
        GridBagConstraints gbc_lblFormat = new GridBagConstraints();
        gbc_lblFormat.insets = new Insets(0, 0, 5, 5);
        gbc_lblFormat.anchor = GridBagConstraints.WEST;
        gbc_lblFormat.gridx = 1;
        gbc_lblFormat.gridy = 3;
        add(lblFormat, gbc_lblFormat);
    }
    
    public void setParameters(Participant p) throws MissingParameterException {
        tfId.setText(p.getString(Participant.ID));
        cbGender.setSelectedItem(p.get(Participant.GENDER));
        tfDateOfBirth.setText(p.getSerialized(Participant.DATE_OF_BIRTH));
        tfHeight.setText(p.getSerialized(Participant.HEIGHT));
        tfWeight.setText(p.getSerialized(Participant.WEIGHT));
        tfWaist.setText(p.getSerialized(Participant.WAIST_CIRCUMFERENCE));
    }

    public void getParameters(Participant p) throws ParseException {
        setIfNotEmpty(p, Participant.ID, tfId.getText());
        p.set(Participant.GENDER, cbGender.getSelectedItem());
        setIfNotEmpty(p, Participant.DATE_OF_BIRTH, tfDateOfBirth.getText());
        setIfNotEmpty(p, Participant.HEIGHT, tfHeight.getText());
        setIfNotEmpty(p, Participant.WEIGHT, tfWeight.getText());
        setIfNotEmpty(p, Participant.WAIST_CIRCUMFERENCE, tfWaist.getText());
    }
    
    private static void setIfNotEmpty(Participant p, String key, String value) throws ParseException {
        if (!"".equals(value)) {
            p.deserializeAndSet(key, value);
        }
    }
}
