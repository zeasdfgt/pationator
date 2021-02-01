package ca.ulaval.glo2004.domain.mesh;

import java.io.Serializable;
import java.util.ArrayList;

public class Mesh implements Cloneable, Serializable {
    private final ArrayList<Triangle> triangles;
    private final ArrayList<Point3D> points;
    public String nomFichier = null;

    public Point3D getDimensions() {
        return dimensions;
    }

    private final Point3D dimensions;
    private Point3D decalage;
    private Point3D scaling;
    private Point3D rotation;

    public Mesh(ArrayList<Point3D> points, ArrayList<Triangle> triangles, Point3D dimensions) {
        this.triangles = triangles;
        this.points = points;
        this.dimensions = dimensions;
    }

    public void setTypeAccessoire(String typeAccessoire) {
        this.nomFichier = typeAccessoire;
    }

    public Mesh(ArrayList<Point3D> points, ArrayList<Triangle> triangles, Point3D dimensions, String nomFichier, Point3D scaling, Point3D decalage, Point3D rotation) {
        ArrayList<Triangle> trianglesCopie = new ArrayList<>();
        for (Triangle triangle : triangles) {
            trianglesCopie.add(triangle.clone());
        }
        this.triangles = trianglesCopie;
        this.points = (ArrayList<Point3D>) points.clone();
        this.nomFichier = nomFichier;
        this.dimensions = new Point3D(dimensions);
        this.scaling = new Point3D(scaling);
        this.decalage = new Point3D(decalage);
        this.rotation = new Point3D(rotation);
    }

    public Mesh(Mesh mesh) {
        this(mesh.points, mesh.triangles, mesh.dimensions, mesh.nomFichier, mesh.scaling, mesh.decalage, mesh.rotation);
    }

    public ArrayList<Point3D> getPoints() {
        return this.points;
    }

    public ArrayList<Triangle> getTriangles() {
        return this.triangles;
    }

    @Override
    public Mesh clone(){
        return new Mesh(this.points, this.triangles, this.dimensions, this.nomFichier, this.scaling, this.decalage, this.rotation);
    }

    public Point3D getDecalage() {
        return this.decalage;
    }

    public void setDecalage(Point3D decalage) {
        this.decalage = decalage;
    }

    public Point3D getScaling() {
        return this.scaling;
    }

    public Point3D getRotation() {
        return rotation;
    }

    public void setScaling(Point3D scaling) { this.scaling = scaling; }

    public void setRotation(Point3D rotation) { this.rotation = rotation; }
}

