package com.joffrey_bion.csv_epoch_synchronizer.mains.phone_vs_k4b2;

import java.io.IOException;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.joffrey_bion.csv_epoch_synchronizer.k4b2.K4b2StatsCalculator;
import com.joffrey_bion.csv_epoch_synchronizer.k4b2.Results;
import com.joffrey_bion.csv_epoch_synchronizer.phone.RawToEpConverter;
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
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
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
        K4b2StatsCalculator k4;
        try {
            k4 = new K4b2StatsCalculator(params.k4b2File);
            Results res = k4.getStats(params.nbSyncMarkers);
            new RawToEpConverter(params.phoneRawFile, "temp.csv").createEpochsFile(params);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
