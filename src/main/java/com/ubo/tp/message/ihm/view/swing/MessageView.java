package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Composant représentant un seul message (bulle) — style simple inspiré de Discord.
 */
public class MessageView extends JComponent implements View {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH'h'mm")
            .withLocale(Locale.FRANCE);

    private static final Color BG_NORMAL    = new Color(54, 57, 63);
    private static final Color BG_HOVER     = new Color(72, 76, 84);
    private static final Color BORDER_HOVER = new Color(90, 95, 105);

    private final ViewContext viewContext;
    private final Message message;

    private JLabel authorLabel;
    private JTextArea contentArea;
    private JLabel timeLabel;
    private boolean hovered = false;

    public MessageView(ViewContext viewContext, Message message) {
        this.viewContext = viewContext;
        this.message = message;
        this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        this.setLayout(new GridBagLayout());
        // Non opaque : on peint tout manuellement pour éviter les coins parasites
        this.setOpaque(false);

        init(message);

        // Un seul listener : tous les enfants ont contains() = false
        // donc les événements souris remontent directement ici.
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                setCursor(Cursor.getDefaultCursor());
                repaint();
            }
        });

        if (this.viewContext.logger() != null)
            this.viewContext.logger().debug("MessageView initialisée pour '" + message.getSender() + "'");
    }

    private void init(Message message) {
        String authorName = (message.getSender() != null) ? message.getSender().getName() : "";
        createBubble(authorName, message.getText(), message.getEmissionDate());
    }

    private void createBubble(String author, String content, long emissionMillis) {
        JPanel bubble = new JPanel(new GridBagLayout()) {
            @Override
            public boolean contains(int x, int y) {
                return false;
            }
        };
        bubble.setOpaque(false);
        bubble.setBorder(new EmptyBorder(6, 8, 6, 8));

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
        Color authorColor = UIManager.getColor("Label.foreground");
        if (authorColor == null) authorColor = Color.WHITE;

        Color timeColor = UIManager.getColor("Label.disabledForeground");
        if (timeColor == null) timeColor = new Color(114, 118, 125);

        Font baseFont = UIManager.getFont("Label.font");
        Font authorFont = (baseFont != null) ? baseFont.deriveFont(Font.BOLD, 13f)
                : new Font("SansSerif", Font.BOLD, 13);
        Font timeFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 11f)
                : new Font("SansSerif", Font.PLAIN, 11);

        authorLabel = new JLabel(author != null ? author : "") {
            @Override public boolean contains(int x, int y) { return false; }
        };
        authorLabel.setForeground(authorColor);
        authorLabel.setFont(authorFont);
        authorLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        authorLabel.setOpaque(false);

        timeLabel = new JLabel(formatTimestamp(emissionMillis)) {
            @Override public boolean contains(int x, int y) { return false; }
        };
        timeLabel.setForeground(timeColor);
        timeLabel.setFont(timeFont);
        timeLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
        timeLabel.setBorder(new EmptyBorder(0, 8, 0, 0));
        timeLabel.setOpaque(false);

        JPanel headerPanel = new JPanel() {
            @Override public boolean contains(int x, int y) { return false; }
        };
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
        Color contentColor = UIManager.getColor("TextArea.foreground");
        if (contentColor == null) contentColor = UIManager.getColor("Label.foreground");
        if (contentColor == null) contentColor = new Color(220, 221, 222);

        Font baseFont = UIManager.getFont("TextArea.font");
        Font contentFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 13f)
                : new Font("SansSerif", Font.PLAIN, 13);

        contentArea = new JTextArea() {
            @Override public boolean contains(int x, int y) { return false; }
        };
        contentArea.setEditable(false);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setOpaque(false);
        contentArea.setForeground(contentColor);
        contentArea.setFont(contentFont);
        contentArea.setBorder(null);
        contentArea.setFocusable(false);
        contentArea.setText(content != null ? content : "");

        Color caretColor = UIManager.getColor("TextArea.caretForeground");
        if (caretColor != null) contentArea.setCaretColor(caretColor);

        GridBagConstraints gbcContent = new GridBagConstraints(
                0, 1, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0
        );
        bubble.add(contentArea, gbcContent);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arc = 12;
        int pad = 2;
        int w = getWidth()  - pad * 2;
        int h = getHeight() - pad * 2;

        if (hovered) {
            g2.setColor(BG_HOVER);
            g2.fillRoundRect(pad, pad, w, h, arc, arc);

            g2.setStroke(new BasicStroke(1.2f));
            g2.setColor(BORDER_HOVER);
            g2.drawRoundRect(pad, pad, w - 1, h - 1, arc, arc);
        } else {
            g2.setColor(BG_NORMAL);
            g2.fillRoundRect(pad, pad, w, h, arc, arc);
        }

        g2.dispose();
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

    private String formatTimestamp(long millis) {
        try {
            return DATE_FORMATTER.format(
                    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()));
        } catch (Exception e) {
            if (this.viewContext.logger() != null)
                this.viewContext.logger().debug("Erreur formatage date: " + e.getMessage());
            return String.valueOf(millis);
        }
    }
}