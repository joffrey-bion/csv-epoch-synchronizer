package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public class Accuracy {

    private HashMap<String, Double> perLvlAccuracy;
    private double overallAccuracy;

    public Accuracy(HashMap<String, Double> phoneDistrib, HashMap<String, Double> k4b2Distrib) {
        perLvlAccuracy = new HashMap<>();
        double totalSum = 0;
        double minSum = 0;
        TreeSet<String> levels = new TreeSet<>();
        levels.addAll(phoneDistrib.keySet());
        levels.addAll(k4b2Distrib.keySet());
        for (String level : levels) {
            Double phone = phoneDistrib.get(level);
            Double k4b2 = k4b2Distrib.get(level);
            phone = phone == null ? 0 : phone;
            k4b2 = k4b2 == null ? 0 : k4b2;
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
    
    public LinkedList<String> getHeaders(String prefix) {
        LinkedList<String> headers = new LinkedList<>();
        headers.add(prefix + "OverallAccuracy");
        return headers;
    }
    
    public LinkedList<String> getValues() {
        LinkedList<String> values = new LinkedList<>();
        values.add(Double.toString(overallAccuracy));
        return values;
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
