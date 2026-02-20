package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Vue affichant une liste d'UserView empilés verticalement dans une zone défilante.
 */
public class ListUserView extends JComponent implements View {

    private final Logger logger;

    private final JPanel usersPanel;
    private final JScrollPane scrollPane;
    private final List<UserView> userViews = new ArrayList<>();

    private Component glue;

    public ListUserView(Logger logger) {
        this.logger = logger;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(new Color(54, 57, 63));
        this.setOpaque(true);

        usersPanel = createUsersPanel();
        scrollPane = createScrollPane(usersPanel);
        addScrollPaneToThis();

        // Glue initial
        glue = Box.createVerticalGlue();
        usersPanel.add(glue, glueConstraints(0));


        if (this.logger != null) this.logger.debug("ListUserView initialisée");
    }

    private JPanel createUsersPanel() {
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

    public void addUser(UserView userView) {
        if (userView == null) return;

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> addUser(userView));
            return;
        }

        int row = userViews.size();
        userViews.add(userView);

        // Retirer uniquement le glue
        usersPanel.remove(glue);

        // Ajouter l'utilisateur au bon gridy
        usersPanel.add(userView, userConstraints(row));

        // Remettre le glue après le dernier élément
        glue = Box.createVerticalGlue();
        usersPanel.add(glue, glueConstraints(userViews.size()));

        usersPanel.revalidate();
        usersPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            if (bar != null) bar.setValue(bar.getMaximum());
        });

        if (this.logger != null) this.logger.debug("User ajouté: " + userView.getUser().getName());
    }

    private GridBagConstraints userConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 4, 4);
        return gbc;
    }

    private GridBagConstraints glueConstraints(int row) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        return gbc;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void addUser(User user) {
        boolean isPresent = userViews.stream().anyMatch(uv -> uv.getUser().equals(user));
        if (isPresent) {
            if (logger != null) logger.debug("User déjà présent, pas ajouté: " + user.getName());
        } else {
            addUser(new UserView(logger, user));
            if (logger != null) logger.debug("User ajouté: " + user.getName());
        }
    }

    public void removeUser(User user) {
        Optional<UserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        if (opt.isPresent()) {
            UserView found = opt.get();
            this.userViews.remove(found);
            // update UI
            removeUserUI();
            if (logger != null) logger.debug("User supprimé de la vue: " + user.getName());
        } else {
            if (logger != null) logger.debug("User non trouvé dans la vue, pas supprimé: " + user.getName());
        }
    }

    public void updateUser(User user) {
        Optional<UserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        if (opt.isPresent()) {
            UserView iUserView = opt.get();
            // update model inside the view and refresh UI
            updateUserUI(iUserView, user);
            if (logger != null) logger.debug("User mis à jour dans la vue: " + user.getName());
        } else {
            if (logger != null) logger.debug("User non trouvé pour mise à jour dans la vue: " + user.getName());
        }
    }

    // --- Helpers UI ---

    private void rebuildUsersPanel() {
        usersPanel.removeAll();
        int row = 0;
        for (UserView uv : userViews) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = row++;
            gbc.gridwidth = 1;
            gbc.gridheight = 1;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(4, 4, 4, 4);
            usersPanel.add(uv, gbc);
        }
        // add glue
        glue = Box.createVerticalGlue();
        usersPanel.add(glue, glueConstraints(row));
        usersPanel.revalidate();
        usersPanel.repaint();
    }

    private void removeUserUI() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::removeUserUI);
            return;
        }
        rebuildUsersPanel();
    }

    private void updateUserUI(UserView view, User user) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateUserUI(view, user));
            return;
        }
        view.updateUser(user);
        usersPanel.revalidate();
        usersPanel.repaint();
    }
}