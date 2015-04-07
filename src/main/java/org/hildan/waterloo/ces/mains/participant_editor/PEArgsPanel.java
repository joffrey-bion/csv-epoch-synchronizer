package org.hildan.waterloo.ces.mains.participant_editor;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.hildan.utils.xml.serialization.parameters.Parameters.MissingParameterException;
import org.hildan.waterloo.ces.participant.Participant;

@SuppressWarnings("serial")
public class PEArgsPanel extends JPanel {

    private static final int ID_TEXTFIELD_WIDTH = 5;

    private static final int DATE_TEXTFIELD_WIDTH = 8;

    private static final int CM_TEXTFIELD_WIDTH = 4;

    private final JTextField tfId;

    private final JTextField tfHeight;

    private final JTextField tfWeight;

    private final JTextField tfWaist;

    private final JTextField tfAge;

    private final JComboBox<Participant.Gender> cbGender;

    private final JTextField tfBMI;

    public PEArgsPanel() {
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0};
        gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        setLayout(gridBagLayout);

        final JLabel lblParticipantId = new JLabel("ID:");
        final GridBagConstraints gbc_lblParticipantId = new GridBagConstraints();
        gbc_lblParticipantId.anchor = GridBagConstraints.WEST;
        gbc_lblParticipantId.insets = new Insets(0, 0, 5, 5);
        gbc_lblParticipantId.gridx = 0;
        gbc_lblParticipantId.gridy = 0;
        add(lblParticipantId, gbc_lblParticipantId);

        tfId = new JTextField();
        final GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.insets = new Insets(0, 0, 5, 5);
        gbc_textField.gridx = 1;
        gbc_textField.gridy = 0;
        add(tfId, gbc_textField);
        tfId.setColumns(ID_TEXTFIELD_WIDTH);

        final Component horizontalStrut = Box.createHorizontalStrut(20);
        final GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
        gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
        gbc_horizontalStrut.gridx = 2;
        gbc_horizontalStrut.gridy = 0;
        add(horizontalStrut, gbc_horizontalStrut);

        final JLabel lblHeight = new JLabel("Height (cm):");
        final GridBagConstraints gbc_lblHeight = new GridBagConstraints();
        gbc_lblHeight.anchor = GridBagConstraints.WEST;
        gbc_lblHeight.insets = new Insets(0, 0, 5, 5);
        gbc_lblHeight.gridx = 3;
        gbc_lblHeight.gridy = 0;
        add(lblHeight, gbc_lblHeight);

        tfHeight = new JTextField();
        final GridBagConstraints gbc_textField_1 = new GridBagConstraints();
        gbc_textField_1.insets = new Insets(0, 0, 5, 0);
        gbc_textField_1.gridx = 4;
        gbc_textField_1.gridy = 0;
        add(tfHeight, gbc_textField_1);
        tfHeight.setColumns(CM_TEXTFIELD_WIDTH);

        final JLabel lblGender = new JLabel("Gender:");
        final GridBagConstraints gbc_lblGender = new GridBagConstraints();
        gbc_lblGender.anchor = GridBagConstraints.WEST;
        gbc_lblGender.insets = new Insets(0, 0, 5, 5);
        gbc_lblGender.gridx = 0;
        gbc_lblGender.gridy = 1;
        add(lblGender, gbc_lblGender);

        cbGender = new JComboBox<>();
        cbGender.setModel(new DefaultComboBoxModel<>(Participant.Gender.values()));
        final GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.insets = new Insets(0, 0, 5, 5);
        gbc_comboBox.gridx = 1;
        gbc_comboBox.gridy = 1;
        add(cbGender, gbc_comboBox);

        final JLabel lblWeight = new JLabel("Weight (kg):");
        final GridBagConstraints gbc_lblWeight = new GridBagConstraints();
        gbc_lblWeight.insets = new Insets(0, 0, 5, 5);
        gbc_lblWeight.anchor = GridBagConstraints.WEST;
        gbc_lblWeight.gridx = 3;
        gbc_lblWeight.gridy = 1;
        add(lblWeight, gbc_lblWeight);

