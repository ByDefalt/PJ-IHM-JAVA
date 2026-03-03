package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class InputMessageView extends JComponent implements View {

    private static final int ARC = 20;
    private static final int MIN_HEIGHT = 48;
    private static final int MAX_VISIBLE_ROWS = 3;

    private final ViewContext viewContext;
    private final JTextArea inputField;
    private Runnable onSendRequested;

    private Color normalBorderColor;
    private Color focusBorderColor;
    private boolean focused = false;

    private JScrollPane inputScrollPane;
    private int currentRows = 1;

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
    // API publique
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
        // Le DocumentListener va être déclenché mais on force aussi en invokeLater
        // pour s'assurer que le reset à 1 ligne est bien appliqué après le setText.
        SwingUtilities.invokeLater(() -> {
            setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            applyRows(1);
        });
    }

    // -------------------------------------------------------------------------
    // Listeners internes
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
                    int rows = Math.min(actualLines, MAX_VISIBLE_ROWS);
                    applyRows(rows);
                    setVerticalScrollBarPolicy(
                            actualLines > MAX_VISIBLE_ROWS
                                    ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
                                    : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
                    );
                });
            }

            @Override public void insertUpdate(DocumentEvent e) { notifyChange(); }
            @Override public void removeUpdate(DocumentEvent e)  { notifyChange(); }
            @Override public void changedUpdate(DocumentEvent e) { notifyChange(); }
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

    /**
     * Met à jour currentRows et demande un recalcul complet du layout
     * jusqu'au parent, sans jamais appeler inputField.setRows() (dont la
     * taille mémorisée par le JScrollPane causait le bug).
     */
    private void applyRows(int rows) {
        if (rows < 1) rows = 1;
        if (rows == currentRows) return; // rien à faire
        currentRows = rows;
        // Invalider toute la chaîne pour effacer les tailles en cache
        inputField.invalidate();
        if (inputScrollPane != null) inputScrollPane.invalidate();
        this.invalidate();
        Container parent = this.getParent();
        if (parent != null) {
            parent.invalidate();
            parent.validate();
            parent.repaint();
        } else {
            this.validate();
            this.repaint();
        }
    }

    private void setVerticalScrollBarPolicy(int policy) {
        if (inputScrollPane != null) inputScrollPane.setVerticalScrollBarPolicy(policy);
    }

    /** Calcule la hauteur en pixels pour un nombre de lignes donné. */
    private int rowsToHeight(int rows) {
        FontMetrics fm = inputField.getFontMetrics(inputField.getFont());
        int lineH = (fm != null && fm.getHeight() > 0) ? fm.getHeight() : 20;
        Insets fi = inputField.getInsets();
        int fieldPad = (fi != null) ? fi.top + fi.bottom : 16;
        return lineH * rows + fieldPad;
    }

    private void createInputField() {
        Color inputBg = UIManager.getColor("TextArea.background");
        Color inputFg = UIManager.getColor("TextArea.foreground");
        Color caretColor = UIManager.getColor("TextArea.caretForeground");
        normalBorderColor = UIManager.getColor("TextField.borderColor");
        focusBorderColor  = UIManager.getColor("TextField.focusedBorderColor");

        if (inputBg == null)        inputBg        = new Color(64, 68, 75);
        if (inputFg == null)        inputFg        = new Color(220, 221, 222);
        if (caretColor == null)     caretColor     = inputFg;
        if (normalBorderColor == null) normalBorderColor = new Color(32, 34, 37);
        if (focusBorderColor  == null) focusBorderColor  = new Color(88, 101, 242);

        Font baseFont  = UIManager.getFont("TextArea.font");
        Font inputFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 14f)
                : new Font("SansSerif", Font.PLAIN, 14);

        inputField.setFont(inputFont);
        inputField.setBackground(inputBg);
        inputField.setForeground(inputFg);
        inputField.setCaretColor(caretColor);
        inputField.setOpaque(false);
        inputField.setRows(1);          // valeur initiale Swing, on ne la touche plus ensuite
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        inputField.setColumns(30);

        // --- UI-side validation: limiter la saisie à 200 caractères ---
        if (inputField.getDocument() instanceof AbstractDocument) {
            AbstractDocument adoc = (AbstractDocument) inputField.getDocument();
            adoc.setDocumentFilter(new MaxLengthFilter(200));
        }

        Color selBg = UIManager.getColor("TextArea.selectionBackground");
        Color selFg = UIManager.getColor("TextArea.selectionForeground");
        if (selBg != null) inputField.setSelectionColor(selBg);
        if (selFg != null) inputField.setSelectedTextColor(selFg);

        // Le scrollPane wrappant le textArea
        final Color finalInputBg = inputBg;
        JScrollPane sp = new JScrollPane(inputField) {
            @Override
            public Dimension getPreferredSize() {
                // Hauteur pilotée par currentRows, jamais par le cache Swing
                int h = rowsToHeight(currentRows);
                // largeur : laisser le layout décider (on retourne 0, le fill=BOTH s'en charge)
                return new Dimension(0, h);
            }
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, rowsToHeight(1));
            }
        };
        this.inputScrollPane = sp;
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(finalInputBg);

        // Wrapper arrondi dont getPreferredSize délègue aussi à currentRows
        JPanel roundedWrapper = createRoundedWrapper(finalInputBg);
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
            @Override
            public Dimension getPreferredSize() {
                // Déléguer au même calcul que InputMessageView
                int h = rowsToHeight(currentRows);
                return new Dimension(0, h);
            }
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, rowsToHeight(1));
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
        int h = rowsToHeight(1) + in.top + in.bottom;
        h = Math.max(h, MIN_HEIGHT);
        return new Dimension(0, h);
    }

    @Override
    public Dimension getPreferredSize() {
        Insets in = getInsets();
        int h = rowsToHeight(currentRows) + in.top + in.bottom;
        h = Math.max(h, MIN_HEIGHT);
        return new Dimension(0, h);
    }

    // DocumentFilter interne pour limiter la longueur du texte
    private static class MaxLengthFilter extends DocumentFilter {
        private final int max;

        public MaxLengthFilter(int max) {
            this.max = max;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            int currentLength = fb.getDocument().getLength();
            int newLength = string.length();
            if (currentLength + newLength <= max) {
                super.insertString(fb, offset, string, attr);
            } else {
                // tronquer le texte pour respecter la taille max
                int allowed = max - currentLength;
                if (allowed > 0) {
                    String cut = string.substring(0, Math.max(0, allowed));
                    super.insertString(fb, offset, cut, attr);
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) {
                super.replace(fb, offset, length, text, attrs);
                return;
            }
            int currentLength = fb.getDocument().getLength();
            int newLength = text.length();
            int resulting = currentLength - length + newLength;
            if (resulting <= max) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                int allowed = max - (currentLength - length);
                if (allowed > 0) {
                    String cut = text.substring(0, Math.max(0, allowed));
                    super.replace(fb, offset, length, cut, attrs);
                    Toolkit.getDefaultToolkit().beep();
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }
    }
}
