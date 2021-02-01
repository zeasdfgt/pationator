package ca.ulaval.glo2004.domain.patio;

import java.io.Serializable;

public enum ValeurNominale implements Serializable {
    UN("1", 0.75f),
    CINQ_QUARTS("5/4", 1f),
    DEUX("2", 1.5f),
    TROIS("3", 2.5f),
    QUATRE("4", 3.5f),
    CINQ("5", 4.5f),
    SIX("6", 5.5f),
    HUIT("8", 7.25f),
    DIX("10", 9.25f),
    DOUZE("12", 11.25f);

    private final String stringValue;
    private final float valeurReelle;

    ValeurNominale(String stringValue, float valeurReelle) {
        this.stringValue = stringValue;
        this.valeurReelle = valeurReelle;
    }

    public float getValeurReelle() {
        return valeurReelle;
    }

    public static ValeurNominale fromString(String valeur) {
        for (ValeurNominale valeurNominale : ValeurNominale.values()) {
            if (valeurNominale.toString().equals(valeur)) {
                return valeurNominale;
            }
        }
        throw new RuntimeException("Cette valeur nominale n'existe pas");
    }

    @Override
    public String toString() {
        return stringValue;
    }
}
