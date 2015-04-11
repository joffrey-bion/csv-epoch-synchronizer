package org.hildan.waterloo.ces.mains.phone_vs_actigraph;

import java.io.IOException;

import org.hildan.utils.ai.classification.ConfusionMatrix;
import org.hildan.utils.csv.CsvReader;

public class PvAAnalyzer {

    public static ConfusionMatrix<String> analyzeClassification(ClassificationAnalysisParams params) throws IOException {
        final CsvReader reader = new CsvReader(params.getTwoLabeledFile());
        // write new columns headers
        final String[] headers = reader.readRow();
        int truthIndex = -1;
        int classificationIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(params.getHeaderTruth())) {
                truthIndex = i;
            } else if (headers[i].equals(params.getHeaderClassifiedAs())) {
                classificationIndex = i;
            }
        }
        final ConfusionMatrix<String> cm = new ConfusionMatrix<>(params.getLevels());
        String[] line;
        while ((line = reader.readRow()) != null) {
            cm.add(line[truthIndex], line[classificationIndex]);
        }
        reader.close();
        return cm;
    }
}
