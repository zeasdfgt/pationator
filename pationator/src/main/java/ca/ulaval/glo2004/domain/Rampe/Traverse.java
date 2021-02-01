package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.patio.Planche;
import ca.ulaval.glo2004.domain.patio.TypeBois;
import ca.ulaval.glo2004.domain.mesh.Point3D;

import java.io.Serializable;

public class Traverse extends Planche implements Serializable {
    public Traverse(float longueur, TypeBois typeBois, Point3D position, Orientation orientation) {
        super(longueur, typeBois, position, orientation);
    }

    @Override
    public String toString() { return "Traverse" + super.toString(); }
}
