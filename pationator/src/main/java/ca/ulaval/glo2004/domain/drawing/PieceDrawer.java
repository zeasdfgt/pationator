package ca.ulaval.glo2004.domain.drawing;

import ca.ulaval.glo2004.domain.Accessoires.Accessoire;
import ca.ulaval.glo2004.domain.Controller;
import ca.ulaval.glo2004.domain.Rampe.*;
import ca.ulaval.glo2004.domain.mesh.Mesh;
import ca.ulaval.glo2004.domain.patio.Patio;
import ca.ulaval.glo2004.domain.patio.Planche;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.domain.mesh.Triangle;
import ca.ulaval.glo2004.domain.patio.Poutre;
import ca.ulaval.glo2004.domain.patio.PoutrePlanche;
import ca.ulaval.glo2004.gui.SousDrawingPanel;
import ca.ulaval.glo2004.gui.dto.AffichageDTO;

import java.awt.*;
import java.util.ArrayList;

public class PieceDrawer {

    private final Controller controller;

    private final Patio patio;
    private final ArrayList<Mesh> meshes;

    private Dimension dimension;
    private float[][] zbuffer;
    private Color[][] colorbuffer;

    private final AffichageDTO affichageDTO;
    private final Matrice matProj;
    private final Matrice matWorld;
    private final ArrayList<Triangle> triangles = new ArrayList<>();

    private float minZ = Float.MAX_VALUE;
    private final int cadre = 0;

    private final Point3D decalage;
    private final float zoom;
    private final Point souris;

    private boolean transparenceActivee = false;
    private boolean wireframe;

    private final Graphics g;


    public PieceDrawer(Graphics g, Controller controller, Point souris, AffichageDTO affichageDTO, Matrice matProj, Matrice matWorld, Point3D decalage, float zoom, ArrayList<Mesh> meshes) {
        this.affichageDTO = affichageDTO;
        this.controller = controller;
        this.patio = controller.getPatio();
        this.matProj = matProj;
        this.matWorld = matWorld;
        this.decalage = decalage;
        this.zoom = zoom;
        this.souris = souris;
        this.meshes = meshes;
        this.g = g;

        for (Color valeur : affichageDTO.couleurs.values()) {
            if (valeur.getAlpha() != 255) {
                this.transparenceActivee = true;
            }
        }
    }

    public float calculerZTriange3D(Triangle triangle, Point3D point) {
        return (triangle.getPoints().get(2).getZ() * (point.getX() - triangle.getPoints().get(0).getX()) * (point.getY() - triangle.getPoints().get(1).getY())
                + triangle.getPoints().get(0).getZ() * (point.getX() - triangle.getPoints().get(1).getX()) * (point.getY() - triangle.getPoints().get(2).getY())
                + triangle.getPoints().get(1).getZ() * (point.getX() - triangle.getPoints().get(2).getX()) * (point.getY() - triangle.getPoints().get(0).getY())
                - triangle.getPoints().get(1).getZ() * (point.getX() - triangle.getPoints().get(0).getX()) * (point.getY() - triangle.getPoints().get(2).getY())
                - triangle.getPoints().get(2).getZ() * (point.getX() - triangle.getPoints().get(1).getX()) * (point.getY() - triangle.getPoints().get(0).getY())
                - triangle.getPoints().get(0).getZ() * (point.getX() - triangle.getPoints().get(2).getX()) * (point.getY() - triangle.getPoints().get(1).getY()))
                / ((point.getX() - triangle.getPoints().get(0).getX()) * (point.getY() - triangle.getPoints().get(1).getY())
                + (point.getX() - triangle.getPoints().get(1).getX()) * (point.getY() - triangle.getPoints().get(2).getY())
                + (point.getX() - triangle.getPoints().get(2).getX()) * (point.getY() - triangle.getPoints().get(0).getY())
                - (point.getX() - triangle.getPoints().get(0).getX()) * (point.getY() - triangle.getPoints().get(2).getY())
                - (point.getX() - triangle.getPoints().get(1).getX()) * (point.getY() - triangle.getPoints().get(0).getY())
                - (point.getX() - triangle.getPoints().get(2).getX()) * (point.getY() - triangle.getPoints().get(1).getY()));
    }

