package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domain.Accessoires.TypeAccessoire;
import ca.ulaval.glo2004.domain.Controller;
import ca.ulaval.glo2004.domain.PrixException;
import ca.ulaval.glo2004.domain.drawing.MainDrawer;
import ca.ulaval.glo2004.domain.mesh.Mesh;
import ca.ulaval.glo2004.domain.mesh.Point3D;
import ca.ulaval.glo2004.gui.dto.SauvegardeDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class SousDrawingPanel extends JPanel {

    private final Controller controller;
    private final MainWindow mainWindow;

    private final Point3D decalage = new Point3D(0, 0, 0);
    private Point3D rotationVue;
    private float zoom = 3;

    private final int i;

    private Point sourisStartPan = new Point(0, 0);
    private Point souris = new Point(0, 0);
    private int typesouris = -1;

    private boolean iswireframe = false;
    private boolean drawShadow = true;
    private boolean pictureMode = false;
    private int width = 0;

    private boolean changement = false;
    private boolean locked = false;

    private JPanel sousPanel = null;
    private Color couleurContour = Color.black;
    private ArrayList<Flocon> flocons = null;
    MainDrawer mainDrawer = null;
    Color couleurArrierePlan;


    public void setModeNoel(boolean modeNoel) {
        if (modeNoel) {
            genererFlocons(ThreadLocalRandom.current().nextInt(100, 200));
        } else {
            flocons = null;
        }
        update();
    }

    public class Flocon {
        public Point2D.Float position;
        float speedY;
        float speedX;

        public Flocon() {
            float x = ThreadLocalRandom.current().nextInt(-20, getWidth());
            float y = ThreadLocalRandom.current().nextInt(-40, getHeight());
            speedX = ThreadLocalRandom.current().nextInt(1, 2) + ThreadLocalRandom.current().nextFloat();
            speedY = ThreadLocalRandom.current().nextInt(1, 6) + ThreadLocalRandom.current().nextFloat();
            position = new Point2D.Float(x, y);
        }
    }

    private void genererFlocons(int nbFlocons) {
        this.flocons = new ArrayList<>();
        for (int i = 0; i < nbFlocons; i++) {
            Flocon flocon = new Flocon();
            flocons.add(flocon);
        }
    }

    public void setPictureMode(boolean pictureMode) {
        this.pictureMode = pictureMode;
    }

    public void setCouleurContour(Color couleurContour) {
        this.couleurContour = couleurContour;
    }

    class CustomMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {

            sourisStartPan = e.getPoint();
            typesouris = e.getButton();

            if (typesouris == 1) {
                // Si aucun accessoire n'est selectionne
                if (controller.getMeshSelectionne() == null) {
                    // et que l'utilisateur clique dessus en mode selection
                    if (controller.getMeshHover() != null && mainWindow.modeSelection) {
                        controller.setMeshSelectionne(controller.getMeshHover());
                    }
                }
                // Si l'esaclier n'est pas selectionne
                if (!controller.escalierSelectionne()) {
                    // et que l'utilisateur clique dessus en mode Selection
                    if (controller.getEscalierHover() && mainWindow.modeSelection) {
                        controller.setEscalierSelectionne(true);
                    }
                }
                // si l'utilisateur a l'escalier de selectionne et clique ailleur
                if (!controller.getEscalierHover()) {
                    controller.setEscalierSelectionne(false);
                }
                // si l'utilisateur a un mesh de selectionne et clique ailleur
                if (controller.getMeshHover() != controller.getMeshSelectionne()) {
                    controller.setMeshSelectionne(null);
                }
                repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {

            requestFocus();
            if (mainWindow.modeSelection) {
                setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
            }

            setBorder(BorderFactory.createLineBorder(Color.WHITE));
            drawShadow = false;
            changement = true;
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            drawShadow = true;
            changement = false;
        }
    }

    class CustomMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            souris = e.getPoint();

            // Panning si aucun objet n'est selectionne
            if (typesouris == 1 && controller.getMeshSelectionne() == null && !controller.escalierSelectionne()) {
                if (sourisStartPan != e.getPoint()) {
                    decalage.setX(decalage.getX() + (e.getX() - sourisStartPan.x) / zoom);
                    decalage.setY(decalage.getY() + (e.getY() - sourisStartPan.y) / zoom);
                    sourisStartPan = e.getPoint();
                    repaint();
                }
            }

            // Deplacement de l'escalier s'il est selectionne
            else if (typesouris == 1 && controller.escalierSelectionne()) {
                if (sourisStartPan != e.getPoint()) {

                    float x = e.getX() / zoom - decalage.getX();
                    float y = e.getY() / zoom - decalage.getY();

                    Point3D dimensionsPatio = controller.getPatio().getDimensions();
                    float PositionX, PositionY;

                    // Section 2
                    if (x < - dimensionsPatio.getX()/2) {
                        PositionX = 0;
                        if (y < -dimensionsPatio.getY() / 2 + 2 * 3.5f + 1) {
                            PositionY = dimensionsPatio.getY() - 3.5f - 1;
                        } else if (y > dimensionsPatio.getY() / 2 - 72 - 1) {
                            PositionY = 72f - 3.5f + 1;
                        } else {
                            PositionY = dimensionsPatio.getY() / 2 - y;
                        }
                    }
                    else {
                        PositionX = 1;
                        PositionY = 0;
                    }

                    // Section 1 -> Section du haut du patio
                    if (y < - dimensionsPatio.getY()/2) {
                        PositionY = dimensionsPatio.getY();
                        if (x < -dimensionsPatio.getX() / 2 + 72 + 1f) {
                            PositionX = 72f + 1f - 3.5f;
                        } else if (x > dimensionsPatio.getX() / 2 - 2 * 3.5f) {
                            PositionX = dimensionsPatio.getX() - 3.5f;
                        } else {
                            PositionX = dimensionsPatio.getX() / 2 + x;
                        }
                    }
                    // Section 3 -> Section du bas du patio
                    else if(y > dimensionsPatio.getY()/2) {
                        PositionY = 0;
                        if (x < -dimensionsPatio.getX() / 2 + 1 + 3.5f) {
                            PositionX = 1;
                        } else if (x > dimensionsPatio.getX() / 2 - 72 - 3.5f) {
                            PositionX = dimensionsPatio.getX() - 72;
                        } else {
                            PositionX = dimensionsPatio.getX() / 2 + x;
                        }
                    }

                    controller.setPositionEscalier(new Point3D(PositionX, PositionY, 0));
                    try {
                        controller.creerGardeCorps(mainWindow.getFloatFromTextFieldString(mainWindow.hauteurRampe.getText()), mainWindow.popUpPrix.getPrixDTO(), mainWindow.getTypeBarreaux());
                    } catch (PrixException prixException) {
                        prixException.printStackTrace();
                    }

                    mainWindow.drawingPanel.updateDrawingPanels();
                }
            }

            // Deplacement d'un mesh s'il est selectionne
            else if (typesouris == 1 && controller.getMeshSelectionne() != null) {
                if (sourisStartPan != e.getPoint()) {

                    Mesh mesh = controller.getMeshSelectionne();

                    float x = (e.getX() - sourisStartPan.x) / zoom;
                    float y = -(e.getY() - sourisStartPan.y) / zoom;

                    sourisStartPan = e.getPoint();

                    Point3D decalage = new Point3D(x, y, 0);
                    Point3D scaling = new Point3D(1, 1, 1);
                    Point3D rotation = new Point3D(0, 0, 0);
                    if (mesh.getRotation().getZ() == - (float) (90f * Math.PI) / 2f) {
                        mesh.getDecalage().setX(mesh.getDecalage().getX() - x);
                        mesh.getDecalage().setY(mesh.getDecalage().getY() - y);
                    }
                    else {
                        mesh.getDecalage().setX(mesh.getDecalage().getX() + x);
                        mesh.getDecalage().setY(mesh.getDecalage().getY() + y);
                    }
                    Controller.modifierMesh(mesh, scaling, decalage, rotation);

                    repaint();
                }
            }

            // Tourner la camera
            else if (typesouris == 3) {
                if (sourisStartPan != e.getPoint() && !locked) {
                    rotationVue.setZ((rotationVue.getZ() + 0.5f * (e.getX() - sourisStartPan.x)));
                    rotationVue.setX((rotationVue.getX() + 0.5f * (e.getY() - sourisStartPan.y)));
                    sourisStartPan = e.getPoint();
                    repaint();
                }
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            souris = e.getPoint();
            repaint();

        }
    }

    public void update() {
        changement = true;
        repaint();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        if (locked) {
            this.remove(sousPanel);
            rotationVue = new Point3D(180f, 0, 0);
            zoom = 3;
            recentrer();
        } else {
            creerBouttonsPanel();
        }
    }

    public void enleverBouttonsPanel() {
        if (sousPanel != null) {
            this.remove(sousPanel);
        }
        this.sousPanel = null;
    }

    public void creerBouttonsPanel() {
        this.setLayout(new BorderLayout());

        this.sousPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));

        JPanel bouttonsPanel = new JPanel(new GridLayout(2, 3));
        bouttonsPanel.setPreferredSize(new Dimension(140, 100));

        JButton top = new JButton(), side = new JButton(), front = new JButton(), recentrerButton = new JButton();
        JToggleButton wireframe = new JToggleButton(), outlineButton = new JToggleButton();

        top.setIcon(new ImageIcon(getClass().getResource("/topicone2.png")));
        side.setIcon(new ImageIcon(getClass().getResource("/sideicone2.png")));
        front.setIcon(new ImageIcon(getClass().getResource("/fronticone2.png")));
        recentrerButton.setIcon(new ImageIcon(getClass().getResource("/recentrer.png")));
        wireframe.setIcon(new ImageIcon(getClass().getResource("/invert.png")));
        outlineButton.setIcon(new ImageIcon(getClass().getResource("/outline.png")));

        bouttonsPanel.add(top);
        bouttonsPanel.add(side);
        bouttonsPanel.add(front);
        bouttonsPanel.add(recentrerButton);
        bouttonsPanel.add(wireframe);
        bouttonsPanel.add(outlineButton);

        bouttonsPanel.setOpaque(false);
        sousPanel.setOpaque(false);
        sousPanel.add(bouttonsPanel);

        this.add(sousPanel, BorderLayout.SOUTH);

        class CustomActionListenerChangeVue implements ActionListener {
            private final Point3D angles;

            public CustomActionListenerChangeVue(Point3D angles) {
                this.angles = angles;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                rotationVue.setX(this.angles.getX());
                rotationVue.setY(this.angles.getY());
                rotationVue.setZ(this.angles.getZ());
                zoom = 3;
                recentrer();
                update();
            }
        }

        side.addActionListener(new CustomActionListenerChangeVue(new Point3D(90f, 0f, 0f)));
        top.addActionListener(new CustomActionListenerChangeVue(new Point3D(180f, 0f, 0f)));
        front.addActionListener(new CustomActionListenerChangeVue(new Point3D(90f, 0f, 90f)));
        recentrerButton.addActionListener(e -> {
            recentrer();
            update();
        });
        wireframe.addActionListener(e -> {
            iswireframe = !iswireframe;
            update();
        });
        outlineButton.addActionListener(e -> {
            if (outlineButton.isSelected()) {
                width = 2;
            } else {
                width = 0;
            }
            update();
        });

        validate();

    }

    public SousDrawingPanel(Dimension dimension, int nbX, int nbY, Controller controller, MainWindow mainWindow, int i, Point3D angles, Color couleurArrierePlan) {
        this.controller = controller;
        this.mainWindow = mainWindow;
        this.rotationVue = angles;
        this.i = i;
        this.couleurArrierePlan = couleurArrierePlan;
        setBackground(couleurArrierePlan);
        this.addMouseListener(new CustomMouseListener());
        this.addMouseMotionListener(new CustomMouseMotionListener());

        setSize(dimension.width / nbX, dimension.height / nbY);

        this.addMouseWheelListener(e -> {

            float ecranXAvantZoom = ((float) souris.x / zoom);
            float ecranYAvantZoom = ((float) souris.y / zoom);

            int rouletteSouris = e.getWheelRotation();
            if (rouletteSouris < 0) {
                zoom *= 1.1f;
            }
            if (rouletteSouris > 0) {
                zoom *= 0.9f;
            }

            float ecranXApresZoom = ((float) souris.x / zoom);
            float ecranYApresZoom = ((float) souris.y / zoom);

            float dX = decalage.getX();
            float dY = decalage.getY();
            decalage.setX(dX - (ecranXAvantZoom - ecranXApresZoom));
            decalage.setY(dY - (ecranYAvantZoom - ecranYApresZoom));

            repaint();
        });

        creerBouttonsPanel();

        recentrer();

        final String DELETE = "Delete";
        final String FLIP = "Flip";
        final String UNDO = "Undo";
        final String REDO = "Redo";

        Action flip = new AbstractAction(FLIP) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller.getMeshSelectionne() != null) {
                    Mesh mesh = controller.getMeshSelectionne();

                    Point3D scaling = mesh.getScaling();
                    Point3D rotation = mesh.getRotation();
                    Mesh nouveauMesh = null;
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

                    Point3D decalage;
                    if (rotation.getZ() == 0) {
                        rotation.setZ(- (float) (90f * Math.PI) / 2f);
                        assert nouveauMesh != null;
                        decalage = new Point3D(-controller.getPatio().getLongueur() / 2, -controller.getPatio().getLargeur() / 2, nouveauMesh.getDimensions().getZ() * scaling.getZ() + controller.getPatio().getHauteur());
                    }
                    else {
                        rotation.setZ(0);
                        assert nouveauMesh != null;
                        decalage = new Point3D(controller.getPatio().getLongueur() / 2, controller.getPatio().getLargeur() / 2, nouveauMesh.getDimensions().getZ() * scaling.getZ() + controller.getPatio().getHauteur());
                    }

                    nouveauMesh.setScaling(scaling);
                    nouveauMesh.setRotation(rotation);
                    nouveauMesh.nomFichier = mesh.nomFichier;
                    nouveauMesh.setDecalage(decalage);

                    controller.removeMesh(mesh);
                    controller.addMesh(nouveauMesh, decalage, scaling, rotation);
                    controller.setMeshSelectionne(nouveauMesh);
                }
            }
        };

        Action delete = new AbstractAction(DELETE) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller.getMeshSelectionne() != null) {
                    Mesh mesh = controller.getMeshSelectionne();
                    controller.removeMesh(mesh);
                    mainWindow.drawingPanel.updateDrawingPanels();
                }
            }
        };

        Action undo = new AbstractAction(UNDO) {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.inUndo = true;
                if (controller.getUndo().size() > 1) {
                    try {
                        SauvegardeDTO sauvegardeDTO = controller.undo();
                        mainWindow.chargerSauvegardeDTO(sauvegardeDTO);
                        if (controller.getUndo().size() == 1) {
                            mainWindow.undoButton.setEnabled(false);
                        }

                        if (!controller.getRedo().empty()) {
                            mainWindow.redoButton.setEnabled(true);
                        }
                    } catch (Exception ignored) {
                    }
                }
                else {
                    Toolkit.getDefaultToolkit().beep();
                }
                mainWindow.inUndo = false;
            }
        };

        Action redo = new AbstractAction(REDO) {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainWindow.inUndo = true;
                if (controller.getRedo().size() >= 1) {
                    try {
                        SauvegardeDTO sauvegardeDTO = controller.redo();
                        mainWindow.chargerSauvegardeDTO(sauvegardeDTO);
                        if (controller.getRedo().empty()) {
                            mainWindow.redoButton.setEnabled(false);
                        }

                        if (controller.getUndo().size() != 1) {
                            mainWindow.undoButton.setEnabled(true);
                        }
                    } catch (Exception ignored) {
                    }
                }
                else {
                    Toolkit.getDefaultToolkit().beep();
                    }
                mainWindow.inUndo = false;
            }
        };

        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), DELETE);
        this.getActionMap().put(DELETE, delete);
        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F, 0), FLIP);
        this.getActionMap().put(FLIP, flip);
        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK), UNDO);
        this.getActionMap().put(UNDO, undo);
        this.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK), REDO);
        this.getActionMap().put(REDO, redo);

    }

    @Override
    protected void paintComponent(Graphics g) {

        if (pictureMode) {
            enleverBouttonsPanel();
        } else if (sousPanel == null) {
            creerBouttonsPanel();
        }

        if (flocons != null) {
            for (Flocon flocon : flocons) {
                if (flocon.position.y >= getHeight() + 10) {
                    flocon.position.y = ThreadLocalRandom.current().nextInt(-100, -20);
                    flocon.position.x = ThreadLocalRandom.current().nextInt(-100, getWidth());
                }
                flocon.position.x += flocon.speedX;
                flocon.position.y += flocon.speedY;
            }
        }

        super.paintComponent(g);
        if (mainDrawer == null) {
            mainDrawer = new MainDrawer(mainWindow.getController(), this.getSize());
        }
        mainDrawer.setSize(this.getSize());
        if (changement || iswireframe) {
            mainDrawer.draw(g, flocons, mainWindow.modeSelection, souris, zoom, decalage, mainWindow.genererAffichageDTO(), rotationVue, iswireframe, drawShadow, width, couleurContour);
        } else {
            boolean firstTime = mainDrawer.drawSansUpdate(g, flocons, width, couleurContour);
            if (firstTime) {
                mainDrawer.draw(g, flocons, mainWindow.modeSelection, souris, zoom, decalage, mainWindow.genererAffichageDTO(), rotationVue, iswireframe, drawShadow, width, couleurContour);
            }
        }

        g.setColor(Color.white);
        g.setFont(new Font("Helvetica", Font.PLAIN, 13));
        g.drawString("Vue " + (i + 1), (getWidth() + 13) / 2, getHeight() - 40);


        if (MouseInfo.getPointerInfo().getLocation().x < getLocationOnScreen().x
                || MouseInfo.getPointerInfo().getLocation().x > getLocationOnScreen().x + getWidth()
                || MouseInfo.getPointerInfo().getLocation().y < getLocationOnScreen().y
                || MouseInfo.getPointerInfo().getLocation().y > getLocationOnScreen().y + getHeight()) {
            changement = false;
        }
    }

    public void recentrer() {
        zoom = Math.min(getWidth(), getHeight()) / 200f;
        this.decalage.setX(getWidth() / (2 * zoom));
        this.decalage.setY(getHeight() / (2 * zoom));
        repaint();
    }
}