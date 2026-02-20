package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.ihm.service.IUserView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

public class UserView extends JComponent implements IUserView {

    private final Logger LOGGER;

    private JLabel userNameLabel;
    private JLabel statusLabel;

    /**
     * Crée un composant UserView simple (nom + statut).
     *
     * @param logger   logger optionnel (peut être null)
     * @param userName nom à afficher
     */
    public UserView(Logger logger, String userName) {
        this.LOGGER = logger;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setOpaque(false);

        init(userName);
        if (LOGGER != null) LOGGER.debug("UserView initialisée pour: " + userName);
    }

    public UserView(String userName) {
        this(null, userName);
    }

    private void init(String userName) {
        createNameLabel(userName);
        createStatusLabel();
        createConnector();
    }

    private void createNameLabel(String userName) {
        userNameLabel = new JLabel(userName != null ? userName : "");
        userNameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        userNameLabel.setForeground(Color.BLACK);

        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(4, 0, 2, 2), 0, 0
        );
        this.add(userNameLabel, gbc);
    }

    private void createStatusLabel() {
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(100, 100, 100));

        GridBagConstraints gbc = new GridBagConstraints(
                0, 1, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 4, 2), 0, 0
        );
        this.add(statusLabel, gbc);
    }

    private void createConnector() {
        // Composant simple : possibilité d'ajouter un listener externe via getUserNameLabel().
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (LOGGER != null) LOGGER.debug("UserView cliqué: " + getUserName());
            }
        });
    }

    // API publique
    public JLabel getUserNameLabel() {
        return userNameLabel;
    }

    public String getUserName() {
        return userNameLabel.getText();
    }

    public void setUserName(String name) {
        this.userNameLabel.setText(name);
    }

    public String getStatus() {
        return this.statusLabel.getText();
    }

    public void setStatus(String status) {
        this.statusLabel.setText(status);
    }

}
