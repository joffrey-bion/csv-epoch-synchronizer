package com.joffrey_bion.csv_epoch_synchronizer.participant;

import java.io.IOException;

import org.xml.sax.SAXException;

import com.joffrey_bion.utils.xml.serializers.DateSerializer;
import com.joffrey_bion.utils.xml.serializers.EnumSerializer;
import com.joffrey_bion.utils.xml.serializers.SimpleSerializer;
import com.joffrey_bion.xml_parameters_serializer.Parameters;
import com.joffrey_bion.xml_parameters_serializer.ParamsSchema;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class Participant extends Parameters {

    public static final String ID = "id";
    public static final String GENDER = "gender";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String WAIST_CIRCOMFERENCE = "waist-circ";
    public static final String DATE_OF_BIRTH = "date-of-birth";

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final DateSerializer DATE_SER = new DateSerializer(DATE_FORMAT);
    private static final EnumSerializer<Gender> GENDER_SER = new EnumSerializer<>(Gender.class);
    
    private static final ParamsSchema SCHEMA = new ParamsSchema(1);
    static {
        SCHEMA.addParam(ID, SimpleSerializer.STRING);
        SCHEMA.addParam(GENDER, GENDER_SER);
        SCHEMA.addParam(WEIGHT, SimpleSerializer.DOUBLE);
        SCHEMA.addParam(HEIGHT, SimpleSerializer.DOUBLE);
        SCHEMA.addParam(WAIST_CIRCOMFERENCE, SimpleSerializer.DOUBLE);
        SCHEMA.addParam(DATE_OF_BIRTH, DATE_SER);
    }
    
    public enum Gender {
        MALE, FEMALE;
    }
    
    public Participant() {
        super(SCHEMA);
    }
    
    public Participant(String participantFile) throws IOException, SAXException, SpecificationNotMetException {
        super(SCHEMA);
        loadFromXml(participantFile);
    }
}
