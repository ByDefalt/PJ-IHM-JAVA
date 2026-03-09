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
 * Vue de connexion — JavaFX.
 */
public class FxLoginView extends GridPane implements View {

    private final ViewContext viewContext;
    private final TextField tagField = new TextField();
    private final TextField nameField = new TextField();
    private final PasswordField pwdField = new PasswordField();
    private final Button loginButton = new Button("Se connecter");
    private final Button registerButton = new Button("S'inscrire");
    private TriConsumer onLoginRequested;
    private Runnable onRegisterRequested;

    public FxLoginView(ViewContext viewContext) {
        this.viewContext = viewContext;
        buildUI();
        installListeners();
        if (viewContext.logger() != null) viewContext.logger().debug("FxLoginView initialisée");
    }

    private void buildUI() {
        setAlignment(Pos.CENTER);
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(20, 40, 20, 40));

        Label title = new Label("Connexion");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        add(title, 0, 0, 2, 1);

        add(new Label("Tag :"), 0, 1);
        add(tagField, 1, 1);
        add(new Label("Nom :"), 0, 2);
        add(nameField, 1, 2);
        add(new Label("Mot de passe :"), 0, 3);
        add(pwdField, 1, 3);

        HBox buttons = new HBox(10, loginButton, registerButton);
        buttons.setAlignment(Pos.CENTER);
        add(buttons, 0, 4, 2, 1);
    }

    private void installListeners() {
        loginButton.setOnAction(e -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Connexion demandée");
            if (onLoginRequested != null)
                onLoginRequested.accept(tagField.getText(), nameField.getText(), pwdField.getText());
        });
        registerButton.setOnAction(e -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Navigation vers inscription");
            if (onRegisterRequested != null) onRegisterRequested.run();
        });
    }

    public void setOnLoginRequested(TriConsumer listener) {
        this.onLoginRequested = listener;
    }

    public void setOnRegisterRequested(Runnable listener) {
        this.onRegisterRequested = listener;
    }

    @FunctionalInterface
    public interface TriConsumer {
        void accept(String tag, String name, String pwd);
    }
}

