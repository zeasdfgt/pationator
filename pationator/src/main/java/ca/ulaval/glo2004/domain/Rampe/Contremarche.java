package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.domain.patio.Planche;
import ca.ulaval.glo2004.domain.patio.TypeBois;

import java.io.Serializable;

public class Contremarche extends Planche implements Serializable {
    public Contremarche(float longueur, float largeur, float hauteur, TypeBois typeBois, Point3D position, Orientation orientation) {
        super(longueur, largeur, hauteur, typeBois, position, orientation);
    }

    @Override
    public String toString() { return "Contremarche" + super.toString(); }
}
