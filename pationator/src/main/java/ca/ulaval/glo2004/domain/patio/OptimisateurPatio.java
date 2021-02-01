package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.domain.*;
import ca.ulaval.glo2004.domain.Rampe.GardeCorpsFactory;
import ca.ulaval.glo2004.domain.Rampe.TypeBarreau;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.gui.dto.MagiqueDTO;
import ca.ulaval.glo2004.gui.dto.PrixDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class OptimisateurPatio implements Serializable {

    private final ValidateurNorme validateurNorme;
    private final HashMap<String, TypeNorme> normes;
    private PrixDTO prixDTO;

    public OptimisateurPatio(HashMap<String, TypeNorme> normes, PrixDTO prixDTO) {
        this.normes = normes;
        this.validateurNorme = new ValidateurNorme(normes);
        this.prixDTO = prixDTO;
    }

    public Params getParams(MagiqueDTO magiqueDTO) throws Exception {
        ArrayList<Params> paramsPossibles = getAllPossibleParams(magiqueDTO);
        if (paramsPossibles.isEmpty()) {
            throw new Exception("test");
        }
        Params cheapestParams = null;
        float cheapestPrice = Float.MAX_VALUE;
        PatioFactory patioFactory = new PatioFactory();
        GardeCorpsFactory gardeCorpsFactory = new GardeCorpsFactory();

        for (Params params : paramsPossibles) {
            Patio patio = patioFactory.creerPatio(params);
            TypeBarreau typeBarreau = TypeBarreau.MODELE3;
            patio.assignerGardeCorps(gardeCorpsFactory.creerGardeCorps(new TypeBois("4X4"), new TypeBois("2X2"), patio.getDimensions(), magiqueDTO.ha_rampe, typeBarreau, true, new Point3D(0,0,0)));
            ArrayList<NormeResultat> resultats = this.validateurNorme.validerNormes(params, patio);
            patio.setValide(resultats.stream().allMatch(NormeResultat::isRespecte));

            if (patio.getValide()) {
                ListeMateriel listeMateriel = new ListeMateriel(patio.getPlanchesEtType(), prixDTO);
                if (listeMateriel.getPrixTotalFloat() < cheapestPrice) {
                    cheapestPrice = listeMateriel.getPrixTotalFloat();
                    cheapestParams = params;
                }
            }
        }
        if (cheapestParams == null) {
            throw new Exception("erreur");
        }
        return cheapestParams;
    }

    private ArrayList<Params> getAllPossibleParams(MagiqueDTO magiqueDTO) {
        ArrayList<Params> paramsPossible = new ArrayList<>();

        ArrayList<TypeBois> typesPoteaux = getTypePoteauPossibles();
        float max_esp_solives = getMaxEspSolives(magiqueDTO.typeRecouvre);

        for (TypeBois typePoteau : typesPoteaux) {
            for(int nb_poteaux : getNbPoteauxMinimumPossible(typePoteau, magiqueDTO.la_patio)) {
                for (TypeBois typeSolive : getTypeSolivePossible()) {
                    for (Float lo_porteafoAdmissible : getLoPorteAFoAdmissible(typeSolive)) {
                        for (int nb_poutres : getNombrePoutreMinimum(magiqueDTO.lo_patio, lo_porteafoAdmissible, typePoteau)) {
                            for (TypeBois typePoutre : getTypePoutrePossible()) {
                                float lo_disp_esp_solives;
                                if (nb_poutres > 2) {
                                    lo_disp_esp_solives = magiqueDTO.la_patio - 2 * ValeurNominale.fromString("2").getValeurReelle();
                                } else {
                                    lo_disp_esp_solives = magiqueDTO.la_patio - ValeurNominale.fromString("2").getValeurReelle();
                                }

                                ArrayList<Integer> nb_solives = getNbSolivesPossible(lo_disp_esp_solives, max_esp_solives);
                                for (int nb_solive : nb_solives) {

                                    float lo_porteafo, esp_solives;
                                    esp_solives = lo_disp_esp_solives / (nb_solive - 1);
                                    float esp_poteaux = (magiqueDTO.la_patio - typePoteau.getLargeur().getValeurReelle()) / (nb_poteaux - 1);
                                    int nb_plisPoutre0 = PatioFactory.calculerNbPlisPoutre(esp_poteaux, nb_poutres, esp_solives, typeSolive, 0);

                                    if (lo_porteafoAdmissible == 0) {
                                        lo_porteafo = 0;
                                    } else {
                                        lo_porteafo = lo_porteafoAdmissible - nb_plisPoutre0 * ValeurNominale.DEUX.getValeurReelle();
                                    }

                                    Params params = new Params(nb_solive,
                                            esp_solives,
                                            typeSolive,
                                            lo_porteafo,
                                            magiqueDTO.la_patio,
                                            magiqueDTO.lo_patio,
                                            magiqueDTO.ha_patio,
                                            nb_poutres,
                                            typePoutre,
                                            magiqueDTO.la_patio,
                                            (magiqueDTO.lo_patio - lo_porteafoAdmissible - typePoteau.getLargeur().getValeurReelle() / 2) / (nb_poutres - 1),
                                            typePoteau,
                                            nb_poteaux,
                                            esp_poteaux,
                                            magiqueDTO.esp_recouvre,
                                            magiqueDTO.typeRecouvre);

                                    paramsPossible.add(params);
                                }
                            }
                        }
                    }
                }
            }
        }

        return paramsPossible;
    }

    private ArrayList<TypeBois> getTypePoutrePossible() {
        ArrayList<TypeBois> typePoutre = new ArrayList<>();
        typePoutre.add(new TypeBois("2X6"));
        typePoutre.add(new TypeBois("2X8"));
        typePoutre.add(new TypeBois("2X10"));
        typePoutre.add(new TypeBois("2X12"));
        return typePoutre;
    }

    private ArrayList<Integer> getNombrePoutreMinimum(float lo_patio, float lo_porteafo_admissible, TypeBois typePoteau) {
        ArrayList<Integer> nbPoutres = new ArrayList<>();

        float lo_disp;
        if (lo_porteafo_admissible < typePoteau.getLargeur().getValeurReelle()/2) {
            lo_disp = lo_patio - typePoteau.getLargeur().getValeurReelle()/2 - (0.5f) * (3 * ValeurNominale.DEUX.getValeurReelle());
        }
        else {
            lo_disp = lo_patio - lo_porteafo_admissible - (0.5f) * (3 * ValeurNominale.DEUX.getValeurReelle());
        }
        for (int i = 2; i < lo_disp; i++) {
            float esp_poutre = lo_disp / (i - 1);
            if (esp_poutre <= 16 * 12 && esp_poutre >= 12) {
                nbPoutres.add(i);
            }
            if (nbPoutres.size() == 6) {
                return nbPoutres;
            }
        }
        return nbPoutres;
    }

    private ArrayList<Float> getLoPorteAFoAdmissible(TypeBois typeSolives) {
        ArrayList<Float> loPorteAFoPoss = new ArrayList<>();
        loPorteAFoPoss.add(Float.parseFloat(normes.get("Norme 2C").getInfo("NULL", typeSolives.toString())));
        loPorteAFoPoss.add(0.0f);
        return loPorteAFoPoss;
    }

    private ArrayList<Integer> getNbPoteauxMinimumPossible(TypeBois typePoteau, float la_patio) {
        float lo_disp_esp = la_patio - typePoteau.getLargeur().getValeurReelle();
        int min_poteau = (int) Math.ceil(lo_disp_esp / 96.0f) + 1;
        ArrayList<Integer> poteaux = new ArrayList<>();
        poteaux.add(min_poteau);
        poteaux.add(min_poteau + 1);
        poteaux.add(min_poteau + 2);

        return poteaux;
    }

    private ArrayList<TypeBois> getTypeSolivePossible() {
        ArrayList<TypeBois> typeSolive = new ArrayList<>();
        typeSolive.add(new TypeBois("2X4"));
        typeSolive.add(new TypeBois("2X6"));
        typeSolive.add(new TypeBois("2X8"));
        typeSolive.add(new TypeBois("2X10"));
        typeSolive.add(new TypeBois("2X12"));
        return typeSolive;
    }

    private ArrayList<TypeBois> getTypePoteauPossibles() {
        ArrayList<TypeBois> typesPoteaux = new ArrayList<>();
        typesPoteaux.add(new TypeBois("4X4"));
        typesPoteaux.add(new TypeBois("6X6"));
        return typesPoteaux;
    }

    private Float getMaxEspSolives(TypeBois typeRecouvre) {
        if (typeRecouvre.toString().equals("2X6")) {
            return 18f;
        } else {
            return 12f;
        }
    }

    private ArrayList<Integer> getNbSolivesPossible(float lo_disp_solives, float max_esp_solives) {
        ArrayList<Integer> list_nb_solives_poss = new ArrayList<>();
        for (int i = 2; i < lo_disp_solives; i++) {
            float esp_solive = lo_disp_solives / i;
            if (esp_solive <= max_esp_solives) {
                list_nb_solives_poss.add(i);
            }
            if (list_nb_solives_poss.size() == 6) {
                return list_nb_solives_poss;
            }
        }
        return list_nb_solives_poss;
    }

    public void setPrixDTO(PrixDTO prixDTO) {
        this.prixDTO = prixDTO;
    }
}
