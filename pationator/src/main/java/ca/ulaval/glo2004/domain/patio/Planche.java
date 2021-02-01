package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.domain.Controller;
import ca.ulaval.glo2004.domain.Materiel;
import ca.ulaval.glo2004.domain.Rampe.TypeBarreau;
import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.domain.mesh.Mesh;
import ca.ulaval.glo2004.domain.mesh.PrismeFactory;

import java.io.Serializable;

public abstract class Planche implements Serializable {
    private final float longueur;
    private final TypeBois type;
    private final Mesh mesh;
    public final Point3D position;

    public Planche(float longueur, TypeBois typeBois, Point3D position, Orientation orientation) {
        this.position = position;
        this.longueur = longueur;
        this.type = typeBois;
        PrismeFactory prismeFactory = new PrismeFactory();
        this.mesh = prismeFactory.createPrisme(position, new Point3D(longueur, typeBois.getLargeur().getValeurReelle(), typeBois.getHauteur().getValeurReelle()), orientation);
    }

    public Planche(float longueur, float largeur, float hauteur, TypeBois typeBois, Point3D position, Orientation orientation) {
        this.position = position;
        this.longueur = longueur;
        this.type = typeBois;
        PrismeFactory prismeFactory = new PrismeFactory();
        this.mesh = prismeFactory.createPrisme(position, new Point3D(longueur, largeur, hauteur), orientation);
    }

    public Planche(float longueur, TypeBarreau typeBarreau, Point3D position, boolean optimisation) {
        this.position = position;
        this.longueur = longueur;
        this.type = new TypeBois("1X1");
        if (!optimisation) {
            this.mesh = typeBarreau.getMesh();
            float ratioz = longueur / (2 * mesh.getDimensions().getZ());
            float ratioxy = 2 / (2 * mesh.getDimensions().getY());
            Point3D scaling = new Point3D(ratioxy, ratioxy, ratioz);
            Point3D rotation = new Point3D(0, 0, 0);
            Controller.modifierMesh(mesh, scaling, position, rotation);
        }
        else {
            this.mesh = null;
        }
    }

    public float getPlancheLongueur() {
        return this.longueur;
    }

    public Mesh getPlanchePrisme() {
        return this.mesh;
    }

    @Override
    public String toString() { return " " + type.toString() + " de longueur " + Materiel.floatToStringFraction(longueur); }

    public TypeBois getTypebois() {
        return type;
    }
}
