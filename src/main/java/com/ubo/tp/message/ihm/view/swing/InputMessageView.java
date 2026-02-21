package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class InputMessageView extends JComponent implements View {

    private static final int ARC = 20; // rayon des coins arrondis

    private final Logger                  LOGGER;
    private final IInputMessageController controller;
    private       JTextArea               inputField;
    private       Runnable                onSendRequested;

    // Couleurs gardées en champs pour le FocusListener et le paintComponent du wrapper
    private Color   normalBorderColor;
    private Color   focusBorderColor;
    private boolean focused = false;

    public InputMessageView(Logger logger, IInputMessageController controller) {
        this.LOGGER     = logger;
        this.controller = controller;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        this.setOpaque(true);

        Color panelBg = UIManager.getColor("Panel.background");
        if (panelBg != null) this.setBackground(panelBg);

        init();
        if (this.LOGGER != null) this.LOGGER.debug("InputMessageView initialisée");
    }

    public InputMessageView(Logger logger) {
        this(logger, null);
    }

    private void init() {
        createInputField();
        createConnector();
    }

    private void createInputField() {
        // ── Couleurs ─────────────────────────────────────────────────────────
        Color inputBg    = UIManager.getColor("TextArea.background");
        Color inputFg    = UIManager.getColor("TextArea.foreground");
        Color caretColor = UIManager.getColor("TextArea.caretForeground");
        normalBorderColor = UIManager.getColor("TextField.borderColor");
        focusBorderColor  = UIManager.getColor("TextField.focusedBorderColor");

        if (inputBg           == null) inputBg           = new Color(64,  68,  75);
        if (inputFg           == null) inputFg           = new Color(220, 221, 222);
        if (caretColor        == null) caretColor        = inputFg;
        if (normalBorderColor == null) normalBorderColor = new Color(32,  34,  37);
        if (focusBorderColor  == null) focusBorderColor  = new Color(88, 101, 242);

        // ── Police ───────────────────────────────────────────────────────────
        Font baseFont  = UIManager.getFont("TextArea.font");
        Font inputFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 14f)
                : new Font("SansSerif", Font.PLAIN, 14);

        // ── JTextArea transparent (le fond sera peint par le wrapper) ────────
        inputField = new JTextArea();
        inputField.setFont(inputFont);
        inputField.setBackground(inputBg);
        inputField.setForeground(inputFg);
        inputField.setCaretColor(caretColor);
        inputField.setOpaque(false); // le wrapper peint le fond arrondi
        inputField.setRows(1);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        inputField.setColumns(30);

        Color selBg = UIManager.getColor("TextArea.selectionBackground");
        Color selFg = UIManager.getColor("TextArea.selectionForeground");
        if (selBg != null) inputField.setSelectionColor(selBg);
        if (selFg != null) inputField.setSelectedTextColor(selFg);

        // ── Wrapper arrondi ──────────────────────────────────────────────────
        final Color finalInputBg = inputBg;
        JPanel roundedWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond arrondi
                g2.setColor(finalInputBg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));

                // Bordure (normale ou focus)
                Color border = focused ? focusBorderColor : normalBorderColor;
                float strokeWidth = focused ? 2f : 1.5f;
                float inset       = strokeWidth / 2f;
                g2.setColor(border);
                g2.setStroke(new BasicStroke(strokeWidth));
                g2.draw(new RoundRectangle2D.Float(
                        inset, inset,
                        getWidth()  - strokeWidth,
                        getHeight() - strokeWidth,
                        ARC, ARC));

                g2.dispose();
            }
        };
        roundedWrapper.setOpaque(false);
        roundedWrapper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ── ScrollPane à l'intérieur du wrapper ──────────────────────────────
        final JScrollPane sp = new JScrollPane(inputField);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(inputBg);

        roundedWrapper.add(sp, BorderLayout.CENTER);

        // ── Focus listener → repeindre la bordure ────────────────────────────
        inputField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                focused = true;
                roundedWrapper.repaint();
            }
            @Override public void focusLost(FocusEvent e) {
                focused = false;
                roundedWrapper.repaint();
            }
        });

        // ── Ajustement dynamique 1..3 lignes ────────────────────────────────
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            private void adjust() {
                SwingUtilities.invokeLater(() -> {
                    int actualLines = inputField.getLineCount();
                    if (actualLines < 1) actualLines = 1;
                    int rows = Math.min(actualLines, 3);

                    sp.setVerticalScrollBarPolicy(inputField.getLineCount() > 3
                            ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED
                            : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

                    if (inputField.getRows() != rows) {
                        inputField.setRows(rows);
                        inputField.revalidate();
                        inputField.repaint();
                        InputMessageView.this.revalidate();
                    }
                });
            }
            @Override public void insertUpdate(DocumentEvent e)  { adjust(); }
            @Override public void removeUpdate(DocumentEvent e)   { adjust(); }
            @Override public void changedUpdate(DocumentEvent e)  { adjust(); }
        });

        // ── Ajout dans le layout principal ───────────────────────────────────
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        );
        this.add(roundedWrapper, gbc);
    }

    private void createConnector() {
        InputMap  im = inputField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = inputField.getActionMap();

        im.put(KeyStroke.getKeyStroke("shift ENTER"), DefaultEditorKit.insertBreakAction);
        im.put(KeyStroke.getKeyStroke("ctrl ENTER"),  DefaultEditorKit.insertBreakAction);
        im.put(KeyStroke.getKeyStroke("ENTER"), "sendMessage");

        am.put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (LOGGER != null) LOGGER.debug("Enter pressed: " + getMessageText());
                if (onSendRequested != null) onSendRequested.run();
            }
        });
    }

    public String getMessageText()  { return inputField.getText(); }
    public void   clearInput()      { inputField.setText(""); }

    public String consumeMessage() {
        String t = getMessageText();
        clearInput();
        return t;
    }

    public void setOnSendRequested(Runnable handler) {
        this.onSendRequested = handler;
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
}