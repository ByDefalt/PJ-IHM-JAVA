package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.contexte.ViewContext;

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
     * Sidebar fixe avec boutons + CardLayout pour Canaux / Utilisateurs.
     */
    private JPanel createSidebar() {
        final int sidebarWidth = 300;
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override public Dimension getPreferredSize() { return new Dimension(sidebarWidth, super.getPreferredSize().height); }
            @Override public Dimension getMinimumSize()   { return new Dimension(220,           super.getMinimumSize().height);   }
            @Override public Dimension getMaximumSize()   { return new Dimension(sidebarWidth, Integer.MAX_VALUE);               }
        };
        sidebar.setOpaque(true);
        sidebar.setBackground(new Color(47, 49, 54));

        // Boutons haut
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 4));
        buttonPanel.setOpaque(false);
        JButton btnCanaux = createSidebarButton("Canaux");
        JButton btnUsers  = createSidebarButton("Utilisateurs");
        buttonPanel.add(btnCanaux);
        buttonPanel.add(btnUsers);
        sidebar.add(buttonPanel, BorderLayout.NORTH);

        // CardLayout pour les vues
        Dimension cardsPreferred = new Dimension(sidebarWidth, 0);
        JPanel cardsPanel = new JPanel(new CardLayout());
        cardsPanel.setOpaque(false);
        cardsPanel.setPreferredSize(cardsPreferred);
        cardsPanel.setMinimumSize(new Dimension(220, 0));
        cardsPanel.setMaximumSize(new Dimension(sidebarWidth, Integer.MAX_VALUE));

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
     * Panel droit : messages (scroll interne dans ListMessageView) + input.
     * On N'emballe PAS listMessageView dans un JScrollPane supplémentaire,
     * car elle possède déjà son propre JScrollPane interne.
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(true);
        rightPanel.setBackground(new Color(54, 57, 63));

        // listMessageView contient déjà son JScrollPane — ajout direct
        addToGrid(rightPanel, listMessageView,  0, 0, 1.0, 1.0, GridBagConstraints.BOTH);

        // Barre de saisie fixe en bas
        addToGrid(rightPanel, inputMessageView, 0, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL);

        return rightPanel;
    }

    private void addToGrid(Container parent, Component comp,
                           int x, int y,
                           double weightX, double weightY,
                           int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx   = x;
        gbc.gridy   = y;
        gbc.weightx = weightX;
        gbc.weighty = weightY;
        gbc.fill    = fill;
        gbc.insets  = new Insets(6, 6, 6, 6);
        parent.add(comp, gbc);
    }

    // --- Getters ---

    public ListCanalView getListCanalView()       { return listCanalView;    }
    public ListUserView getListUserView()          { return listUserView;     }
    public ListMessageView getListMessageView()    { return listMessageView;  }
    public InputMessageView getInputMessageView()  { return inputMessageView; }
}