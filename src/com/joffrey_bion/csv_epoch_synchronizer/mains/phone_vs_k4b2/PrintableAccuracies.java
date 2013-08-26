package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.util.HashMap;
import java.util.LinkedList;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Phase;
import com.joffrey_bion.csv_epoch_synchronizer.validation.PrintableResults;

public class PrintableAccuracies extends PrintableResults {

    private HashMap<Phase, Accuracy> accuracies;

    public PrintableAccuracies(HashMap<Phase, Accuracy> accuracies) {
        this.accuracies = accuracies;
    }

    @Override
    public LinkedList<String> getHeaders() {
        LinkedList<String> headers = new LinkedList<>();
        for (Phase p : Phase.values()) {
            headers.addAll(accuracies.get(p).getHeaders(p.toString() + "-"));
        }
        return headers;
    }

    @Override
    public LinkedList<String> getValues() {
        LinkedList<String> values = new LinkedList<>();
        for (Phase p : Phase.values()) {
            values.addAll(accuracies.get(p).getValues());
        }
        return values;
    }

}
