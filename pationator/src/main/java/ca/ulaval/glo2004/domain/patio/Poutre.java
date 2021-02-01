package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.domain.mesh.Mesh;

import java.io.Serializable;
import java.util.ArrayList;

public class Poutre extends Planche implements Serializable {

    private final ArrayList<PoutrePlanche> plis;
    private final float largeur;
    private final ArrayList<Poteau> poteaux;

    public Poutre(float longueur, TypeBois typeBois, Point3D position, ArrayList<PoutrePlanche> plis, ArrayList<Poteau> poteaux, Orientation orientation) {
        super(longueur, typeBois, position, orientation);

        this.plis = plis;
        this.largeur = plis.size() * typeBois.getLargeur().getValeurReelle();
        this.poteaux = poteaux;
    }

    public ArrayList<Mesh> getPrisme() {
        ArrayList<Mesh> meshArray = new ArrayList<>();

        for (PoutrePlanche planche: this.plis) {
            meshArray.add(planche.getPlanchePrisme());
        }

        return meshArray;
    }

    public ArrayList<Poteau> getPoteaux() {
        return this.poteaux;
    }

    public float getLargeur() {
        return this.largeur;
    }

    public ArrayList<PoutrePlanche> getPlis() {
        return this.plis;
    }

    public Point3D getPosition() {
        return getPrisme().get(0).getPoints().get(0);
    }

    @Override
    public String toString() { return "Poutre composée de " + plis.size() + " plis " + super.toString(); }

    public float getHauteur() {
        return plis.get(0).getTypebois().getHauteur().getValeurReelle();
    }
}
