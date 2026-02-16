package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.ihm.service.View;
import com.ubo.tp.message.ihm.service.IRegisterView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JComponent implements IRegisterView {

    private final Logger LOGGER;

    private JTextField tagField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton loginButton;

    private final IRegisterController controller;
    private final INavigationController navigationController;

    private Runnable onRegisterRequested;
    private Runnable onBackToLoginRequested;

    /**
     * Crée le composant d'inscription et initialise la vue.
     *
     * @param logger logger optionnel pour consigner les actions
     */
    public RegisterView(Logger logger, IRegisterController controller, INavigationController navigationController) {
        this.LOGGER = logger;
        this.controller = controller;
        this.navigationController = navigationController;
        this.init();
    }

    private void init() {
        if (LOGGER != null) LOGGER.debug("Initialisation de RegisterComponent");
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

        createConfirmPasswordLabel();
        createConfirmPasswordField();

        createRegisterButton();
        createLoginButton();

        createConnector();

        if (LOGGER != null) LOGGER.debug("RegisterComponent initialisé");
    }

    // --- UI builders (copied from previous RegisterView) ---
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
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 10, 0), 0, 0
        );
        JLabel titleLabel = new JLabel("Inscription", SwingConstants.LEFT);
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
        int row = 4;
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0
        );
        JLabel lbl = new JLabel("Mot de passe :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, gbc);
    }

    private void createPasswordField() {
        int row = 4;
        GridBagConstraints gbc = new GridBagConstraints(
                1, row, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0
        );
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(passwordField, gbc);
    }

    private void createConfirmPasswordLabel() {
        int row = 5;
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 10, 5), 0, 0
        );
        JLabel lbl = new JLabel("Confirmer Mot de passe :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, gbc);
    }

    private void createConfirmPasswordField() {
        int row = 5;
        GridBagConstraints gbc = new GridBagConstraints(
                1, row, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0
        );
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(confirmPasswordField, gbc);
    }

    private void createRegisterButton() {
        int row = 6;
        GridBagConstraints gbc = new GridBagConstraints(
                1, row, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0
        );
        registerButton = new JButton("S'inscrire");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(registerButton, gbc);
    }

    private void createLoginButton() {
        int row = 6;
        GridBagConstraints gbc = new GridBagConstraints(
                0, row, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0
        );
        loginButton = new JButton("Se Connecter");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(loginButton, gbc);
    }

    public void createConnector(){
        this.getRegisterButton().addActionListener(e -> {
            if (LOGGER != null) LOGGER.debug("Register button clicked");
            if (onRegisterRequested != null) {
                onRegisterRequested.run();
            }
            if (controller != null) {
                boolean userIsCreated = controller.onRegisterButtonClicked(this.getTag(),
                        this.getName(),
                        this.getPassword(),
                        this.getConfirmPassword());
                if (userIsCreated) {
                    if (LOGGER != null) LOGGER.info("User registered successfully, navigating to login view");
                }else{
                    if (LOGGER != null) LOGGER.warn("User registration failed, user already exists");
                }
            }
        });

        this.getLoginButton().addActionListener(e -> {
            if (LOGGER != null) LOGGER.debug("Bouton Retour cliqué");
            if (onBackToLoginRequested != null) onBackToLoginRequested.run();
            if (navigationController != null) navigationController.navigateToLogin();
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

    public JPasswordField getConfirmPasswordField() {
        return confirmPasswordField;
    }

    public JButton getRegisterButton() {
        return registerButton;
    }

    public JButton getLoginButton() { return loginButton; }

    // IRegisterView impl
    @Override
    public String getTag() { return getTagField().getText(); }

    @Override
    public String getName() { return getNameField().getText(); }

    @Override
    public String getPassword() { return new String(getPasswordField().getPassword()); }

    @Override
    public String getConfirmPassword() { return new String(getConfirmPasswordField().getPassword()); }

    @Override
    public void setOnRegisterRequested(Runnable handler) { this.onRegisterRequested = handler; }

    @Override
    public void setOnBackToLoginRequested(Runnable handler) { this.onBackToLoginRequested = handler; }

    @Override
    public void clearFields() {
        getTagField().setText("");
        getNameField().setText("");
        getPasswordField().setText("");
        getConfirmPasswordField().setText("");
    }

}
