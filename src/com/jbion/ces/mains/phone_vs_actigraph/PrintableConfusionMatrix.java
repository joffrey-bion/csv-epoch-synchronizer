package com.jbion.ces.mains.phone_vs_actigraph;

import java.util.LinkedList;

import com.jbion.ces.validation.PrintableResults;
import com.jbion.utils.classification.ConfusionMatrix;

public class PrintableConfusionMatrix extends PrintableResults {

    private ConfusionMatrix<String> cm;

    public PrintableConfusionMatrix(ConfusionMatrix<String> cm) {
        this.cm = cm;
    }

    @Override
    public LinkedList<String> getHeaders() {
        LinkedList<String> headers = new LinkedList<>();
        headers.add("Overall-Accuracy");
        return headers;
    }

    @Override
    public LinkedList<String> getValues() {
        LinkedList<String> values = new LinkedList<>();
        values.add(Double.toString(cm.getCorrectRate()));
        return values;
    }
    
    
}
