package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.domain.patio.Planche;
import ca.ulaval.glo2004.domain.patio.TypeBois;

import java.io.Serializable;

public class Marche extends Planche implements Serializable {

    public Marche(float longueur, TypeBois typeBois, Point3D position, Orientation orientation) {
        super(longueur, typeBois, position, orientation);
    }

    @Override
    public String toString() { return "Marche" + super.toString(); }
}
