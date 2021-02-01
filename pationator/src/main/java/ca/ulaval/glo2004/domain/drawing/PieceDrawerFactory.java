package ca.ulaval.glo2004.domain.drawing;

import ca.ulaval.glo2004.domain.Controller;
import ca.ulaval.glo2004.domain.mesh.Mesh;
import ca.ulaval.glo2004.domain.patio.Patio;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.gui.dto.AffichageDTO;

import java.awt.*;
import java.util.ArrayList;

public class PieceDrawerFactory {

    public PieceDrawer getPieceDrawer(Graphics g, Controller controller, Point souris, float zoom, Point3D angles, Point3D decalage, AffichageDTO affichageDTO, ArrayList<Mesh> meshes) {
        Patio patio = controller.getPatio();
        Point3D angles2 = new Point3D((float) ((angles.getX() * Math.PI) / 180f), (float) ((angles.getY() * Math.PI) / 180f), (float) ((angles.getZ() * Math.PI) / 180f));
        MatriceFactory matriceFactory = new MatriceFactory();
        Point3D milieuPatio = new Point3D(patio.getLongueur() / 2, patio.getLargeur() / 2, patio.getHauteur() / 2);
        Matrice matProj = MatriceFactory.creerProjecteurPerspective();
        Point3D translation = new Point3D(-milieuPatio.getX(), -milieuPatio.getY(), -milieuPatio.getZ());
        Matrice matWorld = matriceFactory.creerWorldMatrice(translation, angles2);
        return new PieceDrawer(g, controller, souris, affichageDTO, matProj, matWorld, decalage, zoom, meshes);
    }
}
