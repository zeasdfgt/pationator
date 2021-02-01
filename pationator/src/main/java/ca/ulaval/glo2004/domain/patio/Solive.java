package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.mesh.Point3D;

import java.io.Serializable;

public class Solive extends Planche implements Serializable {

    public Solive(float longueur, TypeBois typeBois, Point3D position, Orientation orientation) {
        super(longueur, typeBois, position, orientation);
    }

    @Override
    public String toString() { return "Solive" + super.toString(); }
}
