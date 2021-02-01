package ca.ulaval.glo2004.domain.Rampe;

import ca.ulaval.glo2004.domain.mesh.Orientation;
import ca.ulaval.glo2004.domain.patio.TypeBois;
import ca.ulaval.glo2004.domain.mesh.Point3D;

import java.io.Serializable;
import java.util.ArrayList;

public class GardeCorpsFactory implements Serializable {

    public GardeCorps creerGardeCorps(TypeBois typeBalustres, TypeBois typeBoisTraverse, Point3D dimensionsPatio, float hauteurRampe, TypeBarreau typebarreaux, boolean optimisation, Point3D positionEscalier) {
        TypeBois typeBoisMarches = new TypeBois("2X10");
        ListeChaineeRampe balustres = creerBalustres(typeBalustres, dimensionsPatio, hauteurRampe, positionEscalier, optimisation);
        ArrayList<Parcours> parcours = creerParcours(balustres, typeBoisTraverse, typebarreaux, optimisation, typeBoisMarches);
       return new GardeCorps(balustres, parcours);
    }

    private ListeChaineeRampe creerBalustres(TypeBois typeBalustre, Point3D dimensionsPatio, float hauteurRampe, Point3D positionEscalier, boolean optimisation) {
        ListeChaineeRampe balustres = new ListeChaineeRampe(hauteurRampe, 72);
        creerBalustresCoins(balustres, typeBalustre, dimensionsPatio);
        if (!optimisation) {
            creerBalustresEscalier(balustres, typeBalustre, positionEscalier, 72);
        }

        creerBalustresIntermediaires(balustres, typeBalustre);

        return balustres;
    }

    private void creerBalustresCoins(ListeChaineeRampe balustres, TypeBois typeBalustre, Point3D dimensionsPatio) {
        float epaisseurRampe = 0;
        float positionX;
        float positionY;
        float positionZ = dimensionsPatio.getZ();
        float longueurBalustres = balustres.getHauteurMax() - epaisseurRampe;
        float decalage = 1;

        //coin bas droite patio
        positionX = dimensionsPatio.getX() - typeBalustre.getLargeur().getValeurReelle();
        positionY = dimensionsPatio.getY() - typeBalustre.getLargeur().getValeurReelle() - decalage;
        Point3D coinBasDroitePosition = new Point3D(positionX, positionY, positionZ);
        balustres.ajouterFin(new Balustre(longueurBalustres, typeBalustre, coinBasDroitePosition));

        //coin bas gauche patio
        positionX = decalage;
        positionY = dimensionsPatio.getY() - typeBalustre.getLargeur().getValeurReelle() - decalage;
        Point3D coinBasGauchePosition = new Point3D(positionX, positionY, positionZ);
        balustres.ajouterFin(new Balustre(longueurBalustres, typeBalustre, coinBasGauchePosition));

        //coin haut gauche patio
        positionX = decalage;
        positionY = decalage;
        Point3D coinHautGauchePosition = new Point3D(positionX, positionY, positionZ);
        balustres.ajouterFin(new Balustre(longueurBalustres, typeBalustre, coinHautGauchePosition));

        //coin haut droit patio
        positionX = dimensionsPatio.getX() - typeBalustre.getLargeur().getValeurReelle();
        positionY = decalage;
        Point3D coinHautDroitPosition = new Point3D(positionX, positionY, positionZ);
        balustres.ajouterFin(new Balustre(longueurBalustres, typeBalustre, coinHautDroitPosition));

    }

