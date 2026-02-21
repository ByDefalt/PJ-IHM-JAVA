package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class LoginView extends JComponent implements View {

    private final Logger LOGGER;
    private JTextField tagField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    /** Callback : (tag, name, password) → déclenché au clic sur "Se connecter" */
    private BiConsumer<String[], Void> onLoginRequested;

    /** Callback : déclenché au clic sur "S'inscrire" */
    private Runnable onRegisterRequested;

    public LoginView(Logger logger) {
        this.LOGGER = logger;
        this.init();
    }

    private void init() {
        if (LOGGER != null) LOGGER.debug("Initialisation de LoginView");
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
        installListeners();

        if (LOGGER != null) LOGGER.debug("LoginView initialisée");
    }

    // -------------------------------------------------------------------------
    // API publique — appelée par le contrôleur graphique
    // -------------------------------------------------------------------------

    public void setOnLoginRequested(TriConsumer<String, String, String> listener) {
        this.onLoginRequested = (args, v) ->
                listener.accept(args[0], args[1], args[2]);
    }

    public void setOnRegisterRequested(Runnable listener) {
        this.onRegisterRequested = listener;
    }

    // -------------------------------------------------------------------------
    // Listeners internes
    // -------------------------------------------------------------------------

    private void installListeners() {
        loginButton.addActionListener(e -> {
            if (LOGGER != null) LOGGER.debug("Bouton de connexion cliqué");
            if (onLoginRequested != null) {
                onLoginRequested.accept(
                        new String[]{
                                tagField.getText(),
                                nameField.getText(),
                                new String(passwordField.getPassword())
                        }, null);
            }
        });

        registerButton.addActionListener(e -> {
            if (LOGGER != null) LOGGER.debug("Bouton d'inscription cliqué");
            if (onRegisterRequested != null) onRegisterRequested.run();
        });
    }

    // -------------------------------------------------------------------------
    // Interface fonctionnelle utilitaire
    // -------------------------------------------------------------------------

    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }

    // -------------------------------------------------------------------------
    // Construction UI
    // -------------------------------------------------------------------------

    private void createLogo() {
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 10, 10), 0, 0
        );
        try {
            java.net.URL url = getClass().getResource("/images/logo_50.png");
            if (url != null) {
                this.add(new JLabel(new ImageIcon(url)), gbc);
                if (LOGGER != null) LOGGER.info("Logo chargé depuis /images/logo_50.png");
            } else {
                if (LOGGER != null) LOGGER.warn("Ressource introuvable : /images/logo_50.png");
            }
        } catch (Exception e) {
            if (LOGGER != null) LOGGER.error("Erreur lors du chargement du logo", e);
        }
    }

    private void createTitle() {
        JLabel titleLabel = new JLabel("Connexion", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        this.add(titleLabel, new GridBagConstraints(
                1, 0, 1, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 10, 0), 0, 0));
    }

    private void createTagLabel() {
        JLabel lbl = new JLabel("Tag :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, new GridBagConstraints(
                0, 1, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 5, 5, 5), 0, 0));
    }

    private void createTagField() {
        tagField = new JTextField();
        tagField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(tagField, new GridBagConstraints(
                1, 1, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 5, 5, 5), 0, 0));
    }

    private void createNameLabel() {
        JLabel lbl = new JLabel("Nom :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, new GridBagConstraints(
                0, 2, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createNameField() {
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(nameField, new GridBagConstraints(
                1, 2, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createPasswordLabel() {
        JLabel lbl = new JLabel("Mot de passe :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, new GridBagConstraints(
                0, 3, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    private void createPasswordField() {
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(passwordField, new GridBagConstraints(
                1, 3, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    private void createLoginButton() {
        loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(loginButton, new GridBagConstraints(
                0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
    }

    private void createRegisterButton() {
        registerButton = new JButton("S'inscrire");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(registerButton, new GridBagConstraints(
                1, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
    }
}