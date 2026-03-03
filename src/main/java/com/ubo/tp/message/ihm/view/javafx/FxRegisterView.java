package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Vue d'inscription — JavaFX.
 */
public class FxRegisterView extends GridPane implements View {

    private final ViewContext viewContext;
    private final TextField tagField              = new TextField();
    private final TextField nameField             = new TextField();
    private final PasswordField pwdField          = new PasswordField();
    private final PasswordField confirmPwdField   = new PasswordField();
    private final Button registerButton           = new Button("S'inscrire");
    private final Button loginButton              = new Button("Se connecter");

    @FunctionalInterface
    public interface QuadConsumer { void accept(String tag, String name, String pwd, String confirm); }

    private QuadConsumer onRegisterRequested;
    private Runnable     onBackToLoginRequested;

    public FxRegisterView(ViewContext viewContext) {
        this.viewContext = viewContext;
        buildUI();
        installListeners();
        if (viewContext.logger() != null) viewContext.logger().debug("FxRegisterView initialisée");
    }

    private void buildUI() {
        setAlignment(Pos.CENTER);
        setHgap(10); setVgap(10);
        setPadding(new Insets(20, 40, 20, 40));

        Label title = new Label("Inscription");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        add(title, 0, 0, 2, 1);

        add(new Label("Tag :"), 0, 1);              add(tagField,        1, 1);
        add(new Label("Nom :"), 0, 2);              add(nameField,       1, 2);
        add(new Label("Mot de passe :"), 0, 3);     add(pwdField,        1, 3);
        add(new Label("Confirmer mdp :"), 0, 4);    add(confirmPwdField, 1, 4);

        HBox buttons = new HBox(10, registerButton, loginButton);
        buttons.setAlignment(Pos.CENTER);
        add(buttons, 0, 5, 2, 1);
    }

    private void installListeners() {
        registerButton.setOnAction(e -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Inscription demandée");
            if (onRegisterRequested != null)
                onRegisterRequested.accept(tagField.getText(), nameField.getText(),
                        pwdField.getText(), confirmPwdField.getText());
        });
        loginButton.setOnAction(e -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Retour vers connexion");
            if (onBackToLoginRequested != null) onBackToLoginRequested.run();
        });
    }

    public void setOnRegisterRequested(QuadConsumer listener)  { this.onRegisterRequested = listener; }
    public void setOnBackToLoginRequested(Runnable listener)   { this.onBackToLoginRequested = listener; }

    public void clearFields() {
        tagField.clear(); nameField.clear(); pwdField.clear(); confirmPwdField.clear();
    }
}

