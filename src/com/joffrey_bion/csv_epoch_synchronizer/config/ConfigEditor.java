package com.joffrey_bion.csv_epoch_synchronizer.config;

import java.text.ParseException;

import javax.swing.SwingUtilities;

import com.joffrey_bion.generic_guis.LookAndFeel;
import com.joffrey_bion.generic_guis.file_picker.FilePicker;
import com.joffrey_bion.generic_guis.file_picker.JFilePickersPanel;
import com.joffrey_bion.generic_guis.file_processor.JFileProcessorWindow;

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
                "XML Classifier (pocket)",  "XML Classifier (holster)"}, new String[]{});
        FilePicker[] ifps = filePickers.getInputFilePickers();
        ifps[0].addFileTypeFilter(".xml", "XML Classifier file");
        ifps[1].addFileTypeFilter(".xml", "XML Classifier file");
        final ConfigArgsPanel configArgsPanel = new ConfigArgsPanel(filePickers);
        @SuppressWarnings("serial")
        JFileProcessorWindow frame = new JFileProcessorWindow("Config Editor", "Save",
                filePickers, configArgsPanel) {
            @Override
            public void process(String[] inPaths, String[] outPaths) {
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
