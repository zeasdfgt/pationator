package ca.ulaval.glo2004.domain.patio;

import java.io.Serializable;
import java.util.Objects;

public class TypeBois implements Serializable {
    private final ValeurNominale largeur;
    private final ValeurNominale hauteur;

    public TypeBois(String dimensionsNominales) {
        String[] dimensions = dimensionsNominales.split("X");
        this.hauteur = ValeurNominale.fromString(dimensions[1]);
        this.largeur = ValeurNominale.fromString(dimensions[0]);
    }

    public ValeurNominale getLargeur() {
        return largeur;
    }

    public ValeurNominale getHauteur() {
        return hauteur;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TypeBois)) {
            return false;
        }
        TypeBois typeBois = (TypeBois) object;
        if (this == typeBois) {
            return true;
        }

        return (largeur.equals(typeBois.largeur) && hauteur.equals(typeBois.hauteur));
    }

    @Override
    public int hashCode() {
        return Objects.hash(largeur, hauteur);
    }

    @Override
    public String toString() {
        return largeur + "X" + hauteur;
    }

}
