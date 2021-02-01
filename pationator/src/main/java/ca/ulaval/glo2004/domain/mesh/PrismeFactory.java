package ca.ulaval.glo2004.domain.mesh;


import java.util.ArrayList;
import java.util.Arrays;

public class PrismeFactory {

    public Mesh createPrisme(Point3D pointDepart, Point3D dimensions, Orientation orientation) {

        Point3D dimension2 = new Point3D();
        dimension2.matriceFoisVecteur(orientation.getMatrice(), dimensions);

        ArrayList<Point3D> points = dimensionsVersPoints(pointDepart, dimension2);
        ArrayList<Triangle> triangles = pointsVersTriangles(points);

        return new Mesh(points, triangles, dimensions);
    }

    private ArrayList<Point3D> dimensionsVersPoints(Point3D p, Point3D dim) {

        Point3D p1 = new Point3D(p.getX(), p.getY(), p.getZ() + dim.getZ());
        Point3D p2 = new Point3D(p.getX() + dim.getX(), p.getY(), p.getZ() + dim.getZ());
        Point3D p3 = new Point3D(p.getX() + dim.getX(), p.getY(), p.getZ());
        Point3D p4 = new Point3D(p.getX(), p.getY() + dim.getY(), p.getZ());
        Point3D p5 = new Point3D(p.getX(), p.getY() + dim.getY(), p.getZ() + dim.getZ());
        Point3D p6 = new Point3D(p.getX() + dim.getX(), p.getY() + dim.getY(), p.getZ() + dim.getZ());
        Point3D p7 = new Point3D(p.getX() + dim.getX(), p.getY() + dim.getY(), p.getZ());

        return new ArrayList<>(Arrays.asList(p, p1, p2, p3, p4, p5, p6, p7));

    }

    private ArrayList<Triangle> pointsVersTriangles(ArrayList<Point3D> p) {

        // Dessus
        Triangle t1 = new Triangle(p.get(0), p.get(3), p.get(2));
        Triangle t2 = new Triangle(p.get(0), p.get(2), p.get(1));

        // Devant
        Triangle t3 = new Triangle(p.get(0), p.get(1), p.get(5));
        Triangle t4 = new Triangle(p.get(0), p.get(5), p.get(4));

        // Droite
        Triangle t5 = new Triangle(p.get(0), p.get(4), p.get(7));
        Triangle t6 = new Triangle(p.get(0), p.get(7), p.get(3));

        // Derrière
        Triangle t7 = new Triangle(p.get(6), p.get(7), p.get(4));
        Triangle t8 = new Triangle(p.get(6), p.get(4), p.get(5));

        // Gauche
        Triangle t9 = new Triangle(p.get(6), p.get(2), p.get(3));
        Triangle t10 = new Triangle(p.get(6), p.get(3), p.get(7));

        // Dessous
        Triangle t11 = new Triangle(p.get(6), p.get(5), p.get(1));
        Triangle t12 = new Triangle(p.get(6), p.get(1), p.get(2));

        return new ArrayList<>(Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12));
    }
}
