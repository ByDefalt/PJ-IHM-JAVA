package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JComponent implements View {

    private final ViewContext viewContext;
    private JTextField tagField;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton loginButton;

    /**
     * Callback : (tag, name, password, confirmPassword) → déclenché au clic sur "S'inscrire"
     */
    private QuadConsumer<String, String, String, String> onRegisterRequested;

    /**
     * Callback : déclenché au clic sur "Se Connecter"
     */
    private Runnable onBackToLoginRequested;

    public RegisterView(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.init();
    }

    private void init() {
        if (viewContext.logger() != null) viewContext.logger().debug("Initialisation de RegisterView");
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
        installListeners();

        if (viewContext.logger() != null) viewContext.logger().debug("RegisterView initialisée");
    }

    // -------------------------------------------------------------------------
    // API publique — appelée par le contrôleur graphique
    // -------------------------------------------------------------------------

    public void setOnRegisterRequested(QuadConsumer<String, String, String, String> listener) {
        this.onRegisterRequested = listener;
    }

    public void setOnBackToLoginRequested(Runnable listener) {
        this.onBackToLoginRequested = listener;
    }

    public void clearFields() {
        tagField.setText("");
        nameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    // -------------------------------------------------------------------------
    // Listeners internes
    // -------------------------------------------------------------------------

    private void installListeners() {
        registerButton.addActionListener(e -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Bouton d'inscription cliqué");
            if (onRegisterRequested != null) {
                onRegisterRequested.accept(
                        tagField.getText(),
                        nameField.getText(),
                        new String(passwordField.getPassword()),
                        new String(confirmPasswordField.getPassword())
                );
            }
        });

        loginButton.addActionListener(e -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Bouton retour connexion cliqué");
            if (onBackToLoginRequested != null) onBackToLoginRequested.run();
        });
    }

    // -------------------------------------------------------------------------
    // Interface fonctionnelle utilitaire
    // -------------------------------------------------------------------------

    private void createLogo() {
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 10, 10), 0, 0);
        try {
            java.net.URL url = getClass().getResource("/images/logo_50.png");
            if (url != null) {
                this.add(new JLabel(new ImageIcon(url)), gbc);
                if (viewContext.logger() != null) viewContext.logger().info("Logo chargé depuis /images/logo_50.png");
            } else {
                if (viewContext.logger() != null)
                    viewContext.logger().warn("Ressource introuvable : /images/logo_50.png");
            }
        } catch (Exception e) {
            if (viewContext.logger() != null) viewContext.logger().error("Erreur lors du chargement du logo", e);
        }
    }

    // -------------------------------------------------------------------------
    // Construction UI
    // -------------------------------------------------------------------------

    private void createTitle() {
        JLabel titleLabel = new JLabel("Inscription", SwingConstants.LEFT);
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
                0, 4, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createPasswordField() {
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(passwordField, new GridBagConstraints(
                1, 4, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createConfirmPasswordLabel() {
        JLabel lbl = new JLabel("Confirmer Mot de passe :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, new GridBagConstraints(
                0, 5, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    private void createConfirmPasswordField() {
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(confirmPasswordField, new GridBagConstraints(
                1, 5, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 10, 5), 0, 0));
    }

    private void createRegisterButton() {
        registerButton = new JButton("S'inscrire");
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(registerButton, new GridBagConstraints(
                1, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
    }

    private void createLoginButton() {
        loginButton = new JButton("Se Connecter");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(loginButton, new GridBagConstraints(
                0, 6, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
    }

    @FunctionalInterface
    public interface QuadConsumer<A, B, C, D> {
        void accept(A a, B b, C c, D d);
    }
}