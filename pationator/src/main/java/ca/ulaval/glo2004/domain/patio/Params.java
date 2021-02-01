package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.gui.dto.ParametresDTO;

public class Params {
    //Général
    public float lo_porteafo;
    public float la_patio;
    public float lo_patio;
    public float ha_patio;

    //Solives
    public int nb_solives;
    public float esp_solives;
    public TypeBois typeSolives;

    //Poutres
    public int nb_poutres;
    public TypeBois typePoutre;
    public float lo_poutres;
    public float esp_poutres;

    //Poteaux
    public TypeBois typePoteau;
    public int nb_poteaux;
    public float esp_poteaux;

    //Recouvrement
    public float esp_recouvre;
    public TypeBois typeRecouvre;

    public Params(int nb_solives, float esp_solives, TypeBois typeSolives, float lo_porteafo, float la_patio, float lo_patio, float ha_patio, int nb_poutres, TypeBois typePoutre, float lo_poutres, float esp_poutres, TypeBois typePoteau, int nb_poteaux, float esp_poteaux, float esp_recouvre, TypeBois typeRecouvre) {
        this.nb_solives = nb_solives;
        this.esp_solives = esp_solives;
        this.typeSolives = typeSolives;
        this.lo_porteafo = lo_porteafo;
        this.la_patio = la_patio;
        this.lo_patio = lo_patio;
        this.ha_patio = ha_patio;
        this.nb_poutres = nb_poutres;
        this.typePoutre = typePoutre;
        this.lo_poutres = lo_poutres;
        this.esp_poutres = esp_poutres;
        this.typePoteau = typePoteau;
        this.nb_poteaux = nb_poteaux;
        this.esp_poteaux = esp_poteaux;
        this.esp_recouvre = esp_recouvre;
        this.typeRecouvre = typeRecouvre;
    }

    public Params(ParametresDTO parametresDTO) {

        //General
        this.lo_patio = parametresDTO.lo_patio;
        this.la_patio = parametresDTO.la_patio;
        this.lo_porteafo = parametresDTO.lo_porteafo;
        this.ha_patio = parametresDTO.ha_patio;

        //Recouvrement
        this.typeRecouvre = parametresDTO.typeRecouvre;
        this.esp_recouvre = parametresDTO.esp_recouvre;

        //Solives
        this.typeSolives = parametresDTO.typeSolives;
        if (parametresDTO.nb_poutres == 2) {
            this.nb_solives = (int) Math.ceil((la_patio - typeSolives.getLargeur().getValeurReelle())/parametresDTO.esp_solives) + 1;
            this.esp_solives = (parametresDTO.la_patio - parametresDTO.typeSolives.getLargeur().getValeurReelle()) / (nb_solives - 1);
        }
        else {
            this.nb_solives = (int) Math.ceil((la_patio - 2 * typeSolives.getLargeur().getValeurReelle())/parametresDTO.esp_solives) + 1;
            this.esp_solives = (parametresDTO.la_patio - (2 * parametresDTO.typeSolives.getLargeur().getValeurReelle())) / (nb_solives - 1);
        }

        //Poutres
        this.typePoutre = parametresDTO.typePoutre;
        this.lo_poutres = parametresDTO.la_patio;
        this.nb_poutres = parametresDTO.nb_poutres;
        this.esp_poutres = 0;

        //Poteaux
        this.typePoteau = parametresDTO.typePoteau;
        this.nb_poteaux = parametresDTO.nb_poteaux;
        this.esp_poteaux = (parametresDTO.la_patio - parametresDTO.typePoteau.getHauteur().getValeurReelle()) / (nb_poteaux - 1);

        float ha_patioMin;
        if (parametresDTO.typePoutre == null) { ha_patioMin = 11.25f + this.typeSolives.getHauteur().getValeurReelle() + this.typeRecouvre.getLargeur().getValeurReelle(); }
        else { ha_patioMin = this.typePoutre.getHauteur().getValeurReelle() + this.typeSolives.getHauteur().getValeurReelle() + this.typeRecouvre.getLargeur().getValeurReelle(); }
        if (this.ha_patio < ha_patioMin) { this.ha_patio = ha_patioMin; }
    }

    public int getNb_solives() {
        return nb_solives;
    }

    public float getEsp_solives() {
        return esp_solives;
    }

    public TypeBois getTypeSolives() {
        return typeSolives;
    }

    public int getNb_poutres() {
        return nb_poutres;
    }

    public float getLo_poutres() {
        return lo_poutres;
    }

    public float getEsp_poutres() { return esp_poutres; }

    public TypeBois getTypePoteau() {
        return typePoteau;
    }

    public float getEsp_recouvre() {
        return esp_recouvre;
    }

    public TypeBois getTypeRecouvre() {
        return typeRecouvre;
    }

    public float getLa_patio() {
        return la_patio;
    }

    public float getEsp_poteaux() {
        return esp_poteaux;
    }
}
