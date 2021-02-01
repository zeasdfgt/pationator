package ca.ulaval.glo2004.domain.mesh;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ImporterSTL {
    private final String nomFichier;

    public ImporterSTL(String nomFichier) {
        this.nomFichier = nomFichier;
    }
    public Mesh importation(Point3D scaling) {
        ArrayList<Triangle> myArray = new ArrayList<>();
        float xmax = 0, ymax = 0, zmax = 0;

        InputStream in = getClass().getResourceAsStream("/" + nomFichier + ".stl");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        Scanner myReader = new Scanner(reader);
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine();

            if (data.charAt(0) == 'o') {
                String l1 = myReader.nextLine();
                String[] s1 = l1.split(" ");
                String l2 = myReader.nextLine();
                String[] s2 = l2.split(" ");
                String l3 = myReader.nextLine();
                String[] s3 = l3.split(" ");

                Point3D v1 = new Point3D(Float.parseFloat(s1[1]), Float.parseFloat(s1[2]), Float.parseFloat(s1[3]));
                Point3D v2 = new Point3D(Float.parseFloat(s2[1]), Float.parseFloat(s2[2]), Float.parseFloat(s2[3]));
                Point3D v3 = new Point3D(Float.parseFloat(s3[1]), Float.parseFloat(s3[2]), Float.parseFloat(s3[3]));

                Triangle t = new Triangle(v1, v2, v3);
                if (Float.parseFloat(s1[1]) > xmax) { xmax = Float.parseFloat(s1[1]); }
                if (Float.parseFloat(s1[2]) > ymax) { ymax = Float.parseFloat(s1[2]); }
                if (Float.parseFloat(s1[3]) > zmax) { zmax = Float.parseFloat(s1[3]); }
                myArray.add(t);
            }
        }
        Point3D dimensions = new Point3D(xmax, ymax, zmax);
        Mesh mesh = new Mesh(new ArrayList<>(), myArray, dimensions, nomFichier, scaling, new Point3D(0,0,0), new Point3D(0,0,0));
        mesh.setTypeAccessoire(nomFichier);
        return mesh;
    }
}
