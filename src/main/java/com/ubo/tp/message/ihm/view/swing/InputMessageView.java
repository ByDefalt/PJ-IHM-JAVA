package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;

public class InputMessageView extends JComponent implements View {

    private final Logger LOGGER;
    private final IInputMessageController controller;
    private JTextArea inputField;
    private Runnable onSendRequested;

    public InputMessageView(Logger logger, IInputMessageController controller) {
        this.LOGGER = logger;
        this.controller = controller;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        this.setOpaque(true);

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
        // Utiliser JTextArea pour supporter les retours à la ligne
        inputField = new JTextArea();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        // Taille initiale: 1 ligne, mais permettre d'aller jusqu'à 3
        inputField.setRows(1);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 6), 0, 0
        );
        // Mettre le JTextArea dans un JScrollPane pour gérer la taille et le scroll
        final JScrollPane sp = new JScrollPane(inputField);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        sp.setBorder(null);
        this.add(sp, gbc);

        // Ajuster la largeur minimale
        inputField.setColumns(30);

        // Ajouter un listener pour ajuster dynamiquement le nombre de lignes (1..3)
        inputField.getDocument().addDocumentListener(new DocumentListener() {
            private void adjust() {
                SwingUtilities.invokeLater(() -> {
                    int actualLines = inputField.getLineCount();
                    if (actualLines < 1) actualLines = 1;
                    int rows = Math.min(actualLines, 3);

                    // Si le texte dépasse 3 lignes, on active le scroll vertical (AS_NEEDED)
                    if (inputField.getLineCount() > 3) {
                        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                    } else {
                        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
                    }

                    if (inputField.getRows() != rows) {
                        inputField.setRows(rows);
                        inputField.revalidate();
                        inputField.repaint();
                        InputMessageView.this.revalidate();
                    }
                });
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                adjust();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                adjust();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                adjust();
            }
        });
    }

    private void createConnector() {
        // Remplacer le KeyListener par des bindings clavier plus fiables
        InputMap im = inputField.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = inputField.getActionMap();

        // Ctrl+Enter et Shift+Enter doivent insérer un saut de ligne -> utiliser l'action par défaut "insert-break"
        KeyStroke shiftEnter = KeyStroke.getKeyStroke("shift ENTER");
        KeyStroke ctrlEnter = KeyStroke.getKeyStroke("ctrl ENTER");
        im.put(shiftEnter, DefaultEditorKit.insertBreakAction);
        im.put(ctrlEnter, DefaultEditorKit.insertBreakAction);

        // ENTER seul doit déclencher l'envoi
        KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
        im.put(enter, "sendMessage");

        am.put("sendMessage", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (LOGGER != null) LOGGER.debug("Enter pressed in InputMessageView: " + getMessageText());
                if (onSendRequested != null) onSendRequested.run();
            }
        });
    }

    public String getMessageText() {
        return inputField.getText();
    }

    public void clearInput() {
        inputField.setText("");
    }

    /**
     * Récupère le texte courant et le vide (utile pour le controller lors d'un envoi).
     */
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
        super.paintComponent(g);
    }
}
