package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.controller.service.IChatMainController;
import com.ubo.tp.message.ihm.service.*;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Vue principale type Discord : sidebar fixe + panel messages à droite.
 */
public class ChatMainView extends JComponent implements IChatMainView {

    private final Logger logger;

    private final IListCanalView listCanalView;
    private final IListUserView listUserView;
    private final IListMessageView listMessageView;
    private final IInputMessageView inputMessageView;

    private final IChatMainController chatMainController;

    public ChatMainView(
            Logger logger,
            IListCanalView listCanalView,
            IListUserView listUserView,
            IListMessageView listMessageView,
            IInputMessageView inputMessageView,
            IChatMainController chatMainController
    ) {
        this.logger = logger;
        this.listCanalView = listCanalView;
        this.listUserView = listUserView;
        this.listMessageView = listMessageView;
        this.inputMessageView = inputMessageView;
        this.chatMainController = chatMainController;

        SwingUtilities.invokeLater(() -> {
            initView();
            if (this.logger != null) {
                this.logger.debug("ChatMainView initialisée (Discord style)");
            }
        });
    }

    private void initView() {
        setLayout(new BorderLayout());
        setOpaque(true);
        setBackground(new Color(54, 57, 63));

        // Sidebar et panneau droit
        JPanel sidebar = createSidebar();
        JPanel rightPanel = createRightPanel();

        // Utiliser JSplitPane pour garantir une largeur fixe de la sidebar
        int sidebarWidth = 300;
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sidebar, rightPanel);
        splitPane.setDividerLocation(sidebarWidth);
        splitPane.setEnabled(false); // empêcher le déplacement
        splitPane.setDividerSize(1);
        splitPane.setBorder(null);
        add(splitPane, BorderLayout.CENTER);
    }

    /**
     * Sidebar fixe avec boutons + CardLayout pour Canaux / Utilisateurs
     */
    private JPanel createSidebar() {
        final int sidebarWidth = 300;
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(sidebarWidth, d.height);
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension d = super.getMinimumSize();
                return new Dimension(220, d.height);
            }

            @Override
            public Dimension getMaximumSize() {
                Dimension d = super.getMaximumSize();
                return new Dimension(sidebarWidth, Integer.MAX_VALUE);
            }
        };
        sidebar.setOpaque(true);
        sidebar.setBackground(new Color(47, 49, 54));

        // Boutons haut
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        buttonPanel.setOpaque(false);
        JButton btnCanaux = createSidebarButton("Canaux");
        JButton btnUsers = createSidebarButton("Utilisateurs");
        buttonPanel.add(btnCanaux);
        buttonPanel.add(btnUsers);
        sidebar.add(buttonPanel, BorderLayout.NORTH);

        // CardLayout pour les vues
        JPanel cardsPanel = new JPanel(new CardLayout());
        cardsPanel.setOpaque(false);
        // Assurer une taille stable pour le container de cartes
        Dimension cardsPreferred = new Dimension(300, 0);
        cardsPanel.setPreferredSize(cardsPreferred);
        cardsPanel.setMinimumSize(new Dimension(220, 0));
        // Éviter que le container de cartes change de largeur
        cardsPanel.setMaximumSize(new Dimension(cardsPreferred.width, Integer.MAX_VALUE));

        // Wrapper unique + JScrollPane pour chaque vue
        JPanel canalWrapper = new JPanel(new BorderLayout());
        canalWrapper.setOpaque(false);
        // listCanalView fournit déjà son propre JScrollPane si nécessaire — éviter d'imbriquer
        canalWrapper.add((Component) listCanalView, BorderLayout.CENTER);
        // Fixer une taille cohérente pour éviter les variations dues au contenu
        canalWrapper.setPreferredSize(cardsPreferred);
        canalWrapper.setMinimumSize(new Dimension(220, 0));
        canalWrapper.setMaximumSize(new Dimension(cardsPreferred.width, Integer.MAX_VALUE));

        JPanel userWrapper = new JPanel(new BorderLayout());
        userWrapper.setOpaque(false);
        // listUserView contient un JScrollPane interne — ajouter directement pour garder la taille stable
        userWrapper.add((Component) listUserView, BorderLayout.CENTER);
        userWrapper.setPreferredSize(cardsPreferred);
        userWrapper.setMinimumSize(new Dimension(220, 0));
        userWrapper.setMaximumSize(new Dimension(cardsPreferred.width, Integer.MAX_VALUE));

        cardsPanel.add(canalWrapper, "Canaux");
        cardsPanel.add(userWrapper, "Utilisateurs");

        btnCanaux.addActionListener(e -> {
            ((CardLayout) cardsPanel.getLayout()).show(cardsPanel, "Canaux");
            cardsPanel.revalidate();
            cardsPanel.repaint();
            sidebar.revalidate();
            sidebar.repaint();
        });
        btnUsers.addActionListener(e -> {
            ((CardLayout) cardsPanel.getLayout()).show(cardsPanel, "Utilisateurs");
            cardsPanel.revalidate();
            cardsPanel.repaint();
            sidebar.revalidate();
            sidebar.repaint();
        });

        sidebar.add(cardsPanel, BorderLayout.CENTER);
        return sidebar;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(new Color(64, 68, 75));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Panel droit : header + messages scrollables + input
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(true);
        rightPanel.setBackground(new Color(54, 57, 63));

        // Header
        JLabel header = new JLabel("# général");
        header.setFont(new Font("Arial", Font.BOLD, 16));
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        addToGrid(rightPanel, header, 0, 0, 1.0, 0.0, GridBagConstraints.HORIZONTAL);

        // Messages scrollables
        JScrollPane scrollPane = new JScrollPane((Component) listMessageView);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(54, 57, 63));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        addToGrid(rightPanel, scrollPane, 0, 1, 1.0, 1.0, GridBagConstraints.BOTH);

        // Input en bas
        addToGrid(rightPanel, (Component) inputMessageView, 0, 2, 1.0, 0.0, GridBagConstraints.HORIZONTAL);

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

    public IListCanalView getListCanalView() {
        return listCanalView;
    }

    public IListUserView getListUserView() {
        return listUserView;
    }

    public IListMessageView getListMessageView() {
        return listMessageView;
    }

    public IInputMessageView getInputMessageView() {
        return inputMessageView;
    }

    @Override
    public IChatMainController getController() {
        return chatMainController;
    }
}