        tfWeight = new JTextField();
        final GridBagConstraints gbc_tfWeight = new GridBagConstraints();
        gbc_tfWeight.insets = new Insets(0, 0, 5, 0);
        gbc_tfWeight.gridx = 4;
        gbc_tfWeight.gridy = 1;
        add(tfWeight, gbc_tfWeight);
        tfWeight.setColumns(CM_TEXTFIELD_WIDTH);

        final JLabel lblAge = new JLabel("Age:");
        final GridBagConstraints gbc_lblAge = new GridBagConstraints();
        gbc_lblAge.anchor = GridBagConstraints.WEST;
        gbc_lblAge.insets = new Insets(0, 0, 5, 5);
        gbc_lblAge.gridx = 0;
        gbc_lblAge.gridy = 2;
        add(lblAge, gbc_lblAge);

        tfAge = new JTextField();
        final GridBagConstraints gbc_tfAge = new GridBagConstraints();
        gbc_tfAge.fill = GridBagConstraints.HORIZONTAL;
        gbc_tfAge.insets = new Insets(0, 0, 5, 5);
        gbc_tfAge.gridx = 1;
        gbc_tfAge.gridy = 2;
        add(tfAge, gbc_tfAge);
        tfAge.setColumns(DATE_TEXTFIELD_WIDTH);

        final JLabel lblWaist = new JLabel("Waist circ. (cm):");
        final GridBagConstraints gbc_lblWaist = new GridBagConstraints();
        gbc_lblWaist.insets = new Insets(0, 0, 5, 5);
        gbc_lblWaist.anchor = GridBagConstraints.WEST;
        gbc_lblWaist.gridx = 3;
        gbc_lblWaist.gridy = 2;
        add(lblWaist, gbc_lblWaist);

        tfWaist = new JTextField();
        final GridBagConstraints gbc_tfWaist = new GridBagConstraints();
        gbc_tfWaist.insets = new Insets(0, 0, 5, 0);
        gbc_tfWaist.gridx = 4;
        gbc_tfWaist.gridy = 2;
        add(tfWaist, gbc_tfWaist);
        tfWaist.setColumns(CM_TEXTFIELD_WIDTH);

        final JLabel lblBmi = new JLabel("BMI (kg/m2):");
        final GridBagConstraints gbc_lblBmi = new GridBagConstraints();
        gbc_lblBmi.anchor = GridBagConstraints.WEST;
        gbc_lblBmi.insets = new Insets(0, 0, 5, 5);
        gbc_lblBmi.gridx = 3;
        gbc_lblBmi.gridy = 3;
        add(lblBmi, gbc_lblBmi);

        tfBMI = new JTextField();
        final GridBagConstraints gbc_tfBMI = new GridBagConstraints();
        gbc_tfBMI.insets = new Insets(0, 0, 5, 0);
        gbc_tfBMI.gridx = 4;
        gbc_tfBMI.gridy = 3;
        add(tfBMI, gbc_tfBMI);
        tfBMI.setColumns(4);

    }

    public void setParameters(Participant p) throws MissingParameterException {
        tfId.setText(p.getString(Participant.ID));
        cbGender.setSelectedItem(p.get(Participant.GENDER));
        tfAge.setText(p.getSerialized(Participant.AGE));
        tfHeight.setText(p.getSerialized(Participant.HEIGHT));
        tfWeight.setText(p.getSerialized(Participant.WEIGHT));
        tfWaist.setText(p.getSerialized(Participant.WAIST_CIRCUMFERENCE));
        tfBMI.setText(p.getSerialized(Participant.BMI));
    }

    public void getParameters(Participant p) throws ParseException {
        setIfNotEmpty(p, Participant.ID, tfId.getText());
        p.set(Participant.GENDER, cbGender.getSelectedItem());
        setIfNotEmpty(p, Participant.AGE, tfAge.getText());
        setIfNotEmpty(p, Participant.HEIGHT, tfHeight.getText());
        setIfNotEmpty(p, Participant.WEIGHT, tfWeight.getText());
        setIfNotEmpty(p, Participant.WAIST_CIRCUMFERENCE, tfWaist.getText());
        setIfNotEmpty(p, Participant.BMI, tfBMI.getText());
    }

    private static void setIfNotEmpty(Participant p, String key, String value) throws ParseException {
        if (!"".equals(value)) {
            p.deserializeAndSet(key, value);
        }
    }
}