    public void separerTriangle(Triangle triangle) {

        ArrayList<Point3D> points = triangle.getPoints();
        points.sort((p1, p2) -> Float.compare(p1.getY(), p2.getY()));

        // Le triangle n'a pas besoin d'être séparé en deux
        if (points.get(1).getY() == points.get(2).getY()) {
            triangleVersHaut(points.get(0), points.get(1), points.get(2), triangle, triangle.getCouleur());
        }
        // Le triangle n'a pas besoin d'être séparé en deux
        else if (points.get(0).getY() == points.get(1).getY()) {
            triangleVersBas(points.get(0), points.get(1), points.get(2), triangle, triangle.getCouleur());
        }
        // Le triangle doit être séparé en deux
        else {
            Point3D pointMilieu = new Point3D((int) (points.get(0).getX() + ((points.get(1).getY() - points.get(0).getY()) / (points.get(2).getY() - points.get(0).getY())) * (points.get(2).getX() - points.get(0).getX())), points.get(1).getY(), 1);
            triangleVersBas(points.get(1), pointMilieu, points.get(2), triangle, triangle.getCouleur());
            triangleVersHaut(points.get(0), points.get(1), pointMilieu, triangle, triangle.getCouleur());
        }
    }

    //http://www.java2s.com/Code/Java/2D-Graphics-GUI/Blendtwocolors.htm
    public Color blend(Color c0, Color c1) {
        double totalAlpha = c0.getAlpha() + c1.getAlpha();
        double weight0 = c0.getAlpha() / totalAlpha;
        double weight1 = c1.getAlpha() / totalAlpha;

        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = Math.max(c0.getAlpha(), c1.getAlpha());
        return new Color((int) r, (int) g, (int) b, (int) a);
    }

    public boolean sourisDansTriangle(Triangle triangle, Point souris) {
        if (triangle.pointEstDansTriangle(new Point3D(souris.x, souris.y, 1), triangle.getPoints().get(0), triangle.getPoints().get(1), triangle.getPoints().get(2))) {
            float z = calculerZTriange3D(triangle, new Point3D(souris.x, souris.y, 1));
            if (z < minZ) {
                minZ = z;
                patio.setPlancheSelectionnee(null);
                return true;
            }
        }
        return false;
    }

    private void changerCouleurPieceSelectionnee(ArrayList<Triangle> triangles) {

        if (patio.getPlancheSelectionne() != null) {
            if (patio.getPlancheSelectionne().getClass().equals(Poutre.class)) {
                Color couleur = affichageDTO.couleurs.get(PoutrePlanche.class);
                for (PoutrePlanche pli : ((Poutre) patio.getPlancheSelectionne()).getPlis()) {
                    for (Triangle triangle : pli.getPlanchePrisme().getTriangles()) {
                        Triangle Triproj = Triangle.convertirVersEcran(triangle, matProj, matWorld, couleur, decalage, zoom, false, transparenceActivee);
                        triangles.removeIf(triangle1 -> triangle1.equals(Triproj));
                        Triangle Triproj2 = Triangle.convertirVersEcran(triangle, matProj, matWorld, couleur.brighter(), decalage, zoom, false, transparenceActivee);
                        if (Triproj2 != null) {
                            triangles.add(Triproj2);
                        }
                    }
                }
            } else {
                Color couleur = affichageDTO.couleurs.get(patio.getPlancheSelectionne().getClass());
                for (Triangle triangle : patio.getPlancheSelectionne().getPlanchePrisme().getTriangles()) {
                    Triangle Triproj = Triangle.convertirVersEcran(triangle, matProj, matWorld, couleur, decalage, zoom, false, transparenceActivee);
                    triangles.removeIf(triangle1 -> triangle1.equals(Triproj));
                    Triangle Triproj2 = Triangle.convertirVersEcran(triangle, matProj, matWorld, couleur.brighter(), decalage, zoom, false, transparenceActivee);
                    if (Triproj2 != null) {
                        triangles.add(Triproj2);
                    }
                }
            }
        }
    }

