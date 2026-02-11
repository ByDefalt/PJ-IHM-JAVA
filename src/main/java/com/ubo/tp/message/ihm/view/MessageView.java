package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.ihm.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Composant représentant un seul message (bulle) — style simple inspiré de Discord.
 * Utilise GridBagLayout et sépare la construction en méthodes.
 * La vue est autonome et n'a pas de référence au controller.
 */
public class MessageView extends JComponent implements View {

    private final Logger logger;

    private JLabel authorLabel;
    private JTextArea contentArea;
    private JLabel timeLabel;

    public MessageView(Logger logger, String author, String content, String time) {
        this.logger = logger;
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        this.setLayout(new GridBagLayout());
        this.setOpaque(false);

        init(author, content, time);

        if (this.logger != null) this.logger.debug("MessageView initialisée pour '" + author + "'");
    }

    public MessageView(String author, String content) {
        this(null, author, content, null);
    }

    private void init(String author, String content, String time) {
        createBubble(author, content, time);
    }

    private void createBubble(String author, String content, String time) {
        JPanel bubble = new JPanel(new GridBagLayout());
        bubble.setBackground(new Color(64, 68, 75));
        bubble.setBorder(new EmptyBorder(6, 8, 6, 8));
        bubble.setOpaque(true);

        GridBagConstraints gbcBubble = new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(2, 0, 2, 2), 0, 0
        );
        this.add(bubble, gbcBubble);

        // Header and content inside bubble
        createHeader(bubble, author, time);
        createContent(bubble, content);
    }

    private void createHeader(JPanel bubble, String author, String time) {
        authorLabel = new JLabel(author != null ? author : "");
        authorLabel.setForeground(Color.WHITE);
        authorLabel.setFont(new Font("Arial", Font.BOLD, 12));

        timeLabel = new JLabel(time != null ? time : "");
        timeLabel.setForeground(new Color(170, 170, 170));
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));

        GridBagConstraints gbcAuthor = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 4, 4), 0, 0
        );
        GridBagConstraints gbcTime = new GridBagConstraints(
                1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                new Insets(0, 0, 4, 0), 0, 0
        );

        bubble.add(authorLabel, gbcAuthor);
        bubble.add(timeLabel, gbcTime);
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

    public void setMessage(String author, String content, String time) {
        if (author != null) authorLabel.setText(author);
        if (content != null) contentArea.setText(content);
        if (time != null) timeLabel.setText(time);
        revalidate();
        repaint();
    }

    public String getAuthor() { return authorLabel.getText(); }
    public String getContent() { return contentArea.getText(); }
    public String getTime() { return timeLabel.getText(); }


}
