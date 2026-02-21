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
 * Les couleurs et polices sont lues depuis l'UIManager pour s'intégrer au DiscordTheme.
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

        if (this.logger != null)
            this.logger.debug("MessageView initialisée pour '" + message.getSender() + "'");
    }

    private void init(Message message) {
        String authorName = (message.getSender() != null) ? message.getSender().getName() : "";
        createBubble(authorName, message.getText(), message.getEmissionDate());
    }

    private void createBubble(String author, String content, long emissionMillis) {
        JPanel bubble = new JPanel(new GridBagLayout());

        // Fond de la bulle : List.selectionBackground (#4F545C) ou fallback
        Color bubbleBg = UIManager.getColor("List.selectionBackground");
        if (bubbleBg == null) bubbleBg = UIManager.getColor("Panel.background");
        bubble.setBackground(bubbleBg);
        bubble.setBorder(new EmptyBorder(6, 8, 6, 8));
        bubble.setOpaque(false);

        GridBagConstraints gbcBubble = new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 0, 2, 2), 0, 0
        );
        this.add(bubble, gbcBubble);

        createHeader(bubble, author, emissionMillis);
        createContent(bubble, content);
    }

    private void createHeader(JPanel bubble, String author, long emissionMillis) {
        // Couleur auteur : texte heading blanc (#FFFFFF)
        Color authorColor = UIManager.getColor("Label.foreground");
        if (authorColor == null) authorColor = Color.WHITE;

        // Couleur heure : texte muted (#72767D)
        Color timeColor = UIManager.getColor("Label.disabledForeground");
        if (timeColor == null) timeColor = new Color(114, 118, 125);

        // Police de base du thème
        Font baseFont = UIManager.getFont("Label.font");
        Font authorFont = (baseFont != null) ? baseFont.deriveFont(Font.BOLD, 13f)
                : new Font("SansSerif", Font.BOLD, 13);
        Font timeFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 11f)
                : new Font("SansSerif", Font.PLAIN, 11);

        authorLabel = new JLabel(author != null ? author : "");
        authorLabel.setForeground(authorColor);
        authorLabel.setFont(authorFont);
        authorLabel.setAlignmentY(Component.CENTER_ALIGNMENT);

        timeLabel = new JLabel(formatTimestamp(emissionMillis));
        timeLabel.setForeground(timeColor);
        timeLabel.setFont(timeFont);
        timeLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        timeLabel.setBorder(new EmptyBorder(0, 8, 0, 0));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setOpaque(false);
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
        // Couleur du texte du message : text normal (#DCDDDE)
        Color contentColor = UIManager.getColor("TextArea.foreground");
        if (contentColor == null) contentColor = UIManager.getColor("Label.foreground");
        if (contentColor == null) contentColor = new Color(220, 221, 222);

        // Police du contenu : légèrement plus grande que le label
        Font baseFont = UIManager.getFont("TextArea.font");
        Font contentFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 13f)
                : new Font("SansSerif", Font.PLAIN, 13);

        contentArea = new JTextArea();
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setOpaque(false);
        contentArea.setForeground(contentColor);
        contentArea.setFont(contentFont);
        contentArea.setBorder(null);
        contentArea.setFocusable(false);
        contentArea.setText(content != null ? content : "");

        // Couleur du caret (invisible car non éditable, mais cohérent)
        Color caretColor = UIManager.getColor("TextArea.caretForeground");
        if (caretColor != null) contentArea.setCaretColor(caretColor);

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
            return DATE_FORMATTER.format(
                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()));
        } catch (Exception e) {
            if (this.logger != null)
                this.logger.debug("Erreur formatage date: " + e.getMessage());
            return String.valueOf(millis);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}