    public void calculerPixels(boolean zBuffer, boolean meshSelectionMode, Color couleurContour) {

        int meshSelectionne = -1;
        patio.setPlancheSelectionnee(null);
        this.controller.setMeshHover(null);
        this.controller.setEscalierHover(false);

        ArrayList<Triangle> trianglesEscalier = new ArrayList<>();
        ArrayList<ArrayList<Triangle>> trianglesMeshes = new ArrayList<>();

        for (Planche planche : patio.getPlanches()) {

            if (!affichageDTO.visibilites.get(planche.getClass())) {
                continue;
            }

            Color couleur;
            if (controller.escalierSelectionne() && (planche.getClass() == Marche.class || planche.getClass() == Contremarche.class )) {
                couleur = Color.red;
            }
            else {
                couleur = affichageDTO.couleurs.get(planche.getClass());
            }

            for (Triangle triangle : planche.getPlanchePrisme().getTriangles()) {
                Triangle Triproj = Triangle.convertirVersEcran(triangle, matProj, matWorld, couleur, decalage, zoom, wireframe, transparenceActivee);
                if (Triproj != null) {
                    if (wireframe) {
                        determinerLignesExterieurs(triangle, Triproj, couleurContour);
                    } else {
                        if (sourisDansTriangle(Triproj, souris)) {
                            patio.setPlancheSelectionnee(planche);
                            if (planche.getClass() == Contremarche.class || planche.getClass() == Marche.class) {
                                meshSelectionne = 0;
                            }
                        }
                        if (planche.getClass() == Contremarche.class || planche.getClass() == Marche.class) {
                            trianglesEscalier.add(Triproj);
                        } else {
                            triangles.add(Triproj);
                        }
                    }
                }
            }
        }
        trianglesMeshes.add(trianglesEscalier);

        for (Barreau barreau : patio.getGardeCorps().getBarreaux()) {

            if (!affichageDTO.visibilites.get(barreau.getClass())) {
                continue;
            }
            Color couleur = affichageDTO.couleurs.get(barreau.getClass());

            for (Triangle triangle : barreau.getPlanchePrisme().getTriangles()) {
                Triangle Triproj = Triangle.convertirVersEcran(triangle, matProj, matWorld, couleur, decalage, zoom, wireframe, transparenceActivee);
                if (Triproj != null) {
                    if (wireframe) {
                        determinerLignesExterieurs(triangle, Triproj, couleurContour);
                    } else {
                        if (sourisDansTriangle(Triproj, souris)) {
                            patio.setPlancheSelectionnee(barreau);
                        }
                        triangles.add(Triproj);
                    }
                }
            }
        }

        int i = 1;
        for (Mesh mesh : meshes) {

            if (!affichageDTO.visibilites.get(Accessoire.class)) {
                continue;
            }

            ArrayList<Triangle> trianglesProjetes = new ArrayList<>();
            for (Triangle triangle : mesh.getTriangles()) {
                Color couleur = affichageDTO.couleurs.get(Accessoire.class);
                if (mesh.equals(controller.getMeshSelectionne())) {
                    couleur = Color.red;
                }
                Triangle Triproj = Triangle.convertirVersEcran(triangle, matProj, matWorld, couleur, decalage, zoom, wireframe, transparenceActivee);
                if (Triproj != null) {
                    if (wireframe) {
                        determinerLignesExterieurs(triangle, Triproj, couleurContour);
                    } else {
                        if (sourisDansTriangle(Triproj, souris)) {
                            if (meshSelectionMode) {
                                meshSelectionne = i;
                            }
                        }
                        trianglesProjetes.add(Triproj);
                    }
                }
            }
            trianglesMeshes.add(trianglesProjetes);
            i++;
        }

        if (!wireframe) {
            for (int j = 0; j < i; j++) {
                if (j != meshSelectionne) {
                    triangles.addAll(trianglesMeshes.get(j));
                }
            }

            ArrayList<Triangle> trianglesMeshSelectionne;

            if (meshSelectionne == -1) {
                trianglesMeshSelectionne = null;
            } else if (meshSelectionne == 0) {
                trianglesMeshSelectionne = trianglesMeshes.get(meshSelectionne);
                this.controller.setEscalierHover(true);
            } else {
                trianglesMeshSelectionne = trianglesMeshes.get(meshSelectionne);
                this.controller.setMeshHover(meshes.get(meshSelectionne - 1));
            }

            if (trianglesMeshSelectionne != null) {
                if (!meshSelectionMode && patio.getPlancheSelectionne() != null && (patio.getPlancheSelectionne().getClass() == Marche.class || patio.getPlancheSelectionne().getClass() == Contremarche.class)) {
                } else {
                    for (Triangle triangle : trianglesMeshSelectionne) {
                        triangle.setCouleur(triangle.getCouleur().brighter());
                    }
                }
                triangles.addAll(trianglesMeshSelectionne);
            }

            triangles.addAll(trianglesEscalier);

            changerCouleurPieceSelectionnee(triangles);

            if (transparenceActivee || !zBuffer) {
                triangles.sort((t1, t2) -> {
                    float z1 = (t1.getPoints().get(0).getZ() + t1.getPoints().get(1).getZ() + t1.getPoints().get(2).getZ()) / 3f;
                    float z2 = (t2.getPoints().get(0).getZ() + t2.getPoints().get(1).getZ() + t2.getPoints().get(2).getZ()) / 3f;
                    return Float.compare(z2, z1);
                });
            }
            // Si on n'a pas besoin de la transparence, faire le sort dans le sens inverse (de l'avant vers l'arrière) pour permettre de savoir si une pièce est cachée par une autre
            else {
                triangles.sort((t1, t2) -> {
                    float z1 = (t1.getPoints().get(0).getZ() + t1.getPoints().get(1).getZ() + t1.getPoints().get(2).getZ()) / 3f;
                    float z2 = (t2.getPoints().get(0).getZ() + t2.getPoints().get(1).getZ() + t2.getPoints().get(2).getZ()) / 3f;
                    return Float.compare(z1, z2);
                });
            }

            if (zBuffer) {
                for (Triangle triangle : triangles) {
                    separerTriangle(triangle);
                }
            }
        }
    }

