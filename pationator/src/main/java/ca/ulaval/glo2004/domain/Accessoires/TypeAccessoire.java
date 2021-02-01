package ca.ulaval.glo2004.domain.Accessoires;

import ca.ulaval.glo2004.domain.mesh.ImporterSTL;
import ca.ulaval.glo2004.domain.mesh.Mesh;
import ca.ulaval.glo2004.domain.mesh.Point3D;

public enum TypeAccessoire {
    MAISON(new ImporterSTL("maison").importation(new Point3D(40, 40, 40))),
    BBQ(new ImporterSTL("BBQ").importation(new Point3D(15, 15, 15))),
    SAPIN(new ImporterSTL("sapin").importation(new Point3D(25, 25, 25))),
    TABLE(new ImporterSTL("table").importation(new Point3D(40, 40, 40))),
    VASE(new ImporterSTL("vase").importation(new Point3D(5, 5, 5))),
    CHAISE(new ImporterSTL("chaise").importation(new Point3D(3, 3, 3))),
    CHIEN(new ImporterSTL("chien").importation(new Point3D(6, 6, 6))),
    PLANTE(new ImporterSTL("plantepot").importation(new Point3D(12, 12, 12)));

    private final Mesh mesh;

    TypeAccessoire(Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return mesh.clone();
    }

    public Point3D getDimensions() {
        return mesh.getDimensions();
    }
}
