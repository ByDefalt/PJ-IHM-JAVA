package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Liste défilante d'utilisateurs avec champ de recherche — JavaFX.
 */
public class FxListUserView extends VBox implements View {

    private final ViewContext viewContext;
    private final VBox usersBox = new VBox(4);
    private final TextField searchField = new TextField();
    private final List<FxUserView> allUsers = new ArrayList<>();

    public FxListUserView(ViewContext viewContext) {
        this.viewContext = viewContext;
        setPadding(new Insets(8));
        setBackground(new Background(new BackgroundFill(Color.rgb(54, 57, 63), CornerRadii.EMPTY, Insets.EMPTY)));

        searchField.setPromptText("Rechercher un utilisateur…");
        searchField.textProperty().addListener((obs, o, n) -> filterUsers(n));

        usersBox.setPadding(new Insets(4, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(usersBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(searchField, scroll);
        if (viewContext.logger() != null) viewContext.logger().debug("FxListUserView initialisée");
    }

    public void addUserUI(FxUserView userView) {
        allUsers.add(userView);
        filterUsers(searchField.getText());
    }

    public void rebuildUI(List<FxUserView> ordered) {
        allUsers.clear();
        allUsers.addAll(ordered);
        filterUsers(searchField.getText());
    }

    public void updateUserUI(FxUserView view, com.ubo.tp.message.datamodel.User updated) {
        view.updateUser(updated);
    }

    private void filterUsers(String query) {
        usersBox.getChildren().clear();
        String q = query == null ? "" : query.trim().toLowerCase();
        for (FxUserView uv : allUsers) {
            if (q.isEmpty() || uv.getUser().getName().toLowerCase().contains(q))
                usersBox.getChildren().add(uv);
        }
    }
}

