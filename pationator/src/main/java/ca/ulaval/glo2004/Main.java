package ca.ulaval.glo2004;

import ca.ulaval.glo2004.gui.Accueil;
import ca.ulaval.glo2004.gui.MainWindow;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Main extends JFrame {

    public static void main(String[] args) throws Exception {

        UIManager.setLookAndFeel(new NimbusLookAndFeel());

        final MainWindow[] mainWindows = { new MainWindow() };
        mainWindows[0].setExtendedState(mainWindows[0].getExtendedState() | JFrame.MAXIMIZED_BOTH);

        class CustomComponentListener implements ComponentListener {
                @Override
                public void componentResized(ComponentEvent e) {
                    mainWindows[0].drawingPanel.updateDrawingPanels();
                }

                @Override
                public void componentMoved(ComponentEvent e) { }

                @Override
                public void componentShown(ComponentEvent e) { }

                @Override
                public void componentHidden(ComponentEvent e) { }
        }

        class CustomWindowListener implements WindowListener {

            @Override
            public void windowOpened(WindowEvent windowEvent) { }

            @Override
            public void windowClosing(WindowEvent windowEvent) {
            }

            @Override
            public void windowClosed(WindowEvent windowEvent) {
                try {
                    Dimension dimensions = mainWindows[0].getSize();
                    Point location = mainWindows[0].getLocation();
                    mainWindows[0] = new MainWindow();
                    mainWindows[0].addWindowListener(new CustomWindowListener());
                    mainWindows[0].setVisible(true);
                    mainWindows[0].addComponentListener(new CustomComponentListener());
                    mainWindows[0].setSize(dimensions);
                    mainWindows[0].setLocation(location);
                    mainWindows[0].validate();
                    mainWindows[0].drawingPanel.changeNbFenetres(4);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void windowIconified(WindowEvent windowEvent) { }

            @Override
            public void windowDeiconified(WindowEvent windowEvent) { }

            @Override
            public void windowActivated(WindowEvent windowEvent) { }

            @Override
            public void windowDeactivated(WindowEvent windowEvent) { }
        }

        mainWindows[0].addWindowListener(new CustomWindowListener());
        mainWindows[0].addComponentListener(new CustomComponentListener());

        Accueil accueil = new Accueil(mainWindows[0]);
        accueil.setVisible(true);
    }
}
