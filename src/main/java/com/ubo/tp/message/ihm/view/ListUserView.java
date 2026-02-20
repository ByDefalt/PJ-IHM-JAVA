package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.ihm.service.IListUserView;
import com.ubo.tp.message.ihm.service.IUserView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Vue affichant une liste d'UserView empilés verticalement dans une zone défilante.
 * Style cohérent avec `MessageView` / `ListMessageView` : fond sombre et GridBagLayout.
 */
public class ListUserView extends JComponent implements IListUserView {

    private final Logger logger;

    private final JPanel usersPanel;
    private final JScrollPane scrollPane;
    private final List<IUserView> userViews = new ArrayList<>();

    public ListUserView(Logger logger, List<IUserView> initialUsers) {
        this.logger = logger;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(new Color(54, 57, 63));
        this.setOpaque(true);

        usersPanel = createUsersPanel();
        scrollPane = createScrollPane(usersPanel);
        addScrollPaneToThis();

        if (initialUsers != null && !initialUsers.isEmpty()) {
            setUsers(initialUsers);
        } else {
            // ensure a glue so content stays at top
            usersPanel.add(Box.createVerticalGlue(), new GridBagConstraints(
                    0, 0, 1, 1, 1.0, 1.0,
                    GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0
            ));
        }

        if (this.logger != null) this.logger.debug("ListUserView initialisée");
    }

    public ListUserView(Logger logger) {
        this(logger, null);
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

    /**
     * Ajoute un UserView à la fin de la liste et fait défiler vers le bas.
     */
    public void addUser(IUserView userView) {
        if (userView == null) return;

        int row = userViews.size();
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(4, 4, 4, 4), 0, 0
        );
        gbc.fill = GridBagConstraints.HORIZONTAL;

        usersPanel.add((Component) userView, gbc);
        userViews.add(userView);

        // remove old glue and re-add it after the last element
        for (Component c : usersPanel.getComponents()) {
            if (c instanceof Box.Filler) usersPanel.remove(c);
        }

        GridBagConstraints gbcGlue = new GridBagConstraints(
                0, userViews.size(), 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );
        usersPanel.add(Box.createVerticalGlue(), gbcGlue);

        revalidate();
        repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });

        if (this.logger != null) this.logger.debug("User ajouté à ListUserView: " + userView.getUserName());
    }

    public void clearUsers() {
        usersPanel.removeAll();
        userViews.clear();
        // keep a glue so top alignment works when empty
        GridBagConstraints gbcGlue = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.NORTH, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );
        usersPanel.add(Box.createVerticalGlue(), gbcGlue);
        revalidate();
        repaint();
    }

    public List<IUserView> getUsers() {
        return new ArrayList<>(userViews);
    }

    /**
     * Remplace la liste entière d'utilisateurs par `newUsers`.
     */
    public void setUsers(List<IUserView> newUsers) {
        clearUsers();
        if (newUsers == null) return;
        for (IUserView uv : newUsers) addUser(uv);
        revalidate();
        repaint();
    }

}
