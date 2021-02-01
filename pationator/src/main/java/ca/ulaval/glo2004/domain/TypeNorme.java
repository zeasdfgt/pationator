package ca.ulaval.glo2004.domain;

import java.io.Serializable;
import java.util.ArrayList;

public class TypeNorme implements Serializable {
    public String nomAxeX;
    public String nomAxeY;
    public String nomResultat;

    public ArrayList<String> axeX;
    public ArrayList<String> axeY;

    public String[][] tableauNorme;

    public TypeNorme(String nomAxeX, String nomAxeY, String nomResultat,
                     ArrayList<String> axeX, ArrayList<String> axeY, String[][] tableauNorme) {
        this.nomAxeX = nomAxeX;
        this.nomAxeY = nomAxeY;
        this.nomResultat = nomResultat;
        this.axeX = axeX;
        this.axeY = axeY;
        this.tableauNorme = tableauNorme;
    }

    public String getInfo(String donneeX, String donneeY) {
        int indexX;
        int indexY;
        try {
            indexX = axeX.indexOf(donneeX);
        } catch (Exception exception) {
            return nomAxeX;
        }
        try {
            indexY = axeY.indexOf(donneeY);
        } catch (Exception exception) {
            return nomAxeY;
        }

        // WARNING: NEVER USED
        // indexX = axeX.indexOf(donneeX);
        // indexY = axeY.indexOf(donneeY);

        return tableauNorme[indexX][indexY];
    }
}
