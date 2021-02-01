package ca.ulaval.glo2004.gui.dto;

import ca.ulaval.glo2004.gui.modes.TransparenceMode;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;

public class SauvegardeDTO implements Cloneable, Serializable {
    public HashMap<Class<?>, Color> couleurs;
    public HashMap<Class<?>, Boolean> visibilites;
    public ParametresDTO parametresDTO;
    public PrixDTO prixDTO;
    public boolean modeNoel;
    public boolean genererRampe;
    public String typeBalustrade;
    public Color background;
    public Color outline;
    public TransparenceMode modeTransparence;

    public SauvegardeDTO(TransparenceMode modeTransparence, AffichageDTO affichageDTO, ParametresDTO parametresDTO, PrixDTO prixDTO, boolean modeNoel, boolean genererRampe, String typeBalustrade, Color background, Color outline) {
        this.couleurs = new HashMap<>(affichageDTO.couleurs);
        this.visibilites = new HashMap<>(affichageDTO.visibilites);
        this.parametresDTO = parametresDTO;
        this.prixDTO = prixDTO;
        this.modeNoel = modeNoel;
        this.genererRampe = genererRampe;
        this.typeBalustrade = typeBalustrade;
        this.background = background;
        this.outline = outline;
        this.modeTransparence = modeTransparence;
    }
}
