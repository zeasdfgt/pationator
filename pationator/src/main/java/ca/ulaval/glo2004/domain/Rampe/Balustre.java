package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.patio.Planche;
import ca.ulaval.glo2004.domain.patio.TypeBois;
import ca.ulaval.glo2004.domain.mesh.Point3D;

import java.io.Serializable;

public class Balustre extends Planche implements Serializable, Cloneable {

    public Balustre(float longueur, TypeBois typeBois, Point3D position) {
        super(longueur, typeBois, position, Orientation.PARALZ);
    }

    public Balustre(Balustre balustreclone) {
        super(balustreclone.getPlancheLongueur(), balustreclone.getTypebois(), balustreclone.position, Orientation.PARALZ);

    }
    @Override
    public String toString() { return "Balustre" + super.toString(); }

    @Override
    protected Balustre clone()  {
        return new Balustre(this);
    }
}
