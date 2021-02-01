package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.patio.Planche;
import ca.ulaval.glo2004.domain.mesh.Mesh;

import java.io.Serializable;
import java.util.ArrayList;

public class GardeCorps implements Serializable, Cloneable {
    private final ListeChaineeRampe balustres;
    private final ArrayList<Parcours> parcours;

    public ArrayList<Parcours> getParcours() {
        return parcours;
    }

    public GardeCorps(ListeChaineeRampe balustres, ArrayList<Parcours> parcours) {
        this.balustres = balustres;
        this.parcours = parcours;
    }

    public GardeCorps(GardeCorps gardeCorpsclone) {
        this.balustres = gardeCorpsclone.balustres.clone();
        this.parcours = (ArrayList<Parcours>) gardeCorpsclone.parcours.clone();
    }

    public float getHauteur() {
        return balustres.getHauteurMax();
    }

    @Override
    public GardeCorps clone()  {
        return new GardeCorps(this);
    }

    public ArrayList<Planche> getPlanches() {
        ArrayList<Planche> planches = new ArrayList<>();
        for (Parcours unParcours : parcours) {
            planches.addAll(unParcours.getPlanches());
        }
        planches.addAll(balustres.getBalustres());
        return planches;
    }

    public ArrayList<Barreau> getBarreaux() {
        ArrayList<Barreau> barreaux = new ArrayList<>();
        for (Parcours unParcours : parcours) {
            barreaux.addAll(unParcours.getBarreaux());
        }
        return barreaux;
    }

    public ArrayList<Mesh> getMeshes() {
        ArrayList<Mesh> meshes = new ArrayList<>();
        for (Planche planche : getPlanches()) {
            meshes.add(planche.getPlanchePrisme());
        }

        for (Barreau barreau : getBarreaux()) {
            meshes.add(barreau.getPlanchePrisme());
        }
        return meshes;
    }
}
