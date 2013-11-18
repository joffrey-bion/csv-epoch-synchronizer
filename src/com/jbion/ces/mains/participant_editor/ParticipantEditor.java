package com.jbion.ces.mains.participant_editor;

import java.io.IOException;
import java.text.ParseException;

import javax.swing.SwingUtilities;

import org.xml.sax.SAXException;

import com.jbion.ces.participant.Participant;
import com.jbion.utils.fpgui.LookAndFeel;
import com.jbion.utils.fpgui.gwindows.JFileEditorWindow;
import com.jbion.utils.xml.serialization.parameters.SpecificationNotMetException;

public class ParticipantEditor {

    /**
     * Choose between GUI or console version according to the number of arguments.
     * 
     * @param args
     *            No arguments will start the GUI, otherwise 3 filenames have to be
     *            specified: decision tree, source dataset, then destination.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                openWindow();
            }
        });
    }

    /**
     * Starts the GUI.
     */
    private static void openWindow() {
        LookAndFeel.setSystemLookAndFeel();
        final PEArgsPanel pap = new PEArgsPanel();
        @SuppressWarnings("serial")
        JFileEditorWindow frame = new JFileEditorWindow("Participant Editor", pap) {
            @Override
            public void saveToFile(String filePath) {
                clearLog();
                try {
                    Participant p = new Participant();
                    pap.getParameters(p);
                    p.saveToXml(filePath);
                    System.out.println("Participant successfully saved.");
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                } catch (SpecificationNotMetException e) {
                    System.err.println(e.getMessage());
                }
            }

            @Override
            public void loadFromFile(String filePath) {
                clearLog();
                try {
                    Participant p = new Participant();
                    p.loadFromXml(filePath);
                    pap.setParameters(p);
                    System.out.println("Participant successfully loaded.");
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                } catch (SAXException e) {
                    System.err.println(e.getMessage());
                } catch (SpecificationNotMetException e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }
}
