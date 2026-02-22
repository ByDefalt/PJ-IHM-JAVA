package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;

/**
 * Vue affichant une liste d'UserView empilés verticalement dans une zone défilante.
 */
public class ListUserView extends JComponent implements View {

    private final ViewContext viewContext;

    private final JPanel usersPanel;
    private final JScrollPane scrollPane;
    private Component glue;

    // Champ de recherche
    private JTextField searchField;

    public ListUserView(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setBackground(new Color(54, 57, 63));
        this.setOpaque(true);

        usersPanel = createUsersPanel();
        scrollPane = createScrollPane(usersPanel);

        // create and insert search field
        searchField = createSearchField();

        addScrollPaneToThis();

        glue = Box.createVerticalGlue();
        usersPanel.add(glue, glueConstraints(0));

        if (this.viewContext.logger() != null) this.viewContext.logger().debug("ListUserView initialisée");
    }

    // -------------------------------------------------------------------------
    // API publique — appelée par le contrôleur graphique
    // -------------------------------------------------------------------------

    /**
     * Ajoute une UserView à la fin de la liste (UI uniquement).
     */
    public void addUserUI(UserView userView, int row) {
        if (userView == null) return;

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> addUserUI(userView, row));
            return;
        }

        usersPanel.remove(glue);
        usersPanel.add(userView, userConstraints(row));

        glue = Box.createVerticalGlue();
        usersPanel.add(glue, glueConstraints(row + 1));

        usersPanel.revalidate();
        usersPanel.repaint();

        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = scrollPane.getVerticalScrollBar();
            if (bar != null) bar.setValue(bar.getMaximum());
        });

        if (this.viewContext.logger() != null) this.viewContext.logger().debug("UserView ajoutée (row=" + row + ")");
    }

    /**
     * Reconstruit entièrement le panel à partir de la liste ordonnée fournie par le contrôleur.
     */
    public void rebuildUI(List<UserView> ordered) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> rebuildUI(ordered));
            return;
        }
        usersPanel.removeAll();
        int row = 0;
        for (UserView uv : ordered) {
            usersPanel.add(uv, userConstraints(row++));
        }
        glue = Box.createVerticalGlue();
        usersPanel.add(glue, glueConstraints(row));
        usersPanel.revalidate();
        usersPanel.repaint();

        // réappliquer le filtre courant si nécessaire
        String q = searchField != null ? searchField.getText() : null;
        applyFilter(q);
    }

    /**
     * Met à jour l'affichage d'une UserView existante.
     */
    public void updateUserUI(UserView view, User user) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> updateUserUI(view, user));
            return;
        }
        view.updateUser(user);
        usersPanel.revalidate();
        usersPanel.repaint();
    }

    // -------------------------------------------------------------------------
    // Initialisation interne
    // -------------------------------------------------------------------------

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
        // search field at y=0
        GridBagConstraints gbcSearch = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 6, 0), 0, 0
        );
        this.add(searchField, gbcSearch);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0
        );
        this.add(scrollPane, gbc);
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

    // Création du champ de recherche et du DocumentListener
    private JTextField createSearchField() {
        JTextField tf = new JTextField();
        tf.setColumns(15);
        tf.setOpaque(true);
        tf.setBackground(new Color(47, 49, 54));
        tf.setForeground(new Color(220, 221, 222));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(32, 34, 37)),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        tf.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter(tf.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter(tf.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter(tf.getText());
            }
        });
        return tf;
    }

    // Applique le filtre côté client en masquant/montrant les UserView
    private void applyFilter(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> applyFilter(query));
            return;
        }
        for (Component c : usersPanel.getComponents()) {
            if (c instanceof UserView) {
                boolean match = matches((UserView) c, q);
                c.setVisible(match);
            }
        }
        usersPanel.revalidate();
        usersPanel.repaint();
    }

    private boolean matches(UserView uv, String q) {
        if (q == null || q.isEmpty()) return true;
        User u = uv.getUser();
        if (u == null) return false;
        String name = u.getName() != null ? u.getName().toLowerCase() : "";
        return name.contains(q);
    }
}

