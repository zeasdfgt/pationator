package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.patio.Planche;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Parcours implements Serializable {
    abstract ArrayList<Planche> getPlanches();

    abstract ArrayList<Barreau> getBarreaux();
}
