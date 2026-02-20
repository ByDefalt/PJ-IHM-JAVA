package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.service.IUserView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UserView extends JComponent implements IUserView {

    private final Logger LOGGER;

    private JLabel userNameLabel;
    private JLabel statusLabel;

    private User user;

    /**
     * Crée un composant UserView simple (nom + statut).
     *
     * @param logger logger optionnel (peut être null)
     * @param user   le User
     */
    public UserView(Logger logger, User user) {
        this.LOGGER = logger;
        this.user = user;
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        this.setOpaque(false);

        init();
        if (LOGGER != null) LOGGER.debug("UserView initialisée pour: " + user.getName());
    }

    private void init() {
        setOpaque(true);
        createNameLabel();
        createStatusLabel();
        createConnector();
    }

    private void createNameLabel() {
        userNameLabel = new JLabel(user.getName() != null ? user.getName() : "");
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
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (LOGGER != null) LOGGER.debug("UserView cliqué: " + user.getName());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
        userNameLabel.setText(user.getName() != null ? user.getName() : "");
        // TODO : mettre à jour le statut aussi
    }

    @Override
    public void updateUser(User user) {
        this.setUser(user);
    }
}
