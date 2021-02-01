package ca.ulaval.glo2004.gui;

import ca.ulaval.glo2004.domain.PrixException;
import ca.ulaval.glo2004.gui.dto.PrixDTO;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Locale;

public class PopUpPrix extends JFrame {

    private final MainWindow mainWindow;
    public JPanel panelPrix;
    public JButton buttonConfirme;
    public JPanel prixPanel;
    public JTextField prix22;
    public JTextField prix28;
    public JTextField prix26;
    public JTextField prix212;
    public JTextField prix24;
    public JTextField prix210;
    public JTextField prix44;
    public JTextField prix66;
    public JTextField prix56;


    public PrixDTO getPrixDTO() throws PrixException {
        try {
            PrixDTO prixDTO = new PrixDTO();

            prixDTO.prix2x2 = getPrixText(prix22);
            prixDTO.prix2x4 = getPrixText(prix24);
            prixDTO.prix2x6 = getPrixText(prix26);
            prixDTO.prix2x8 = getPrixText(prix28);
            prixDTO.prix2x10 = getPrixText(prix210);
            prixDTO.prix2x12 = getPrixText(prix212);
            prixDTO.prix5quartx6 = getPrixText(prix56);
            prixDTO.prix4x4 = getPrixText(prix44);
            prixDTO.prix6x6 = getPrixText(prix66);

            mainWindow.prixButton.setBackground(Color.WHITE);
            return prixDTO;
        } catch (Exception error) {
            mainWindow.prixButton.setBackground(new Color(243, 75, 109));
            throw new PrixException("prix ex getprixdto");
        }
    }

    public void setPrixDTO(PrixDTO prixDTO) {
        prix22.setText(String.valueOf(prixDTO.prix2x2));
        prix24.setText(String.valueOf(prixDTO.prix2x4));
        prix26.setText(String.valueOf(prixDTO.prix2x6));
        prix28.setText(String.valueOf(prixDTO.prix2x8));
        prix210.setText(String.valueOf(prixDTO.prix2x10));
        prix212.setText(String.valueOf(prixDTO.prix2x12));
        prix56.setText(String.valueOf(prixDTO.prix5quartx6));
        prix44.setText(String.valueOf(prixDTO.prix4x4));
        prix66.setText(String.valueOf(prixDTO.prix6x6));
    }

    public Float getPrixText(JTextField prixTextField) throws Exception {
        if (prixTextField.getText().endsWith("f") || prixTextField.getText().endsWith("d")) {
            throw new Exception();
        } else {
            return Float.parseFloat(prixTextField.getText());
        }
    }

    public PopUpPrix(MainWindow mainWindow) {
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setContentPane(panelPrix);
        this.setTitle("Ajustement des Prix");
        this.pack();
        this.mainWindow = mainWindow;

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {

            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {
                try {
                    mainWindow.getController().sendPrixDTOtoListeMateriel(getPrixDTO());
                    mainWindow.miseAJourListeMateriel(mainWindow.getController().getListeMateriel());
                } catch (Exception ignored) {
                }
            }
        });

        class PrixDocumentListener implements DocumentListener {

            private final JTextField jTextField;

            public PrixDocumentListener(JTextField jTextField) {
                this.jTextField = jTextField;
            }

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                customUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                customUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                customUpdate();

            }

