package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.mesh.Point3D;

import java.io.Serializable;
import java.util.ArrayList;

public class PatioFactory implements Serializable {

    public Patio creerPatio(Params params) {

        float longueurPatioAjustee = ajusterLongueurPatio(params);
        calculerEspacementPoutres(params, longueurPatioAjustee);

        ArrayList<Poutre> poutres = creerPoutres(params);
        ArrayList<Section> sections = creerSections(params, poutres);
        ArrayList<RecouvrementPlanche> recouvrement = creerRecouvrement(params, longueurPatioAjustee);

        return new Patio(poutres, sections, recouvrement);

    }

    private int calculerNbSections(Params params) {
        return params.nb_poutres - 1;
    }

    private ArrayList<Poutre> creerPoutres(Params params) {

        ArrayList<Poutre> poutres = new ArrayList<>();
        ArrayList<Poteau> poteaux = new ArrayList<>();

        TypeBois typeBois = determinerTypeBoisPoutreToute(params);
        float longueurPoteau = params.ha_patio - params.typeRecouvre.getLargeur().getValeurReelle() - params.typeSolives.getHauteur().getValeurReelle() - typeBois.getHauteur().getValeurReelle();

        for (int noPoutre = 0; noPoutre < params.nb_poutres; noPoutre++) {

            ArrayList<PoutrePlanche> plis = creerPlisPoutre(params, longueurPoteau, noPoutre, typeBois);

            if (noPoutre < params.nb_poutres) {
                poteaux = creerPoteaux(params, longueurPoteau, noPoutre, plis);
            }
            Point3D positionPoutre = calculerPositionPoutre(params, longueurPoteau, noPoutre);

            Poutre poutre = new Poutre(params.lo_poutres, typeBois, positionPoutre, plis, poteaux, Orientation.PARALY);
            poutres.add(poutre);
        }

        return poutres;
    }

    private TypeBois determinerTypeBoisPoutreToute(Params params) {

        TypeBois typeBois = new TypeBois("2X2");
        if (params.typePoutre != null) {
            return params.typePoutre;
        }

        for (int noPoutre = 0; noPoutre < params.nb_poutres; noPoutre++) {
            TypeBois typeBoisRetourne = determinerTypeBoisPoutre(params, noPoutre);
            if (typeBoisRetourne.getHauteur().getValeurReelle() > typeBois.getHauteur().getValeurReelle()) {
                typeBois = typeBoisRetourne;
            }
        }
        return typeBois;
    }

    private ArrayList<PoutrePlanche> creerPlisPoutre(Params params, float longueurPoteau, int noPoutre, TypeBois typeBois) {

        ArrayList<PoutrePlanche> plis = new ArrayList<>();
        int nbPlisPoutre = calculerNbPlisPoutre(params.esp_poteaux, params.nb_poutres, params.esp_solives, params.typeSolives, noPoutre);
        if (nbPlisPoutre == 2 && typeBois.toString().equals("2X8") && determinerTypeBoisPoutre(params, noPoutre).toString().equals("2X10")) {
            nbPlisPoutre = 3;
        }
        if (nbPlisPoutre == 2 && typeBois.toString().equals("2X10") && determinerTypeBoisPoutre(params, noPoutre).toString().equals("2X12")) {
            nbPlisPoutre = 3;
        }

        for (int noPli = 0; noPli < nbPlisPoutre; noPli++) {

            Point3D position = calculerPositionPli(params, longueurPoteau, noPoutre, noPli, typeBois);
            PoutrePlanche pli = new PoutrePlanche(params.lo_poutres, typeBois, position, Orientation.PARALY);
            plis.add(pli);
        }
        return plis;
    }

    public static int calculerNbPlisPoutre(float esp_poteaux, int nb_poutres, float esp_solives, TypeBois typeSolives, int noPoutre) {

        int porteeSolives = calculPorteeMaximalSolive(esp_solives, typeSolives);
        int espacementPoteaux = (int) Math.ceil(esp_poteaux);

        if (noPoutre != 0 && noPoutre != (nb_poutres - 1)) {
            if (espacementPoteaux <= 48) {
                if (porteeSolives <= 84) {
                    return 1;
                }
                else {
                    return 2;
                }
            }
            else if (espacementPoteaux <= 72) {
                return 2;
            }
            else {
                if (porteeSolives <= 132) {
                    return 2;
                }
                else {
                    return 3;
                }
            }
        }
        else {
            if (espacementPoteaux <= 48) {
                if (porteeSolives <= 180) {
                    return 1;
                }
                else {
                    return 2;
                }
            }
            else if (espacementPoteaux <= 72) {
                if (porteeSolives <= 84) {
                    return 1;
                }
                else {
                    return 2;
                }
            }
            else {
                return 2;
            }
        }
    }

