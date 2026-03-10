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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Vue d'inscription — JavaFX.
 */
public class FxRegisterView extends GridPane implements View {

    private final ViewContext viewContext;
    private final TextField tagField = new TextField();
    private final TextField nameField = new TextField();
    private final PasswordField pwdField = new PasswordField();
    private final PasswordField confirmPwdField = new PasswordField();
    private final Button registerButton = new Button("S'inscrire");
    private final Button loginButton = new Button("Se connecter");
    private QuadConsumer onRegisterRequested;
    private Runnable onBackToLoginRequested;

    public FxRegisterView(ViewContext viewContext) {
        this.viewContext = viewContext;
        buildUI();
        installListeners();
        if (viewContext.logger() != null) viewContext.logger().debug("FxRegisterView initialisée");
    }

    private void buildUI() {
        setAlignment(Pos.CENTER);
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(20, 40, 20, 40));

        Label title = new Label("Inscription");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.rgb(220, 221, 222));
        add(title, 0, 0, 2, 1);

        Label tagLabel = new Label("Tag :");
        tagLabel.setTextFill(Color.rgb(220, 221, 222));
        add(tagLabel, 0, 1);
        tagField.setStyle("-fx-text-fill: #DCDCDC; -fx-prompt-text-fill: #9AA0A6;");
        add(tagField, 1, 1);

        Label nameLabel = new Label("Nom :");
        nameLabel.setTextFill(Color.rgb(220, 221, 222));
        add(nameLabel, 0, 2);
        nameField.setStyle("-fx-text-fill: #DCDCDC; -fx-prompt-text-fill: #9AA0A6;");
        add(nameField, 1, 2);

        Label pwdLabel = new Label("Mot de passe :");
        pwdLabel.setTextFill(Color.rgb(220, 221, 222));
        add(pwdLabel, 0, 3);
        pwdField.setStyle("-fx-text-fill: #DCDCDC; -fx-prompt-text-fill: #9AA0A6;");
        add(pwdField, 1, 3);

        Label confirmLabel = new Label("Confirmer mdp :");
        confirmLabel.setTextFill(Color.rgb(220, 221, 222));
        add(confirmLabel, 0, 4);
        confirmPwdField.setStyle("-fx-text-fill: #DCDCDC; -fx-prompt-text-fill: #9AA0A6;");
        add(confirmPwdField, 1, 4);

        registerButton.setStyle("-fx-text-fill: #DCDCDC;");
        loginButton.setStyle("-fx-text-fill: #DCDCDC;");

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

    public void setOnRegisterRequested(QuadConsumer listener) {
        this.onRegisterRequested = listener;
    }

    public void setOnBackToLoginRequested(Runnable listener) {
        this.onBackToLoginRequested = listener;
    }

    public void clearFields() {
        tagField.clear();
        nameField.clear();
        pwdField.clear();
        confirmPwdField.clear();
    }

    @FunctionalInterface
    public interface QuadConsumer {
        void accept(String tag, String name, String pwd, String confirm);
    }
}
