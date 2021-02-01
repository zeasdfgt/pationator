package ca.ulaval.glo2004.domain.drawing;

public class Matrice {

    protected final float[][] elements = new float[4][4];

    public Matrice(float[][] elements) {
        for (int i = 0; i < 4; i++) {
            System.arraycopy(elements[i], 0, this.elements[i], 0, 4);
        }
    }

    public float[][] getElements() {
        return elements;
    }

    @Override
    public String toString() {
        String resultat = "";
        resultat += (elements[0][0] + "\t" + elements[0][1] + "\t" + elements[0][2] + "\t" + elements[0][3] + "\n");
        resultat += (elements[1][0] + "\t" + elements[1][1] + "\t" + elements[1][2] + "\t" + elements[1][3] + "\n");
        resultat +=(elements[2][0] + "\t" + elements[2][1] + "\t" + elements[2][2] + "\t" + elements[2][3] + "\n");
        resultat +=(elements[3][0] + "\t" + elements[3][1] + "\t" + elements[3][2] + "\t" + elements[3][3] + "\n");
        return resultat;
    }

    public static Matrice PermuterL1L2(Matrice m1) {
        Matrice matriceElem = new MatriceFactory().identitee();
        matriceElem.elements[0][0] = 0;
        matriceElem.elements[0][1] = 1;
        matriceElem.elements[1][0] = 1;
        matriceElem.elements[1][1] = 0;
        return multiplier(m1, matriceElem);
    }

    public static Matrice PermuterL2L3(Matrice m1) {
        Matrice matriceElem = new MatriceFactory().identitee();
        matriceElem.elements[0][0] = 1;
        matriceElem.elements[1][1] = 0;
        matriceElem.elements[1][2] = 1;
        matriceElem.elements[2][2] = 0;
        matriceElem.elements[2][1] = 1;
        return multiplier(m1, matriceElem);
    }

    public static Matrice PermuterL1L3(Matrice m1) {
        Matrice matriceElem = new MatriceFactory().identitee();
        matriceElem.elements[0][0] = 0;
        matriceElem.elements[0][2] = 1;
        matriceElem.elements[2][2] = 0;
        matriceElem.elements[2][0] = 1;
        return multiplier(m1, matriceElem);
    }

    public static Matrice multiplier(Matrice m1, Matrice m2) {

        float[][] elements = new float[4][4];

        for (int c = 0; c < 4; c++) {
            for (int r = 0; r < 4; r++) {
                elements[r][c] = m1.elements[r][0] * m2.elements[0][c]
                        + m1.elements[r][1] * m2.elements[1][c]
                        + m1.elements[r][2] * m2.elements[2][c]
                        + m1.elements[r][3] * m2.elements[3][c]; } }
        return new Matrice(elements);
    }

}
