package ca.ulaval.glo2004.gui.dto;

import ca.ulaval.glo2004.domain.patio.TypeBois;

import java.io.Serializable;

public class ParametresDTO implements Serializable, Cloneable {

    // Solives
    public float esp_solives;
    public TypeBois typeSolives;

    // Général
    public float lo_porteafo;
    public float la_patio;
    public float lo_patio;
    public float ha_patio;

    // Poutres
    public int nb_poutres;
    public TypeBois typePoutre;

    // Poteaux
    public TypeBois typePoteau;
    public int nb_poteaux;

    // Recouvrement
    public float esp_recouvre;
    public TypeBois typeRecouvre;
    public float ha_rampe;

    // public float escalierX; // extras

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
