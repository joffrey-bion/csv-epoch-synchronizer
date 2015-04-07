package org.hildan.waterloo.ces.validation;

import java.io.IOException;
import java.util.LinkedList;

import org.hildan.utils.csv.CsvWriter;
import org.hildan.utils.xml.serialization.parameters.SpecificationNotMetException;
import org.hildan.waterloo.ces.participant.Participant;
import org.xml.sax.SAXException;

public abstract class PrintableResults {

    public abstract LinkedList<String> getHeaders();

    public abstract LinkedList<String> getValues();

    public void writeResults(String participantFile, String validationOutputFile) throws IOException, SAXException,
            SpecificationNotMetException {
        final Participant participant = new Participant(participantFile);
        final CsvWriter writer = new CsvWriter(validationOutputFile);

        final LinkedList<String> headers = participant.getHeaders();
        headers.addAll(getHeaders());
        writer.writeRow(headers.toArray(new String[headers.size()]));

        final LinkedList<String> values = participant.getValues();
        values.addAll(getValues());
        writer.writeRow(values.toArray(new String[values.size()]));

        writer.close();
    }
}
