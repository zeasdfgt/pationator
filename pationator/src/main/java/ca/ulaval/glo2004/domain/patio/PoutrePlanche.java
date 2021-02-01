package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.mesh.Point3D;

import java.io.Serializable;

public class PoutrePlanche extends Planche implements Serializable {

    public PoutrePlanche(float longueur, TypeBois typeBois, Point3D position, Orientation orientation) {

        super(longueur, typeBois, position, orientation);
    }

    public Point3D getPosition() {
        return getPlanchePrisme().getPoints().get(0);
    }
}
