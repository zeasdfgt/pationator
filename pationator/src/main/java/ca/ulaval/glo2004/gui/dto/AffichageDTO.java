package ca.ulaval.glo2004.gui.dto;

import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;

public class AffichageDTO implements Cloneable, Serializable {

    public HashMap<Class<?>, Color> couleurs = new HashMap<>();
    public HashMap<Class<?>, Boolean> visibilites = new HashMap<>();

    // public AffichageDTO(HashMap<Class<?>, Color> couleurs, HashMap<Class<?>, Boolean> visibilites) {
        // this.couleurs = (HashMap<Class<?>, Color>) couleurs.clone();
        // this.visibilites = (HashMap<Class<?>, Boolean>) visibilites.clone();
    // }

    public AffichageDTO() {
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
