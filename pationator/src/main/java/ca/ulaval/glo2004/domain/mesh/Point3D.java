package ca.ulaval.glo2004.domain.mesh;

import ca.ulaval.glo2004.domain.drawing.Matrice;

import java.io.Serializable;
import java.util.Objects;

public class Point3D implements Cloneable, Serializable {
    private float x;
    private float y;
    private float z;
    private float w = 1;

    public Point3D(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Point3D() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Point3D(Point3D p) {
        this.x = p.x;
        this.y = p.y;
        this.z = p.z;
        this.w = p.w;
    }

    @Override
    public Point3D clone() throws CloneNotSupportedException {
        return (Point3D) super.clone();
    }

    public static Point3D convertirVersEcran(Point3D p,  Matrice matProj, Matrice matWorld, Point3D decalage, float zoom) {
        Point3D nouveauPoint = new Point3D();

        // transformations
        nouveauPoint.matriceFoisVecteur(matWorld, p);
        // Projection
        nouveauPoint.matriceFoisVecteur(matProj, nouveauPoint);

        // Décalage
        nouveauPoint.additionner(nouveauPoint, decalage);

        nouveauPoint.multiplier(nouveauPoint, zoom);
        nouveauPoint.setX((int) nouveauPoint.getX());
        nouveauPoint.setY((int) nouveauPoint.getY());
        nouveauPoint.setZ((int) nouveauPoint.getZ());
        return nouveauPoint;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public void soustraction(Point3D v1, Point3D v2) {

        this.x = v1.x - v2.x;
        this.y = v1.y - v2.y;
        this.z = v1.z - v2.z;
    }

    public void produitVectoriel(Point3D v1, Point3D v2) {
        this.x = v1.y * v2.z - v1.z * v2.y;
        this.y = v1.z * v2.x - v1.x * v2.z;
        this.z = v1.x * v2.y - v1.y * v2.x;
    }

    public static Point3D normaliser(Point3D v) {
        float l = longueur(v);
        if (l != 0) {
            return new Point3D(v.x / l, v.y / l, v.z / l);
        }
        return v;
    }


    public static Float produitScalaire(Point3D v1, Point3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static Float longueur(Point3D v) {
        return (float) Math.sqrt(produitScalaire(v, v));
    }


    public void multiplier(Point3D v1, float k) {

        this.x = v1.x * k;
        this.y = v1.y * k;
        this.z = v1.z * k;
    }

    public void additionner(Point3D v1, Point3D v2) {

        this.x = v1.x + v2.x;
        this.y = v1.y + v2.y;
        this.z = v1.z + v2.z;
    }

    public void matriceFoisVecteur(Matrice m, Point3D p) {

        this.x = p.x * m.getElements()[0][0] + p.y * m.getElements()[1][0] + p.z * m.getElements()[2][0] + p.w * m.getElements()[3][0];
        this.y = p.x * m.getElements()[0][1] + p.y * m.getElements()[1][1] + p.z * m.getElements()[2][1] + p.w * m.getElements()[3][1];
        this.z = p.x * m.getElements()[0][2] + p.y * m.getElements()[1][2] + p.z * m.getElements()[2][2] + p.w * m.getElements()[3][2];
        this.w = p.x * m.getElements()[0][3] + p.y * m.getElements()[1][3] + p.z * m.getElements()[2][3] + p.w * m.getElements()[3][3];
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point3D point3D = (Point3D) o;
        return Float.compare(point3D.x, x) == 0 &&
            Float.compare(point3D.y, y) == 0 &&
            Float.compare(point3D.z, z) == 0 &&
            Float.compare(point3D.w, w) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, w);
    }
}
