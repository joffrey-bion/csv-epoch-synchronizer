package com.joffrey_bion.csv_epoch_synchronizer.participant;

import java.io.IOException;
import java.util.LinkedList;

import org.xml.sax.SAXException;

import com.joffrey_bion.utils.xml.serializers.EnumSerializer;
import com.joffrey_bion.utils.xml.serializers.SimpleSerializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class Participant extends Parameters {

    public static final String ID = "id";
    public static final String GENDER = "gender";
    public static final String AGE = "age";
    public static final String HEIGHT = "height";
    public static final String WEIGHT = "weight";
    public static final String WAIST_CIRCUMFERENCE = "waist-circ";
    public static final String BMI = "bmi";

    private static final EnumSerializer<Gender> GENDER_SER = new EnumSerializer<>(Gender.class);

    private static final ParamsSchema SCHEMA = new ParamsSchema("participant", 1);
    static {
        SCHEMA.addParam(ID, SimpleSerializer.STRING, "The ID given to the participant.");
        SCHEMA.addParam(GENDER, GENDER_SER, "The participant's gender.");
        SCHEMA.addParam(AGE, SimpleSerializer.INTEGER, "The age of the participant at the time of the test.");
        SCHEMA.addParam(HEIGHT, SimpleSerializer.DOUBLE, "The participant's height in cm.");
        SCHEMA.addParam(WEIGHT, SimpleSerializer.DOUBLE, "The participant's weight in kg.");
        SCHEMA.addParam(WAIST_CIRCUMFERENCE, SimpleSerializer.DOUBLE,
                "The participant's waist circumference in cm.");
        SCHEMA.addParam(BMI, SimpleSerializer.DOUBLE, "The participant's BMI in kg/m2.");
    }

    public enum Gender {
        MALE,
        FEMALE,
        OTHER;
    }

    public Participant() {
        super(SCHEMA);
    }

    public Participant(String participantFile) throws IOException, SAXException,
            SpecificationNotMetException {
        super(SCHEMA);
        loadFromXml(participantFile);
    }

    public LinkedList<String> getHeaders() {
        LinkedList<String> headers = new LinkedList<>();
        headers.add("ID");
        headers.add("Gender");
        headers.add("Age");
        headers.add("Weight");
        headers.add("Height");
        headers.add("WaistCirc");
        headers.add("BMI");
        return headers;
    }

    public LinkedList<String> getValues() {
        LinkedList<String> values = new LinkedList<>();
        values.add(getSerialized(ID));
        values.add(getSerialized(GENDER));
        values.add(getSerialized(AGE));
        values.add(getSerialized(WEIGHT));
        values.add(getSerialized(HEIGHT));
        values.add(getSerialized(WAIST_CIRCUMFERENCE));
        values.add(getSerialized(BMI));
        return values;
    }
}