            private void customUpdate() {
                String textForm = this.jTextField.getText();

                try {
                    getPrixDTO();
                } catch (Exception ignored) {
                }

                try {
                    float prixForm = Float.parseFloat(textForm);

                    if (textForm.endsWith("f") || textForm.endsWith("d")) {
                        throw new Exception("float");
                    }

                    if (prixForm < 0) {
                        throw new Exception("zero");
                    }

                    this.jTextField.setBackground(Color.WHITE);
                    this.jTextField.setForeground(Color.BLACK);


                } catch (Exception error) {
                    this.jTextField.setBackground(new Color(243, 75, 109));
                    this.jTextField.setForeground(Color.WHITE);
                    mainWindow.prixButton.setBackground(new Color(243, 75, 109));
                }
            }
        }

        prix22.getDocument().addDocumentListener(new PrixDocumentListener(prix22));
        prix24.getDocument().addDocumentListener(new PrixDocumentListener(prix24));
        prix26.getDocument().addDocumentListener(new PrixDocumentListener(prix26));
        prix28.getDocument().addDocumentListener(new PrixDocumentListener(prix28));
        prix210.getDocument().addDocumentListener(new PrixDocumentListener(prix210));
        prix212.getDocument().addDocumentListener(new PrixDocumentListener(prix212));
        prix56.getDocument().addDocumentListener(new PrixDocumentListener(prix56));
        prix44.getDocument().addDocumentListener(new PrixDocumentListener(prix44));
        prix66.getDocument().addDocumentListener(new PrixDocumentListener(prix66));

        buttonConfirme.addActionListener(actionEvent -> {
            try {
                mainWindow.getController().sendPrixDTOtoListeMateriel(getPrixDTO());
                mainWindow.miseAJourListeMateriel(mainWindow.getController().getListeMateriel());
                this.setVisible(false);
                mainWindow.majPileSauvegarde();
            } catch (PrixException prixException) {
                Toolkit.getDefaultToolkit().beep();
            } catch (Exception ignored) {
            }
        });
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panelPrix = new JPanel();
        panelPrix.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelPrix.setBackground(new Color(-14735571));
        buttonConfirme = new JButton();
        buttonConfirme.setText("Enregistrer");
        panelPrix.add(buttonConfirme, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        prixPanel = new JPanel();
        prixPanel.setLayout(new BorderLayout(0, 0));
        prixPanel.setMinimumSize(new Dimension(-1, -1));
        prixPanel.setOpaque(false);
        prixPanel.setPreferredSize(new Dimension(360, 240));
        panelPrix.add(prixPanel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        prixPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-8856597)), "Prix linéaire", TitledBorder.CENTER, TitledBorder.TOP, this.$$$getFont$$$(null, -1, 16, prixPanel.getFont()), new Color(-8856597)));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(5, 2, new Insets(10, 20, 10, 20), 20, 10, true, false));
        panel1.setMinimumSize(new Dimension(-1, -1));
        panel1.setOpaque(false);
        panel1.setPreferredSize(new Dimension(0, 0));
        prixPanel.add(panel1, BorderLayout.CENTER);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout(0, 0));
        panel2.setOpaque(false);
        panel1.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setForeground(new Color(-1));
        label1.setMinimumSize(new Dimension(30, 16));
        label1.setPreferredSize(new Dimension(30, 16));
        label1.setText("2x2");
        panel2.add(label1, BorderLayout.WEST);
        prix22 = new JTextField();
        prix22.setBackground(new Color(-1));
        prix22.setEditable(true);
        prix22.setHorizontalAlignment(0);
        prix22.setMinimumSize(new Dimension(-1, 30));
        prix22.setOpaque(true);
        prix22.setPreferredSize(new Dimension(-1, 30));
        prix22.setText("0.30");
        panel2.add(prix22, BorderLayout.CENTER);
        final JLabel label2 = new JLabel();
        label2.setForeground(new Color(-1));
        label2.setHorizontalAlignment(0);
        label2.setMaximumSize(new Dimension(-1, 16));
        label2.setMinimumSize(new Dimension(14, 16));
        label2.setPreferredSize(new Dimension(14, 16));
        label2.setText("$");
        panel2.add(label2, BorderLayout.EAST);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel3.setOpaque(false);
        panel1.add(panel3, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setForeground(new Color(-1));
        label3.setMinimumSize(new Dimension(30, 16));
        label3.setPreferredSize(new Dimension(30, 16));
        label3.setText("2x8");
        panel3.add(label3, BorderLayout.WEST);
        prix28 = new JTextField();
        prix28.setBackground(new Color(-1));
        prix28.setEditable(true);
        prix28.setHorizontalAlignment(0);
        prix28.setMinimumSize(new Dimension(100, 30));
        prix28.setOpaque(true);
        prix28.setPreferredSize(new Dimension(-1, 30));
        prix28.setText("1.72");
        panel3.add(prix28, BorderLayout.CENTER);
        final JLabel label4 = new JLabel();
        label4.setForeground(new Color(-1));
        label4.setHorizontalAlignment(0);
        label4.setMaximumSize(new Dimension(-1, 16));
        label4.setMinimumSize(new Dimension(14, 16));
        label4.setPreferredSize(new Dimension(14, 16));
        label4.setText("$");
        panel3.add(label4, BorderLayout.EAST);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new BorderLayout(0, 0));
        panel4.setOpaque(false);
        panel1.add(panel4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setForeground(new Color(-1));
        label5.setMinimumSize(new Dimension(30, 16));
        label5.setPreferredSize(new Dimension(30, 16));
        label5.setText("2x6");
        panel4.add(label5, BorderLayout.WEST);
        prix26 = new JTextField();
        prix26.setBackground(new Color(-1));
        prix26.setEditable(true);
        prix26.setHorizontalAlignment(0);
        prix26.setMinimumSize(new Dimension(100, 30));
        prix26.setOpaque(true);
        prix26.setPreferredSize(new Dimension(-1, 30));
        prix26.setText("1.12");
        panel4.add(prix26, BorderLayout.CENTER);
        final JLabel label6 = new JLabel();
        label6.setForeground(new Color(-1));
        label6.setHorizontalAlignment(0);
        label6.setMaximumSize(new Dimension(-1, 16));
        label6.setMinimumSize(new Dimension(14, 16));
        label6.setPreferredSize(new Dimension(14, 16));
        label6.setText("$");
        panel4.add(label6, BorderLayout.EAST);
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new BorderLayout(0, 0));
        panel5.setOpaque(false);
        panel1.add(panel5, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setForeground(new Color(-1));
        label7.setMinimumSize(new Dimension(30, 16));
        label7.setPreferredSize(new Dimension(30, 16));
        label7.setText("2x12");
        panel5.add(label7, BorderLayout.WEST);
        prix212 = new JTextField();
        prix212.setBackground(new Color(-1));
        prix212.setEditable(true);
        prix212.setHorizontalAlignment(0);
        prix212.setMinimumSize(new Dimension(100, 30));
        prix212.setOpaque(true);
        prix212.setPreferredSize(new Dimension(-1, 30));
        prix212.setText("2.50");
        panel5.add(prix212, BorderLayout.CENTER);
        final JLabel label8 = new JLabel();
        label8.setForeground(new Color(-1));
        label8.setHorizontalAlignment(0);
        label8.setMaximumSize(new Dimension(-1, 16));
        label8.setMinimumSize(new Dimension(14, 16));
        label8.setPreferredSize(new Dimension(14, 16));
        label8.setText("$");
        panel5.add(label8, BorderLayout.EAST);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new BorderLayout(0, 0));
        panel6.setOpaque(false);
        panel1.add(panel6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setForeground(new Color(-1));
        label9.setMinimumSize(new Dimension(30, 16));
        label9.setPreferredSize(new Dimension(30, 16));
        label9.setText("2x4");
        panel6.add(label9, BorderLayout.WEST);
        prix24 = new JTextField();
        prix24.setBackground(new Color(-1));
        prix24.setEditable(true);
        prix24.setHorizontalAlignment(0);
        prix24.setMinimumSize(new Dimension(100, 30));
        prix24.setOpaque(true);
        prix24.setPreferredSize(new Dimension(-1, 30));
        prix24.setText("0.71");
        panel6.add(prix24, BorderLayout.CENTER);
        final JLabel label10 = new JLabel();
        label10.setForeground(new Color(-1));
        label10.setHorizontalAlignment(0);
        label10.setMaximumSize(new Dimension(-1, 16));
        label10.setMinimumSize(new Dimension(14, 16));
        label10.setPreferredSize(new Dimension(14, 16));
        label10.setText("$");
        panel6.add(label10, BorderLayout.EAST);
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new BorderLayout(0, 0));
        panel7.setOpaque(false);
        panel1.add(panel7, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setForeground(new Color(-1));
        label11.setMinimumSize(new Dimension(30, 16));
        label11.setPreferredSize(new Dimension(30, 16));
        label11.setText("2x10");
        panel7.add(label11, BorderLayout.WEST);
        prix210 = new JTextField();
        prix210.setBackground(new Color(-1));
        prix210.setEditable(true);
        prix210.setHorizontalAlignment(0);
        prix210.setMinimumSize(new Dimension(100, 30));
        prix210.setOpaque(true);
        prix210.setPreferredSize(new Dimension(-1, 30));
        prix210.setText("2.00");
        panel7.add(prix210, BorderLayout.CENTER);
        final JLabel label12 = new JLabel();
        label12.setForeground(new Color(-1));
        label12.setHorizontalAlignment(0);
        label12.setMaximumSize(new Dimension(-1, 16));
        label12.setMinimumSize(new Dimension(14, 16));
        label12.setPreferredSize(new Dimension(14, 16));
        label12.setText("$");
        panel7.add(label12, BorderLayout.EAST);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new BorderLayout(0, 0));
        panel8.setOpaque(false);
        panel1.add(panel8, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setForeground(new Color(-1));
        label13.setMinimumSize(new Dimension(30, 16));
        label13.setPreferredSize(new Dimension(30, 16));
        label13.setText("4x4");
        panel8.add(label13, BorderLayout.WEST);
        prix44 = new JTextField();
        prix44.setBackground(new Color(-1));
        prix44.setHorizontalAlignment(0);
        prix44.setMinimumSize(new Dimension(-1, 30));
        prix44.setPreferredSize(new Dimension(-1, 30));
        prix44.setText("1.49");
        panel8.add(prix44, BorderLayout.CENTER);
        final JLabel label14 = new JLabel();
        label14.setForeground(new Color(-1));
        label14.setHorizontalAlignment(0);
        label14.setMaximumSize(new Dimension(-1, 16));
        label14.setMinimumSize(new Dimension(14, 16));
        label14.setPreferredSize(new Dimension(14, 16));
        label14.setText("$");
        panel8.add(label14, BorderLayout.EAST);
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new BorderLayout(0, 0));
        panel9.setOpaque(false);
        panel1.add(panel9, new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label15 = new JLabel();
        label15.setForeground(new Color(-1));
        label15.setMinimumSize(new Dimension(30, 16));
        label15.setPreferredSize(new Dimension(30, 16));
        label15.setText("6x6");
        panel9.add(label15, BorderLayout.WEST);
        prix66 = new JTextField();
        prix66.setBackground(new Color(-1));
        prix66.setEditable(true);
        prix66.setHorizontalAlignment(0);
        prix66.setMinimumSize(new Dimension(100, 30));
        prix66.setOpaque(true);
        prix66.setPreferredSize(new Dimension(-1, 30));
        prix66.setText("3.84");
        panel9.add(prix66, BorderLayout.CENTER);
        final JLabel label16 = new JLabel();
        label16.setForeground(new Color(-1));
        label16.setHorizontalAlignment(0);
        label16.setMaximumSize(new Dimension(-1, 16));
        label16.setMinimumSize(new Dimension(14, 16));
        label16.setPreferredSize(new Dimension(14, 16));
        label16.setText("$");
        panel9.add(label16, BorderLayout.EAST);
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new BorderLayout(0, 0));
        panel10.setOpaque(false);
        panel1.add(panel10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label17 = new JLabel();
        label17.setForeground(new Color(-1));
        label17.setMinimumSize(new Dimension(30, 16));
        label17.setPreferredSize(new Dimension(30, 16));
        label17.setText("5/4x6");
        panel10.add(label17, BorderLayout.WEST);
        prix56 = new JTextField();
        prix56.setBackground(new Color(-1));
        prix56.setHorizontalAlignment(0);
        prix56.setMinimumSize(new Dimension(100, 30));
        prix56.setPreferredSize(new Dimension(-1, 30));
        prix56.setText("0.97");
        panel10.add(prix56, BorderLayout.CENTER);
        final JLabel label18 = new JLabel();
        label18.setForeground(new Color(-1));
        label18.setHorizontalAlignment(0);
        label18.setMaximumSize(new Dimension(-1, 16));
        label18.setMinimumSize(new Dimension(14, 16));
        label18.setPreferredSize(new Dimension(14, 16));
        label18.setText("$");
        panel10.add(label18, BorderLayout.EAST);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panelPrix;
    }

}