    private TypeBois determinerTypeBoisPoutre(Params params, int noPoutre) {

        int porteeSolives = calculPorteeMaximalSolive(params.esp_solives, params.typeSolives);
        float espacementPoteaux = params.esp_poteaux;

        if (noPoutre != 0 && noPoutre != (params.nb_poutres - 1)) {
            if (espacementPoteaux <= 48) {
                if (porteeSolives <= 180) {
                    return new TypeBois("2X6");
                }
                else {
                    return new TypeBois("2X8");
                }
            }
            else if (espacementPoteaux <= 72) {
                if (porteeSolives <= 84) {
                    return new TypeBois("2X6");
                }
                else if (porteeSolives <= 120) {
                    return new TypeBois("2X8");
                }
                else if (porteeSolives <= 180) {
                    return new TypeBois("2X10");
                }
                else {
                    return new TypeBois("2X12");
                }
            }
            else {
                if (porteeSolives <= 60) {
                    return new TypeBois("2X8");
                }
                else if (porteeSolives <= 96) {
                    return new TypeBois("2X10");
                }
                else if (porteeSolives <= 132) {
                    return new TypeBois("2X12");
                }
                else if (porteeSolives <= 168) {
                    return new TypeBois("2X10");
                }
                else {
                    return new TypeBois("2X12");
                }
            }
        }
        else {
            if (espacementPoteaux <= 48) {
                return new TypeBois("2X6");
            }
            else if (espacementPoteaux <= 72) {
                if (porteeSolives <= 168) {
                    return new TypeBois("2X6");
                }
                else {
                    return new TypeBois("2X8");
                }
            }
            else {
                if (porteeSolives <= 84) {
                    return new TypeBois("2X6");
                }
                else if (porteeSolives <= 132) {
                    return new TypeBois("2X8");
                }
                else {
                    return new TypeBois("2X10");
                }
            }
        }
    }

    private Point3D calculerPositionPoutre(Params params, float longueurPoteau, int noPoutre) {

        float positionX = params.lo_porteafo + (noPoutre * params.esp_poutres);
        float positionY = 0;
        float positionZ = longueurPoteau;
        return new Point3D(positionX, positionY, positionZ);
    }

    private Point3D calculerPositionPli(Params params, float longueurPoteau, int noPoutre, int noPli, TypeBois typeBois) {

        Point3D positionPoutre = calculerPositionPoutre(params, longueurPoteau, noPoutre);
        float positionX = positionPoutre.getX() + (typeBois.getLargeur().getValeurReelle() * noPli);
        float positionY = 0;
        float positionZ = longueurPoteau;
        return new Point3D(positionX, positionY, positionZ);
    }

    private Point3D calculerPositionPoteau(Params params, float longueurPoteau, int noPoutre, int noPoteau, float largeurPoutre) {

        Point3D positionPoutre = calculerPositionPoutre(params, longueurPoteau, noPoutre);
        float positionPoteauX = positionPoutre.getX() - ((params.typePoteau.getLargeur().getValeurReelle() - largeurPoutre)/2);
        float positionPoteauY = noPoteau * params.esp_poteaux;
        float positionPoteauZ = 0;

        return new Point3D(positionPoteauX, positionPoteauY, positionPoteauZ);
    }

    private ArrayList<Poteau> creerPoteaux(Params params, float longueurPoteau, int noPoutre, ArrayList<PoutrePlanche> plis) {

        ArrayList<Poteau> poteaux = new ArrayList<>();

        float largeurPoutre = plis.size() * plis.get(0).getTypebois().getLargeur().getValeurReelle();
        if (noPoutre == params.nb_poutres - 1) {
            return poteaux;
        }
        else {
            for (int noPoteau = 0; noPoteau < params.nb_poteaux; noPoteau++) {
                Point3D position = calculerPositionPoteau(params, longueurPoteau, noPoutre, noPoteau, largeurPoutre);
                Poteau poteau = new Poteau(longueurPoteau, params.typePoteau, position, Orientation.PARALZ);
                poteaux.add(poteau);
            }
        }
        return poteaux;
    }

