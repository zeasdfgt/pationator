package ca.ulaval.glo2004.domain;

import ca.ulaval.glo2004.gui.dto.ParametresDTO;

import java.io.Serializable;

public class ValidateurEntrees implements Serializable {

    public boolean validerDTO(ParametresDTO parametresDTO) {
        return  validerParametre("esp_recouvre", parametresDTO.esp_recouvre) &&
                validerParametre("lo_porteafo", parametresDTO.lo_porteafo) &&
                validerParametre("nb_poteaux", (float)parametresDTO.nb_poteaux) &&
                validerParametre("nb_poutres", (float)parametresDTO.nb_poutres) &&
                validerParametre("esp_solive", parametresDTO.esp_solives) &&
                validerParametre("ha_rampe", parametresDTO.ha_rampe);
    }

    public boolean validerParametre(String nom_param, Float valeur) {
        boolean valide = true;

        switch (nom_param) {
            case "lo_poteau":
            case "esp_recouvre":
            case "lo_porteafo":
            case "esp_solive":
            case "esp_poteaux":
                valide = valeur >= 0f;
                break;
            case "nb_poutres":
            case "nb_poteaux":
                valide = valeur >= 2f;
                break;
            case "ha_rampe":
                valide = valeur >= 5f;
                break;
        }

        return valide;
    }

}
