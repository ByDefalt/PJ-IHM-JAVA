package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.utils.EmojiBinders;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.function.Supplier;

public class InputMessageView extends JComponent implements View {

    private static final int ARC = 20;
    private static final int MIN_HEIGHT = 48;
    private static final int MAX_VISIBLE_ROWS = 3;

    private final ViewContext viewContext;
    private final JTextArea inputField;
    private Runnable onSendRequested;
    private Supplier<List<User>> usersSupplier;

    private Color normalBorderColor;
    private Color focusBorderColor;
    private boolean focused = false;

    private JScrollPane inputScrollPane;
    private int currentRows = 1;

    // Autocomplete UI
    private final JPopupMenu suggestionPopup = new JPopupMenu();
    private final DefaultListModel<User> suggestionModel = new DefaultListModel<>();
    private final JList<User> suggestionList = new JList<>(suggestionModel);
    private int mentionStart = -1; // position du '@' dans le texte
    private int mentionEnd   = -1; // position du caret au moment de la complétion

    // Autocomplete emoji
    private final JPopupMenu emojiPopup = new JPopupMenu();
    private final DefaultListModel<String> emojiModel = new DefaultListModel<>();
    private final JList<String> emojiList = new JList<>(emojiModel);
    private int emojiStart = -1; // position du ':' dans le texte
    private int emojiEnd   = -1;

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
        setupSuggestionPopup();
        setupEmojiPopup();

