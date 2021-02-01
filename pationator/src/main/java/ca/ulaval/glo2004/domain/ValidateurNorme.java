package ca.ulaval.glo2004.domain;

import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.domain.patio.Params;
import ca.ulaval.glo2004.domain.patio.Patio;
import ca.ulaval.glo2004.domain.patio.Poutre;
import ca.ulaval.glo2004.domain.patio.TypeBois;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ValidateurNorme implements Serializable {
    private final HashMap<String, TypeNorme> normes;

    public ValidateurNorme(HashMap<String, TypeNorme> normes) {
        this.normes = normes;
    }

    public ArrayList<NormeResultat> validerNormes(Params params, Patio patio) {
        ArrayList<NormeResultat> resultats = new ArrayList<>();

        resultats.add(validerPorteafo(patio, params));
        resultats.add(validerRecouvrement(params));
        resultats.add(validerEspacementPoutres(patio, params));
        resultats.add(validerEspacementLargeurPoteaux(params));
        resultats.add(validerEspacementLongueurPoteaux(params));
        resultats.add(validerEspacementSolives(params));
        resultats.add(validerLongueurPoteaux(patio, params));
        resultats.add(validerPresenceRampeSiNecessaire(patio));

        for (int noPoutre = 0; noPoutre < params.getNb_poutres() - 1; noPoutre++) {
            resultats.add(validerTypePoteaux(patio, params, noPoutre));
        }

        if (!(params.getEsp_solives() > 24)) {
            for (int noSection = 0; noSection < params.getNb_poutres() - 1; noSection++) {
                resultats.add(validerPorteeSolives(patio, params, noSection));
            }
        }

        if (!(params.getEsp_poteaux() > 96)) {
            if (params.getNb_poutres() > 2 && getPorteeSolivesMax(patio, params, params.getNb_poutres() - 1) > 16) {
                resultats.add(new NormeResultat("Norme 6B", false, "Les solives de toutes les sections sont trop longues pour pouvoir valider la sélection " +
                    "des poutres, les solives doivent au maximum mesurer 16 pieds!", "Il est possible de corriger ce problème en ajoutant des poutres"));
            }
            else {
                resultats.add(validerPoutresPorteeSimple(patio, params, 0));
                resultats.add(validerPoutresPorteeSimple(patio, params, params.nb_poutres - 1));
                for (int noPoutre = 1; noPoutre < params.nb_poutres - 1; noPoutre++) {
                    resultats.add(validerPoutresPorteeDouble(patio, params, noPoutre));
                }
            }
        }
        return resultats;
    }

    private NormeResultat validerPorteafo(Patio patio, Params params) {
        TypeNorme normesPorteafo = normes.get("Norme 2C");
        String valeurX = "NULL";
        String valeurY = params.typeSolives.toString();
        String normeApplicable = normesPorteafo.getInfo(valeurX, valeurY);
        int normeApplicableEnPouces = Integer.parseInt(normeApplicable);

        if (params.lo_porteafo + (patio.getPoutres().get(0).getLargeur() / 2)  <= normeApplicableEnPouces) {
            return new NormeResultat("Norme 2C", true, "Le porte-à-faux est de longueur adéquate", "");
        }
        else {
            return new NormeResultat("Norme 2C", false, "Le porte-à-faux est trop long pour respecter les normes!", "Pour des solives de taille " + valeurY +
                " la longueur maximale du porte-à-faux est de " + normeApplicable + " pouces");
        }
    }

    private NormeResultat validerRecouvrement(Params params) {
        TypeNorme normeRecouvrement = normes.get("Norme Recouvrement");
        String valeurX = "NULL";
        String valeurY = params.getTypeRecouvre().toString();
        String normeApplicable = normeRecouvrement.getInfo(valeurX, valeurY);
        int normeApplicableEnPouces = Integer.parseInt(normeApplicable);

        if (params.getEsp_solives() <= normeApplicableEnPouces) {
            return new NormeResultat("Norme Recouvrement", true, "L'espacement des solives est adéquat pour le type de recouvrement", "");
        }
        else {
            return new NormeResultat("Norme Recouvrement", false, "L'espacement des solives est trop grand pour le type de recouvrement choisi!", "Pour un " +
                "type de recouvrement " + valeurY + " l'espacement maximal des solives est de " + normeApplicableEnPouces + " pouces");
        }
    }

    private NormeResultat validerEspacementPoutres(Patio patio, Params params) {
        if (params.getEsp_poutres() < getPlusGrandePoutreLargeur(patio)) {
            return new NormeResultat("Espacement poutres", false, "Les poutres sont trop près les unes de autres pour respecter les normes!", "L'espacement " +
                "des poutres minimal est la largeur de la poutre la plus large");
        }
        else return new NormeResultat("Espacement poutres", true, "Les poutres sont assez éloignées les unes des autres!", "");
    }

    private NormeResultat validerEspacementLargeurPoteaux(Params params) {
        if (params.getEsp_poteaux() < params.getTypePoteau().getLargeur().getValeurReelle()) {
            return new NormeResultat("Espacement poteaux largeur", false, "Les poteaux sont trop près les uns des autres pour respecter les normes", "Les " +
                "poteaux doivent avoir un espacement au moins égal à leur largeur");
        }
        else if (params.getEsp_poteaux() > 96) {
            return new NormeResultat("Espacement poteaux largeur", false, "Les poteaux sont trop espacés pour respecter les normes!", "L'espacement des " +
                "poteaux maximal accepté par le guide est de 96 pouces");
        }
        else return new NormeResultat("Espacement poteaux largeur", true, "Les poteaux ont un espacement qui respecte les normes", "");
    }

    private NormeResultat validerEspacementLongueurPoteaux(Params params) {
        if (params.getEsp_poutres() < params.getTypePoteau().getHauteur().getValeurReelle()) {
            return new NormeResultat("Espacement poteaux longueur", false, "Les poteaux sont trop près les uns des autres pour " +
                "respecter les normes", "Les poteaux doivent avoir un espacement au moins égal à leur largeur");
        }
        else return new NormeResultat("Espacement poteaux longueur", true, "Les poteaux ont un espacement qui respecte les normes", "");
    }

    private NormeResultat validerEspacementSolives(Params params) {
        if (params.getEsp_solives() > 24) {
            return new NormeResultat("Espacement solives",false, "Les solives sont trop espacées pour respecter les normes!", "L" +
                "'espacement des solives maximal accepté par le guide est de 24 pouces");
        }
        else if (params.getEsp_solives() < params.getTypeSolives().getLargeur().getValeurReelle() && params.nb_poutres == 2) {
            return new NormeResultat("Espacement solives",false, "Les solives sont trop près les unes de autres pour respecter les normes!", "L" +
                "'espacement des solives doit être supérieur à leur largeur");
        }
        else if (params.getEsp_solives() < params.getTypeSolives().getLargeur().getValeurReelle() * 2 && params.nb_poutres > 2) {
            return new NormeResultat("Espacement solives",false, "Les solives sont trop près les unes de autres pour respecter les normes!", "L" +
                    "'espacement des solives doit être supérieur à deux fois leur largeur");
        }
        else return new NormeResultat("Espacement solives", true, "Les solives ont un espacement qui respecte les normes", "");
    }

    private NormeResultat validerLongueurPoteaux(Patio patio, Params params) {
        TypeNorme normeLongueurPoteaux = normes.get("Norme Longueur Poteaux");
        String valeurX = "NULL";
        String valeurY = params.getTypePoteau().toString();
        String normeApplicable = normeLongueurPoteaux.getInfo(valeurX, valeurY);

        String[] PiedsPouces = normeApplicable.split("-");
        int normeApplicableEnPouce = (Integer.parseInt(PiedsPouces[0]) * 12) + Integer.parseInt(PiedsPouces[1]);

        float longueurPoteaux = patio.getPoteaux().get(0).getPlancheLongueur();

        if (longueurPoteaux <= normeApplicableEnPouce) {
            return new NormeResultat("Norme Longueur Poteaux", true, "Les poteaux du patio sont de longueur adéquate", "");
        }
        else {
            return new NormeResultat("Norme Longueur Poteaux", false, "Les poteaux sont trop longs pour le type de bois choisi!", "Pour des poteaux de taille" +
                " " + valeurY + " la longueur maximal des poteaux est de " + normeApplicableEnPouce + " pouces");
        }

    }

    private NormeResultat validerTypePoteaux(Patio patio, Params params, int noPoutre) {
        TypeBois typePoteaux = params.getTypePoteau();
        int nbPlis = patio.getPoutres().get(noPoutre).getPlis().size();

        if (typePoteaux.toString().equals("4X4") && nbPlis == 3) {
            return new NormeResultat("Type poteaux", false, "Le type de bois choisi pour les poteaux soutenant la poutre numéro " + (noPoutre + 1) + " ne " +
                "respecte pas les exigences minimales d'appui!", "Pour une poutre composée de trois plis, des poteaux de 6X6 sont nécéssaires");
        }
        else {
            return new NormeResultat("Type poteaux", true, "Le type de bois choisi pour les poteaux soutenant la poutre numéro " + (noPoutre + 1) + " " +
                "respecte les exigences minimales d'appui!", "");
        }
    }

    private NormeResultat validerPorteeSolives(Patio patio, Params params, int noSection) {
        TypeNorme normesPorteeSolives = normes.get("Norme 2B");
        String valeurX = getEspSoliveMax(params);
        String valeurY = params.getTypeSolives().toString();
        String normeApplicable = normesPorteeSolives.getInfo(valeurX, valeurY);

        String[] PiedsPouces = normeApplicable.split("-");
        int normeApplicableEnPouce = (Integer.parseInt(PiedsPouces[0]) * 12) + Integer.parseInt(PiedsPouces[1]);

        float porteeSolives = patio.getSections().get(noSection).getSolives().get(1).getPlancheLongueur();

        if (noSection == 0) {
            porteeSolives = porteeSolives + params.getTypeSolives().getLargeur().getValeurReelle();
        }

        if (porteeSolives <= normeApplicableEnPouce) {
            return new NormeResultat("Norme 2B", true, "Les solives de la section " + (noSection + 1) + " sont de longueur adéquate", "");
        }
        else {
            return new NormeResultat("Norme 2B",false, "Les solives de la section " + (noSection + 1) + " sont trop longues pour respecter les normes!", "Pour un " +
                    "espacement des solives de " + valeurX + " pouces et des solives de taille " + valeurY + ", la longueur maximum des solives est de " + normeApplicableEnPouce + " pouces");
        }
    }

    private NormeResultat validerPoutresPorteeSimple(Patio patio, Params params, int noPoutre) {
        TypeNorme normePoutreSimple = normes.get("Norme 4B");
        String valeurX = getEspPoteauxMax(params);

        int porteeSolivesMax = getPorteeSolivesMax(patio, params, noPoutre);

        if (porteeSolivesMax > 16) {
            return new NormeResultat("Norme 4B", false,
                "Les solives reposant sur le côté droit de la poutre numéro " + (noPoutre + 1) + " sont trop longues pour pouvoir valider la sélection " +
                "des poutres, les solives doivent au maximum mesurer 16 pieds!", "Il est possible de corriger ce problème en ajoutant des poutres");
        }

        String valeurY = String.valueOf(porteeSolivesMax);

        try {
            String normeApplicable = normePoutreSimple.getInfo(valeurX, valeurY);

            String[] nbPlisEtDimensions = normeApplicable.split("-");
            int nbPlisNorme = Integer.parseInt(nbPlisEtDimensions[0]);
            TypeBois typeBoisPoutreNorme = new TypeBois(nbPlisEtDimensions[1]);

            int nbPlisPatio = patio.getPoutres().get(noPoutre).getPlis().size();
            TypeBois typeBoisPoutrePatio = patio.getPoutres().get(noPoutre).getPlis().get(0).getTypebois();

            if (nbPlisPatio >= nbPlisNorme && typeBoisPoutrePatio.getHauteur().getValeurReelle() >= typeBoisPoutreNorme.getHauteur().getValeurReelle()) {
                return new NormeResultat("Norme 4B", true, "La poutre numéro " + (noPoutre + 1) + " respecte les normes de portee simple", "");
            }
            if ((nbPlisNorme == 2 && typeBoisPoutreNorme.equals(new TypeBois("2X10"))) && (nbPlisPatio == 3 && typeBoisPoutrePatio.equals(new TypeBois("2X8")))) {
                return new NormeResultat("Norme 4B", true, "La poutre numéro " + (noPoutre + 1) + " respecte les normes de portee simple", "");
            }
            if ((nbPlisNorme == 2 && typeBoisPoutreNorme.equals(new TypeBois("2X12"))) && (nbPlisPatio == 3 && typeBoisPoutrePatio.equals(new TypeBois("2X10")))) {
                return new NormeResultat("Norme 4B", true, "La poutre numéro " + (noPoutre + 1) + " respecte les normes de portee simple", "");
            }
            return new NormeResultat("Norme 4B", false, "La poutre numéro " + (noPoutre + 1) + " ne respecte pas les normes de portee simple", "Pour un " +
                "espacement " +
                    "des poteaux de " + valeurX + " pouces et une portée des solives maximale de " + valeurY + " pouces, la taille des poutres à portée " +
                    "simple doit être de " + typeBoisPoutreNorme.toString() + " ou plus grande et le nombre de plis doit être de " + nbPlisNorme + " ou plus.");
        } catch (Exception exception) {
            return new NormeResultat("Norme 4B", false, "Oups, ceci est une erreur complètement imprévue par les élèves!", "Veuillez aviser les élèves si " +
                "elle est lancée! merci :)");
        }
    }

    private NormeResultat validerPoutresPorteeDouble(Patio patio, Params params, int noPoutre) {
        TypeNorme normePoutreSimple = normes.get("Norme 6B");
        String valeurX = getEspPoteauxMax(params);

        int porteeSolivesMax = getPorteeSolivesMax(patio, params, noPoutre);

        if (porteeSolivesMax > 16) {
            return new NormeResultat("Norme 6B", false,
                "Les solives reposant sur le côté gauche de la poutre numéro " + (noPoutre + 1) + " sont trop longues pour pouvoir valider la sélection " +
                    "des poutres, les solives doivent au maximum mesurer 16 pieds!", "Il est possible de corriger ce problème en ajoutant des poutres");
        }

        String valeurY = String.valueOf(porteeSolivesMax);

        try {
            String normeApplicable = normePoutreSimple.getInfo(valeurX, valeurY);

            String[] nbPlisEtDimensions = normeApplicable.split("-");
            int nbPlisNorme = Integer.parseInt(nbPlisEtDimensions[0]);
            TypeBois typeBoisPoutreNorme = new TypeBois(nbPlisEtDimensions[1]);

            int nbPlisPatio = patio.getPoutres().get(noPoutre).getPlis().size();
            TypeBois typeBoisPoutrePatio = patio.getPoutres().get(noPoutre).getPlis().get(0).getTypebois();

            if (nbPlisPatio >= nbPlisNorme && typeBoisPoutrePatio.getHauteur().getValeurReelle() >= typeBoisPoutreNorme.getHauteur().getValeurReelle()) {
                return new NormeResultat("Norme 6B", true, "La poutre numéro " + (noPoutre + 1) + " respecte les normes de portee double", "");
            }
            if ((nbPlisNorme == 2 && typeBoisPoutreNorme.equals(new TypeBois("2X10"))) && (nbPlisPatio == 3 && typeBoisPoutrePatio.equals(new TypeBois("2X8")))) {
                return new NormeResultat("Norme 6B", true, "La poutre numéro " + (noPoutre + 1) + " respecte les normes de portee double", "");
            }
            if ((nbPlisNorme == 2 && typeBoisPoutreNorme.equals(new TypeBois("2X12"))) && (nbPlisPatio == 3 && typeBoisPoutrePatio.equals(new TypeBois("2X10")))) {
                return new NormeResultat("Norme 6B", true, "La poutre numéro " + (noPoutre + 1) + " respecte les normes de portee double", "");
            }
            return new NormeResultat("Norme 6B", false, "La poutre numéro " + (noPoutre + 1) + " ne respecte pas les normes de portee double", "Pour un espacement " +
                    "des poteaux de " + valeurX + " pouces et une portée des solives maximale de " + valeurY + " pouces, la taille des poutres à portée " +
                    "double doit être de " + typeBoisPoutreNorme.toString() + " ou plus grande et le nombre de plis doit être de " + nbPlisNorme + " ou plus.");
        } catch (Exception exception) {
            return new NormeResultat("Norme 6B", false, "Oups, ceci est une erreur complètement imprévue par les élèves!", "Veuillez aviser les élèves si " +
                "elle est lancée! merci :)");
        }
    }

    private NormeResultat validerPresenceRampeSiNecessaire(Patio patio) {
        Point3D dimensionsPatio = patio.getDimensions();
        if (dimensionsPatio.getZ() >= 24 && dimensionsPatio.getZ() < 72) {
            if (patio.getGardeCorps() == null || patio.getGardeCorps().getHauteur() < 36) {
                return new NormeResultat("Hauteur Rampe", false, "La hauteur de la rampe ne respecte pas les normes", "Pour un patio avec une hauteur de 2 à 6 " +
                        "pieds, une rampe de 36 pouces ou plus est nécéssaire");
            }
            else {
                return new NormeResultat("Hauteur Rampe", true, "La hauteur de la rampe respecte les normes", "");
            }
        }
        else if (dimensionsPatio.getZ() >= 72) {
            if (patio.getGardeCorps() == null || patio.getGardeCorps().getHauteur() < 42) {
                return new NormeResultat("Hauteur Rampe", false, "La hauteur de la rampe ne respecte pas les normes", "Pour un patio avec une hauteur de 6 " +
                        "pieds et plus, une rampe de 42 pouces ou plus est nécéssaire");
            }
            else {
                return new NormeResultat("Hauteur Rampe", true, "La hauteur de la rampe respecte les normes", "");
            }
        }
        else {
            return new NormeResultat("Hauteur Rampe", true, "La hauteur de la rampe respecte les normes", "");
        }
    }

    private String getEspSoliveMax(Params params) {
        float esp_solives = params.getEsp_solives();
        int esp_return = 24;

        int[] esp_solives_max_possible = new int[4];
        esp_solives_max_possible[0] = 24;
        esp_solives_max_possible[1] = 16;
        esp_solives_max_possible[2] = 12;
        esp_solives_max_possible[3] = 8;

        for(int esp_solives_max : esp_solives_max_possible) {
            if(esp_solives <= esp_solives_max) {
                esp_return = esp_solives_max;
            }
        }

        return String.valueOf(esp_return);
    }

    private String getEspPoteauxMax(Params params) {
        float esp_poteaux = params.getEsp_poteaux();
        int esp_return = 96;

        int[] esp_poteaux_max_possible = new int[3];
        esp_poteaux_max_possible[0] = 96;
        esp_poteaux_max_possible[1] = 72;
        esp_poteaux_max_possible[2] = 48;

        for(int esp_poteaux_max : esp_poteaux_max_possible) {
            if(esp_poteaux <= esp_poteaux_max) {
                esp_return = esp_poteaux_max;
            }
        }

        return String.valueOf(esp_return);
    }

    private int getPorteeSolivesMax(Patio patio, Params params, int noPoutre) {
        float porteeSolivesMax;
        if (noPoutre == 0 || noPoutre == (params.nb_poutres - 1)) {
            porteeSolivesMax = patio.getSections().get(Math.max(0, noPoutre - 1)).getSolives().get(1).getPlancheLongueur();

        }
        else {
            float porteeSolivesGauche = patio.getSections().get(noPoutre - 1).getSolives().get(1).getPlancheLongueur();
            float porteeSolivesDroite = patio.getSections().get(noPoutre).getSolives().get(1).getPlancheLongueur();
            porteeSolivesMax = Math.max(porteeSolivesGauche, porteeSolivesDroite);

        }
        porteeSolivesMax = (float) Math.ceil(porteeSolivesMax / 12);
        if (porteeSolivesMax < 4) {
            porteeSolivesMax = 4;
        }
        return  (int)porteeSolivesMax;
    }

    private float getPlusGrandePoutreLargeur(Patio patio) {
        float plusGrandeLargeur = 0;
        for (Poutre poutre : patio.getPoutres()) {
            if (poutre.getLargeur() > plusGrandeLargeur) {
                plusGrandeLargeur = poutre.getLargeur();
            }
        }
        return plusGrandeLargeur;
    }

}
