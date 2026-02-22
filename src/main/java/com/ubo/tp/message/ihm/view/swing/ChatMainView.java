package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;

/**
 * Vue principale type Discord : sidebar fixe + panel messages à droite.
 */
public class ChatMainView extends JComponent implements View {

    private final ViewContext viewContext;

    private final ListCanalView listCanalView;
    private final ListUserView listUserView;
    private final ListMessageView listMessageView;
    private final InputMessageView inputMessageView;

    public ChatMainView(
            ViewContext viewContext,
            ListCanalView listCanalView,
            ListUserView listUserView,
            ListMessageView listMessageView,
            InputMessageView inputMessageView
    ) {
        this.viewContext = viewContext;
        this.listCanalView = listCanalView;
        this.listUserView = listUserView;
        this.listMessageView = listMessageView;
        this.inputMessageView = inputMessageView;

        SwingUtilities.invokeLater(() -> {
            initView();
            if (this.viewContext.logger() != null) {
                this.viewContext.logger().debug("ChatMainView initialisée (Discord style)");
            }
        });
    }

    private void initView() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(54, 57, 63));

        JPanel sidebar = createSidebar();
        JPanel rightPanel = createRightPanel();

        int sidebarWidth = 300;
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, rightPanel);
        splitPane.setDividerLocation(sidebarWidth);
        splitPane.setEnabled(false);
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Sidebar fixe avec JTabbedPane pour Canaux / Utilisateurs.
     */
    private JPanel createSidebar() {
        final int sidebarWidth = 300;
        JPanel sidebar = createJPanel(sidebarWidth);

        // Utilisation d'un JTabbedPane pour remplacer les deux boutons + CardLayout
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setOpaque(true);
        tabbedPane.setBackground(new Color(47, 49, 54));
        tabbedPane.setForeground(Color.WHITE);

        Dimension cardsPreferred = new Dimension(sidebarWidth, 0);

        JPanel canalWrapper = new JPanel(new BorderLayout());
        canalWrapper.setOpaque(false);
        canalWrapper.add(listCanalView, BorderLayout.CENTER);
        canalWrapper.setPreferredSize(cardsPreferred);
        canalWrapper.setMinimumSize(new Dimension(220, 0));
        canalWrapper.setMaximumSize(new Dimension(sidebarWidth, Integer.MAX_VALUE));

        JPanel userWrapper = new JPanel(new BorderLayout());
        userWrapper.setOpaque(false);
        userWrapper.add(listUserView, BorderLayout.CENTER);
        userWrapper.setPreferredSize(cardsPreferred);
        userWrapper.setMinimumSize(new Dimension(220, 0));
        userWrapper.setMaximumSize(new Dimension(sidebarWidth, Integer.MAX_VALUE));

        tabbedPane.addTab("Canaux", canalWrapper);
        tabbedPane.addTab("Utilisateurs", userWrapper);

        // Stylisation minimale des onglets
        UIManager.put("TabbedPane.contentAreaColor", new Color(47, 49, 54));

        sidebar.add(tabbedPane, BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel createJPanel(int sidebarWidth) {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(sidebarWidth, super.getPreferredSize().height);
            }

            @Override
            public Dimension getMinimumSize() {
                return new Dimension(220, super.getMinimumSize().height);
            }

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(sidebarWidth, Integer.MAX_VALUE);
            }
        };
        sidebar.setOpaque(true);
        sidebar.setBackground(new Color(47, 49, 54));
        return sidebar;
    }

    /**
     * Panel droit : messages (scroll interne dans ListMessageView) + input.
     * On N'emballe PAS listMessageView dans un JScrollPane supplémentaire,
     * car elle possède déjà son propre JScrollPane interne.
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(true);
        rightPanel.setBackground(new Color(54, 57, 63));

        // listMessageView contient déjà son JScrollPane — ajout direct
        addToGrid(rightPanel, listMessageView, 0, 0, 1.0, 1.0, GridBagConstraints.BOTH);

        // Barre de saisie fixe en bas
        addToGrid(rightPanel, inputMessageView, 0, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL);

        return rightPanel;
    }

    private void addToGrid(Container parent, Component comp,
                           int x, int y,
                           double weightX, double weightY,
                           int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = weightX;
        gbc.weighty = weightY;
        gbc.fill = fill;
        gbc.insets = new Insets(6, 6, 6, 6);
        parent.add(comp, gbc);
    }

    // --- Getters ---

    public ListCanalView getListCanalView() {
        return listCanalView;
    }

    public ListUserView getListUserView() {
        return listUserView;
    }

    public ListMessageView getListMessageView() {
        return listMessageView;
    }

    public InputMessageView getInputMessageView() {
        return inputMessageView;
    }
}

