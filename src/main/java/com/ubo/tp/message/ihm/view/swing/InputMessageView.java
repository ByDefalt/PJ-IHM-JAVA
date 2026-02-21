package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class InputMessageView extends JComponent implements View {

    private static final int ARC = 20;
    // Hauteur minimale raisonnable pour la zone de saisie (inclut padding)
    private static final int MIN_HEIGHT = 48;

    private final ViewContext viewContext;
    private final JTextArea inputField;
    private Runnable onSendRequested;

    private Color normalBorderColor;
    private Color focusBorderColor;
    private boolean focused = false;

    private JScrollPane inputScrollPane;

    public InputMessageView(ViewContext viewContext) {
        this.viewContext = viewContext;
        inputField = new JTextArea();
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        this.setOpaque(true);

        Color panelBg = UIManager.getColor("Panel.background");
        if (panelBg != null) this.setBackground(panelBg);

        createInputField();
        installInternalListeners();

        if (this.viewContext.logger() != null) this.viewContext.logger().debug("InputMessageView initialisée");
    }

    // -------------------------------------------------------------------------
    // API publique — appelée par le contrôleur graphique
    // -------------------------------------------------------------------------

    /**
     * Enregistre le callback déclenché quand l'utilisateur valide l'envoi (Entrée).
     */
    public void setOnSendRequested(Runnable onSendRequested) {
        this.onSendRequested = onSendRequested;
    }

    /**
     * Retourne le texte saisi.
     */
    public String getText() {
        return inputField.getText();
    }

    /**
     * Efface le champ de saisie et remet la mise en page à une ligne.
     */
    public void clearText() {
        inputField.setText("");
        inputField.requestFocusInWindow();
        SwingUtilities.invokeLater(() -> {
            setInputRows(1);
            setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        });
    }

    // -------------------------------------------------------------------------
    // Listeners internes — purement visuels
    // -------------------------------------------------------------------------

    private void installInternalListeners() {
        // Keybindings : Shift+Enter / Ctrl+Enter → saut de ligne, Enter → envoi
        InputMap im = inputField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = inputField.getActionMap();
        im.put(KeyStroke.getKeyStroke("shift ENTER"), DefaultEditorKit.insertBreakAction);
        im.put(KeyStroke.getKeyStroke("ctrl ENTER"), DefaultEditorKit.insertBreakAction);
        im.put(KeyStroke.getKeyStroke("ENTER"), "sendMessage");

        am.put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (onSendRequested != null) onSendRequested.run();
            }
        });

        // Adaptation visuelle du nombre de lignes
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            private void notifyChange() {
                SwingUtilities.invokeLater(() -> {
                    String current = inputField.getText();
                    int actualLines = 1;
                    if (current != null && !current.isEmpty()) {
                        actualLines = current.split("\r\n|\r|\n", -1).length;
                        if (actualLines < 1) actualLines = 1;
                    }
                    int rows = Math.min(actualLines, 3);
                    setInputRows(rows);
                    setVerticalScrollBarPolicy(
                            actualLines > 3
                                    ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
                                    : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
                    );
                });
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                notifyChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                notifyChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                notifyChange();
            }
        });

        // Mise en évidence de la bordure au focus
        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setFocused(true);
                if (viewContext.logger() != null) viewContext.logger().debug("Input focus: true");
            }

            @Override
            public void focusLost(FocusEvent e) {
                setFocused(false);
                if (viewContext.logger() != null) viewContext.logger().debug("Input focus: false");
            }
        });
    }

    // -------------------------------------------------------------------------
    // Helpers internes
    // -------------------------------------------------------------------------

    private void setFocused(boolean focused) {
        this.focused = focused;
        this.repaint();
    }

    private void setInputRows(int rows) {
        if (rows < 1) rows = 1;
        inputField.setRows(rows);
        inputField.revalidate();
        inputField.repaint();
        if (inputScrollPane != null) {
            inputScrollPane.revalidate();
            inputScrollPane.repaint();
        }
        this.revalidate();
    }

    private void setVerticalScrollBarPolicy(int policy) {
        if (inputScrollPane != null) inputScrollPane.setVerticalScrollBarPolicy(policy);
    }

    private void createInputField() {
        Color inputBg = UIManager.getColor("TextArea.background");
        Color inputFg = UIManager.getColor("TextArea.foreground");
        Color caretColor = UIManager.getColor("TextArea.caretForeground");
        normalBorderColor = UIManager.getColor("TextField.borderColor");
        focusBorderColor = UIManager.getColor("TextField.focusedBorderColor");

        if (inputBg == null) inputBg = new Color(64, 68, 75);
        if (inputFg == null) inputFg = new Color(220, 221, 222);
        if (caretColor == null) caretColor = inputFg;
        if (normalBorderColor == null) normalBorderColor = new Color(32, 34, 37);
        if (focusBorderColor == null) focusBorderColor = new Color(88, 101, 242);

        Font baseFont = UIManager.getFont("TextArea.font");
        Font inputFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 14f)
                : new Font("SansSerif", Font.PLAIN, 14);

        inputField.setFont(inputFont);
        inputField.setBackground(inputBg);
        inputField.setForeground(inputFg);
        inputField.setCaretColor(caretColor);
        inputField.setOpaque(false);
        inputField.setRows(1);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        inputField.setColumns(30);

        Color selBg = UIManager.getColor("TextArea.selectionBackground");
        Color selFg = UIManager.getColor("TextArea.selectionForeground");
        if (selBg != null) inputField.setSelectionColor(selBg);
        if (selFg != null) inputField.setSelectedTextColor(selFg);

        JPanel roundedWrapper = createRoundedWrapper(inputBg);

        JScrollPane sp = new JScrollPane(inputField);
        this.inputScrollPane = sp;
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(inputBg);

        roundedWrapper.add(sp, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        );
        this.add(roundedWrapper, gbc);
    }

    private JPanel createRoundedWrapper(Color inputBg) {
        JPanel roundedWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(inputBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));
                Color border = focused ? focusBorderColor : normalBorderColor;
                float strokeWidth = focused ? 2f : 1.5f;
                float inset = strokeWidth / 2f;
                g2.setColor(border);
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.draw(new RoundRectangle2D.Float(
                        inset, inset,
                        getWidth() - strokeWidth,
                        getHeight() - strokeWidth,
                        ARC, ARC));
                g2.dispose();
            }
        };
        roundedWrapper.setOpaque(false);
        roundedWrapper.setBorder(BorderFactory.createEmptyBorder());
        return roundedWrapper;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg = UIManager.getColor("Panel.background");
        if (bg == null) bg = new Color(54, 57, 63);
        g2.setColor(bg);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
        super.paintComponent(g);
    }

    // Garantir une taille minimale et préférée pour éviter d'être écrasé par le JScrollPane
    @Override
    public Dimension getMinimumSize() {
        Insets in = getInsets();
        int minH = MIN_HEIGHT + in.top + in.bottom;
        return new Dimension(0, minH);
    }

    @Override
    public Dimension getPreferredSize() {
        Insets in = getInsets();
        Dimension inner = (inputScrollPane != null) ? inputScrollPane.getPreferredSize() : new Dimension(0, MIN_HEIGHT);
        int h = inner.height + in.top + in.bottom;
        h = Math.max(h, MIN_HEIGHT);
        return new Dimension(inner.width, h);
    }
}

