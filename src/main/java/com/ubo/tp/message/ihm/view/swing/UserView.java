package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;

public class UserView extends JComponent implements View {

    private final ViewContext viewContext;

    private JLabel userNameLabel;
    private JLabel statusLabel;
    private User user;

    /**
     * Crée un composant UserView simple (nom + statut).
     *
     * @param viewContext contexte de la vue (contient le logger)
     * @param user        le User
     */
    public UserView(ViewContext viewContext, User user) {
        this.viewContext = viewContext;
        this.user = user;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setOpaque(false);

        init();
        if (viewContext.logger() != null) viewContext.logger().debug("UserView initialisée pour: " + user.getName());
    }

    private void init() {
        setOpaque(true);
        createNameLabel();
        createStatusLabel();
    }

    private void createNameLabel() {
        // Police bold 13 depuis le thème
        Font baseFont = UIManager.getFont("Label.font");
        Font nameFont = (baseFont != null) ? baseFont.deriveFont(Font.BOLD, 13f)
                : new Font("SansSerif", Font.BOLD, 13);

        // Texte normal Discord (#DCDDDE)
        Color nameColor = UIManager.getColor("Label.foreground");
        if (nameColor == null) nameColor = new Color(220, 221, 222);

        userNameLabel = new JLabel(user.getName() != null ? user.getName() : "");
        userNameLabel.setFont(nameFont);
        userNameLabel.setForeground(nameColor);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(4, 0, 2, 2), 0, 0
        );
        this.add(userNameLabel, gbc);
    }

    private void createStatusLabel() {
        // Police plain 11 depuis le thème
        Font baseFont = UIManager.getFont("Label.font");
        Font statusFont = (baseFont != null) ? baseFont.deriveFont(Font.PLAIN, 11f)
                : new Font("SansSerif", Font.PLAIN, 11);

        // Texte muted Discord (#72767D)
        Color statusColor = UIManager.getColor("Label.disabledForeground");
        if (statusColor == null) statusColor = new Color(114, 118, 125);

        statusLabel = new JLabel("");
        statusLabel.setFont(statusFont);
        statusLabel.setForeground(statusColor);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 4, 2), 0, 0
        );
        this.add(statusLabel, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
        userNameLabel.setText(user.getName() != null ? user.getName() : "");
        // TODO : mettre à jour le statut aussi
    }

    public void updateUser(User user) {
        this.setUser(user);
    }
}