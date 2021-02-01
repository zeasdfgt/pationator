package ca.ulaval.glo2004.domain.drawing;

import ca.ulaval.glo2004.domain.mesh.Point3D;

public class MatriceFactory {

    public Matrice creerWorldMatrice(Point3D translation, Point3D angles) {
        Matrice matWorld, matTrans, matRotX, matRotY, matRotZ;
        matTrans = creerMatriceTranslation(new Point3D(translation.getX(), translation.getY(), translation.getZ()));
        matRotX = rotationX(angles.getX());
        matRotY = rotationY(angles.getY());
        matRotZ = rotationZ(angles.getZ());
        matWorld = identitee();
        matWorld = Matrice.multiplier(matWorld, matTrans);
        matWorld = Matrice.multiplier(matWorld, matRotZ);
        matWorld = Matrice.multiplier(matWorld, matRotY);
        matWorld = Matrice.multiplier(matWorld, matRotX);
        return matWorld;
    }

    public static Matrice creerProjecteurPerspective() {
        float[][] elements = new float[4][4];
        float fFar = 1000f, fNear = 0.1f;
        float fFovRad = 1.0f / (float) Math.tan(90f * 0.5f / 180.0f * Math.PI);
        elements[0][0] = 1 * fFovRad;
        elements[1][1] = fFovRad;
        elements[2][2] = fFar / (fFar - fNear);
        elements[3][2] = (-fFar * fNear) / (fFar - fNear);
        elements[2][3] = 1.0f;
        elements[3][3] = 1.0f;
        return new Matrice(elements);
    }

    private Matrice creerMatriceTranslation(Point3D translation) {
        float[][] elements = new float[4][4];
        elements[0][0] = 1;
        elements[1][1] = 1;
        elements[2][2] = 1;
        elements[3][3] = 1;
        elements[3][0] = translation.getX();
        elements[3][1] = translation.getY();
        elements[3][2] = translation.getZ();
        return new Matrice(elements);
    }

    public Matrice identitee() {
        float[][] elements = new float[4][4];
        elements[0][0] = 1;
        elements[1][1] = 1;
        elements[2][2] = 1;
        elements[3][3] = 1;
        return new Matrice(elements);
    }

    private Matrice rotationX(float angle) {
        float[][] elements = new float[4][4];
        elements[0][0] = 1.0f;
        elements[1][1] = (float) Math.cos(angle);
        elements[1][2] = (float) Math.sin(angle);
        elements[2][1] = (float) -Math.sin(angle);
        elements[2][2] = (float) Math.cos(angle);
        elements[3][3] = 1.0f;
        return new Matrice(elements);
    }

    private Matrice rotationY(float angle) {
        float[][] elements = new float[4][4];
        elements[0][0] = (float) Math.cos(angle);
        elements[0][2] = (float) Math.sin(angle);
        elements[2][0] = (float) -Math.sin(angle);
        elements[1][1] = 1.0f;
        elements[2][2] = (float) Math.cos(angle);
        elements[3][3] = 1.0f;
        return new Matrice(elements);
    }

    private Matrice rotationZ(float angle) {
        float[][] elements = new float[4][4];
        elements[0][0] = (float) Math.cos(angle);
        elements[0][1] = (float) Math.sin(angle);
        elements[1][0] = (float) -Math.sin(angle);
        elements[1][1] = (float) Math.cos(angle);
        elements[2][2] = 1.0f;
        elements[3][3] = 1.0f;
        return new Matrice(elements);
    }

    public Matrice scaling(Point3D point3D) {
        float[][] elements = new float[4][4];
        elements[0][0] = point3D.getX();
        elements[1][1] = point3D.getY();
        elements[2][2] = point3D.getZ();
        elements[3][3] = 1.0f;
        return new Matrice(elements);
    }
}