    private void determinerLignesExterieurs(Triangle triangleOriginal, Triangle triproj, Color couleurContour) {
        ArrayList<Point3D> points = triangleOriginal.getPoints();
        ArrayList<Point3D> points2 = triproj.getPoints();

        float side1y = Math.abs(points.get(0).getY() - points.get(1).getY());
        float side1x = Math.abs(points.get(0).getX() - points.get(1).getX());
        float side1z = Math.abs(points.get(0).getZ() - points.get(1).getZ());
        float length11 = (float) Math.sqrt(Math.pow(side1x, 2) + Math.pow(side1y, 2));
        float length12 = (float) Math.sqrt(Math.pow(side1z, 2) + Math.pow(side1y, 2));
        float length13 = (float) Math.sqrt(Math.pow(side1x, 2) + Math.pow(side1z, 2));
        float length1 = Math.max(Math.max(length11, length12), length13);

        float side2y = Math.abs(points.get(2).getY() - points.get(0).getY());
        float side2x = Math.abs(points.get(2).getX() - points.get(0).getX());
        float side2z = Math.abs(points.get(2).getZ() - points.get(0).getZ());
        float length21 = (float) Math.sqrt(Math.pow(side2x, 2) + Math.pow(side2y, 2));
        float length22 = (float) Math.sqrt(Math.pow(side2z, 2) + Math.pow(side2y, 2));
        float length23 = (float) Math.sqrt(Math.pow(side2x, 2) + Math.pow(side2z, 2));
        float length2 = Math.max(Math.max(length21, length22), length23);

        float side3y = Math.abs(points.get(2).getY() - points.get(1).getY());
        float side3x = Math.abs(points.get(2).getX() - points.get(1).getX());
        float side3z = Math.abs(points.get(2).getZ() - points.get(1).getZ());
        float length31 = (float) Math.sqrt(Math.pow(side3x, 2) + Math.pow(side3y, 2));
        float length32 = (float) Math.sqrt(Math.pow(side3z, 2) + Math.pow(side3y, 2));
        float length33 = (float) Math.sqrt(Math.pow(side3x, 2) + Math.pow(side3z, 2));
        float length3 = Math.max(Math.max(length31, length32), length33);

        g.setColor(couleurContour);

        if (length1 > length2 && length1 > length3) {
            g.drawLine((int) points2.get(2).getX(), (int) points2.get(2).getY(), (int) points2.get(0).getX(), (int) points2.get(0).getY());
            g.drawLine((int) points2.get(2).getX(), (int) points2.get(2).getY(), (int) points2.get(1).getX(), (int) points2.get(1).getY());
        } else if (length2 > length1 && length2 > length3) {
            g.drawLine((int) points2.get(1).getX(), (int) points2.get(1).getY(), (int) points2.get(0).getX(), (int) points2.get(0).getY());
            g.drawLine((int) points2.get(2).getX(), (int) points2.get(2).getY(), (int) points2.get(1).getX(), (int) points2.get(1).getY());
        } else {
            g.drawLine((int) points2.get(1).getX(), (int) points2.get(1).getY(), (int) points2.get(0).getX(), (int) points2.get(0).getY());
            g.drawLine((int) points2.get(2).getX(), (int) points2.get(2).getY(), (int) points2.get(0).getX(), (int) points2.get(0).getY());
        }
    }


