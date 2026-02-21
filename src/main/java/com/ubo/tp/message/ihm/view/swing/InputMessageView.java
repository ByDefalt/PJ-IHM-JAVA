package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class InputMessageView extends JComponent implements View {

    private static final int ARC = 20; // rayon des coins arrondis

    private final Logger                  LOGGER;
    private final JTextArea               inputField;
    private       Runnable                onSendRequested;

    private Color   normalBorderColor;
    private Color   focusBorderColor;
    private boolean focused = false;

    // stocke le JScrollPane pour que le controller puisse agir sur la scrollbar
    private JScrollPane inputScrollPane;

    public InputMessageView(Logger logger) {
        this.LOGGER     = logger;
        inputField = new JTextArea();
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        this.setOpaque(true);

        Color panelBg = UIManager.getColor("Panel.background");
        if (panelBg != null) this.setBackground(panelBg);

        init();
        if (this.LOGGER != null) this.LOGGER.debug("InputMessageView initialisée");
    }

    private void init() {
        createInputField();
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
        // store in field so controller can adjust policy
        this.inputScrollPane = sp;
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(inputBg);

        roundedWrapper.add(sp, BorderLayout.CENTER);

        // Focus and document listeners removed — controller now attaches them

        // ── Ajout dans le layout principal ───────────────────────────────────
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        );
        this.add(roundedWrapper, gbc);
    }


    public JTextArea getInputField() {
        return inputField;
    }

    public void setOnSendRequested(Runnable onSendRequested) {
        this.onSendRequested = onSendRequested;
    }

    /**
     * Permet au controller de forcer l'état de focus (met à jour le flag et repaint le wrapper).
     */
    public void setFocused(boolean focused) {
        this.focused = focused;
        // repaint entire component so rounded wrapper repaints using the focused field
        this.repaint();
    }

    public void setInputRows(int rows) {
        if (rows < 1) rows = 1;
        inputField.setRows(rows);
        inputField.revalidate();
        inputField.repaint();
        this.revalidate();
    }

    public void setVerticalScrollBarPolicy(int policy) {
        if (inputScrollPane != null) inputScrollPane.setVerticalScrollBarPolicy(policy);
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