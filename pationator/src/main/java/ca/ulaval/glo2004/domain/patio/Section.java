package ca.ulaval.glo2004.domain.patio;

import java.io.Serializable;
import java.util.ArrayList;

public class Section implements Serializable {
    private final ArrayList<Solive> solives;

    public Section(ArrayList<Solive> solives) {
        this.solives = solives;
    }

    public ArrayList<Solive> getSolives() {
        return this.solives;
    }
}
