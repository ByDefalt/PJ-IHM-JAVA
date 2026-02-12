package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.service.IListMessageView;
import com.ubo.tp.message.ihm.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Vue affichant une liste de MessageView empilés verticalement dans une zone défilante.
 * Style sombre conservé via les couleurs de fond du conteneur.
 */
public class ListMessageView extends JComponent implements IListMessageView {

    private final Logger logger;

    private final JPanel messagesPanel;
    private final JScrollPane scrollPane;
    private Runnable onRefreshRequested;


    private final List<MessageView> messages = new ArrayList<>();

    public ListMessageView(Logger logger) {
        this.logger = logger;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(new Color(54, 57, 63));
        this.setOpaque(true);

        // création des éléments
        messagesPanel = createMessagesPanel();
        scrollPane = createScrollPane(messagesPanel);

        addScrollPaneToThis();



        if (this.logger != null) this.logger.debug("ListMessageView initialisée");
    }

    private JPanel createMessagesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(54, 57, 63));
        panel.setOpaque(true);
        return panel;
    }

    private JScrollPane createScrollPane(JPanel content) {
        JScrollPane sp = new JScrollPane(content);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(new Color(54, 57, 63));
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    private void addScrollPaneToThis() {
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );
        this.add(scrollPane, gbc);
    }


    @Override
    public void setOnRefreshRequested(Runnable onRefreshRequested) {
        this.onRefreshRequested = onRefreshRequested;
    }


    /**
     * Remplace la liste entière de messages par `newMessages`.
     */
    public void setMessages(List<MessageView> newMessages) {
        clearMessages();
        if (newMessages == null) return;
        // TODO()
        revalidate();
        repaint();
    }

    /**
     * Ajoute un message à la fin de la liste et fait défiler vers le bas.
     */
    public void addMessage(MessageView messageView) {
        if (messageView == null) return;

        int row = messages.size();
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(6, 6, 6, 6), 0, 0
        );
        // Make the message component stretch horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL;

        messagesPanel.add(messageView, gbc);
        messages.add(messageView);

        // add a glue/filler after last element so messages stay at top when few
        GridBagConstraints gbcGlue = new GridBagConstraints(
                0, messages.size(), 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );

        // remove old glue if present
        // we assume glue was last component if present; safer to remove and re-add
        for (Component c : messagesPanel.getComponents()) {
            if (c instanceof Box.Filler) {
                messagesPanel.remove(c);
            }
        }
        messagesPanel.add(Box.createVerticalGlue(), gbcGlue);

        revalidate();
        repaint();

        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });

        if (this.logger != null) this.logger.debug("Message ajouté à ListMessageView: " + messageView.getAuthor());
    }

    /**
     * Vide la liste de messages.
     */
    public void clearMessages() {
        messagesPanel.removeAll();
        messages.clear();
        // ensure panel retains its background by re-adding an invisible glue so top alignment works
        GridBagConstraints gbcGlue = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );
        messagesPanel.add(Box.createVerticalGlue(), gbcGlue);
        revalidate();
        repaint();
    }

    public List<MessageView> getMessages() { return new ArrayList<>(messages); }

}