    public void triangleVersHaut(Point3D p1, Point3D p2, Point3D p3, Triangle vraiTriangle, Color couleur) {

        float delta1 = (p2.getX() - p1.getX()) / (p2.getY() - p1.getY());
        float delta2 = (p3.getX() - p1.getX()) / (p3.getY() - p1.getY());
        float x1 = p1.getX();
        float x2 = p1.getX();

        for (float y = p1.getY(); y <= p2.getY(); y++) {
            int pY = (int) y;
            if (y < cadre) {
                x1 += (-y + 1 + cadre) * delta1;
                x2 += (-y + 1 + cadre) * delta2;
                y = cadre;
                continue;
            }

            if (y > dimension.height - 1 - cadre) {
                break;
            }

            float minX = Math.max(cadre, Math.min(x1, x2));
            float maxX = Math.min(dimension.width - 1 - cadre, Math.max(x1, x2));

            for (float x = minX; x < maxX; x++) {
                int pX = (int) x;
                float z = calculerZTriange3D(vraiTriangle, new Point3D(x, y, 1));

                if (x == minX && y == p1.getY() && maxX - minX > 10) {
                    if (z > zbuffer[pY][pX]) {
                        //test if triangle is hidden
                        boolean test1, test2, test3;
                        test1 = calculerZTriange3D(vraiTriangle, new Point3D(p1.getX(), p1.getY(), 1)) > zbuffer[pY][pX];
                        test2 = calculerZTriange3D(vraiTriangle, new Point3D(p2.getX(), p2.getY(), 1)) > zbuffer[pY][pX];
                        test3 = calculerZTriange3D(vraiTriangle, new Point3D(p3.getX(), p3.getY(), 1)) > zbuffer[pY][pX];

                        if (test1 && test2 && test3) {
                            break;
                        }
                    }
                }

                if (z < zbuffer[pY][pX]) {
                    zbuffer[pY][pX] = z;
                    if (couleur.getAlpha() != 255) {
                        colorbuffer[pY][pX] = blend(couleur, colorbuffer[pY][pX]);
                    } else {
                        colorbuffer[pY][pX] = couleur;
                    }
                }
            }
            x1 += delta1;
            x2 += delta2;
        }
    }

    void triangleVersBas(Point3D p1, Point3D p2, Point3D p3, Triangle vraiTriangle, Color couleur) {

        float delta1 = (p3.getX() - p1.getX()) / (p3.getY() - p1.getY());
        float delta2 = (p3.getX() - p2.getX()) / (p3.getY() - p2.getY());
        float x1 = p3.getX();
        float x2 = p3.getX();

        for (float y = p3.getY(); y >= p1.getY(); y--) {
            int pY = (int) y;
            if (y < cadre) {
                break;
            }
            if (y > dimension.height - 1 - cadre) {
                x1 -= (y - dimension.height + 1 + cadre) * delta1;
                x2 -= (y - dimension.height + 1 + cadre) * delta2;
                y = dimension.height - cadre;
                continue;
            }

            float minX = Math.max(cadre, Math.min(x1, x2));
            float maxX = Math.min(dimension.width - 1 - cadre, Math.max(x1, x2));

            for (float x = minX; x < maxX; x++) {
                int pX = (int) x;

                float z = calculerZTriange3D(vraiTriangle, new Point3D(x, y, 1));

                if (x == minX && y == p3.getY() && maxX - minX > 10) {
                    if (z > zbuffer[pY][pX]) {
                        //test if triangle is hidden
                        boolean test1, test2, test3;
                        test1 = calculerZTriange3D(vraiTriangle, new Point3D(p1.getX(), p1.getY(), 1)) > zbuffer[pY][pX];
                        test2 = calculerZTriange3D(vraiTriangle, new Point3D(p2.getX(), p2.getY(), 1)) > zbuffer[pY][pX];
                        test3 = calculerZTriange3D(vraiTriangle, new Point3D(p3.getX(), p3.getY(), 1)) > zbuffer[pY][pX];

                        if (test1 && test2 && test3) {
                            break;
                        }
                    }
                }

                if (z < zbuffer[pY][pX]) {
                    zbuffer[pY][pX] = z;
                    if (couleur.getAlpha() != 255) {
                        colorbuffer[pY][pX] = blend(couleur, colorbuffer[pY][pX]);
                    } else {
                        colorbuffer[pY][pX] = couleur;
                    }
                }
            }
            x1 -= delta1;
            x2 -= delta2;
        }
    }

