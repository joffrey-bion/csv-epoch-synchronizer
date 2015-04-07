package org.hildan.waterloo.ces.mains.phone_vs_k4b2;

import java.util.HashMap;
import java.util.LinkedList;

import org.hildan.waterloo.ces.k4b2.Phase;
import org.hildan.waterloo.ces.validation.PrintableResults;

public class PrintableAccuracies extends PrintableResults {

    private final HashMap<Phase, Accuracy> accuracies;

    public PrintableAccuracies(HashMap<Phase, Accuracy> accuracies) {
        this.accuracies = accuracies;
    }

    @Override
    public LinkedList<String> getHeaders() {
        final LinkedList<String> headers = new LinkedList<>();
        for (final Phase p : Phase.values()) {
            headers.addAll(accuracies.get(p).getHeaders(p.toString() + "-"));
        }
        return headers;
    }

    @Override
    public LinkedList<String> getValues() {
        final LinkedList<String> values = new LinkedList<>();
        for (final Phase p : Phase.values()) {
            values.addAll(accuracies.get(p).getValues());
        }
        return values;
    }

}
