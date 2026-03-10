package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListMessageView extends JComponent implements View {

    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy")
            .withLocale(Locale.FRANCE);

    private final ViewContext viewContext;
    private final JPanel messagesPanel;
    private final JScrollPane scrollPane;
    private final JTextField searchField;
    private Component verticalGlue;
    private volatile boolean pendingScroll = false;

    /**
     * Dernière liste complète fournie par rebuildUI.
     */
    private List<MessageView> allMessages = new ArrayList<>();

    public ListMessageView(ViewContext viewContext) {
        this.viewContext = viewContext;

        messagesPanel = createMessagesPanel();
        scrollPane = createScrollPane(messagesPanel);
        searchField = createSearchField();

        initLayout();

        verticalGlue = Box.createVerticalGlue();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        messagesPanel.add(verticalGlue, gbc);
    }

    // Initialisation des sous-composants extraites pour single responsibility
    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        return panel;
    }

    private JScrollPane createScrollPane(JPanel messagesPanel) {
        JScrollPane sp = new JScrollPane(messagesPanel);
        sp.getViewport().setOpaque(false);
        sp.getViewport().setBackground(UIManager.getColor("Panel.background"));
        sp.setOpaque(false);
        sp.setBackground(UIManager.getColor("Panel.background"));
        sp.setBorder(null);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private JTextField createSearchField() {
        JTextField field = new JTextField();
        field.setOpaque(true);
        field.setBackground(new Color(47, 49, 54));
        field.setForeground(new Color(220, 221, 222));
        field.setCaretColor(new Color(220, 221, 222));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 34, 37), 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        // Placeholder via prompt text (Java 7+)
        field.putClientProperty("JTextField.placeholderText", "Rechercher un message\u2026");
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
        return field;
    }

    private void initLayout() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 4, 8));
        searchPanel.add(searchField, BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API publique
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Demande un scroll vers le bas à effectuer une seule fois après le prochain rebuildUI.
     */
    public void requestScrollToBottomOnce() {
        this.pendingScroll = true;
    }

    /**
     * Reconstruit entièrement le panneau à partir de la liste ordonnée.
     * Le filtre de recherche est réappliqué immédiatement.
     */
    public void rebuildUI(List<MessageView> ordered) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> rebuildUI(ordered));
            return;
        }
        allMessages = ordered == null ? new ArrayList<>() : new ArrayList<>(ordered);
        applyFilter();
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
        invokeLaterTriple(() -> {
            try {
                JScrollBar sb = scrollPane.getVerticalScrollBar();
                if (sb != null) sb.setValue(sb.getMaximum());
            } catch (Exception ignored) {
            }
        });
    }

    private void invokeLaterTriple(Runnable r) {
        SwingUtilities.invokeLater(() ->
                SwingUtilities.invokeLater(() ->
                        SwingUtilities.invokeLater(r)));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Filtre interne
    // ─────────────────────────────────────────────────────────────────────────

    private void applyFilter() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::applyFilter);
            return;
        }
        String q = getQuery();
        List<MessageView> toDisplay = buildFilteredList(q);
        renderMessages(toDisplay);
    }

    private String getQuery() {
        return searchField.getText().trim().toLowerCase();
    }

    private List<MessageView> buildFilteredList(String q) {
        if (q.isEmpty()) return allMessages;
        return allMessages.stream().filter(mv ->
                mv.getMessage() != null &&
                        mv.getMessage().getText() != null &&
                        mv.getMessage().getText().toLowerCase().contains(q)
        ).toList();
    }

    private void renderMessages(List<MessageView> ordered) {
        messagesPanel.removeAll();
        int row = 0;
        LocalDate prevDate = null;

        for (MessageView mv : ordered) {
            Message msg = mv.getMessage();
            LocalDate msgDate = null;
            if (msg != null) {
                msgDate = Instant.ofEpochMilli(msg.getEmissionDate())
                        .atZone(ZoneId.systemDefault()).toLocalDate();
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

        verticalGlue = Box.createVerticalGlue();
        messagesPanel.add(verticalGlue, new GridBagConstraints(
                0, row, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));

        messagesPanel.revalidate();
        messagesPanel.repaint();

        if (pendingScroll) {
            pendingScroll = false;
            scrollToBottom();
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Rendu interne
    // ─────────────────────────────────────────────────────────────────────────

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

