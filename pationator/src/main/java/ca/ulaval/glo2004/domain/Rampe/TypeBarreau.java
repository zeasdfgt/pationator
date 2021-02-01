package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.mesh.ImporterSTL;
import ca.ulaval.glo2004.domain.mesh.Mesh;
import ca.ulaval.glo2004.domain.mesh.Point3D;

import java.io.Serializable;

public enum TypeBarreau implements Serializable {
    MODELE1(new ImporterSTL("barreau7").importation(new Point3D(1, 1, 1))),
    MODELE2(new ImporterSTL("barreau4").importation(new Point3D(1, 1, 1))),
    MODELE3(new ImporterSTL("barreau5").importation(new Point3D(1, 1, 1))),
    MODELE4(new ImporterSTL("barreau8").importation(new Point3D(1, 1, 1))),
    MODELE5(new ImporterSTL("barreau9").importation(new Point3D(1, 1, 1))),
    MODELE6(new ImporterSTL("barreau10").importation(new Point3D(1, 1, 1))),
    MODELE7(new ImporterSTL("barreau11").importation(new Point3D(1, 1, 1))),;

    private final Mesh mesh;

    TypeBarreau(Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return mesh.clone();
    }

    public Point3D getDimensions() {
        return mesh.getDimensions();
    }
}
