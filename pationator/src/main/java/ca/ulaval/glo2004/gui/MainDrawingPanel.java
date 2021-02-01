package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domain.mesh.Point3D;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MainDrawingPanel extends JPanel {

    private MainWindow mainWindow;
    private ArrayList<SousDrawingPanel> drawingPanels = new ArrayList<>();
    private boolean premiereFois = true;
    private boolean pictureMode = false;
    private boolean modeNoel = false;

    private Color couleurArrierePlan = new Color(80, 83, 104);
    private Color couleurContour = Color.black;
    final Timer timer = new Timer(1, e -> repaint());

    public MainDrawingPanel() {
        this.setBackground(Color.BLACK);
    }

    public void changeNbFenetres(int nouveauNbFenetres) {

        Dimension dimension = this.getSize();
        int nombreY, nombreX;

        double test = Math.sqrt(nouveauNbFenetres);
        if (Math.round(test) == test) {
            nombreX = (int) test;
            nombreY = (int) test;
        } else {
            int test2 = nouveauNbFenetres += nouveauNbFenetres % 2;
            nombreX = 2;
            nombreY = test2 / 2;
        }

        removeAll();
        drawingPanels = new ArrayList<>();

        this.setLayout(new GridLayout(nombreY, nombreX));
        Point3D angles = new Point3D();

        for (int i = 0; i < nouveauNbFenetres; i++) {
            switch (i) {
                case 0:
                    angles = new Point3D(180, 0, 0);
                    break;
                case 1:
                    angles = new Point3D(90, 0, 0);
                    break;
                case 2:
                    angles = new Point3D(90, 0, 90);
                    break;
                case 3:
                    angles = new Point3D(125, 0, 45);
                default:
                    break;
            }
            int nbX;
            if (nombreX > 1) {
                nbX = 2;
            }
            else {
                nbX = 1;
            }

            SousDrawingPanel sousDrawingPanel = new SousDrawingPanel(dimension, nbX, nombreY, mainWindow.getController(), mainWindow, i, angles, couleurArrierePlan);
            sousDrawingPanel.setOpaque(true);
            sousDrawingPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            sousDrawingPanel.setCouleurContour(couleurContour);
            sousDrawingPanel.recentrer();
            sousDrawingPanel.setModeNoel(modeNoel);
            drawingPanels.add(sousDrawingPanel);
            this.add(sousDrawingPanel);
        }
        revalidate();
        this.repaint();
    }


    @Override
    protected void paintComponent(Graphics g) {

        if (mainWindow != null) {
            if (premiereFois) {
                changeNbFenetres(4);
                premiereFois = false;
            }

            super.paintComponent(g);
            for (SousDrawingPanel sousDrawingPanel : drawingPanels) {
                sousDrawingPanel.setPictureMode(pictureMode);
                sousDrawingPanel.repaint();
            }
        }
    }

    public void setMainWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public void setPictureMode(boolean pictureMode) { this.pictureMode = pictureMode; }

    public void setCouleurArrierePlan(Color couleurArrierePlan) {
        this.couleurArrierePlan = couleurArrierePlan;
        for (SousDrawingPanel sousDrawingPanel : drawingPanels) {
            sousDrawingPanel.setBackground(couleurArrierePlan);
        }
        repaint();
    }

    public void setCouleurContour(Color couleur) {
        this.couleurContour = couleur;
        for (SousDrawingPanel sousDrawingPanel : drawingPanels) { sousDrawingPanel.setCouleurContour(couleurContour); }
        repaint();
    }

    public void updateDrawingPanels() {
        for (SousDrawingPanel sousDrawingPanel : drawingPanels) { sousDrawingPanel.update(); }
    }


    public void setModeNoel(boolean selected) {
        if (selected) {
            timer.start();
            for (SousDrawingPanel sousDrawingPanel : drawingPanels) { sousDrawingPanel.setModeNoel(true); }
        }
        else {
            timer.stop();
            for (SousDrawingPanel sousDrawingPanel : drawingPanels) { sousDrawingPanel.setModeNoel(false); }
        }
        this.modeNoel = selected;
    }

    public void setLocked(boolean locked) {
        for (SousDrawingPanel sousDrawingPanel : drawingPanels) { sousDrawingPanel.setLocked(locked); }
    }
}