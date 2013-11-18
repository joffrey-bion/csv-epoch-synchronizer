package com.jbion.ces.config;

import java.text.ParseException;

import javax.swing.SwingUtilities;

import com.jbion.utils.fpgui.LookAndFeel;
import com.jbion.utils.fpgui.fpickers.FilePicker;
import com.jbion.utils.fpgui.fpickers.JFilePickersPanel;
import com.jbion.utils.fpgui.gwindows.JFileProcessorWindow;

public class ConfigEditor {
    public static void main(String[] args) {
        if (args.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    openWindow();
                }
            });
        }
    }
    
    /**
     * Starts the GUI.
     */
    private static void openWindow() {
        LookAndFeel.setSystemLookAndFeel();
        // file pickers source and destination
        final JFilePickersPanel filePickers = new JFilePickersPanel(new String[] {
                "Classifier (pocket, gyro)",  "Classifier (holster, gyro)", 
                "Classifier (pocket, no gyro)",  "Classifier (holster, no gyro)"}, new String[]{});
        FilePicker[] ifps = filePickers.getInputFilePickers();
        ifps[0].addFileTypeFilter(".xml", "XML Classifier file");
        ifps[1].addFileTypeFilter(".xml", "XML Classifier file");
        ifps[2].addFileTypeFilter(".xml", "XML Classifier file");
        ifps[3].addFileTypeFilter(".xml", "XML Classifier file");
        final ConfigArgsPanel configArgsPanel = new ConfigArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Config Editor", filePickers,
                configArgsPanel, "Save") {
            @Override
            public void process(String[] inPaths, String[] outPaths, int processBtnIndex) {
                this.clearLog();
                try {
                    configArgsPanel.updateConfig();
                    Config.get().saveToConfigFile();
                    System.out.println("Configuration saved.");
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                } catch (Exception e) {
                    System.err.println(e.getMessage() + " (" + e.getClass().getSimpleName() + ")");
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }
}
