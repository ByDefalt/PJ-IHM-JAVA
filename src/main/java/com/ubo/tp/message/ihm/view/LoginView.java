package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.ihm.service.ILoginView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JComponent implements ILoginView {


    private final Logger LOGGER;
    private final ILoginController controller;
    private final INavigationController navigationController;
    private JTextField tagField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private Runnable onLoginRequested;
    private Runnable onRegisterRequested;

    /**
     * Crée et initialise le composant de login.
     *
     * @param logger logger optionnel pour consigner les actions
     */
    public LoginView(Logger logger, ILoginController controller, INavigationController navigationController) {
        this.LOGGER = logger;
        this.controller = controller;
        this.navigationController = navigationController;
        this.init();
    }

    private void init() {
        if (LOGGER != null) LOGGER.debug("Initialisation de LoginComponent");
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        createLogo();
        createTitle();

        createTagLabel();
        createTagField();

        createNameLabel();
        createNameField();

        createPasswordLabel();
        createPasswordField();

        createLoginButton();
        createRegisterButton();

        createConnector();

        if (LOGGER != null) LOGGER.debug("LoginComponent initialisé");
    }

    private void createLogo() {
        int row = 0;
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 10, 10), 0, 0
        );
        try {
            java.net.URL url = getClass().getResource("/images/logo_50.png");
            if (url != null) {
                ImageIcon logo = new ImageIcon(url);
                JLabel logoLabel = new JLabel(logo);
                this.add(logoLabel, gbc);
                if (LOGGER != null) LOGGER.info("Logo chargé depuis /images/logo_50.png");
            } else {
                if (LOGGER != null) LOGGER.warn("Ressource introuvable : /images/logo_50.png");
            }
        } catch (Exception e) {
            if (LOGGER != null) LOGGER.error("Erreur lors du chargement du logo", e);
        }
    }

    private void createTitle() {
        int row = 0;
        GridBagConstraints gbc = new GridBagConstraints(
                1, row, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 10, 0), 0, 0
        );
        JLabel titleLabel = new JLabel("Connexion", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        this.add(titleLabel, gbc);
    }

    private void createTagLabel() {
        int row = 1;
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 5, 5, 5), 0, 0
        );
        JLabel lbl = new JLabel("Tag :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, gbc);
    }

    private void createTagField() {
        int row = 1;
        GridBagConstraints gbc = new GridBagConstraints(
                1, row, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0
        );
        tagField = new JTextField();
        tagField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(tagField, gbc);
    }

    private void createNameLabel() {
        int row = 2;
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0
        );
        JLabel lbl = new JLabel("Nom :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, gbc);
    }

    private void createNameField() {
        int row = 2;
        GridBagConstraints gbc = new GridBagConstraints(
                1, row, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0
        );
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(nameField, gbc);
    }

    private void createPasswordLabel() {
        int row = 3;
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 10, 5), 0, 0
        );
        JLabel lbl = new JLabel("Mot de passe :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, gbc);
    }

    private void createPasswordField() {
        int row = 3;
        GridBagConstraints gbc = new GridBagConstraints(
                1, row, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0
        );
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(passwordField, gbc);
    }

    private void createLoginButton() {
        int row = 4;
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0
        );
        loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(loginButton, gbc);
    }

    private void createRegisterButton() {
        int row = 4;
        GridBagConstraints gbc = new GridBagConstraints(
                1, row, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0
        );
        registerButton = new JButton("S'inscrire");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(registerButton, gbc);
    }


    public void createConnector() {
        this.getRegisterButton().addActionListener(e -> {
            if (LOGGER != null) LOGGER.debug("Bouton d'inscription cliqué");
            if (onRegisterRequested != null) onRegisterRequested.run();
            navigationController.navigateToRegister();
        });

        this.getLoginButton().addActionListener(e -> {
            if (LOGGER != null) LOGGER.debug("Bouton de connexion cliqué");
            if (onLoginRequested != null) onLoginRequested.run();
            if (controller != null) controller.onLoginButtonClicked(this.getTag(),
                    this.getName(),
                    this.getPassword());
        });
    }

    public JTextField getTagField() {
        return tagField;
    }

    public JTextField getNameField() {
        return nameField;
    }

    public JPasswordField getPasswordField() {
        return passwordField;
    }

    public JButton getLoginButton() {
        return loginButton;
    }

    public JButton getRegisterButton() {
        return registerButton;
    }

    // ILoginView impl
    @Override
    public String getTag() {
        return getTagField().getText();
    }

    @Override
    public String getName() {
        return getNameField().getText();
    }

    @Override
    public String getPassword() {
        return new String(getPasswordField().getPassword());
    }

    @Override
    public void setOnLoginRequested(Runnable handler) {
        this.onLoginRequested = handler;
    }

    @Override
    public void setOnRegisterRequested(Runnable handler) {
        this.onRegisterRequested = handler;
    }

}
