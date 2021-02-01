package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.mesh.Point3D;

import java.io.Serializable;

public class RecouvrementPlanche extends Planche implements Serializable {

    public RecouvrementPlanche(float longueur, TypeBois typeBois, Point3D position, Orientation orientation) {
        super(longueur, typeBois, position, orientation);
    }

    public RecouvrementPlanche(float longueur, float largeur, float hauteur, TypeBois typeBois, Point3D point3D, Orientation orientation) {
        super(longueur, largeur, hauteur, typeBois, point3D, orientation);
    }

    @Override
    public String toString() { return "Planche de recouvrement" + super.toString(); }
}
