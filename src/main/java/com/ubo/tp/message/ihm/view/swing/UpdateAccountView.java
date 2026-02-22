package com.ubo.tp.message.ihm.view.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class UpdateAccountView extends JComponent implements View {

    private final ViewContext viewContext;

    private JTextField tagField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JButton updateButton;

    public UpdateAccountView(ViewContext viewContext) {
        this.viewContext = viewContext;
        init();
    }

    private void init() {
        if (viewContext != null && viewContext.logger() != null) viewContext.logger().debug("Initialisation de UpdateAccountView");
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        createTitle();
        createTagLabel();
        createTagField();
        createPasswordLabel();
        createPasswordField();
        createNameLabel();
        createNameField();
        createUpdateButton();

        if (viewContext != null && viewContext.logger() != null) viewContext.logger().debug("UpdateAccountView initialisée");
    }

    public void setOnUpdateRequested(Consumer<String> callback) {
        updateButton.addActionListener(e -> {
            String newName = nameField.getText();
            if (callback != null) {
                callback.accept(newName);
            }
        });
    }

    public void setUser(User user) {
        if (user == null) {
            tagField.setText("");
            nameField.setText("");
            passwordField.setText("");
            return;
        }
        tagField.setText(user.getUserTag());
        nameField.setText(user.getName());
        passwordField.setText(user.getUserPassword() != null ? user.getUserPassword() : "");
    }

    private void createTitle() {
        JLabel titleLabel = new JLabel("Mettre à jour le compte", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        this.add(titleLabel, new GridBagConstraints(
                0, 0, 2, 1, 1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 10, 0), 0, 0));
    }

    private void createTagLabel() {
        JLabel lbl = new JLabel("Tag :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, new GridBagConstraints(
                0, 1, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createTagField() {
        tagField = new JTextField();
        tagField.setFont(new Font("Arial", Font.PLAIN, 14));
        tagField.setEditable(false);
        tagField.setEnabled(false); // grisé
        this.add(tagField, new GridBagConstraints(
                1, 1, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createPasswordLabel() {
        JLabel lbl = new JLabel("Mot de passe :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, new GridBagConstraints(
                0, 2, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createPasswordField() {
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setEditable(false);
        passwordField.setEnabled(false); // grisé
        this.add(passwordField, new GridBagConstraints(
                1, 2, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createNameLabel() {
        JLabel lbl = new JLabel("Nom :");
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(lbl, new GridBagConstraints(
                0, 3, 1, 1, 0.3, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createNameField() {
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        this.add(nameField, new GridBagConstraints(
                1, 3, 1, 1, 0.7, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));
    }

    private void createUpdateButton() {
        updateButton = new JButton("Mettre à jour");
        updateButton.setFont(new Font("Arial", Font.BOLD, 14));
        this.add(updateButton, new GridBagConstraints(
                1, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 10, 10), 0, 0));
    }
}
