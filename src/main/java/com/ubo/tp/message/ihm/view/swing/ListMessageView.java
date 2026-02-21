package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ListMessageView extends JComponent implements View {

    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy")
            .withLocale(Locale.FRANCE);

    private final ViewContext viewContext;
    private final JPanel messagesPanel;
    private final JScrollPane scrollPane;
    private Component verticalGlue;
    private volatile boolean pendingScroll = false;

    public ListMessageView(ViewContext viewContext) {
        this.viewContext = viewContext;

        messagesPanel = new JPanel(new GridBagLayout());
        messagesPanel.setOpaque(false);

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(UIManager.getColor("Panel.background"));
        scrollPane.setOpaque(false);
        scrollPane.setBackground(UIManager.getColor("Panel.background"));
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        verticalGlue = Box.createVerticalGlue();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        messagesPanel.add(verticalGlue, gbc);
    }

    // -------------------------------------------------------------------------
    // API publique
    // -------------------------------------------------------------------------

    /**
     * Demande un scroll vers le bas à effectuer une seule fois après le prochain rebuildUI.
     */
    public void requestScrollToBottomOnce() {
        this.pendingScroll = true;
    }

    /**
     * Reconstruit entièrement le panneau à partir de la liste ordonnée.
     */
    public void rebuildUI(List<MessageView> ordered) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> rebuildUI(ordered));
            return;
        }

        messagesPanel.removeAll();
        int row = 0;
        LocalDate prevDate = null;

        for (MessageView mv : ordered) {
            Message msg = mv.getMessage();
            LocalDate msgDate = null;

            if (msg != null) {
                msgDate = Instant.ofEpochMilli(msg.getEmissionDate())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            if (msgDate != null && !msgDate.equals(prevDate)) {
                JComponent sep = createDateSeparator(msgDate);
                messagesPanel.add(sep, new GridBagConstraints(
                        0, row++, 1, 1, 1.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(8, 6, 4, 6), 0, 0));
                prevDate = msgDate;
            }

            GridBagConstraints gbc = new GridBagConstraints(
                    0, row++, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(6, 6, 6, 6), 0, 0);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            messagesPanel.add(mv, gbc);
        }

        // Glue final pour pousser les messages vers le haut
        verticalGlue = Box.createVerticalGlue();
        messagesPanel.add(verticalGlue, new GridBagConstraints(
                0, row, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        messagesPanel.revalidate();
        messagesPanel.repaint();

        // Scroll différé : on attend que le layout soit vraiment terminé
        if (pendingScroll) {
            pendingScroll = false;
            scrollToBottom();
        }
    }

    /**
     * Met à jour l'affichage d'un MessageView existant.
     */
    public void updateMessageUI(MessageView view, Message message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateMessageUI(view, message));
            return;
        }
        view.updateMessage(message);
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }

    /**
     * Fait défiler jusqu'en bas.
     * Utilise trois invokeLater imbriqués pour s'assurer que le layout complet
     * (revalidate → layout → paint) est terminé avant de lire getMaximum().
     */
    public void scrollToBottom() {
        SwingUtilities.invokeLater(() ->
                SwingUtilities.invokeLater(() ->
                        SwingUtilities.invokeLater(() -> {
                            try {
                                JScrollBar sb = scrollPane.getVerticalScrollBar();
                                if (sb != null) sb.setValue(sb.getMaximum());
                            } catch (Exception ignored) {
                            }
                        })
                )
        );
    }

    // -------------------------------------------------------------------------
    // Rendu interne
    // -------------------------------------------------------------------------

    private JComponent createDateSeparator(LocalDate date) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        Color separatorColor = UIManager.getColor("Separator.foreground");
        Color textColor = UIManager.getColor("Label.disabledForeground");
        Font baseFont = UIManager.getFont("Label.font");
        Font labelFont = (baseFont != null)
                ? baseFont.deriveFont(Font.BOLD, 12f)
                : new Font("SansSerif", Font.BOLD, 12);

        JSeparator left = new JSeparator();
        JSeparator right = new JSeparator();
        if (separatorColor != null) {
            left.setForeground(separatorColor);
            right.setForeground(separatorColor);
        }

        JLabel label = new JLabel(date.format(DATE_ONLY_FORMATTER));
        label.setForeground(textColor);
        label.setFont(labelFont);

        panel.add(left, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 6, 0, 6), 0, 0));
        panel.add(label, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 6, 0, 6), 0, 0));
        panel.add(right, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 6, 0, 6), 0, 0));

        return panel;
    }
}