package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.io.IOException;
import java.util.HashMap;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2StatsCalculator;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Phase;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Results;
import com.joffrey_bion.csv_epoch_synchronizer.phone.RawToEpConverter;
import com.joffrey_bion.csv_epoch_synchronizer.phone.decision.LabelAppender;
import com.joffrey_bion.xml_parameters_serializer.SpecificationNotMetException;

public class PhoneVSK4b2Analyzer {

    public static void main(String[] args) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    openWindow();
                }
            });
        } else {
            // console version, process parameter files one by one.
            for (String xmlParamsFile : args) {
                System.out.println("------[ " + xmlParamsFile + " ]---------------------");
                System.out.println();
                try {
                    PvKParams params = new PvKParams(xmlParamsFile);
                    analyze(params);
                } catch (IOException e) {
                    System.err.println("I/O error: " + e.getMessage());
                } catch (SAXException e) {
                    System.err.println("Incorrect XML file: " + e.getMessage());
                } catch (SpecificationNotMetException e) {
                    System.err.println("Incorrect parameter file: " + e.getMessage());
                }
                System.out.println();
            }
        }
    }

    /**
     * Starts the GUI.
     */
    private static void openWindow() {
        // TODO Auto-generated method stub
    }

    private static void analyze(PvKParams params) {
        try {
            K4b2StatsCalculator k4 = new K4b2StatsCalculator(params.k4b2File);
            Results res = k4.getStats(params.nbSyncMarkers);
            PhasePhoneParams ppp = new PhasePhoneParams(params.delayPhoneToK4b2);
            for (Phase p : Phase.values()) {
                ppp.setPhaseResults(res.get(p));
                String tempFile = "temp-" + p + ".csv";
                RawToEpConverter converter = new RawToEpConverter(params.phoneRawFile, tempFile);
                converter.createEpochsFile(ppp);
                String tempFile2 = "temp-" + p + "-labeled.csv";
                LabelAppender appender = new LabelAppender(params.xmlTreeFile);
                HashMap<String, Integer> lvlsDistrib = appender.appendLabels(tempFile, tempFile2);
                System.out.println(lvlsDistrib);
                // TODO actually compare differences between levels' distributions
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (SAXException e) {
            System.err.println("Incorrect decision tree file: " + e.getMessage());
        }
    }

}
