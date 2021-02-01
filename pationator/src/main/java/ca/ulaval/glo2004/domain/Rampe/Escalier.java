package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.patio.Planche;

import java.io.Serializable;
import java.util.ArrayList;

public class Escalier extends Parcours implements Serializable {

    private final ArrayList<Marche> marches;
    private final ArrayList<Contremarche> contreMarches;

    public Escalier(ArrayList<Marche> marches, ArrayList<Contremarche> contreMarches) {
        this.marches = marches;
        this.contreMarches = contreMarches;
    }

    @Override
    ArrayList<Planche> getPlanches() {
        ArrayList<Planche> planches = new ArrayList<>();
        planches.addAll(marches);
        planches.addAll(contreMarches);

        return planches;
    }

    @Override
    ArrayList<Barreau> getBarreaux() {
        return new ArrayList<>();
    }
}
