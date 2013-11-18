package com.jbion.ces.validation;

import java.io.IOException;
import java.util.LinkedList;

import org.xml.sax.SAXException;

import com.jbion.ces.participant.Participant;
import com.jbion.utils.csv.CsvWriter;
import com.jbion.utils.xml.serialization.parameters.SpecificationNotMetException;

public abstract class PrintableResults {

    public abstract LinkedList<String> getHeaders();

    public abstract LinkedList<String> getValues();

    public void writeResults(String participantFile, String validationOutputFile) throws IOException, SAXException, SpecificationNotMetException {
        Participant participant = new Participant(participantFile);
        CsvWriter writer = new CsvWriter(validationOutputFile);
        
        LinkedList<String> headers = participant.getHeaders();
        headers.addAll(getHeaders());
        writer.writeRow(headers.toArray(new String[headers.size()]));
        
        LinkedList<String> values = participant.getValues();
        values.addAll(getValues());
        writer.writeRow(values.toArray(new String[values.size()]));
        
        writer.close();
    }
}
