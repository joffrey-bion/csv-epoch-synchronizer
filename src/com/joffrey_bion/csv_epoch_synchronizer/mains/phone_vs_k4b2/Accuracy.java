package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.util.HashMap;

public class Accuracy {

    private HashMap<String, Double> perLvlAccuracy;
    private double overallAccuracy;

    public Accuracy(HashMap<String, Double> phoneDistrib, HashMap<String, Double> k4b2Distrib) {
        if (!phoneDistrib.keySet().equals(k4b2Distrib.keySet())) {
            throw new IllegalArgumentException("The 2 distributions must contain the same levels.");
        }
        perLvlAccuracy = new HashMap<>();
        double totalSum = 0;
        double minSum = 0;
        for (String level : phoneDistrib.keySet()) {
            double phone = phoneDistrib.get(level);
            double k4b2 = k4b2Distrib.get(level);
            totalSum += k4b2;
            minSum += Math.min(phone, k4b2);
            double acc = Math.min(phone, k4b2) / Math.max(phone, k4b2);
            perLvlAccuracy.put(level, acc);
        }
        overallAccuracy = minSum / totalSum;
    }

    public double getOverallAccuracy() {
        return overallAccuracy;
    }

    public HashMap<String, Double> getPerLvlAccuracy() {
        return perLvlAccuracy;
    }

    @Override
    public String toString() {
        String res = "Overall accuracy: ";
        res += overallAccuracy;
        res += "\nPer level accuracy: ";
        res += perLvlAccuracy;
        return res;
    }
}
