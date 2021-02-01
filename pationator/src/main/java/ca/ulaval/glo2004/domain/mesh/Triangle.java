package ca.ulaval.glo2004.domain.mesh;

import ca.ulaval.glo2004.domain.drawing.Matrice;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Triangle implements Cloneable, Serializable {

    private final ArrayList<Point3D> points = new ArrayList<>(3);
    private Color couleur;

    public Triangle(Point3D p1, Point3D p2, Point3D p3) {
        this.points.add(p1);
        this.points.add(p2);
        this.points.add(p3);
    }

    public Triangle(Triangle triangle) {
        this.points.add(new Point3D(triangle.points.get(0)));
        this.points.add(new Point3D(triangle.points.get(1)));
        this.points.add(new Point3D(triangle.points.get(2)));
        this.couleur = triangle.couleur;
    }

    public ArrayList<Point3D> getPoints() {
        return points;
    }

    public Color getCouleur() {
        return couleur;
    }

    public static Triangle convertirVersEcran(Triangle triangle, Matrice matProj, Matrice matWorld, Color couleur, Point3D decalage, float zoom, boolean wireframe, boolean transparenceActivee) {

        Point3D p1 = Point3D.convertirVersEcran(triangle.points.get(0), matProj, matWorld, decalage, zoom);
        Point3D p2 = Point3D.convertirVersEcran(triangle.points.get(1), matProj, matWorld, decalage, zoom);
        Point3D p3 = Point3D.convertirVersEcran(triangle.points.get(2), matProj, matWorld, decalage, zoom);

        Point3D normal = new Point3D(), directionCamera, line1 = new Point3D(), line2 = new Point3D();

        line1.soustraction(p2, p1);
        line2.soustraction(p3, p1);
        normal.produitVectoriel(line1, line2);
        directionCamera = Point3D.normaliser(new Point3D(0, 0, -10.0f));
        float dansCamera = Point3D.produitScalaire(directionCamera, normal);

        if (!transparenceActivee && !wireframe && dansCamera < 0) {
            return null;
        }

        Triangle triangleProjete = new Triangle(p1, p2, p3);

        // Calculer la normale du triangle
        normal = Point3D.normaliser(new Point3D(normal));

        // Illumination
        Point3D directionLumiere;
        directionLumiere = Point3D.normaliser(new Point3D(3f, -5f, -10.0f));

        // Calculer l'intensité de la luminosité
        float dp = Math.max(0.1f, Point3D.produitScalaire(directionLumiere, normal));

        int transparence = couleur.getAlpha();
        float[] hsbVals = Color.RGBtoHSB(couleur.getRed(), couleur.getGreen(), couleur.getBlue(), null);
        couleur = Color.getHSBColor(hsbVals[0], hsbVals[1], 0.5f * (dp + hsbVals[2]));
        couleur = new Color(couleur.getRed(), couleur.getGreen(), couleur.getBlue(), transparence);

        triangleProjete.couleur = couleur;

        return triangleProjete;

    }

    public boolean pointEstDansTriangle(Point3D souris, Point3D v1, Point3D v2, Point3D v3) {
        float aire = 0.5f * (-v2.getY() * v3.getX() + v1.getY() * (-v2.getX() + v3.getX()) + v1.getX() * (v2.getY() - v3.getY()) + v2.getX() * v3.getY());
        float s = 1 / (2 * aire) * (v1.getY() * v3.getX() - v1.getX() * v3.getY() + (v3.getY() - v1.getY()) * souris.getX() + (v1.getX() - v3.getX()) * souris.getY());
        float t = 1 / (2 * aire) * (v1.getX() * v2.getY() - v1.getY() * v2.getX() + (v1.getY() - v2.getY()) * souris.getX() + (v2.getX() - v1.getX()) * souris.getY());

        return s >= 0 && t >= 0 && 1 - s - t >= 0;
    }

    @Override
    public Triangle clone() {
        return new Triangle(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triangle triangle = (Triangle) o;
        return Objects.equals(points, triangle.points) &&
                Objects.equals(couleur, triangle.couleur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(points, couleur);
    }

    public void setCouleur(Color couleur) {
        this.couleur = couleur;
    }

}