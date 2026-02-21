package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ListMessageView extends JComponent implements View {

    // Formatteur pour l'entête de journée (ex: 12/12/2024)
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy")
            .withLocale(Locale.FRANCE);

    private final Logger logger;
    private final JPanel messagesPanel;
    private final List<MessageView> messages = new ArrayList<>();
    private final JScrollPane scrollPane;
    private Component verticalGlue;

    public ListMessageView(Logger logger) {
        this.logger = logger;

        messagesPanel = new JPanel(new GridBagLayout());
        messagesPanel.setOpaque(false);

        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(UIManager.getColor("Panel.background"));
        scrollPane.setOpaque(false);
        scrollPane.setBackground(UIManager.getColor("Panel.background"));
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        // Initial vertical glue
        verticalGlue = Box.createVerticalGlue();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx  = 0;
        gbc.gridy  = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill   = GridBagConstraints.VERTICAL;
        messagesPanel.add(verticalGlue, gbc);
    }

    /**
     * Ajoute un MessageView au panneau des messages.
     * Insère l'élément dans la liste en respectant l'ordre chronologique (ancien → récent).
     */
    public void addMessage(MessageView messageView) {
        if (messageView == null) {
            return;
        }

        messagesPanel.remove(verticalGlue);

        Message m        = messageView.getMessage();
        long emission    = (m != null) ? m.getEmissionDate() : Long.MAX_VALUE;
        int insertIndex  = 0;

        while (insertIndex < messages.size()) {
            MessageView existing      = messages.get(insertIndex);
            Message     em            = existing.getMessage();
            long        existingEmission = (em != null) ? em.getEmissionDate() : Long.MAX_VALUE;
            if (existingEmission > emission) break;
            insertIndex++;
        }

        messages.add(insertIndex, messageView);
        rebuildMessagesPanel();

        // Scroll to bottom uniquement si on a inséré le message le plus récent
        if (insertIndex == messages.size() - 1) {
            SwingUtilities.invokeLater(() -> {
                JScrollBar sb = scrollPane.getVerticalScrollBar();
                sb.setValue(sb.getMaximum());
            });
        }
    }

    public void addMessage(Message message) {
        boolean isPresent = messages.stream().anyMatch(mv -> mv.getMessage().equals(message));
        if (isPresent) {
            if (logger != null) logger.debug("Message déjà présent dans la vue, pas ajouté : " + message);
        } else {
            addMessage(new MessageView(logger, message));
            if (logger != null) logger.debug("Message ajouté à la vue : " + message);
        }
    }

    public void removeMessage(Message message) {
        Optional<MessageView> opt = messages.stream()
                .filter(mv -> mv.getMessage().equals(message))
                .findFirst();

        if (opt.isPresent()) {
            messages.remove(opt.get());
            removeMessageUI();
            if (logger != null) logger.debug("Message supprimé de la vue : " + message);
        } else {
            if (logger != null) logger.debug("Message non trouvé dans la vue, pas supprimé : " + message);
        }
    }

    public void updateMessage(Message message) {
        Optional<MessageView> opt = messages.stream()
                .filter(mv -> mv.getMessage().equals(message))
                .findFirst();

        if (opt.isPresent()) {
            MessageView mv = opt.get();
            messages.remove(mv);
            updateMessageUI(mv, message);

            long emission    = message.getEmissionDate();
            int insertIndex  = 0;
            while (insertIndex < messages.size()) {
                MessageView existing       = messages.get(insertIndex);
                Message     em             = existing.getMessage();
                long        existingEmission = (em != null) ? em.getEmissionDate() : Long.MAX_VALUE;
                if (existingEmission > emission) break;
                insertIndex++;
            }

            messages.add(insertIndex, mv);
            rebuildMessagesPanel();
            if (logger != null) logger.debug("Message mis à jour dans la vue : " + message);
        } else {
            if (logger != null) logger.debug("Message non trouvé pour mise à jour dans la vue : " + message);
        }
    }

    // ── Construction du panneau ──────────────────────────────────────────────

    /**
     * Reconstruit entièrement le panneau des messages à partir de la liste interne.
     */
    private void rebuildMessagesPanel() {
        messagesPanel.removeAll();

        int       row      = 0;
        LocalDate prevDate = null;

        for (MessageView mv : messages) {
            Message   msg     = mv.getMessage();
            LocalDate msgDate = null;

            if (msg != null) {
                msgDate = Instant.ofEpochMilli(msg.getEmissionDate())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            // Séparateur de journée si la date change
            if (msgDate != null && !msgDate.equals(prevDate)) {
                JComponent sep    = createDateSeparator(msgDate);
                GridBagConstraints gbcSep = new GridBagConstraints(
                        0, row++, 1, 1, 1.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(8, 6, 4, 6), 0, 0);
                messagesPanel.add(sep, gbcSep);
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
        GridBagConstraints gbcGlue = new GridBagConstraints(
                0, row, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0);
        messagesPanel.add(verticalGlue, gbcGlue);

        messagesPanel.revalidate();
        messagesPanel.repaint();
    }

    /**
     * Crée un séparateur centré de type :  ──────── 12/01/2025 ────────
     * Les couleurs et la police sont lues depuis l'UIManager (thème Discord).
     */
    private JComponent createDateSeparator(LocalDate date) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        // Couleur de la ligne : Separator.foreground défini dans DiscordTheme (#42454B)
        Color separatorColor = UIManager.getColor("Separator.foreground");
        // Texte en muted : Label.disabledForeground défini dans DiscordTheme (#72767D)
        Color textColor      = UIManager.getColor("Label.disabledForeground");
        // Police héritée du thème, taille 12 bold
        Font  baseFont       = UIManager.getFont("Label.font");
        Font  labelFont      = (baseFont != null)
                ? baseFont.deriveFont(Font.BOLD, 12f)
                : new Font("SansSerif", Font.BOLD, 12);

        JSeparator left  = new JSeparator();
        JSeparator right = new JSeparator();
        if (separatorColor != null) {
            left.setForeground(separatorColor);
            right.setForeground(separatorColor);
        }

        JLabel label = new JLabel(date.format(DATE_ONLY_FORMATTER));
        label.setForeground(textColor);
        label.setFont(labelFont);

        GridBagConstraints gbcLeft  = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 6, 0, 6), 0, 0);
        GridBagConstraints gbcLabel = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 6, 0, 6), 0, 0);
        GridBagConstraints gbcRight = new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 6, 0, 6), 0, 0);

        panel.add(left,  gbcLeft);
        panel.add(label, gbcLabel);
        panel.add(right, gbcRight);

        return panel;
    }

    // ── Mises à jour thread-safe ─────────────────────────────────────────────

    private void removeMessageUI() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::removeMessageUI);
            return;
        }
        rebuildMessagesPanel();
    }

    private void updateMessageUI(MessageView view, Message message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateMessageUI(view, message));
            return;
        }
        view.updateMessage(message);
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }
}