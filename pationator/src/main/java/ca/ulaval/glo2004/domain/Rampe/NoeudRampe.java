package ca.ulaval.glo2004.domain.Rampe;

import java.io.Serializable;

public class NoeudRampe implements Serializable, Cloneable {
    private final Balustre balustre;
    private NoeudRampe noeudSuivant;
    private boolean estEscalier = false;

    public NoeudRampe(Balustre balustre) {
        this.balustre = balustre;
    }

    public NoeudRampe(NoeudRampe noeudRampeclone) {
        this.balustre = noeudRampeclone.balustre.clone();
        if (noeudSuivant != null) {
            this.noeudSuivant = noeudRampeclone.noeudSuivant.clone();
        }
    }

    public Balustre getBalustre() {
        return balustre;
    }

    public NoeudRampe suivant() {
        return noeudSuivant;
    }

    public void setNoeudSuivant(NoeudRampe noeudSuivant) {
        this.noeudSuivant = noeudSuivant;
    }

    public void setEstEscalier(boolean estEscalier) {
        this.estEscalier = estEscalier;
    }

    public boolean estEscalier() {
        return estEscalier;
    }

    @Override
    protected NoeudRampe clone() {
        return new NoeudRampe(this);
    }
}
