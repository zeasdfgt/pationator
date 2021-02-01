package ca.ulaval.glo2004.domain;

import ca.ulaval.glo2004.domain.Accessoires.TypeAccessoire;
import ca.ulaval.glo2004.domain.Rampe.GardeCorpsFactory;
import ca.ulaval.glo2004.domain.Rampe.TypeBarreau;
import ca.ulaval.glo2004.domain.drawing.Matrice;
import ca.ulaval.glo2004.domain.drawing.MatriceFactory;
import ca.ulaval.glo2004.domain.mesh.Mesh;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.domain.mesh.Triangle;
import ca.ulaval.glo2004.domain.patio.*;
import ca.ulaval.glo2004.gui.MainDrawingPanel;
import ca.ulaval.glo2004.gui.dto.MagiqueDTO;
import ca.ulaval.glo2004.gui.dto.ParametresDTO;
import ca.ulaval.glo2004.gui.dto.PrixDTO;
import ca.ulaval.glo2004.gui.dto.SauvegardeDTO;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.html2pdf.HtmlConverter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Controller implements Serializable {

    private Patio patio;
    private final OptimisateurPatio optimisateurPatio;
    private final PatioFactory patioFactory;
    private final GardeCorpsFactory gardeCorpsFactory;
    private final Stack<Sauvegardable> undo = new Stack<>();
    private final Stack<Sauvegardable> redo = new Stack<>();
    private final ValidateurEntrees validateurEntrees;
    private final HashMap<String, TypeNorme> normes;
    private final ValidateurNorme validateurNorme;
    private ListeMateriel listeMateriel;
    private ArrayList<Mesh> meshes = new ArrayList<>();
    private Mesh meshHover;
    private Mesh meshSelectionne;
    private boolean escalierSelectionne;
    private boolean escalierHover;
    private Point3D positionEscalier = new Point3D(0, 85, 50);

    public Controller(ParametresDTO parametresDTO, PrixDTO prixDTO, float hauteurRampe, TypeBarreau typeBarreau) {

        Params params = new Params(parametresDTO);
        this.patioFactory = new PatioFactory();
        this.gardeCorpsFactory = new GardeCorpsFactory();
        this.validateurEntrees = new ValidateurEntrees();
        this.normes = new HashMap<>();
        makeNormes();
        this.optimisateurPatio = new OptimisateurPatio(this.normes, prixDTO);
        this.validateurNorme = new ValidateurNorme(this.normes);
        this.patio = patioFactory.creerPatio(params);
        creerGardeCorps(hauteurRampe, prixDTO, typeBarreau);
        this.listeMateriel = new ListeMateriel(this.patio.getPlanchesEtType(), prixDTO);
        Mesh maison = TypeAccessoire.MAISON.getMesh();
        maison.nomFichier = "maison";
        this.meshes.add(maison);
        updateHauteurMeshes();
        ArrayList<NormeResultat> resultats = this.validateurNorme.validerNormes(params, this.patio);
        patio.setValide(resultats.stream().allMatch(NormeResultat::isRespecte));

    }

    public static void modifierMesh(Mesh mesh, Point3D scaling, Point3D decalage, Point3D rotation) {

        Matrice matScaling = new MatriceFactory().scaling(scaling);
        Matrice matTransformations = new MatriceFactory().creerWorldMatrice(decalage, rotation);

        for (Triangle triangle : mesh.getTriangles()) {
            for (Point3D point3D : triangle.getPoints()) {
                point3D.matriceFoisVecteur(matScaling, point3D);
                point3D.matriceFoisVecteur(matTransformations, point3D);
            }
        }
    }

    public void updateHauteurMeshes() {

        ArrayList<Mesh> meshesAvant = (ArrayList<Mesh>) meshes.clone();

        Point3D decalage, scaling, rotation;
        meshes = new ArrayList<>();
        if (meshesAvant.size() > 0) {
            if (meshesAvant.get(0).nomFichier.equals("maison")) {
                meshesAvant.remove(meshesAvant.get(0));

                Mesh maison = TypeAccessoire.MAISON.getMesh();

                scaling = new Point3D(40, 40, 40);
                Point3D decalagemaison = new Point3D(patio.getLongueur() + (maison.getDimensions().getX() * scaling.getX() - 3f),
                        patio.getLargeur() / 2,
                        maison.getDimensions().getZ() * scaling.getZ() + patio.getHauteur());
                rotation = new Point3D(0, 0, 0);
                modifierMesh(maison, scaling, decalagemaison, rotation);
                maison.nomFichier = "maison";
                this.meshes.add(maison);
            }
        }

        for (Mesh mesh : meshesAvant) {

            Mesh nouveauMesh = TypeAccessoire.BBQ.getMesh();
            nouveauMesh.nomFichier = mesh.nomFichier;
            decalage = mesh.getDecalage();
            scaling = mesh.getScaling();
            rotation = mesh.getRotation();

            decalage.setZ(patio.getHauteur() + scaling.getZ() * mesh.getDimensions().getZ());

            switch (mesh.nomFichier) {
                case "BBQ":
                    nouveauMesh = TypeAccessoire.BBQ.getMesh();
                    break;
                case "vase":
                    nouveauMesh = TypeAccessoire.VASE.getMesh();
                    break;
                case "chaise":
                    nouveauMesh = TypeAccessoire.CHAISE.getMesh();
                    break;
                case "table":
                    nouveauMesh = TypeAccessoire.TABLE.getMesh();
                    break;
                case "chien":
                    nouveauMesh = TypeAccessoire.CHIEN.getMesh();
                    break;
                case "plantepot":
                    nouveauMesh = TypeAccessoire.PLANTE.getMesh();
                    break;
                case "maison":
                    nouveauMesh = TypeAccessoire.MAISON.getMesh();
                    break;
                case "sapin":
                    nouveauMesh = TypeAccessoire.SAPIN.getMesh();
                    break;
            }
            modifierMesh(nouveauMesh, scaling, decalage, rotation);
            nouveauMesh.setDecalage(decalage);
            nouveauMesh.setRotation(rotation);
            this.meshes.add(nouveauMesh);
        }
    }

    private void makeNormes() {
        String norme2B = "Norme 2B";
        String norme2C = "Norme 2C";
        String norme4B = "Norme 4B";
        String norme6B = "Norme 6B";
        String normeRecouvrement = "Norme Recouvrement";
        String normeLongueurPoteaux = "Norme Longueur Poteaux";

        String nomNorme2BX = "esp_solives";
        ArrayList<String> norme2BX = new ArrayList<>();
        norme2BX.add("8");
        norme2BX.add("12");
        norme2BX.add("16");
        norme2BX.add("24");
        String nomNorme2BY = "typeSolives";
        ArrayList<String> norme2BY = new ArrayList<>();
        norme2BY.add("2X4");
        norme2BY.add("2X6");
        norme2BY.add("2X8");
        norme2BY.add("2X10");
        norme2BY.add("2X12");
        String nomNorme2BR = "lo_solives";
        String[][] norme2BR = new String[norme2BX.size()][norme2BY.size()];
        norme2BR[0][0] = "7-4";
        norme2BR[0][1] = "11-6";
        norme2BR[0][2] = "15-1";
        norme2BR[0][3] = "19-3";
        norme2BR[0][4] = "23-5";

        norme2BR[1][0] = "6-5";
        norme2BR[1][1] = "10-0";
        norme2BR[1][2] = "13-2";
        norme2BR[1][3] = "16-10";
        norme2BR[1][4] = "20-4";

        norme2BR[2][0] = "5-10";
        norme2BR[2][1] = "9-1";
        norme2BR[2][2] = "12-0";
        norme2BR[2][3] = "15-2";
        norme2BR[2][4] = "17-7";

        norme2BR[3][0] = "5-1";
        norme2BR[3][1] = "8-0";
        norme2BR[3][2] = "10-2";
        norme2BR[3][3] = "12-5";
        norme2BR[3][4] = "14-4";

        String nomNorme2CX = "NULL";
        ArrayList<String> norme2CX = new ArrayList<>();
        norme2CX.add("NULL");
        String nomNorme2CY = "typeSolives";
        ArrayList<String> norme2CY = new ArrayList<>();
        norme2CY.add("2X4");
        norme2CY.add("2X6");
        norme2CY.add("2X8");
        norme2CY.add("2X10");
        norme2CY.add("2X12");
        String nomNorme2CR = "lo_porteafo--max";
        String[][] norme2CR = new String[norme2CX.size()][norme2CY.size()];
        norme2CR[0][0] = "8";
        norme2CR[0][1] = "16";
        norme2CR[0][2] = "16";
        norme2CR[0][3] = "24";
        norme2CR[0][4] = "24";

        String nomNorme4BX = "esp_poteau";
        ArrayList<String> norme4BX = new ArrayList<>();
        norme4BX.add("48");
        norme4BX.add("72");
        norme4BX.add("96");
        String nomNorme4BY = "portee_solives";
        ArrayList<String> norme4BY = new ArrayList<>();
        norme4BY.add("4");
        norme4BY.add("5");
        norme4BY.add("6");
        norme4BY.add("7");
        norme4BY.add("8");
        norme4BY.add("9");
        norme4BY.add("10");
        norme4BY.add("11");
        norme4BY.add("12");
        norme4BY.add("13");
        norme4BY.add("14");
        norme4BY.add("15");
        norme4BY.add("16");
        String nomNorme4BR = "selection_poutre_simple";
        String[][] norme4BR = new String[norme4BX.size()][norme4BY.size()];
        norme4BR[0][0] = "1-2X6";
        norme4BR[0][1] = "1-2X6";
        norme4BR[0][2] = "1-2X6";
        norme4BR[0][3] = "1-2X6";
        norme4BR[0][4] = "1-2X6";
        norme4BR[0][5] = "1-2X6";
        norme4BR[0][6] = "1-2X6";
        norme4BR[0][7] = "1-2X6";
        norme4BR[0][8] = "1-2X6";
        norme4BR[0][9] = "1-2X6";
        norme4BR[0][10] = "1-2X6";
        norme4BR[0][11] = "1-2X6";
        norme4BR[0][12] = "2-2X6";

        norme4BR[1][0] = "1-2X6";
        norme4BR[1][1] = "1-2X6";
        norme4BR[1][2] = "1-2X6";
        norme4BR[1][3] = "1-2X6";
        norme4BR[1][4] = "2-2X6";
        norme4BR[1][5] = "2-2X6";
        norme4BR[1][6] = "2-2X6";
        norme4BR[1][7] = "2-2X6";
        norme4BR[1][8] = "2-2X6";
        norme4BR[1][9] = "2-2X6";
        norme4BR[1][10] = "2-2X6";
        norme4BR[1][11] = "2-2X8";
        norme4BR[1][12] = "2-2X8";

        norme4BR[2][0] = "2-2X6";
        norme4BR[2][1] = "2-2X6";
        norme4BR[2][2] = "2-2X6";
        norme4BR[2][3] = "2-2X6";
        norme4BR[2][4] = "2-2X8";
        norme4BR[2][5] = "2-2X8";
        norme4BR[2][6] = "2-2X8";
        norme4BR[2][7] = "2-2X8";
        norme4BR[2][8] = "2-2X10";
        norme4BR[2][9] = "2-2X10";
        norme4BR[2][10] = "2-2X10";
        norme4BR[2][11] = "2-2X10";
        norme4BR[2][12] = "2-2X10";

        String nomNorme6BX = "esp_poteau";
        ArrayList<String> norme6BX = new ArrayList<>();
        norme6BX.add("48");
        norme6BX.add("72");
        norme6BX.add("96");
        String nomNorme6BY = "lo_solives";
        ArrayList<String> norme6BY = new ArrayList<>();
        norme6BY.add("4");
        norme6BY.add("5");
        norme6BY.add("6");
        norme6BY.add("7");
        norme6BY.add("8");
        norme6BY.add("9");
        norme6BY.add("10");
        norme6BY.add("11");
        norme6BY.add("12");
        norme6BY.add("13");
        norme6BY.add("14");
        norme6BY.add("15");
        norme6BY.add("16");
        String nomNorme6BR = "selection_poutre_double";
        String[][] norme6BR = new String[norme6BX.size()][norme6BY.size()];
        norme6BR[0][0] = "1-2X6";
        norme6BR[0][1] = "1-2X6";
        norme6BR[0][2] = "1-2X6";
        norme6BR[0][3] = "1-2X6";
        norme6BR[0][4] = "2-2X6";
        norme6BR[0][5] = "2-2X6";
        norme6BR[0][6] = "2-2X6";
        norme6BR[0][7] = "2-2X6";
        norme6BR[0][8] = "2-2X6";
        norme6BR[0][9] = "2-2X6";
        norme6BR[0][10] = "2-2X6";
        norme6BR[0][11] = "2-2X6";
        norme6BR[0][12] = "2-2X8";

        norme6BR[1][0] = "2-2X6";
        norme6BR[1][1] = "2-2X6";
        norme6BR[1][2] = "2-2X6";
        norme6BR[1][3] = "2-2X6";
        norme6BR[1][4] = "2-2X8";
        norme6BR[1][5] = "2-2X8";
        norme6BR[1][6] = "2-2X8";
        norme6BR[1][7] = "2-2X10";
        norme6BR[1][8] = "2-2X10";
        norme6BR[1][9] = "2-2X10";
        norme6BR[1][10] = "2-2X10";
        norme6BR[1][11] = "2-2X10";
        norme6BR[1][12] = "2-2X12";

        norme6BR[2][0] = "2-2X8";
        norme6BR[2][1] = "2-2X8";
        norme6BR[2][2] = "2-2X10";
        norme6BR[2][3] = "2-2X10";
        norme6BR[2][4] = "2-2X10";
        norme6BR[2][5] = "2-2X12";
        norme6BR[2][6] = "2-2X12";
        norme6BR[2][7] = "2-2X12";
        norme6BR[2][8] = "3-2X10";
        norme6BR[2][9] = "3-2X10";
        norme6BR[2][10] = "3-2X10";
        norme6BR[2][11] = "3-2X12";
        norme6BR[2][12] = "3-2X12";

        String nomNormeRecouvrementX = "NULL";
        ArrayList<String> normeRecouvrementX = new ArrayList<>();
        normeRecouvrementX.add("NULL");
        String nomNormeRecouvrementY = "Type Recouvrement";
        ArrayList<String> normeRecouvrementY = new ArrayList<>();
        normeRecouvrementY.add("5/4X6");
        normeRecouvrementY.add("2X6");
        String nomNormeRecouvrementR = "Espacement Solives Maximal";
        String[][] normeRecouvrementR = new String[normeRecouvrementX.size()][normeRecouvrementY.size()];
        normeRecouvrementR[0][0] = "12";
        normeRecouvrementR[0][1] = "18";

        String nomNormeLongueurPoteauxX = "NULL";
        ArrayList<String> normeLongueurPoteauxX = new ArrayList<>();
        normeLongueurPoteauxX.add("NULL");
        String nomNormeLongueurPoteauxY = "Type Poteaux";
        ArrayList<String> normeLongueurPoteauxY = new ArrayList<>();
        normeLongueurPoteauxY.add("4X4");
        normeLongueurPoteauxY.add("6X6");
        String nomNormeLongueurPoteauxR = "Longueur Maximal";
        String[][] normeLongueurPoteauxR = new String[normeLongueurPoteauxX.size()][normeLongueurPoteauxY.size()];
        normeLongueurPoteauxR[0][0] = "6-6";
        normeLongueurPoteauxR[0][1] = "12-0";

        TypeNorme typeNorme2B = new TypeNorme(nomNorme2BX, nomNorme2BY, nomNorme2BR,
                norme2BX, norme2BY, norme2BR);
        TypeNorme typeNorme2C = new TypeNorme(nomNorme2CX, nomNorme2CY, nomNorme2CR,
                norme2CX, norme2CY, norme2CR);
        TypeNorme typeNorme4B = new TypeNorme(nomNorme4BX, nomNorme4BY, nomNorme4BR,
                norme4BX, norme4BY, norme4BR);
        TypeNorme typeNorme6B = new TypeNorme(nomNorme6BX, nomNorme6BY, nomNorme6BR,
                norme6BX, norme6BY, norme6BR);
        TypeNorme typeNormeRecouvrement = new TypeNorme(nomNormeRecouvrementX, nomNormeRecouvrementY, nomNormeRecouvrementR,
                normeRecouvrementX, normeRecouvrementY, normeRecouvrementR);
        TypeNorme typeNormeLongueurPoteaux = new TypeNorme(nomNormeLongueurPoteauxX, nomNormeLongueurPoteauxY, nomNormeLongueurPoteauxR,
                normeLongueurPoteauxX, normeLongueurPoteauxY, normeLongueurPoteauxR);

        normes.put(norme2B, typeNorme2B);
        normes.put(norme2C, typeNorme2C);
        normes.put(norme4B, typeNorme4B);
        normes.put(norme6B, typeNorme6B);
        normes.put(normeRecouvrement, typeNormeRecouvrement);
        normes.put(normeLongueurPoteaux, typeNormeLongueurPoteaux);
    }

    public ArrayList<NormeResultat> creerPatio(ParametresDTO parametresDTO, PrixDTO prixDTO, float hauteurRampe, TypeBarreau typeBarreaux) {

        Params params = new Params(parametresDTO);
        this.patio = patioFactory.creerPatio(params);
        creerGardeCorps(hauteurRampe, prixDTO, typeBarreaux);
        ArrayList<NormeResultat> resultats = validateurNorme.validerNormes(params, patio);
        patio.setValide(resultats.stream().allMatch(NormeResultat::isRespecte));
        this.listeMateriel = new ListeMateriel(this.patio.getPlanchesEtType(), prixDTO);

        updateHauteurMeshes();
        return resultats;
    }

    public boolean validerParametre(String nom_param, float valeur) {
        return validateurEntrees.validerParametre(nom_param, valeur);
    }

    public boolean validerDTO(ParametresDTO parametresDTO) {
        return validateurEntrees.validerDTO(parametresDTO);
    }

    public ListeMateriel getListeMateriel() {
        return this.listeMateriel;
    }

    public SauvegardeDTO chargerProjet() throws IOException, ClassNotFoundException {
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".patio", "patio");
            fileChooser.setFileFilter(filter);

            int returnedVal = fileChooser.showOpenDialog(null);

            if (returnedVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                file = new File(file.getParentFile(), file.getName());
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Sauvegardable sauvegardable = (Sauvegardable) objectInputStream.readObject();
                objectInputStream.close();
                this.chargerController(sauvegardable);
                return sauvegardable.getSauvegardeDTO();
            } else if (returnedVal != JFileChooser.CANCEL_OPTION) {
                JOptionPane.showMessageDialog(null, "Problème à l'ouverture d'un projet, le fichier peut être endommagé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Problème à l'ouverture d'un projet, le fichier peut être endommagé.", "Erreur", JOptionPane.ERROR_MESSAGE);
            throw e;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "La classe ne s'est pas chargée, veuillez réessayer", "Erreur", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
        return null;
    }

    public void empilerStackUndo(SauvegardeDTO sauvegardeDTO) {
        Sauvegardable sauvegardable = new Sauvegardable(this.patio, this.listeMateriel, sauvegardeDTO);
        undo.push(sauvegardable);
    }

    public void supprimerStackRedo() {
        while (!this.redo.empty()) {
            this.redo.pop();
        }
    }

    public void supprimerStackUndo() {
        while (this.undo.size() != 1) {
            this.undo.pop();
        }
    }

    public SauvegardeDTO undo() {
        try {
            redo.push(undo.pop());
            chargerController(undo.peek());
            undo.peek().printInfo();
            return undo.peek().getSauvegardeDTO();
        } catch (Exception e) {
            return null;
        }
    }

    public SauvegardeDTO redo() {
        try {
            undo.push(redo.pop());
            chargerController(undo.peek());
            undo.peek().printInfo();
            return undo.peek().getSauvegardeDTO();
        } catch (Exception e) {
            return null;
        }
    }

    public Stack<Sauvegardable> getRedo() {
        return redo;
    }

    public Stack<Sauvegardable> getUndo() {
        return this.undo;
    }

    private void chargerController(Sauvegardable sauvegardable) {
        this.patio = sauvegardable.getPatio();
        this.listeMateriel = sauvegardable.getListeMateriel();
    }

    public void sauvegarder() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".patio", "patio");
            fileChooser.setFileFilter(filter);

            int returnedVal = fileChooser.showSaveDialog(null);

            if (returnedVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (file.getName().contains(".patio")) {
                    file = new File(file.getParentFile(), file.getName());
                }
                else {
                    file = new File(file.getParentFile(), file.getName() + ".patio");
                }
                if (file.createNewFile()) {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
                    Sauvegardable sauvegardable = undo.peek();
                    objectOutputStream.writeObject(sauvegardable);
                    objectOutputStream.flush();
                    objectOutputStream.close();
                }
            } else if (returnedVal != JFileChooser.CANCEL_OPTION) {
                JOptionPane.showMessageDialog(null, "Le fichier existe déjà", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }

    public void sendPrixDTOtoListeMateriel(PrixDTO prixDTO) {
        this.listeMateriel.setPrix(prixDTO);
    }

    public void exporterSTL() {
        this.patio.exporterSTL();
    }

    public void exporterJPG(MainDrawingPanel drawingPanel) {
        BufferedImage bi = new BufferedImage(drawingPanel.getSize().width, drawingPanel.getSize().height, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        drawingPanel.setPictureMode(true);
        drawingPanel.paint(g);  //this == JComponent
        drawingPanel.setPictureMode(false);
        drawingPanel.repaint();
        g.dispose();
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".png", "png");
            fileChooser.setFileFilter(filter);

            int returnVal = fileChooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                file = new File(file.getParentFile(), file.getName() + ".png");
                ImageIO.write(bi, "png", file);
            }
        } catch (Exception e) {
            System.out.println("Erreur - exportation JPG");
        }

    }

    public Patio getPatio() {
        return this.patio;
    }

    public void creerGardeCorps(float hauteurRampe, PrixDTO prixDTO, TypeBarreau typebarreaux) {
        positionEscalier.setZ(patio.getHauteur());
        patio.assignerGardeCorps(gardeCorpsFactory.creerGardeCorps(new TypeBois("4X4"), new TypeBois("2X2"), patio.getDimensions(), hauteurRampe, typebarreaux, false, positionEscalier));
        this.listeMateriel = new ListeMateriel(patio.getPlanchesEtType(), prixDTO);
    }

    public Params creerPatioMagique(MagiqueDTO magiqueDTO, PrixDTO prixDTO, TypeBarreau typeBarreau) throws Exception {

        this.optimisateurPatio.setPrixDTO(prixDTO);
        Params params = this.optimisateurPatio.getParams(magiqueDTO);
        this.patio = this.patioFactory.creerPatio(params);
        this.patio.setValide(true);
        positionEscalier.setZ(patio.getHauteur());
        this.patio.assignerGardeCorps(gardeCorpsFactory.creerGardeCorps(new TypeBois("4X4"), new TypeBois("2X2"), patio.getDimensions(), magiqueDTO.ha_rampe, typeBarreau, false, positionEscalier));
        this.listeMateriel = new ListeMateriel(this.patio.getPlanchesEtType(), prixDTO);

        updateHauteurMeshes();
        return params;
    }

    public ArrayList<Mesh> getMeshes() {
        return this.meshes;
    }

    public void setMeshHover(Mesh mesh) {
        this.meshHover = mesh;
    }

    public Mesh getMeshHover() {
        return this.meshHover;
    }

    public Mesh getMeshSelectionne() {
        return meshSelectionne;
    }

    public void setMeshSelectionne(Mesh mesh) {
        this.meshSelectionne = mesh;
    }

    public void sauverListeMateriel(HashMap<String, Boolean> mapString, String nom_projet) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(".pdf", "pdf");
        fileChooser.setFileFilter(filter);

        int returnVal = fileChooser.showSaveDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = new File(fileChooser.getSelectedFile().getParentFile(), fileChooser.getSelectedFile().getName() + ".pdf");
            file.getParentFile().mkdirs();
            creerPdf(file.getAbsolutePath(), mapString, nom_projet);
        }
    }

    public void creerPdf(String nom, HashMap<String, Boolean> mapString, String description) {
        try {
            String prixtotal = "Prix total : ";
            StringBuilder points = new StringBuilder();
            float cout = listeMateriel.getPrixTotalString(mapString);
            String coutString = String.format("%.2f", cout);
            for(int i = 1; i<(70 - prixtotal.length() - coutString.length()); i++){
                points.append(".");
            }

            String output =
                    "<html>" +
                            "<h1 style=\"font-family:Courier; text-align:center;\">" +
                                "Pationator" +
                            "</h1>" +
                            "<br>" +
                            "<hr>" +
                            "<h3>" +
                                "Description du projet : " + description +
                            "</h3>" +
                            "<hr>" +
                            "<br>" +
                            "<div style=\"font-family:Courier\">" +
                                listeMateriel.toString(mapString, 72) +
                                "<br>" +
                                "<hr>" +
                                "<br>" +
                                "<b>" +
                                    prixtotal +
                                "</b> " +
                                points +
                                String.format("%.2f$", listeMateriel.getPrixTotalString(mapString)) +
                            "</div>" +
                    "</html>";
            HtmlConverter.convertToPdf(output, new FileOutputStream(nom));
        } catch (Exception ignored) {
        }
    }

    public void addMesh(Mesh mesh, Point3D position, Point3D scaling, Point3D rotation) {
        modifierMesh(mesh, scaling, position, rotation);
        meshes.add(mesh);
    }

    public void removeMesh(Mesh mesh) {
        try { this.meshes.remove(mesh); }
        catch (Exception ignored) { }
    }

    public boolean escalierSelectionne() {
        return this.escalierSelectionne;
    }

    public void setEscalierSelectionne(boolean escalierSelectionne) {
        this.escalierSelectionne = escalierSelectionne;
    }

    public boolean getEscalierHover() {
        return this.escalierHover;
    }

    public void setEscalierHover(boolean b) {
        this.escalierHover = b;
    }

    public void setPositionEscalier(Point3D positionEscalier) {
        this.positionEscalier = positionEscalier;
    }
}
