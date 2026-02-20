package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.ihm.service.IInputMessageView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputMessageView extends JComponent implements IInputMessageView {

    private final Logger LOGGER;
    private final IInputMessageController controller;
    private JTextField inputField;
    private JButton sendButton;
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
        createSendButton();
        createConnector();
    }

    private void createInputField() {
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setColumns(30);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 6), 0, 0
        );
        this.add(inputField, gbc);
    }

    private void createSendButton() {
        sendButton = new JButton("➤");
        sendButton.setToolTipText("Envoyer");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(88, 101, 242));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        GridBagConstraints gbc = new GridBagConstraints(
                1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0
        );
        this.add(sendButton, gbc);
    }

    private void createConnector() {
        // Enter key
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (LOGGER != null) LOGGER.debug("Enter pressed in InputMessageView: " + getMessageText());
                    if (onSendRequested != null) onSendRequested.run();
                }
            }
        });

        // Send button action (simple debug log; controller may add its own ActionListener)
        sendButton.addActionListener(e -> {
            if (LOGGER != null) LOGGER.debug("Send button clicked in InputMessageView: " + getMessageText());
            if (onSendRequested != null) onSendRequested.run();
            // default behavior: do nothing else. Controller should attach own listener to sendButton.
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

    @Override
    public void setOnSendRequested(Runnable handler) {
        this.onSendRequested = handler;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
