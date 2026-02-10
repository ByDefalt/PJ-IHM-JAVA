package com.ubo.tp.message.ihm.component;

import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Composant UI pur pour le formulaire d'inscription.
 * <p>
 * Fournit les champs nécessaires (tag, nom, email, mot de passe) et expose des
 * accesseurs ainsi qu'une méthode pour attacher le listener du bouton d'enregistrement.
 * </p>
 */
public class RegisterComponent extends JPanel {

    private final Logger LOGGER;

    private JTextField tagField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;

    /**
     * Crée le composant d'inscription et initialise la vue.
     *
     * @param logger logger optionnel pour consigner les actions
     */
    public RegisterComponent(Logger logger) {
        this.LOGGER = logger;
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
        JLabel lbl = new JLabel("Confirmer :");
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
                0, row, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0
        );
        registerButton = new JButton("S'inscrire");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(registerButton, gbc);
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

    /**
     * Ajoute un listener au bouton d'inscription.
     *
     * @param l listener d'action
     */
    public void addRegisterListener(ActionListener l) { if (registerButton != null) registerButton.addActionListener(l); }
}