    private void creerBalustresEscalier(ListeChaineeRampe balustres, TypeBois typeBalustres, Point3D position, float largeur) {
        float epaisseurRampe = 0;
        float hauteurBalustres = balustres.getHauteurMax() - epaisseurRampe;
        float espaceEntreBalustre = largeur - typeBalustres.getLargeur().getValeurReelle();

        if (position.getY() != 0 && position.getX() != 0) {
            NoeudRampe noeudCourant = balustres.getTete();
            Point3D positionCourante = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0);
            Point3D positionDebut = new Point3D(position.getX(), positionCourante.getY(), positionCourante.getZ());
            if (!(noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getX() <= positionDebut.getX() + typeBalustres.getLargeur().getValeurReelle())) {
                Balustre balustreDebut = new Balustre(hauteurBalustres, typeBalustres, positionDebut);
                balustres.ajouterApres(noeudCourant.getBalustre(), balustreDebut);
                noeudCourant = noeudCourant.suivant();
            }
            noeudCourant.setEstEscalier(true);

            Point3D positionFin = new Point3D(positionDebut.getX() - espaceEntreBalustre, positionDebut.getY(), positionDebut.getZ());
            if (!(noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(3).getX() >= positionFin.getX())) {
                Balustre balustreFin = new Balustre(hauteurBalustres, typeBalustres, positionFin);
                balustres.ajouterApres(noeudCourant.getBalustre(), balustreFin);
            }
            noeudCourant.suivant().setEstEscalier(true);
        }
        else if(position.getX() == 0 && position.getY() != 0) {
            NoeudRampe noeudCourant = balustres.getTete().suivant();
            Point3D positionCourante = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0);
            Point3D positionDebut = new Point3D(positionCourante.getX(), position.getY(), positionCourante.getZ());
            if (!(noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getY() <= positionDebut.getY() + typeBalustres.getHauteur().getValeurReelle())) {
                Balustre balustreDebut = new Balustre(hauteurBalustres, typeBalustres, positionDebut);
                balustres.ajouterApres(noeudCourant.getBalustre(), balustreDebut);
                noeudCourant = noeudCourant.suivant();
            }
            noeudCourant.setEstEscalier(true);

