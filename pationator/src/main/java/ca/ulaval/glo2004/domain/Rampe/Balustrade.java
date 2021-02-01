package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.patio.Planche;
import ca.ulaval.glo2004.domain.mesh.Mesh;

import java.io.Serializable;
import java.util.ArrayList;

public class Balustrade extends Parcours implements Serializable {
    private final ArrayList<Barreau> barreaux;
    private final Traverse traverseBas;
    private final Traverse traverseHaut;
    private final MainCourante mainCourante;

    public Balustrade(ArrayList<Barreau> barreaux, Traverse traverseBas, Traverse traverseHaut, MainCourante mainCourante) {
        this.barreaux = barreaux;
        this.traverseBas = traverseBas;
        this.traverseHaut = traverseHaut;
        this.mainCourante = mainCourante;
    }

    public ArrayList<Mesh> getMeshes() {
        ArrayList<Mesh> meshes = new ArrayList<>();
        meshes.add(traverseBas.getPlanchePrisme());
        meshes.add(traverseHaut.getPlanchePrisme());
        meshes.add(mainCourante.getPlanchePrisme());
        for (Barreau barreau : barreaux) {
            meshes.add(barreau.getPlanchePrisme());
        }
        return meshes;
    }

    @Override
    public ArrayList<Planche> getPlanches() {
        ArrayList<Planche> planches = new ArrayList<>();
        planches.add(traverseBas);
        planches.add(traverseHaut);
        return planches;
    }

    @Override
    public ArrayList<Barreau> getBarreaux() {
        return barreaux;
    }
}
