package ca.ulaval.glo2004.domain.drawing;

import ca.ulaval.glo2004.domain.Controller;
import ca.ulaval.glo2004.domain.patio.*;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.gui.SousDrawingPanel;
import ca.ulaval.glo2004.gui.dto.AffichageDTO;

import java.awt.*;
import java.util.ArrayList;

public class MainDrawer {

    private final Controller controller;
    private final PieceDrawerFactory pieceDrawerFactory;
    private Dimension initialDimension;
    private PieceDrawer pieceDrawer;
    private boolean drawn = false;


    public MainDrawer(Controller controller, Dimension initialDimension) {
        this.controller = controller;
        this.pieceDrawerFactory = new PieceDrawerFactory();
        this.initialDimension = initialDimension;
    }

    private void dessinerInfoPiece(Graphics g, Planche planche, Point souris) {

        String texte = planche.toString();
        int fontSize = 16;
        int width = texte.length() * fontSize / 2;
        int height = 2 * fontSize;

        g.setColor(new Color(255, 255, 255, 200));
        g.fillRoundRect(souris.x, souris.y - height, width, height, 5, 5);
        g.setColor(Color.BLACK);
        g.setFont(new Font("helvetica", Font.PLAIN, fontSize));
        g.drawRoundRect(souris.x, souris.y - height, width, height, 5, 5);
        g.drawString(texte, souris.x + 5, souris.y - height + 3 * fontSize / 2);
    }

    public void draw(Graphics g, ArrayList<SousDrawingPanel.Flocon> flocons, boolean modeSelection, Point souris, float zoom, Point3D decalage, AffichageDTO affichageDTO, Point3D angles, boolean wireframe, boolean drawshadow, int width, Color couleurContour) {

        this.pieceDrawer = pieceDrawerFactory.getPieceDrawer(g, controller, souris, zoom, angles, decalage, affichageDTO, controller.getMeshes());
        this.drawn = pieceDrawer.dessinerTriangles(flocons, initialDimension, wireframe, modeSelection, width, couleurContour);
        Planche plancheselectionnee = controller.getPatio().getPlancheSelectionne();
        if (plancheselectionnee != null) { dessinerInfoPiece(g, plancheselectionnee, souris); }
        if (drawshadow) { dessinerOmbre(g); }
    }

    private void dessinerOmbre(Graphics g) {
        g.setColor(new Color(0, 0, 0, 30));
        g.fillRect(0, 0, 15, initialDimension.height);
        g.fillRect(initialDimension.width - 15, 0, 15, initialDimension.height);
        g.fillRect(15, 0, initialDimension.width - 30, 15);
        g.fillRect(15, initialDimension.height - 15, initialDimension.width - 30, initialDimension.height);
        g.setColor(Color.BLACK);
    }

    public boolean drawSansUpdate(Graphics g, ArrayList<SousDrawingPanel.Flocon> flocons, int width, Color couleurcontour) {
        if (pieceDrawer != null && drawn) {
            pieceDrawer.dessinerSansUpdate(g, flocons, width, couleurcontour);
            dessinerOmbre(g);
            return false;
        }
        else { return true; }
    }

    public void setSize(Dimension size) {
        this.initialDimension = size;
    }
}