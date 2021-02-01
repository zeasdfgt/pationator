package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.Materiel;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.domain.patio.Planche;

import java.io.Serializable;


public class Barreau extends Planche implements Serializable {

    public Barreau(float longueur, TypeBarreau typeBarreau, Point3D position, boolean optimisation) {
        super(longueur, typeBarreau, position, optimisation);
    }

    @Override
    public String toString() { return "Barreau de longueur " + Materiel.floatToStringFraction(getPlancheLongueur()); }
}
