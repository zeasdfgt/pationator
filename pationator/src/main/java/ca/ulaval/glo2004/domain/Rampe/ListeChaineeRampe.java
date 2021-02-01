package ca.ulaval.glo2004.domain.Rampe;

import java.io.Serializable;
import java.util.ArrayList;

public class ListeChaineeRampe implements Serializable, Cloneable {
    private NoeudRampe tete;
    private ArrayList<Balustre> balustres = new ArrayList<>();
    private final float hauteurMax;
    private final float espacementMax;

    public ListeChaineeRampe(float hauteurMax, float espacementMax) {
        this.hauteurMax = hauteurMax;
        this.espacementMax = espacementMax;
    }

    public ListeChaineeRampe(ListeChaineeRampe listeChaineeRampeclone) {
        this.hauteurMax = listeChaineeRampeclone.hauteurMax;
        this.espacementMax = listeChaineeRampeclone.espacementMax;
        this.tete = listeChaineeRampeclone.tete.clone();
        this.balustres = (ArrayList<Balustre>) listeChaineeRampeclone.balustres.clone();
    }

    @Override
    protected ListeChaineeRampe clone() {
        return new ListeChaineeRampe(this);
    }

    public void ajouterApres(Balustre balustreCourrante, Balustre nouvelleBalustre) {
        if (tete == null) {
            tete = new NoeudRampe(nouvelleBalustre);
        }
        NoeudRampe noeudCourrant = tete;
        while (noeudCourrant.suivant() != null) {
            if (noeudCourrant.getBalustre() == balustreCourrante) {
                NoeudRampe noeudSuivant = noeudCourrant.suivant();

                NoeudRampe nouveauNoeud = new NoeudRampe(nouvelleBalustre);
                nouveauNoeud.setNoeudSuivant(noeudSuivant);

                noeudCourrant.setNoeudSuivant(nouveauNoeud);
                balustres.add(nouvelleBalustre);

                return;
            }
            noeudCourrant = noeudCourrant.suivant();
        }
    }

    public void ajouterFin(Balustre nouvelleBalustre) {
        if (tete == null) {
            tete = new NoeudRampe(nouvelleBalustre);
            balustres.add(nouvelleBalustre);
            return;
        }
        NoeudRampe noeudCourrant = tete;
        while (noeudCourrant.suivant() != null) {
            noeudCourrant = noeudCourrant.suivant();
        }

        NoeudRampe nouveauNoeud = new NoeudRampe(nouvelleBalustre);
        noeudCourrant.setNoeudSuivant(nouveauNoeud);
        balustres.add(nouvelleBalustre);
    }

    public ArrayList<Balustre> getBalustres() {
        return balustres;
    }

    public float getHauteurMax() {
        return hauteurMax;
    }

    public float getEspacementMax() {
        return espacementMax;
    }

    public NoeudRampe getTete() {
        return tete;
    }
}
