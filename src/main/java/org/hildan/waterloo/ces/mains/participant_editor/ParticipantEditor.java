package org.hildan.waterloo.ces.mains.participant_editor;

import java.io.IOException;
import java.text.ParseException;

import javax.swing.SwingUtilities;

import org.hildan.utils.fpgui.LookAndFeel;
import org.hildan.utils.fpgui.gwindows.JFileEditorWindow;
import org.hildan.utils.xml.serialization.parameters.SpecificationNotMetException;
import org.hildan.waterloo.ces.participant.Participant;
import org.xml.sax.SAXException;

public class ParticipantEditor {

    /**
     * Choose between GUI or console version according to the number of arguments.
     *
     * @param args
     *            No arguments will start the GUI, otherwise 3 filenames have to be specified:
     *            decision tree, source dataset, then destination.
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
        final JFileEditorWindow frame = new JFileEditorWindow("Participant Editor", pap) {

            @Override
            public void saveToFile(String filePath) {
                clearLog();
                try {
                    final Participant p = new Participant();
                    pap.getParameters(p);
                    p.saveToXml(filePath);
                    System.out.println("Participant successfully saved.");
                } catch (final IOException e) {
                    System.err.println(e.getMessage());
                } catch (final ParseException e) {
                    System.err.println(e.getMessage());
                } catch (final SpecificationNotMetException e) {
                    System.err.println(e.getMessage());
                }
            }

            @Override
            public void loadFromFile(String filePath) {
                clearLog();
                try {
                    final Participant p = new Participant();
                    p.loadFromXml(filePath);
                    pap.setParameters(p);
                    System.out.println("Participant successfully loaded.");
                } catch (final IOException e) {
                    System.err.println(e.getMessage());
                } catch (final SAXException e) {
                    System.err.println(e.getMessage());
                } catch (final SpecificationNotMetException e) {
                    System.err.println(e.getMessage());
                }
            }
        };
        frame.pack();
        frame.setVisible(true);
    }
}