            Point3D positionFin = new Point3D(positionDebut.getX(), positionDebut.getY() - espaceEntreBalustre, positionDebut.getZ());
            if (!(noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(4).getY() >= positionFin.getY())) {
                Balustre balustreFin = new Balustre(hauteurBalustres, typeBalustres, positionFin);
                balustres.ajouterApres(noeudCourant.getBalustre(), balustreFin);
            }
            noeudCourant.suivant().setEstEscalier(true);
        }
        else {
            NoeudRampe noeudCourant = balustres.getTete().suivant().suivant();
            Point3D positionCourante = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0);
            Point3D positionDebut = new Point3D(position.getX(), positionCourante.getY(), positionCourante.getZ());
            if (!(noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(3).getX() >= positionDebut.getX())) {
                Balustre balustreDebut = new Balustre(hauteurBalustres, typeBalustres, positionDebut);
                balustres.ajouterApres(noeudCourant.getBalustre(), balustreDebut);
                noeudCourant = noeudCourant.suivant();
            }
            noeudCourant.setEstEscalier(true);

            Point3D positionFin = new Point3D(positionDebut.getX() + espaceEntreBalustre, positionDebut.getY(), positionDebut.getZ());
            if (!(noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(0).getX() <= positionFin.getX() + typeBalustres.getLargeur().getValeurReelle())) {
                Balustre balustreFin = new Balustre(hauteurBalustres, typeBalustres, positionFin);
                balustres.ajouterApres(noeudCourant.getBalustre(), balustreFin);
            }
            noeudCourant.suivant().setEstEscalier(true);
        }
    }

    private void creerBalustresIntermediaires(ListeChaineeRampe balustres, TypeBois typeBalustre) {
        NoeudRampe noeudCourant = balustres.getTete();
        float epaisseurRampe = 0;
        float hauteurBalustres = balustres.getHauteurMax() - epaisseurRampe;

        while (noeudCourant.suivant() != null) {
            int nbSubdivisions = calculerSubdivision(balustres.getEspacementMax(), noeudCourant.getBalustre(), noeudCourant.suivant().getBalustre());

            if (noeudCourant.estEscalier() && noeudCourant.suivant().estEscalier()) {
                noeudCourant = noeudCourant.suivant();
                continue;
            }

            if (determinerHorizontal(noeudCourant.getBalustre(), noeudCourant.suivant().getBalustre())) {
                float positionXPremiereBalustre = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getX();
                float positionXSecondeBalustre = noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(0).getX();

                float positionY = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getY();
                float positionZ = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getZ();

                if (determinerDirectionXNegativeQuandHorizontal(noeudCourant.getBalustre(), noeudCourant.suivant().getBalustre())) {
                    float espacement = (positionXPremiereBalustre - positionXSecondeBalustre) / nbSubdivisions;
                    Balustre pointDeDepart = noeudCourant.getBalustre();

                    for (int noSubdivision = 1; noSubdivision < nbSubdivisions; noSubdivision++) {
                        float positionX = positionXPremiereBalustre - (noSubdivision * espacement);
                        Point3D positionNouvelleBalustre = new Point3D(positionX, positionY, positionZ);
                        Balustre nouvelleBalustre = new Balustre(hauteurBalustres, typeBalustre, positionNouvelleBalustre);
                        balustres.ajouterApres(pointDeDepart, nouvelleBalustre);
                        pointDeDepart = nouvelleBalustre;
                    }
                }
                else {
                    float espacement = (positionXSecondeBalustre - positionXPremiereBalustre) / nbSubdivisions;
                    Balustre pointDeDepart = noeudCourant.getBalustre();

                    for (int noSubdivision = 1; noSubdivision < nbSubdivisions; noSubdivision++) {
                        float positionX = positionXPremiereBalustre + (noSubdivision * espacement);
                        Point3D positionNouvelleBalustre = new Point3D(positionX, positionY, positionZ);
                        Balustre nouvelleBalustre = new Balustre(hauteurBalustres, typeBalustre, positionNouvelleBalustre);
                        balustres.ajouterApres(pointDeDepart, nouvelleBalustre);
                        pointDeDepart = nouvelleBalustre;
                    }
                }

            }
            else {
                float positionYPremierBalustre = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getY();
                float positionYSecondeBalustre = noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(0).getY();
                float espacement = (positionYPremierBalustre - positionYSecondeBalustre) / nbSubdivisions;
                Balustre pointDeDepart = noeudCourant.getBalustre();

                for (int noSubdivision = 1; noSubdivision < nbSubdivisions; noSubdivision++) {
                    float positionX = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getX();
                    float positionY = positionYPremierBalustre - (noSubdivision * espacement);
                    float positionZ = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getZ();
                    Point3D positionNouvelleBalustre = new Point3D(positionX, positionY, positionZ);
                    Balustre nouvelleBalustre = new Balustre(hauteurBalustres, typeBalustre, positionNouvelleBalustre);
                    balustres.ajouterApres(pointDeDepart, nouvelleBalustre);
                    pointDeDepart = nouvelleBalustre;
                }
            }
            noeudCourant = noeudCourant.suivant();
        }
    }

    private int calculerSubdivision(float espacementMaximum, Balustre premiereBalustre, Balustre secondeBalustre) {
        int diviseur = 1;

        if (determinerHorizontal(premiereBalustre, secondeBalustre)) {
            float positionXPremierBalustre = premiereBalustre.getPlanchePrisme().getPoints().get(0).getX();
            float positionXSecondeBalustre = secondeBalustre.getPlanchePrisme().getPoints().get(0).getX();

            if (determinerDirectionXNegativeQuandHorizontal(premiereBalustre, secondeBalustre)) {
                while (((positionXPremierBalustre - positionXSecondeBalustre) / diviseur) >= espacementMaximum) {
                    diviseur++;
                }
            }
            else {
                while (((positionXSecondeBalustre - positionXPremierBalustre) / diviseur) >= espacementMaximum) {
                    diviseur++;
                }
            }
        }
        else {
            float positionYPremierBalustre = premiereBalustre.getPlanchePrisme().getPoints().get(0).getY();
            float positionYSecondeBalustre = secondeBalustre.getPlanchePrisme().getPoints().get(0).getY();
            while (((positionYPremierBalustre - positionYSecondeBalustre) / diviseur) >= espacementMaximum) {
                diviseur++;
            }

        }
        return diviseur;
    }

    private boolean determinerHorizontal(Balustre premiereBalustre, Balustre secondeBalustre) {
        return premiereBalustre.getPlanchePrisme().getPoints().get(0).getY() == secondeBalustre.getPlanchePrisme().getPoints().get(0).getY();
    }

    private boolean determinerDirectionXNegativeQuandHorizontal(Balustre premiereBalustre, Balustre secondeBalustre) {
        return premiereBalustre.getPlanchePrisme().getPoints().get(0).getX() > secondeBalustre.getPlanchePrisme().getPoints().get(0).getX();
    }

    private ArrayList<Parcours> creerParcours(ListeChaineeRampe balustres, TypeBois typeBoisTraverse, TypeBarreau typeBarreaux, boolean optimisation,
                                              TypeBois typeBoisMarches) {
        ArrayList<Parcours> parcours = new ArrayList<>();
        NoeudRampe noeudCourant = balustres.getTete();
        TypeBois typeBoisBalustres = noeudCourant.getBalustre().getTypebois();
        float espacementPatioTraverseBas = 1;
        float positionZTraverseBas = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getZ() + espacementPatioTraverseBas;
        float positionZTraverseHaut = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(1).getZ() - typeBoisTraverse.getLargeur().getValeurReelle();
        float espacementMaximalBarreaux = 5;
        float longueurBarreaux = positionZTraverseHaut - (positionZTraverseBas + typeBoisTraverse.getLargeur().getValeurReelle());
        float positionZBarreaux = positionZTraverseBas + typeBoisTraverse.getLargeur().getValeurReelle() + longueurBarreaux/2;

        while (noeudCourant.suivant() != null) {

            if (noeudCourant.estEscalier() && noeudCourant.suivant().estEscalier()) {
                Escalier escalier = creerEscalier(noeudCourant.getBalustre(), noeudCourant.suivant().getBalustre(), typeBoisMarches);
                parcours.add(escalier);
                noeudCourant = noeudCourant.suivant();
                continue;
            }

            if (determinerHorizontal(noeudCourant.getBalustre(), noeudCourant.suivant().getBalustre())) {
                float decalageY = (typeBoisBalustres.getHauteur().getValeurReelle() - typeBoisTraverse.getHauteur().getValeurReelle())/2;
                float positionYTraverses = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getY() + decalageY;

                if (determinerDirectionXNegativeQuandHorizontal(noeudCourant.getBalustre(), noeudCourant.suivant().getBalustre())) {
                    float positionXTraverses = noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(3).getX();
                    float longueurTraverses = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getX() - positionXTraverses;

                    Point3D positionTraverseBas = new Point3D(positionXTraverses, positionYTraverses, positionZTraverseBas);
                    Point3D positionTraverseHaut = new Point3D(positionXTraverses, positionYTraverses, positionZTraverseHaut);

                    Traverse traverseBas = new Traverse(longueurTraverses, typeBoisTraverse, positionTraverseBas, Orientation.PARALX_PERPZ);
                    Traverse traverseHaut = new Traverse(longueurTraverses, typeBoisTraverse, positionTraverseHaut, Orientation.PARALX_PERPZ);

                    MainCourante mainCourante = new MainCourante(30, new TypeBois("2X2"), new Point3D(0,0,0), Orientation.PARALX);
                    ArrayList<Barreau> barreaux = creerBarreaux(noeudCourant, longueurTraverses, espacementMaximalBarreaux, typeBarreaux, longueurBarreaux, positionZBarreaux, optimisation);
                    Balustrade balustrade = new Balustrade(barreaux, traverseBas, traverseHaut, mainCourante);
                    parcours.add(balustrade);
                }
                else {
                    float positionXTraverses = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(3).getX();
                    float longueurTraverses = noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(0).getX() - positionXTraverses;

                    Point3D positionTraverseBas = new Point3D(positionXTraverses, positionYTraverses, positionZTraverseBas);
                    Point3D positionTraverseHaut = new Point3D(positionXTraverses, positionYTraverses, positionZTraverseHaut);

                    Traverse traverseBas = new Traverse(longueurTraverses, typeBoisTraverse, positionTraverseBas, Orientation.PARALX_PERPZ);
                    Traverse traverseHaut = new Traverse(longueurTraverses, typeBoisTraverse, positionTraverseHaut, Orientation.PARALX_PERPZ);

                    MainCourante mainCourante = new MainCourante(30, new TypeBois("2X2"), new Point3D(0,0,0), Orientation.PARALX);
                    ArrayList<Barreau> barreaux = creerBarreaux(noeudCourant, longueurTraverses, espacementMaximalBarreaux, typeBarreaux, longueurBarreaux, positionZBarreaux, optimisation);
                    Balustrade balustrade = new Balustrade(barreaux, traverseBas, traverseHaut, mainCourante);
                    parcours.add(balustrade);
                }
            }
            else {
                float decalageXTraverses = (typeBoisBalustres.getLargeur().getValeurReelle() - typeBoisTraverse.getHauteur().getValeurReelle())/2;
                float positionXTraverses = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getX() + decalageXTraverses;
                float positionYTraverses = noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(4).getY();
                float longueurTraverses = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getY() - positionYTraverses;

                Point3D positionTraverseBas = new Point3D(positionXTraverses, positionYTraverses, positionZTraverseBas);
                Point3D positionTraverseHaut = new Point3D(positionXTraverses, positionYTraverses, positionZTraverseHaut);

                Traverse traverseBas = new Traverse(longueurTraverses, typeBoisTraverse, positionTraverseBas, Orientation.PARALY_PERPZ);
                Traverse traverseHaut = new Traverse(longueurTraverses, typeBoisTraverse, positionTraverseHaut, Orientation.PARALY_PERPZ);

                MainCourante mainCourante = new MainCourante(30, new TypeBois("2X2"), new Point3D(0,0,0), Orientation.PARALX);
                ArrayList<Barreau> barreaux = creerBarreaux(noeudCourant, longueurTraverses, espacementMaximalBarreaux, typeBarreaux, longueurBarreaux, positionZBarreaux, optimisation);
                Balustrade balustrade = new Balustrade(barreaux, traverseBas, traverseHaut, mainCourante);
                parcours.add(balustrade);
            }
            noeudCourant = noeudCourant.suivant();
        }
        return parcours;
    }

    private ArrayList<Barreau> creerBarreaux(NoeudRampe noeudCourant, float longueurTraverse, float espacementMaximal, TypeBarreau typeBarreau,
                                             float longueurBarreaux, float positionZBarreaux, boolean optimisation) {

        float largeurBarreau = typeBarreau.getDimensions().getX();

        int nbBarreaux = (int) Math.ceil(longueurTraverse/(espacementMaximal + largeurBarreau));
        float espacementReel = (longueurTraverse - (nbBarreaux * largeurBarreau))/(nbBarreaux + 1);
        TypeBois typeBoisBalustres = noeudCourant.getBalustre().getTypebois();
        ArrayList<Barreau> barreaux = new ArrayList<>();

        if (determinerHorizontal(noeudCourant.getBalustre(), noeudCourant.suivant().getBalustre())) {
            float decalageY = noeudCourant.getBalustre().getTypebois().getLargeur().getValeurReelle()/2;
            float positionY = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getY() + decalageY;

            if (determinerDirectionXNegativeQuandHorizontal(noeudCourant.getBalustre(), noeudCourant.suivant().getBalustre())) {
                float positionXTraverses = noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(3).getX();

                for (int noBarreau = 0; noBarreau < nbBarreaux; noBarreau++) {
                    float positionX = positionXTraverses + espacementReel + (noBarreau * (espacementReel + largeurBarreau));
                    Point3D position = new Point3D(positionX, positionY, positionZBarreaux);
                    Barreau barreau = new Barreau(longueurBarreaux, typeBarreau, position, optimisation);
                    barreaux.add(barreau);
                }
            }
            else {
                float positionXTraverses = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(3).getX();

                for (int noBarreau = 0; noBarreau < nbBarreaux; noBarreau++) {
                    float positionX = positionXTraverses + espacementReel + (noBarreau * (espacementReel + largeurBarreau));
                    Point3D position = new Point3D(positionX, positionY, positionZBarreaux);
                    Barreau barreau = new Barreau(longueurBarreaux, typeBarreau, position, optimisation);
                    barreaux.add(barreau);
                }
            }
        }
        else {
            float decalageX = (typeBoisBalustres.getLargeur().getValeurReelle() - largeurBarreau)/2;
            float positionX = noeudCourant.getBalustre().getPlanchePrisme().getPoints().get(0).getX() + decalageX;
            float positionYTraverses = noeudCourant.suivant().getBalustre().getPlanchePrisme().getPoints().get(4).getY();

            for (int noBarreau = 0; noBarreau < nbBarreaux; noBarreau++) {
                float positionY = positionYTraverses + espacementReel + (noBarreau * (espacementReel + largeurBarreau));
                Point3D position = new Point3D(positionX, positionY, positionZBarreaux);
                Barreau barreau = new Barreau(longueurBarreaux, typeBarreau, position, optimisation);
                barreaux.add(barreau);
            }
        }
        return barreaux;
    }

    private Escalier creerEscalier(Balustre balustreDebut, Balustre balustreFin, TypeBois typeBoisMarches) {
        ArrayList<Marche> marches = new ArrayList<>();
        ArrayList<Contremarche> contremarches = new ArrayList<>();
        float decalageBalustres = 1;
        float marcheOverHang = 0.5f;
        float hauteurPatio = balustreDebut.getPlanchePrisme().getPoints().get(0).getZ();
        float hauteurContremarcheEstimee = (25 - typeBoisMarches.getHauteur().getValeurReelle()) / 2;
        int nombreMarches = (int) Math.ceil(hauteurPatio / (typeBoisMarches.getLargeur().getValeurReelle() + hauteurContremarcheEstimee));
        float espacementVertical = hauteurPatio / nombreMarches;
        float espacementHorizontal = typeBoisMarches.getHauteur().getValeurReelle() - typeBoisMarches.getLargeur().getValeurReelle() - marcheOverHang;
        float hauteurExacteContremarche = espacementVertical - typeBoisMarches.getLargeur().getValeurReelle();

        if (determinerHorizontal(balustreDebut, balustreFin)) {
            if (determinerDirectionXNegativeQuandHorizontal(balustreDebut, balustreFin)) {
                float positionMarcheEtContremarcheX = balustreFin.getPlanchePrisme().getPoints().get(0).getX();
                float longueurMarche = balustreDebut.getPlanchePrisme().getPoints().get(3).getX() - balustreFin.getPlanchePrisme().getPoints().get(0).getX();

                for (int noMarche = 0; noMarche < nombreMarches; noMarche++) {
                    float positionMarcheY = balustreFin.getPlanchePrisme().getPoints().get(4).getY() + noMarche * espacementHorizontal + decalageBalustres;
                    float positionMarcheZ = hauteurPatio - noMarche * espacementVertical - typeBoisMarches.getLargeur().getValeurReelle();
                    Point3D positionMarche = new Point3D(positionMarcheEtContremarcheX, positionMarcheY, positionMarcheZ);
                    Marche marche = new Marche(longueurMarche, typeBoisMarches, positionMarche, Orientation.PARALX_PERPZ);
                    marches.add(marche);

                    float positionContremarcheY = positionMarcheY + espacementHorizontal;
                    float positionContremarcheZ = positionMarcheZ - hauteurExacteContremarche;
                    Point3D positionContremarche = new Point3D(positionMarcheEtContremarcheX, positionContremarcheY, positionContremarcheZ);
                    Contremarche contremarche = new Contremarche(longueurMarche, typeBoisMarches.getLargeur().getValeurReelle(), hauteurExacteContremarche,
                            typeBoisMarches, positionContremarche, Orientation.PARALX);
                    contremarches.add(contremarche);
                }
            }
            else {
                float positionMarcheEtContremarcheX = balustreDebut.getPlanchePrisme().getPoints().get(0).getX();
                float longueurMarche = balustreFin.getPlanchePrisme().getPoints().get(3).getX() - balustreDebut.getPlanchePrisme().getPoints().get(0).getX();

                for (int noMarche = 0; noMarche < nombreMarches; noMarche++) {
                    float positionMarcheY =
                            balustreFin.getPlanchePrisme().getPoints().get(0).getY() - noMarche * espacementHorizontal - decalageBalustres - typeBoisMarches.getHauteur().getValeurReelle();
                    float positionMarcheZ = hauteurPatio - noMarche * espacementVertical - typeBoisMarches.getLargeur().getValeurReelle();
                    Point3D positionMarche = new Point3D(positionMarcheEtContremarcheX, positionMarcheY, positionMarcheZ);
                    Marche marche = new Marche(longueurMarche, typeBoisMarches, positionMarche, Orientation.PARALX_PERPZ);
                    marches.add(marche);

                    float positionContremarcheY = positionMarcheY + marcheOverHang;
                    float positionContremarcheZ = positionMarcheZ - hauteurExacteContremarche;
                    Point3D positionContremarche = new Point3D(positionMarcheEtContremarcheX, positionContremarcheY, positionContremarcheZ);
                    Contremarche contremarche = new Contremarche(longueurMarche, typeBoisMarches.getLargeur().getValeurReelle(), hauteurExacteContremarche,
                            typeBoisMarches, positionContremarche, Orientation.PARALX);
                    contremarches.add(contremarche);
                }
            }
        }
        else {
            float positionMarcheEtContremarcheY = balustreFin.getPlanchePrisme().getPoints().get(0).getY();
            float longueurMarche = balustreDebut.getPlanchePrisme().getPoints().get(4).getY() - balustreFin.getPlanchePrisme().getPoints().get(0).getY();

            for (int noMarche = 0; noMarche < nombreMarches; noMarche++) {
                float positionMarcheX = balustreFin.getPlanchePrisme().getPoints().get(0).getX() - noMarche * espacementHorizontal - decalageBalustres - typeBoisMarches.getHauteur().getValeurReelle();
                float positionMarcheZ = hauteurPatio - noMarche * espacementVertical - typeBoisMarches.getLargeur().getValeurReelle();
                Point3D positionMarche = new Point3D(positionMarcheX, positionMarcheEtContremarcheY, positionMarcheZ);
                Marche marche = new Marche(longueurMarche, typeBoisMarches, positionMarche, Orientation.PARALY_PERPZ);
                marches.add(marche);

                float positionContremarcheX = positionMarcheX + marcheOverHang;
                float positionContremarcheZ = positionMarcheZ - hauteurExacteContremarche;
                Point3D positionContremarche = new Point3D(positionContremarcheX, positionMarcheEtContremarcheY, positionContremarcheZ);
                Contremarche contremarche = new Contremarche(longueurMarche, typeBoisMarches.getLargeur().getValeurReelle(), hauteurExacteContremarche,
                        typeBoisMarches, positionContremarche, Orientation.PARALY);
                contremarches.add(contremarche);
            }
        }
        return new Escalier(marches, contremarches);
    }
}
