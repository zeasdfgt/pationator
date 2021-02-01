package ca.ulaval.glo2004.domain;

import ca.ulaval.glo2004.domain.patio.*;
import ca.ulaval.glo2004.gui.dto.SauvegardeDTO;

import java.io.Serializable;

public class Sauvegardable implements Cloneable, Serializable {
    private final Patio patio;
    private final ListeMateriel listeMateriel;
    private final SauvegardeDTO sauvegardeDTO;

    public Patio getPatio() {
        return patio;
    }

    public ListeMateriel getListeMateriel() {
        return listeMateriel;
    }

    public SauvegardeDTO getSauvegardeDTO() {
        return sauvegardeDTO;
    }

    public Sauvegardable(Patio patio, ListeMateriel listeMateriel, SauvegardeDTO sauvegardeDTO) {
        this.patio = patio;
        this.listeMateriel = listeMateriel;
        this.sauvegardeDTO = sauvegardeDTO;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void printInfo() {
//        System.out.println("\tObject : " + this.sauvegardeDTO.parametresDTO + "\n" +
//                "\tSolives : " + sauvegardeDTO.parametresDTO.esp_solives + "\n");
    }
}