    private ArrayList<Section> creerSections(Params params, ArrayList<Poutre> poutres) {

        ArrayList<Section> sections = new ArrayList<>();
        int nbSections = calculerNbSections(params);

        for (int noSection = 0; noSection < nbSections; noSection++) {
            ArrayList<Solive> solives = new ArrayList<>();

            if (noSection == 0) {
                float positionZ = params.ha_patio - params.typeRecouvre.getLargeur().getValeurReelle() - params.typeSolives.getHauteur().getValeurReelle();
                Point3D position = new Point3D(0, 0, positionZ);
                Solive plancheDeBout = new Solive(params.getLa_patio(), params.getTypeSolives(), position, Orientation.PARALY);
                solives.add(plancheDeBout);
            }

            for (int noSolive = 0; noSolive < params.nb_solives; noSolive++) {

                Point3D positionSolive = calculerPositionSolive(params, noSection, noSolive);
                float largeurProchainePoutre = poutres.get(noSection + 1).getLargeur();
                Solive solive;
                if (noSection == 0) {
                    solive = new Solive((params.esp_poutres + params.lo_porteafo + largeurProchainePoutre - params.typeSolives.getLargeur().getValeurReelle()),
                        params.typeSolives, positionSolive, Orientation.PARALX);
                }
                else {
                    solive = new Solive(params.esp_poutres + largeurProchainePoutre, params.typeSolives, positionSolive, Orientation.PARALX);
                }
                solives.add(solive);
            }
            Section section = new Section(solives);
            sections.add(section);
        }

        return sections;
    }

    private Point3D calculerPositionSolive(Params params, int noSection, int noSolive) {

        float positionX;
        float positionY;
        if (calculerNbSections(params) > 1) {
            positionY = noSolive * params.esp_solives + params.typeSolives.getLargeur().getValeurReelle();
        }
        else {
            positionY = noSolive * params.esp_solives;
        }

        if (noSection % 2 != 0) {
            positionY = noSolive * params.esp_solives;
        }
        float positionZ = params.ha_patio - params.typeRecouvre.getLargeur().getValeurReelle() - params.typeSolives.getHauteur().getValeurReelle();

        if (noSection == 0) {
            positionX = params.getTypeSolives().getLargeur().getValeurReelle();
        }
        else {
            positionX = params.lo_porteafo + (noSection * params.esp_poutres);
        }
        return new Point3D(positionX, positionY, positionZ);
    }

    private ArrayList<RecouvrementPlanche> creerRecouvrement(Params params, float longueurPatioAjustee) {

        ArrayList<RecouvrementPlanche> recouvrement = new ArrayList<>();
        float planchesEtEspace = params.typeRecouvre.getHauteur().getValeurReelle() + params.esp_recouvre;
        int nbPlanchesRecouvrement = (int) Math.ceil(longueurPatioAjustee/ planchesEtEspace);
        float dernierePlanche = longueurPatioAjustee % planchesEtEspace;

        float positionX;
        float positionY = 0;
        float positionZ = params.ha_patio - params.typeRecouvre.getLargeur().getValeurReelle();
        Point3D position;

        for (int numeroPlanche = 0; numeroPlanche < nbPlanchesRecouvrement - 1; numeroPlanche++) {
            positionX = numeroPlanche * planchesEtEspace;
            position = new Point3D(positionX, positionY, positionZ);
            RecouvrementPlanche planche = new RecouvrementPlanche(params.lo_poutres, params.typeRecouvre, position, Orientation.PARALY_PERPZ);
            recouvrement.add(planche);
        }

        if (dernierePlanche > 0) {
            if (dernierePlanche > params.typeRecouvre.getHauteur().getValeurReelle()) {
                dernierePlanche = params.typeRecouvre.getHauteur().getValeurReelle();
            }
            positionX = (nbPlanchesRecouvrement - 1) * planchesEtEspace;
            position = new Point3D(positionX, positionY, positionZ);
            RecouvrementPlanche planche = new RecouvrementPlanche(params.lo_poutres, params.typeRecouvre.getLargeur().getValeurReelle(), dernierePlanche,
                    params.typeRecouvre, position, Orientation.PARALY_PERPZ);
            recouvrement.add(planche);
        }

        if (dernierePlanche == 0) {
            positionX = (nbPlanchesRecouvrement - 1) * planchesEtEspace;
            position = new Point3D(positionX, positionY, positionZ);
            RecouvrementPlanche planche = new RecouvrementPlanche(params.lo_poutres, params.typeRecouvre.getLargeur().getValeurReelle(),
                params.getTypeRecouvre().getHauteur().getValeurReelle(), params.typeRecouvre, position, Orientation.PARALY_PERPZ);
            recouvrement.add(planche);
        }

        return recouvrement;
    }

