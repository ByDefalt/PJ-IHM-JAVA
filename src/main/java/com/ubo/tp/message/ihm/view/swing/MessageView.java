package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Composant représentant un seul message (bulle) — style simple inspiré de Discord.
 * Utilise GridBagLayout et sépare la construction en méthodes.
 * La vue est autonome et n'a pas de référence au controller.
 */
public class MessageView extends JComponent implements View {

    // Formatteur pour afficher la date en jj/MM/aaaa HH'h'mm
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH'h'mm")
            .withLocale(Locale.FRANCE);
    private final Logger logger;
    private final Message message;
    private JLabel authorLabel;
    private JTextArea contentArea;
    private JLabel timeLabel;

    public MessageView(Logger logger, Message message) {
        this.logger = logger;
        this.message = message;
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        this.setLayout(new GridBagLayout());
        this.setOpaque(true);

        init(message);

        if (this.logger != null) this.logger.debug("MessageView initialisée pour '" + message.getSender() + "'");
    }

    private void init(Message message) {
        // On transmet le timestamp (millis) et on le formate localement
        String authorName = (message.getSender() != null) ? message.getSender().getName() : "";
        createBubble(authorName, message.getText(), message.getEmissionDate());
    }

    private void createBubble(String author, String content, long emissionMillis) {
        JPanel bubble = new JPanel(new GridBagLayout());
        bubble.setBackground(new Color(64, 68, 75));
        bubble.setBorder(new EmptyBorder(6, 8, 6, 8));
        bubble.setOpaque(false);

        GridBagConstraints gbcBubble = new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 0, 2, 2), 0, 0
        );
        this.add(bubble, gbcBubble);

        // Header and content inside bubble
        createHeader(bubble, author, emissionMillis);
        createContent(bubble, content);
    }

    private void createHeader(JPanel bubble, String author, long emissionMillis) {
        authorLabel = new JLabel(author != null ? author : "");
        authorLabel.setForeground(Color.WHITE);
        authorLabel.setFont(new Font("Arial", Font.BOLD, 12));

        timeLabel = new JLabel();
        timeLabel.setForeground(new Color(170, 170, 170));
        // Utiliser la même taille de police que l'auteur pour éviter un léger décalage vertical
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        // Formatter la date à partir du timestamp
        String formattedTime = formatTimestamp(emissionMillis);
        timeLabel.setText(formattedTime != null ? formattedTime : "");

        // Header panel pour afficher le nom et la date côte à côte avec un petit espacement
        // Utiliser BoxLayout horizontal et aligner verticalement les composants au centre
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setOpaque(false);

        // Aligner verticalement au centre pour corriger la différence d'alignement
        authorLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        timeLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        // Ajouter un petit espace entre le nom et la date en utilisant une bordure gauche sur la date
        timeLabel.setBorder(new EmptyBorder(0, 8, 0, 0));

        headerPanel.add(authorLabel);
        headerPanel.add(timeLabel);

        GridBagConstraints gbcHeader = new GridBagConstraints(
                0, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 4, 0), 0, 0
        );

        bubble.add(headerPanel, gbcHeader);
    }

    private void createContent(JPanel bubble, String content) {
        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setOpaque(false);
        contentArea.setForeground(Color.WHITE);
        contentArea.setFont(new Font("Arial", Font.PLAIN, 13));
        contentArea.setBorder(null);
        contentArea.setFocusable(false);
        contentArea.setText(content != null ? content : "");

        GridBagConstraints gbcContent = new GridBagConstraints(
                0, 1, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        );
        bubble.add(contentArea, gbcContent);
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        authorLabel.setText(message.getSender() != null ? message.getSender().getName() : "");
        contentArea.setText(message.getText() != null ? message.getText() : "");
        timeLabel.setText(formatTimestamp(message.getEmissionDate()));
        revalidate();
        repaint();
    }

    public void updateMessage(Message message) {
        this.setMessage(message);
    }

    /**
     * Convertit un timestamp (millis) en chaîne formatée : jj/MM/aaaa HH'h'mm
     */
    private String formatTimestamp(long millis) {
        try {
            return DATE_FORMATTER.format(Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()));
        } catch (Exception e) {
            if (this.logger != null) this.logger.debug("Erreur formatage date: " + e.getMessage());
            return String.valueOf(millis);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
