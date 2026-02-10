package com.ubo.tp.message.ihm.component;

import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Composant UI pur pour le formulaire de connexion.
 * <p>
 * Ce composant contient uniquement les éléments d'interface (champs et
 * boutons) et expose des accesseurs et méthodes pour attacher des listeners.
 * Il ne contient aucune logique métier ni dépendance vers les controllers.
 * </p>
 */
public class LoginComponent extends JPanel implements Component {

    private final Logger LOGGER;

    private JTextField tagField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    /**
     * Crée et initialise le composant de login.
     *
     * @param logger logger optionnel pour consigner les actions
     */
    public LoginComponent(Logger logger) {
        this.LOGGER = logger;
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

    public JTextField getTagField() { return tagField; }
    public JTextField getNameField() { return nameField; }
    public JPasswordField getPasswordField() { return passwordField; }
    public JButton getLoginButton() { return loginButton; }
    public JButton getRegisterButton() { return registerButton; }

    /**
     * Ajoute un listener exécuté lorsque l'utilisateur clique sur "Se connecter".
     *
     * @param l listener action (non-null)
     */
    public void addLoginListener(ActionListener l) { if (loginButton != null) loginButton.addActionListener(l); }

    /**
     * Ajoute un listener exécuté lorsque l'utilisateur clique sur "S'inscrire".
     *
     * @param l listener action (non-null)
     */
    public void addRegisterListener(ActionListener l) { if (registerButton != null) registerButton.addActionListener(l); }
}
