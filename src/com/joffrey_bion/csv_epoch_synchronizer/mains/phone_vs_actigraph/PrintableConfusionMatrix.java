package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_actigraph;

import java.util.LinkedList;

import com.joffrey_bion.csv_epoch_synchronizer.validation.PrintableResults;
import com.joffrey_bion.utils.classification.ConfusionMatrix;

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