    public void dessinerContour(Graphics g, int i, int j, Color couleurContour, int width) {
        try {
            g.setColor(couleurContour);
            if (zbuffer[i + 1][j] != Integer.MAX_VALUE) {
                for (int w = 1; w < width; w++) {
                    if (zbuffer[i - w][j] == Integer.MAX_VALUE) {
                        g.drawLine(j, i - w, j, i - w);
                    }
                }
                g.drawLine(j, i, j, i);
            }

            if (zbuffer[i - 1][j] != Integer.MAX_VALUE) {
                for (int w = 1; w < width; w++) {
                    if (zbuffer[i + w][j] == Integer.MAX_VALUE) {
                        g.drawLine(j, i + w, j, i + w);
                    }
                }
                g.drawLine(j, i, j, i);
            }

            if (zbuffer[i][j - 1] != Integer.MAX_VALUE) {
                for (int w = 1; w < width; w++) {
                    if (zbuffer[i][j + w] == Integer.MAX_VALUE) {
                        g.drawLine(j + w, i, j + w, i);
                    }
                }
                g.drawLine(j, i, j, i);
            }

            if (zbuffer[i][j + 1] != Integer.MAX_VALUE) {
                for (int w = 1; w < width; w++) {
                    if (zbuffer[i][j - w] == Integer.MAX_VALUE) {
                        g.drawLine(j - w, i, j - w, i);
                    }
                }
                g.drawLine(j, i, j, i);
            }
        } catch (Exception ignored) {
        }

    }

    public boolean dessinerTriangles(ArrayList<SousDrawingPanel.Flocon> flocons, Dimension dimension, boolean wireframe, boolean modeSelection, int width, Color couleurContour) {

        this.dimension = dimension;

        //initialiser les buffers
        this.wireframe = wireframe;
        this.zbuffer = new float[dimension.height][dimension.width];
        this.colorbuffer = new Color[dimension.height][dimension.width];
        Color background = new Color(80, 83, 104);
        for (int i = 0; i < dimension.height; i++) {
            for (int j = 0; j < dimension.width; j++) {
                zbuffer[i][j] = Integer.MAX_VALUE;
                colorbuffer[i][j] = background;
            }
        }

        // Calculer les données du zbuffer et du colorbuffer
        calculerPixels(true, modeSelection, couleurContour);

        // itérer sur tous les pixels de l'écran
        for (int i = 0; i < dimension.height; i++) {
            for (int j = 0; j < dimension.width; j++) {

                // Si le zbuffer n'a pas bougé
                if (zbuffer[i][j] == Integer.MAX_VALUE) {

                    // Dessiner le contour si demandé
                    if (width != 0) {
                        dessinerContour(g, i, j, couleurContour, width);
                    }

                    // sinon passer au pixel suivant
                    continue;
                }

                // Dessiner le pixel
                Color couleur = colorbuffer[i][j];
                g.setColor(couleur);
                g.drawLine(j, i, j, i);
            }
        }

        // Si le mode Noël est activé
        if (flocons != null) {
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", Font.ITALIC, 16));
            for (SousDrawingPanel.Flocon flocon : flocons) {
                g.drawString("*", (int) flocon.position.x, (int) flocon.position.y);
            }
        }

        return true;
    }

    public void dessinerSansUpdate(Graphics g, ArrayList<SousDrawingPanel.Flocon> flocons, int width, Color couleurContour) {

        // Redessiner le patio comme il était
        for (int i = 0; i < dimension.height; i++) {
            for (int j = 0; j < dimension.width; j++) {

                if (zbuffer[i][j] == Integer.MAX_VALUE) {
                    if (width != 0) {
                        dessinerContour(g, i, j, couleurContour, width);
                    }
                    continue;
                }

                Color couleur = colorbuffer[i][j];
                g.setColor(couleur);
                g.drawLine(j, i, j, i);
            }
        }

        // Si le mode Noël est activé
        if (flocons != null) {
            g.setColor(Color.white);
            g.setFont(new Font("Helvetica", Font.ITALIC, 16));
            for (SousDrawingPanel.Flocon flocon : flocons) {
                g.drawString("*", (int) flocon.position.x, (int) flocon.position.y);
            }
        }
    }
}