        if (this.viewContext.logger() != null) this.viewContext.logger().debug("InputMessageView initialisée");
    }

    // -------------------------------------------------------------------------
    // API publique
    // -------------------------------------------------------------------------

    public void setOnSendRequested(Runnable onSendRequested) {
        this.onSendRequested = onSendRequested;
    }

    public String getText() {
        return inputField.getText();
    }

    public void clearText() {
        inputField.setText("");
        inputField.requestFocusInWindow();
        SwingUtilities.invokeLater(() -> {
            setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            applyRows(1);
            hideSuggestionPopup();
        });
    }

    // -------------------------------------------------------------------------
    // Listeners internes
    // -------------------------------------------------------------------------

    private void installInternalListeners() {
        setupKeyBindings();
        setupDocumentListener();
        setupFocusListener();
    }

    private void setupKeyBindings() {
        InputMap im = inputField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = inputField.getActionMap();
        im.put(KeyStroke.getKeyStroke("shift ENTER"), DefaultEditorKit.insertBreakAction);
        im.put(KeyStroke.getKeyStroke("ctrl ENTER"), DefaultEditorKit.insertBreakAction);
        im.put(KeyStroke.getKeyStroke("ENTER"), "sendMessage");
        im.put(KeyStroke.getKeyStroke("ESCAPE"), "hideSuggestions");
        im.put(KeyStroke.getKeyStroke("TAB"), "completeSelection");
        im.put(KeyStroke.getKeyStroke("DOWN"), "selectNextSuggestion");
        im.put(KeyStroke.getKeyStroke("UP"), "selectPrevSuggestion");

        am.put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suggestionPopup.isVisible()) {
                    // Entrée avec popup ouvert = valider la complétion sélectionnée
                    insertSelectedSuggestion();
                } else if (emojiPopup.isVisible()) {
                    insertSelectedEmoji();
                } else {
                    // Popup fermé = envoyer le message
                    if (onSendRequested != null) onSendRequested.run();
                }
            }
        });

        am.put("hideSuggestions", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideSuggestionPopup();
                hideEmojiPopup();
            }
        });

        am.put("completeSelection", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suggestionPopup.isVisible()) {
                    insertSelectedSuggestion();
                } else if (emojiPopup.isVisible()) {
                    insertSelectedEmoji();
                } else {
                    try {
                        inputField.getDocument().insertString(inputField.getCaretPosition(), "\t", null);
                    } catch (BadLocationException ex) { /* ignore */ }
                }
            }
        });

        am.put("selectNextSuggestion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suggestionPopup.isVisible()) {
                    int idx = suggestionList.getSelectedIndex();
                    if (idx < suggestionModel.size() - 1) suggestionList.setSelectedIndex(idx + 1);
                    suggestionList.ensureIndexIsVisible(suggestionList.getSelectedIndex());
                } else if (emojiPopup.isVisible()) {
                    int idx = emojiList.getSelectedIndex();
                    if (idx < emojiModel.size() - 1) emojiList.setSelectedIndex(idx + 1);
                    emojiList.ensureIndexIsVisible(emojiList.getSelectedIndex());
                }
            }
        });

        am.put("selectPrevSuggestion", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (suggestionPopup.isVisible()) {
                    int idx = suggestionList.getSelectedIndex();
                    if (idx > 0) suggestionList.setSelectedIndex(idx - 1);
                    suggestionList.ensureIndexIsVisible(suggestionList.getSelectedIndex());
                } else if (emojiPopup.isVisible()) {
                    int idx = emojiList.getSelectedIndex();
                    if (idx > 0) emojiList.setSelectedIndex(idx - 1);
                    emojiList.ensureIndexIsVisible(emojiList.getSelectedIndex());
                }
            }
        });
    }

    private void setupDocumentListener() {
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
                    handleAutocomplete();
                    handleEmojiAutocomplete();
                });
            }

            @Override public void insertUpdate(DocumentEvent e)  { notifyChange(); }
            @Override public void removeUpdate(DocumentEvent e)  { notifyChange(); }
            @Override public void changedUpdate(DocumentEvent e) { notifyChange(); }
        });
    }

    private void setupFocusListener() {
        inputField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                setFocused(true);
                if (viewContext.logger() != null) viewContext.logger().debug("Input focus: true");
            }

            @Override
            public void focusLost(FocusEvent e) {
                setFocused(false);
                Component opposite = e.getOppositeComponent();
                if (opposite != null) {
                    if (opposite == suggestionList
                            || SwingUtilities.isDescendingFrom(opposite, suggestionList)
                            || SwingUtilities.isDescendingFrom(opposite, suggestionPopup)) {
                        return;
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if (owner == null
                            || owner == suggestionList
                            || SwingUtilities.isDescendingFrom(owner, suggestionList)
                            || SwingUtilities.isDescendingFrom(owner, suggestionPopup)
                            || owner == emojiList
                            || SwingUtilities.isDescendingFrom(owner, emojiList)
                            || SwingUtilities.isDescendingFrom(owner, emojiPopup)) {
                        return;
                    }
                    hideSuggestionPopup();
                    hideEmojiPopup();
                });
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

    private void applyRows(int rows) {
        if (rows < 1) rows = 1;
        if (rows == currentRows) return;
        currentRows = rows;
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

    private int rowsToHeight(int rows) {
        FontMetrics fm = inputField.getFontMetrics(inputField.getFont());
        int lineH = (fm != null && fm.getHeight() > 0) ? fm.getHeight() : 20;
        Insets fi = inputField.getInsets();
        int fieldPad = (fi != null) ? fi.top + fi.bottom : 16;
        return lineH * rows + fieldPad;
    }

    private void createInputField() {
        configureColorsAndFonts();
        configureInputFieldBasics();
        createScrollAndWrapper();
    }

    private void configureColorsAndFonts() {
        Color inputBg    = UIManager.getColor("TextArea.background");
        Color inputFg    = UIManager.getColor("TextArea.foreground");
        Color caretColor = UIManager.getColor("TextArea.caretForeground");
        normalBorderColor = UIManager.getColor("TextField.borderColor");
        focusBorderColor  = UIManager.getColor("TextField.focusedBorderColor");

        if (inputBg == null)           inputBg           = new Color(64, 68, 75);
        if (inputFg == null)           inputFg           = new Color(220, 221, 222);
        if (caretColor == null)        caretColor        = inputFg;
        if (normalBorderColor == null) normalBorderColor = new Color(32, 34, 37);
        if (focusBorderColor == null)  focusBorderColor  = new Color(88, 101, 242);

        Font baseFont  = UIManager.getFont("TextArea.font");
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

        if (inputField.getDocument() instanceof AbstractDocument adoc) {
            adoc.setDocumentFilter(new MaxLengthFilter(200));
        }

        Color selBg = UIManager.getColor("TextArea.selectionBackground");
        Color selFg = UIManager.getColor("TextArea.selectionForeground");
        if (selBg != null) inputField.setSelectionColor(selBg);
        if (selFg != null) inputField.setSelectedTextColor(selFg);
    }

    private void configureInputFieldBasics() { }

    private void createScrollAndWrapper() {
        final Color finalInputBg = inputField.getBackground();
        JScrollPane sp = new JScrollPane(inputField) {
            @Override public Dimension getPreferredSize() { return new Dimension(0, rowsToHeight(currentRows)); }
            @Override public Dimension getMinimumSize()   { return new Dimension(0, rowsToHeight(1)); }
        };
        this.inputScrollPane = sp;
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(finalInputBg);

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
                g2.draw(new RoundRectangle2D.Float(inset, inset, getWidth() - strokeWidth, getHeight() - strokeWidth, ARC, ARC));
                g2.dispose();
            }
            @Override public Dimension getPreferredSize() { return new Dimension(0, rowsToHeight(currentRows)); }
            @Override public Dimension getMinimumSize()   { return new Dimension(0, rowsToHeight(1)); }
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

    @Override
    public Dimension getMinimumSize() {
        Insets in = getInsets();
        int h = rowsToHeight(1) + in.top + in.bottom;
        return new Dimension(0, Math.max(h, MIN_HEIGHT));
    }

    @Override
    public Dimension getPreferredSize() {
        Insets in = getInsets();
        int h = rowsToHeight(currentRows) + in.top + in.bottom;
        return new Dimension(0, Math.max(h, MIN_HEIGHT));
    }

    // -------------------------------------------------------------------------
    // DocumentFilter : limite de 200 caractères
    // -------------------------------------------------------------------------

    private static class MaxLengthFilter extends DocumentFilter {
        private final int max;
        public MaxLengthFilter(int max) { this.max = max; }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null) return;
            int allowed = max - fb.getDocument().getLength();
            if (allowed <= 0) { Toolkit.getDefaultToolkit().beep(); return; }
            if (string.length() <= allowed) {
                super.insertString(fb, offset, string, attr);
            } else {
                super.insertString(fb, offset, string.substring(0, allowed), attr);
                Toolkit.getDefaultToolkit().beep();
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null) { super.replace(fb, offset, length, text, attrs); return; }
            int allowed = max - (fb.getDocument().getLength() - length);
            if (allowed <= 0) { Toolkit.getDefaultToolkit().beep(); return; }
            if (text.length() <= allowed) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                super.replace(fb, offset, length, text.substring(0, allowed), attrs);
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    // -------------------------------------------------------------------------
    // API publique : fournisseur d'utilisateurs
    // -------------------------------------------------------------------------

    public void setUsersSupplier(Supplier<List<User>> usersSupplier) {
        this.usersSupplier = usersSupplier;
    }

    // -------------------------------------------------------------------------
    // Autocomplete
    // -------------------------------------------------------------------------

    private void setupSuggestionPopup() {
        // Doit être appelé avant la création du JPopupMenu pour être effectif
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);

        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        suggestionList.setVisibleRowCount(8);
        suggestionList.setFixedCellHeight(24);
        suggestionList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof User u) setText(u.getName() + " (@" + u.getUserTag() + ")");
                return this;
            }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = suggestionList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    suggestionList.setSelectedIndex(index);
                    insertSelectedSuggestion();
                }
            }
        });

        // Non-focusable : le popup n'arrache jamais le focus de l'inputField
        suggestionPopup.setFocusable(false);
        suggestionList.setFocusable(false);

        JScrollPane sp = new JScrollPane(suggestionList);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        suggestionPopup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        suggestionPopup.add(sp);
    }

    /**
     * Détecte un token "@xxx" actif juste avant le caret.
     * Remonte caractère par caractère : si on trouve '@' → token actif.
     * Si on trouve un espace/retour avant '@' → pas de mention active, popup fermé.
     * Le token après '@' ne doit pas contenir d'espace (sinon la mention est déjà terminée).
     */
    private void handleAutocomplete() {
        if (usersSupplier == null) { hideSuggestionPopup(); return; }

        int caretPos = inputField.getCaretPosition();
        String text  = inputField.getText();
        if (text == null || text.isEmpty() || caretPos == 0) { hideSuggestionPopup(); return; }

        // Remonter depuis le caret pour trouver un '@' sans espace intermédiaire
        int atPos = -1;
        for (int i = caretPos - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == '@') { atPos = i; break; }
            if (Character.isWhitespace(c)) { hideSuggestionPopup(); return; }
        }

        if (atPos < 0) { hideSuggestionPopup(); return; }

        // Token entre '@' et le caret — ne doit pas contenir d'espace
        String token = text.substring(atPos + 1, caretPos);
        if (token.contains(" ") || token.contains("\t") || token.contains("\n")) {
            hideSuggestionPopup();
            return;
        }

        // Récupérer et filtrer les utilisateurs
        List<User> all = usersSupplier.get();
        if (all == null || all.isEmpty()) { hideSuggestionPopup(); return; }

        String q = token.toLowerCase();
        suggestionModel.clear();
        for (User u : all) {
            String tag  = (u.getUserTag() != null) ? u.getUserTag().toLowerCase() : "";
            String name = (u.getName()    != null) ? u.getName().toLowerCase()    : "";
            if (q.isEmpty() || tag.contains(q) || name.contains(q)) {
                suggestionModel.addElement(u);
            }
        }

        if (suggestionModel.isEmpty()) { hideSuggestionPopup(); return; }

        // Afficher le popup sous le '@'
        try {
            Rectangle rect;
            try {
                java.awt.geom.Rectangle2D r2 = inputField.modelToView2D(atPos);
                rect = new Rectangle((int) Math.round(r2.getX()), (int) Math.round(r2.getY()),
                        (int) Math.round(r2.getWidth()), (int) Math.round(r2.getHeight()));
            } catch (Throwable t) {
                rect = inputField.modelToView(atPos);
            }

            Component comp = suggestionPopup.getComponent(0);
            if (comp instanceof JScrollPane jsp) {
                int cellH = suggestionList.getFixedCellHeight();
                if (cellH <= 0) {
                    FontMetrics fm = suggestionList.getFontMetrics(suggestionList.getFont());
                    cellH = Math.max(20, fm.getHeight() + 4);
                    suggestionList.setFixedCellHeight(cellH);
                }
                int prefH = Math.min(8, suggestionModel.getSize()) * cellH;
                prefH = Math.max(48, Math.min(200, prefH));
                jsp.setPreferredSize(new Dimension(240, prefH));
            }

            suggestionList.setSelectedIndex(0);
            suggestionPopup.show(inputField, rect.x, rect.y + rect.height);

            mentionStart = atPos;    // inclut le '@'
            mentionEnd   = caretPos;

        } catch (BadLocationException ex) {
            hideSuggestionPopup();
        }
    }

    /**
     * Insère "@userTag " en remplaçant la zone "@token" courante.
     * L'espace final garantit que handleAutocomplete() ne rouvre PAS le popup
     * (le token après insertion contient un espace → fermeture automatique).
     */
    private void insertSelectedSuggestion() {
        User u = suggestionList.getSelectedValue();
        if (u == null) return;

        // Fermer le popup AVANT de modifier le document
        hideSuggestionPopup();

        String mention = u.getUserTag() + " ";
        try {
            Document doc = inputField.getDocument();
            int docLen = doc.getLength();
            if (mentionStart >= 0 && mentionEnd >= mentionStart && mentionEnd <= docLen) {
                doc.remove(mentionStart, mentionEnd - mentionStart);
                doc.insertString(mentionStart, mention, null);
            } else {
                int pos = Math.min(inputField.getCaretPosition(), docLen);
                doc.insertString(pos, mention, null);
            }
        } catch (BadLocationException ex) {
            // ignore
        }
        SwingUtilities.invokeLater(inputField::requestFocusInWindow);
    }

    private void hideSuggestionPopup() {
        if (suggestionPopup.isVisible()) suggestionPopup.setVisible(false);
        mentionStart = -1;
        mentionEnd   = -1;
    }

    private void setupEmojiPopup() {
        emojiList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        emojiList.setVisibleRowCount(8);
        emojiList.setFixedCellHeight(24);
        emojiList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof String code) {
                    setText(code);
                }
                return this;
            }
        });

        emojiList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int index = emojiList.locationToIndex(e.getPoint());
                if (index >= 0) {
                    emojiList.setSelectedIndex(index);
                    insertSelectedEmoji();
                }
            }
        });

        emojiPopup.setFocusable(false);
        emojiList.setFocusable(false);

        JScrollPane sp = new JScrollPane(emojiList);
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        emojiPopup.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        emojiPopup.add(sp);
    }

    /**
     * Détecte un token ":xxx" actif juste avant le caret.
     * Remonte depuis le caret : si on trouve ':' → token emoji actif.
     * Si on trouve un espace/retour avant ':' → pas d'emoji actif.
     */
    private void handleEmojiAutocomplete() {
        int caretPos = inputField.getCaretPosition();
        String text  = inputField.getText();
        if (text == null || text.isEmpty() || caretPos == 0) { hideEmojiPopup(); return; }

        // Remonter depuis le caret pour trouver un ':' sans espace intermédiaire
        int colonPos = -1;
        for (int i = caretPos - 1; i >= 0; i--) {
            char c = text.charAt(i);
            if (c == ':') { colonPos = i; break; }
            if (Character.isWhitespace(c)) { hideEmojiPopup(); return; }
        }

        if (colonPos < 0) { hideEmojiPopup(); return; }

        // Token entre ':' et le caret
        String token = text.substring(colonPos + 1, caretPos);
        if (token.contains(" ") || token.contains("\t") || token.contains("\n")) {
            hideEmojiPopup();
            return;
        }

        // Filtrer les codes emoji supportés
        String[] allCodes = EmojiBinders.getSupportedCodes();
        String q = token.toLowerCase();
        emojiModel.clear();
        for (String code : allCodes) {
            // code ressemble à ":smile:" — on cherche dans la partie centrale
            String inner = code.replace(":", "");
            if (q.isEmpty() || inner.contains(q)) {
                emojiModel.addElement(code);
            }
        }

        if (emojiModel.isEmpty()) { hideEmojiPopup(); return; }

        // Afficher le popup sous le ':'
        try {
            Rectangle rect;
            try {
                java.awt.geom.Rectangle2D r2 = inputField.modelToView2D(colonPos);
                rect = new Rectangle((int) Math.round(r2.getX()), (int) Math.round(r2.getY()),
                        (int) Math.round(r2.getWidth()), (int) Math.round(r2.getHeight()));
            } catch (Throwable t) {
                rect = inputField.modelToView(colonPos);
            }

            Component comp = emojiPopup.getComponent(0);
            if (comp instanceof JScrollPane jsp) {
                int cellH = emojiList.getFixedCellHeight();
                if (cellH <= 0) {
                    FontMetrics fm = emojiList.getFontMetrics(emojiList.getFont());
                    cellH = Math.max(20, fm.getHeight() + 4);
                    emojiList.setFixedCellHeight(cellH);
                }
                int prefH = Math.min(8, emojiModel.getSize()) * cellH;
                prefH = Math.max(48, Math.min(200, prefH));
                jsp.setPreferredSize(new Dimension(200, prefH));
            }

            emojiList.setSelectedIndex(0);
            emojiPopup.show(inputField, rect.x, rect.y + rect.height);

            emojiStart = colonPos;
            emojiEnd   = caretPos;

        } catch (BadLocationException ex) {
            hideEmojiPopup();
        }
    }

    /**
     * Insère le code emoji sélectionné (ex: ":smile:") en remplaçant la zone ":token" courante.
     * Un espace final est ajouté pour éviter de rouvrir immédiatement le popup.
     */
    private void insertSelectedEmoji() {
        String code = emojiList.getSelectedValue();
        if (code == null) return;

        int currentCaret = inputField.getCaretPosition();
        int savedEmojiStart = emojiStart; // sauvegarder avant hideEmojiPopup() qui remet à -1
        hideEmojiPopup();

        String insertion = code + " ";
        try {
            Document doc = inputField.getDocument();
            int docLen = doc.getLength();
            if (savedEmojiStart >= 0 && savedEmojiStart < docLen) {
                int end = Math.min(currentCaret, docLen);
                int removeLen = end - savedEmojiStart;
                if (removeLen > 0) doc.remove(savedEmojiStart, removeLen);
                doc.insertString(savedEmojiStart, insertion, null);
            } else {
                int pos = Math.min(currentCaret, docLen);
                doc.insertString(pos, insertion, null);
            }
        } catch (BadLocationException ex) {
        }
        SwingUtilities.invokeLater(inputField::requestFocusInWindow);
    }

    private void hideEmojiPopup() {
        if (emojiPopup.isVisible()) emojiPopup.setVisible(false);
        emojiStart = -1;
        emojiEnd   = -1;
    }
}