    public static int calculPorteeMaximalSolive(float esp_solives, TypeBois typeSolives) {
        if (esp_solives <= 8) {
            if (typeSolives.equals(new TypeBois("2X4"))) {
                return 88;
            }
            if (typeSolives.equals(new TypeBois("2X6"))) {
                return 138;
            }
            if (typeSolives.equals(new TypeBois("2X8"))) {
                return 181;
            }
            if (typeSolives.equals(new TypeBois("2X10"))) {
                return 231;
            }
            else {
                return 281;
            }
        }
        else if (esp_solives <= 12) {
            if (typeSolives.equals(new TypeBois("2X4"))) {
                return 77;
            }
            if (typeSolives.equals(new TypeBois("2X6"))) {
                return 120;
            }
            if (typeSolives.equals(new TypeBois("2X8"))) {
                return 158;
            }
            if (typeSolives.equals(new TypeBois("2X10"))) {
                return 202;
            }
            else {
                return 244;
            }
        }
        else if (esp_solives <= 16) {
            if (typeSolives.equals(new TypeBois("2X4"))) {
                return 70;
            }
            if (typeSolives.equals(new TypeBois("2X6"))) {
                return 109;
            }
            if (typeSolives.equals(new TypeBois("2X8"))) {
                return 144;
            }
            if (typeSolives.equals(new TypeBois("2X10"))) {
                return 182;
            }
            else {
                return 211;
            }
        }
        else {
            if (typeSolives.equals(new TypeBois("2X4"))) {
                return 61;
            }
            if (typeSolives.equals(new TypeBois("2X6"))) {
                return 96;
            }
            if (typeSolives.equals(new TypeBois("2X8"))) {
                return 122;
            }
            if (typeSolives.equals(new TypeBois("2X10"))) {
                return 149;
            }
            else {
                return 172;
            }
        }
    }

    private float ajusterLongueurPatio(Params params) {
        int nbPlisPremierePoutre = calculerNbPlisPoutre(params.esp_poteaux, params.nb_poutres, params.esp_solives, params.typeSolives, 0);
        TypeBois typeBoisPremierePoutre = determinerTypeBoisPoutreToute(params);
        float largeurPoutre = nbPlisPremierePoutre * typeBoisPremierePoutre.getLargeur().getValeurReelle();
        float espacePoutrePoteau = (params.typePoteau.getLargeur().getValeurReelle() - largeurPoutre) / 2;
        if (params.lo_porteafo < espacePoutrePoteau) {
            return (params.lo_patio - (espacePoutrePoteau));
        }
        else {
            return params.lo_patio;
        }
    }

    private void calculerEspacementPoutres(Params params, float longueurPatioAjustee) {
        int nbPlisDernierePoutre = calculerNbPlisPoutre(params.esp_poteaux, params.nb_poutres, params.esp_solives, params.typeSolives, params.nb_poutres - 1);
        TypeBois typeBoisDernierePoutre = determinerTypeBoisPoutreToute(params);
        float largeurPoutre = nbPlisDernierePoutre * typeBoisDernierePoutre.getLargeur().getValeurReelle();

        float espacementPoutres = (longueurPatioAjustee - params.lo_porteafo - largeurPoutre) / (params.nb_poutres - 1);
        params.esp_poutres = espacementPoutres;
    }

}
