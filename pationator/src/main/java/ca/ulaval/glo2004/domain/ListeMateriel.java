package ca.ulaval.glo2004.domain;

import ca.ulaval.glo2004.domain.patio.Planche;
import ca.ulaval.glo2004.domain.patio.TypeBois;
import ca.ulaval.glo2004.gui.dto.PrixDTO;

import java.io.Serializable;
import java.util.*;

public class ListeMateriel implements Serializable, Cloneable {

    private final ArrayList<Materiel> materiels;
    private final HashMap<String, Float> prixHashMap;

    public ListeMateriel(HashMap<Planche, String> planches, PrixDTO prixDTO) {
        this.materiels = new ArrayList<>();
        this.prixHashMap = new HashMap<>();
        makeHashMapPrix(prixDTO);
        makeArrayListMateriels(planches);
        materiels.sort(Comparator.comparing(Materiel::getQuantite).thenComparing(Materiel::getLongueur));
    }

    private void makeArrayListMateriels(HashMap<Planche, String> planches) {
        for(Map.Entry<Planche, String> entry : planches.entrySet()) {
            Planche planche = entry.getKey();
            String typePlanche = entry.getValue();
            boolean present = false;

            for(Materiel materiel : materiels) {
                if(materiel.getTypeMateriel() == planche.getTypebois() &&
                   materiel.getLongueur() == planche.getPlancheLongueur()) {
                    present = true;
                    materiel.upQuantite();
                }
            }

            if(!present) {
                materiels.add(new Materiel(planche.getTypebois(), 1,
                                           planche.getPlancheLongueur(),
                                           typePlanche));
            }
        }
    }

    private void makeHashMapPrix(PrixDTO prixDTO) {
        prixHashMap.put(new TypeBois("2X2").toString(), prixDTO.prix2x2);
        prixHashMap.put(new TypeBois("2X4").toString(), prixDTO.prix2x4);
        prixHashMap.put(new TypeBois("2X6").toString(), prixDTO.prix2x6);
        prixHashMap.put(new TypeBois("2X8").toString(), prixDTO.prix2x8);
        prixHashMap.put(new TypeBois("2X10").toString(), prixDTO.prix2x10);
        prixHashMap.put(new TypeBois("2X12").toString(), prixDTO.prix2x12);
        prixHashMap.put(new TypeBois("5/4X6").toString(), prixDTO.prix5quartx6);
        prixHashMap.put(new TypeBois("4X4").toString(), prixDTO.prix4x4);
        prixHashMap.put(new TypeBois("6X6").toString(), prixDTO.prix6x6);
    }

    public void setPrix(PrixDTO prixDTO) {
        prixHashMap.clear();
        makeHashMapPrix(prixDTO);
    }

    public float getPrixTotalFloat() {
        float prixTotal = 0;
        for (Materiel materiel : materiels) {
            float prixLineaire = prixHashMap.get(materiel.getTypeMateriel().toString());
            int quantite = materiel.getQuantite();
            float longueur = materiel.getLongueur();

            prixTotal += quantite*longueur*prixLineaire;
        }

        return prixTotal;
    }

    public float getPrixTotalString(HashMap<String, Boolean> mapString) {
        float prixTotal = 0;
        for (Materiel materiel : materiels) {
            if(mapString.get(materiel.getTypePlanche())) {
                float prixLineaire = prixHashMap.get(materiel.getTypeMateriel().toString());
                int quantite = materiel.getQuantite();
                float longueur = materiel.getLongueur();

                prixTotal += quantite*longueur*prixLineaire;
            }
        }

        return prixTotal;
    }

    public String toString(HashMap<String, Boolean> mapString, int nombrepicots) {
        StringBuilder texte = new StringBuilder();
        texte.append("<html>");

        HashMap<String, String> texteFinalMap = new HashMap<>();
        texteFinalMap.put("Recouvrement", "");
        texteFinalMap.put("Solives", "");
        texteFinalMap.put("Poutres", "");
        texteFinalMap.put("Appuis", "");
        texteFinalMap.put("Garde-Corps", "");

        for (Materiel materiel : materiels) {
            if(mapString.get(materiel.getTypePlanche())) {
                StringBuilder materielString = new StringBuilder();
                materielString.append(materiel.toString());
                int sizeMaterielToString = materiel.toString().length();
                float cout = materiel.getQuantite()*materiel.getLongueur()*prixHashMap.get(materiel.getTypeMateriel().toString());
                String coutString = String.format("%.2f", cout);
                for(int i = 1; i<(nombrepicots - sizeMaterielToString - coutString.length()); i++){
                    materielString.append(".");
                }
                materielString.append(coutString);
                materielString.append("$");
                materielString.append("<br>");
                String texteDansHashMap = texteFinalMap.get(materiel.getTypePlanche());
                materielString.append("</p>");
                texteFinalMap.put(materiel.getTypePlanche(), texteDansHashMap + materielString);
            }
        }

        ArrayList<String> listTypePlanche =  new ArrayList<>();
        listTypePlanche.add("Recouvrement");
        listTypePlanche.add("Solives");
        listTypePlanche.add("Poutres");
        listTypePlanche.add("Appuis");
        listTypePlanche.add("Garde-Corps");

        for(String typePlanche : listTypePlanche) {
            if(mapString.get(typePlanche)) {
                texte.append("<h1 style=\"font-size:13px\"><i><u>").append(typePlanche).append(":</i></u></h1>");
                texte.append("   ").append(texteFinalMap.get(typePlanche));
                texte.append("<br>");
            }
        }

        texte.append("</html>");
        return texte.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListeMateriel that = (ListeMateriel) o;
        return Objects.equals(materiels, that.materiels) &&
            Objects.equals(prixHashMap, that.prixHashMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materiels, prixHashMap);
    }

    public int getSize() {
        return materiels.size();
    }

    public ArrayList<Materiel> getMateriels() {
        return this.materiels;
    }
}
