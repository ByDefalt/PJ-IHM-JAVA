package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.service.IListMessageView;
import com.ubo.tp.message.ihm.service.IMessageView;
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

public class ListMessageView extends JComponent implements IListMessageView {

    private final Logger logger;
    private final JPanel messagesPanel;
    private final List<IMessageView> messages = new ArrayList<>();
    private Component verticalGlue;
    private final JScrollPane scrollPane;

    // Formatteur pour l'entête de journée (ex: 12/12/2024)
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy")
            .withLocale(Locale.FRANCE);

    public ListMessageView(Logger logger) {
        this.logger = logger;

        messagesPanel = new JPanel(new GridBagLayout());
        messagesPanel.setOpaque(false);

        // Crée un JScrollPane interne au lieu d'hériter de JScrollPane
        scrollPane = new JScrollPane(messagesPanel);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Utiliser BorderLayout pour contenir le scrollPane
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);

        // Initial vertical glue
        verticalGlue = Box.createVerticalGlue();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        messagesPanel.add(verticalGlue, gbc);
    }

    /**
     * Ajoute un IMessageView au panneau des messages.
     * Insère l'élément dans la liste `messages` en respectant l'ordre chronologique
     * (ancien -> récent) défini par message.getEmissionDate().
     */
    public void addMessage(IMessageView messageView) {
        if (messageView == null) {
            return;
        }

        // remove existing glue so we can re-layout messages
        messagesPanel.remove(verticalGlue);

        // Trouver la position d'insertion triée (ordre ascendant sur emissionDate)
        Message m = messageView.getMessage();
        long emission = (m != null) ? m.getEmissionDate() : Long.MAX_VALUE;

        int insertIndex = 0;
        while (insertIndex < messages.size()) {
            IMessageView existing = messages.get(insertIndex);
            Message em = existing.getMessage();
            long existingEmission = (em != null) ? em.getEmissionDate() : Long.MAX_VALUE;
            if (existingEmission > emission) {
                break;
            }
            insertIndex++;
        }

        // Insérer dans la liste interne et reconstruire l'UI
        messages.add(insertIndex, messageView);
        rebuildMessagesPanel();

        // Scroll to bottom only if we inserted at the end (le plus récent)
        if (insertIndex == messages.size() - 1) {
            SwingUtilities.invokeLater(() -> {
                JScrollBar sb = scrollPane.getVerticalScrollBar();
                sb.setValue(sb.getMaximum());
            });
        }
    }

    @Override
    public void addMessage(Message message) {
        boolean isPresent = messages.stream().anyMatch(mv -> mv.getMessage().equals(message));
        if (isPresent) {
            if (logger != null) logger.debug("Message déjà présent dans la vue, pas ajouté : " + message);
        } else {
            this.addMessage(new MessageView(logger, message));
            if (logger != null) logger.debug("Message ajouté à la vue : " + message);
        }
    }

    @Override
    public void removeMessage(Message message) {
        Optional<IMessageView> opt = messages.stream().filter(mv -> mv.getMessage().equals(message)).findFirst();
        if (opt.isPresent()) {
            IMessageView found = opt.get();
            // remove from internal list
            this.messages.remove(found);
            // update UI
            removeMessageUI();
            if (logger != null) logger.debug("Message supprimé de la vue : " + message);
        } else {
            if (logger != null) logger.debug("Message non trouvé dans la vue, pas supprimé : " + message);
        }
    }

    @Override
    public void updateMessage(Message message) {
        Optional<IMessageView> opt = messages.stream().filter(mv -> mv.getMessage().equals(message)).findFirst();
        if (opt.isPresent()) {
            IMessageView iMessageView = opt.get();
            // remove from internal list so we can reinsert sorted if date changed
            this.messages.remove(iMessageView);

            // update model inside the view and refresh UI
            updateMessageUI(iMessageView, message);

            // Reposition according to the (potentially new) emission date
            long emission = message.getEmissionDate();
            int insertIndex = 0;
            while (insertIndex < messages.size()) {
                IMessageView existing = messages.get(insertIndex);
                Message em = existing.getMessage();
                long existingEmission = (em != null) ? em.getEmissionDate() : Long.MAX_VALUE;
                if (existingEmission > emission) {
                    break;
                }
                insertIndex++;
            }

            messages.add(insertIndex, iMessageView);
            rebuildMessagesPanel();

            if (logger != null) logger.debug("Message mis à jour dans la vue : " + message);
        } else {
            if (logger != null) logger.debug("Message non trouvé pour mise à jour dans la vue : " + message);
        }
    }

    /**
     * Reconstruit entièrement le panneau des messages à partir de la liste interne.
     */
    private void rebuildMessagesPanel() {
        messagesPanel.removeAll();
        // re-add all message components in order
        int row = 0;
        LocalDate prevDate = null;
        for (IMessageView mv : messages) {
            Message msg = mv.getMessage();
            LocalDate msgDate = null;
            if (msg != null) {
                msgDate = Instant.ofEpochMilli(msg.getEmissionDate()).atZone(ZoneId.systemDefault()).toLocalDate();
            }

            // Si la date du message diffère de la précédente (ou si c'est le premier), on insère un séparateur
            if (msgDate != null && !msgDate.equals(prevDate)) {
                JComponent sep = createDateSeparator(msgDate);
                GridBagConstraints gbcSep = new GridBagConstraints(
                        0, row++, 1, 1, 1.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(8, 6, 4, 6), 0, 0
                );
                messagesPanel.add(sep, gbcSep);
                prevDate = msgDate;
            }

            GridBagConstraints gbc = new GridBagConstraints(
                    0, row++, 1, 1, 1.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                    new Insets(6, 6, 6, 6), 0, 0
            );
            gbc.fill = GridBagConstraints.HORIZONTAL;
            messagesPanel.add((Component) mv, gbc);
        }
        // add glue at the end
        verticalGlue = Box.createVerticalGlue();
        GridBagConstraints gbcGlue = new GridBagConstraints(
                0, row, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );
        messagesPanel.add(verticalGlue, gbcGlue);
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }

    /**
     * Crée un composant séparateur centré avec une ligne de chaque côté et la date au centre.
     */
    private JComponent createDateSeparator(LocalDate date) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        JSeparator left = new JSeparator();
        JSeparator right = new JSeparator();

        JLabel label = new JLabel(date.format(DATE_ONLY_FORMATTER));
        label.setForeground(new Color(150, 150, 150));
        label.setFont(new Font("Arial", Font.BOLD, 12));

        GridBagConstraints gbcLeft = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 6, 0, 6), 0, 0);
        GridBagConstraints gbcLabel = new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 6, 0, 6), 0, 0);
        GridBagConstraints gbcRight = new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 6, 0, 6), 0, 0);

        panel.add(left, gbcLeft);
        panel.add(label, gbcLabel);
        panel.add(right, gbcRight);

        return panel;
    }

    private void removeMessageUI() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::removeMessageUI);
            return;
        }
        rebuildMessagesPanel();
    }

    private void updateMessageUI(IMessageView view, Message message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateMessageUI(view, message));
            return;
        }
        view.updateMessage(message);
        messagesPanel.revalidate();
        messagesPanel.repaint();
    }
}