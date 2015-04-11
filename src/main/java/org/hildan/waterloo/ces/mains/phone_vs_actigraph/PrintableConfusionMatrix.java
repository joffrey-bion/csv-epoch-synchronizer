package org.hildan.waterloo.ces.mains.phone_vs_actigraph;

import java.util.LinkedList;

import org.hildan.utils.ai.classification.ConfusionMatrix;
import org.hildan.waterloo.ces.validation.PrintableResults;

public class PrintableConfusionMatrix extends PrintableResults {

    private final ConfusionMatrix<String> cm;

    public PrintableConfusionMatrix(ConfusionMatrix<String> cm) {
        this.cm = cm;
    }

    @Override
    public LinkedList<String> getHeaders() {
        final LinkedList<String> headers = new LinkedList<>();
        headers.add("Overall-Accuracy");
        return headers;
    }

    @Override
    public LinkedList<String> getValues() {
        final LinkedList<String> values = new LinkedList<>();
        values.add(Double.toString(cm.getCorrectRate()));
        return values;
    }

}
