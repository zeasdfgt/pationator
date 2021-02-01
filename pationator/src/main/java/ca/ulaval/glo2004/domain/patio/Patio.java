package ca.ulaval.glo2004.domain.patio;

import ca.ulaval.glo2004.domain.Rampe.GardeCorps;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.domain.mesh.Mesh;
import ca.ulaval.glo2004.domain.mesh.Triangle;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Patio implements Serializable, Cloneable {

    private final ArrayList<Poutre> poutres;
    private final ArrayList<Section> sections;
    private final ArrayList<RecouvrementPlanche> recouvrement;
    private final ArrayList<Poteau> poteaux;
    private GardeCorps gardeCorps;
    private boolean valide;
    private Planche plancheSelectionnee;

    /**
     * Constructeur de classe
     *
     * @param poutres      les poutres du patio
     * @param sections     les sections du patio
     * @param recouvrement les planches du recouvrement du patio
     */
    public Patio(ArrayList<Poutre> poutres, ArrayList<Section> sections, ArrayList<RecouvrementPlanche> recouvrement) {
        this.poutres = poutres;
        this.sections = sections;
        this.recouvrement = recouvrement;
        ArrayList<Poteau> poteaux = new ArrayList<>();
        for (Poutre poutre : poutres) {
            poteaux.addAll(poutre.getPoteaux());
        }
        this.poteaux = poteaux;
    }

    public Patio(Patio patioclone) {
        this.poutres = (ArrayList<Poutre>) patioclone.poutres.clone();
        this.sections = (ArrayList<Section>) patioclone.sections.clone();
        this.recouvrement = (ArrayList<RecouvrementPlanche>) patioclone.recouvrement.clone();
        this.poteaux = (ArrayList<Poteau>) patioclone.poteaux.clone();
        this.gardeCorps = patioclone.gardeCorps.clone();
    }

    /**
     * Retourne la liste de poutres du patio
     *
     * @return la liste de poutres du patio
     */
    public ArrayList<Poutre> getPoutres() {
        return poutres;
    }

    public ArrayList<Section> getSections() {
        return sections;
    }

    public ArrayList<RecouvrementPlanche> getRecouvrement() {
        return recouvrement;
    }

    public ArrayList<Poteau> getPoteaux() {
        return poteaux;
    }

    public ArrayList<Planche> getPlanches() {
        ArrayList<Planche> planches = new ArrayList<>();

        for (Poutre poutre : this.poutres) {
            planches.addAll(poutre.getPlis());
            planches.addAll(poutre.getPoteaux());
        }
        for (Section section : this.sections) {
            planches.addAll(section.getSolives());
        }

        planches.addAll(this.recouvrement);
        planches.addAll(gardeCorps.getPlanches());

        return planches;
    }

    public HashMap<Planche, String> getPlanchesEtType() {
        HashMap<Planche, String> planchesEtType = new HashMap<>();

        for (Poutre poutre : this.poutres) {
            for (Poteau poteau : poutre.getPoteaux()) {
                planchesEtType.put(poteau, "Appuis");
            }

            for (PoutrePlanche plis : poutre.getPlis()) {
                planchesEtType.put(plis, "Poutres");
            }
        }

        for (Section section : this.sections) {
            for (Solive solive : section.getSolives()) {
                planchesEtType.put(solive, "Solives");
            }
        }

        for (RecouvrementPlanche recouvre : this.recouvrement) {
            planchesEtType.put(recouvre, "Recouvrement");
        }

        for (Planche gardes : gardeCorps.getPlanches()) {
            planchesEtType.put(gardes, "Garde-Corps");
        }

        return planchesEtType;
    }


    public ArrayList<Mesh> getPrismes() {

        ArrayList<Mesh> meshes = new ArrayList<>();

        for (Poutre poutre : this.poutres) {
            for (Poteau poteau : poutre.getPoteaux()) {
                meshes.add(poteau.getPlanchePrisme());
            }
            meshes.addAll(poutre.getPrisme());
        }
        for (Section section : this.sections) {
            for (Solive solive : section.getSolives()) {
                meshes.add(solive.getPlanchePrisme());
            }
        }

        for (RecouvrementPlanche planche : this.recouvrement) {
            meshes.add(planche.getPlanchePrisme());
        }
        meshes.addAll(gardeCorps.getMeshes());

        return meshes;
    }

    public void setValide(boolean valide) {
        this.valide = valide;
    }

    public void exporterSTL() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".stl", "stl");
            fileChooser.setFileFilter(filter);

            int returnVal = fileChooser.showSaveDialog(null);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                file = new File(file.getParentFile(), file.getName() + ".stl");
                if (file.createNewFile()) {
                    FileWriter myWriter = new FileWriter(file);
                    myWriter.write("solid" + "\n");
                    for (Mesh mesh : getPrismes()) {
                        for (Triangle tri : mesh.getTriangles()) {

                            Point3D normal = new Point3D(), line1 = new Point3D(), line2 = new Point3D();
                            line1.soustraction(tri.getPoints().get(1), tri.getPoints().get(0));
                            line2.soustraction(tri.getPoints().get(2), tri.getPoints().get(0));
                            normal.produitVectoriel(line1, line2);

                            myWriter.write("facet normal " + normal.getX() + " " + normal.getY() + " " + normal.getZ() + "\n");
                            myWriter.write("outer loop" + "\n");
                            myWriter.write("vertex " + tri.getPoints().get(0).getX() + " " + tri.getPoints().get(0).getY() + " " + tri.getPoints().get(0).getZ() + "\n");
                            myWriter.write("vertex " + tri.getPoints().get(1).getX() + " " + tri.getPoints().get(1).getY() + " " + tri.getPoints().get(1).getZ() + "\n");
                            myWriter.write("vertex " + tri.getPoints().get(2).getX() + " " + tri.getPoints().get(2).getY() + " " + tri.getPoints().get(2).getZ() + "\n");
                            myWriter.write("endloop" + "\n");
                            myWriter.write("endfacet" + "\n");
                        }
                    }
                    myWriter.write("endsolid" + "\n");
                    myWriter.close();
                } else {
                    JOptionPane.showMessageDialog(null, "Le fichier existe déjà", "Erreur", JOptionPane.ERROR_MESSAGE);
                    exporterSTL();
                }
            }
        } catch (IOException e) {
            System.out.println("An error occured");
            e.printStackTrace();
        }
    }


    public Point3D getDimensions() {
        return new Point3D(getLongueur(), getLargeur(), getHauteur());
    }

    public float getLongueur() {
        return poutres.get(poutres.size() - 1).getPosition().getX() + poutres.get(poutres.size() - 1).getLargeur();
    }

    public float getLargeur() {
        return poutres.get(0).getPlancheLongueur();
    }

    public float getHauteur() {
        return recouvrement.get(0).getPlanchePrisme().getPoints().get(0).getZ() + recouvrement.get(0).getTypebois().getLargeur().getValeurReelle();
    }

    public boolean getValide() {
        return this.valide;
    }

    public void assignerGardeCorps(GardeCorps gardeCorps) {
        this.gardeCorps = gardeCorps;
    }

    public void setPlancheSelectionnee(Planche planche) {

        if (planche != null) {
            if (planche.getClass() == PoutrePlanche.class) {
                for (Poutre poutre : this.poutres) {
                    for (PoutrePlanche poutrePlanche : poutre.getPlis()) {
                        if (planche.equals(poutrePlanche)) {
                            this.plancheSelectionnee = poutre;
                            return;
                        }
                    }
                }
            }
        }
        this.plancheSelectionnee = planche;
    }

    public Planche getPlancheSelectionne() {
        return this.plancheSelectionnee;
    }

    public GardeCorps getGardeCorps() {
        return this.gardeCorps;
    }

    @Override
    public Patio clone() {
        return new Patio(this);
    }
}
