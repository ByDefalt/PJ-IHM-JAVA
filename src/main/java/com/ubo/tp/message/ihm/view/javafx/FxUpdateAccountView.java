package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

/**
 * Vue de modification de profil — JavaFX.
 */
public class FxUpdateAccountView extends GridPane implements View {

    private final ViewContext viewContext;
    private final TextField tagField = new TextField();
    private final PasswordField pwdField = new PasswordField();
    private final TextField nameField = new TextField();
    private final Button updateBtn = new Button("Mettre à jour");

    public FxUpdateAccountView(ViewContext viewContext) {
        this.viewContext = viewContext;
        buildUI();
        if (viewContext.logger() != null) viewContext.logger().debug("FxUpdateAccountView initialisée");
    }

    private void buildUI() {
        setAlignment(Pos.CENTER);
        setHgap(10);
        setVgap(10);
        setPadding(new Insets(20, 40, 20, 40));

        Label title = new Label("Mettre à jour le compte");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        add(title, 0, 0, 2, 1);

        tagField.setEditable(false);
        pwdField.setEditable(false);

        add(new Label("Tag :"), 0, 1);
        add(tagField, 1, 1);
        add(new Label("Mot de passe :"), 0, 2);
        add(pwdField, 1, 2);
        add(new Label("Nom :"), 0, 3);
        add(nameField, 1, 3);
        add(updateBtn, 1, 4);
    }

    public void setOnUpdateRequested(Consumer<String> callback) {
        updateBtn.setOnAction(e -> {
            if (callback != null) callback.accept(nameField.getText());
        });
    }

    public void setUser(User user) {
        if (user == null) {
            tagField.clear();
            nameField.clear();
            pwdField.clear();
            return;
        }
        tagField.setText(user.getUserTag());
        nameField.setText(user.getName());
        pwdField.setText(user.getUserPassword() != null ? user.getUserPassword() : "");
    }
}

