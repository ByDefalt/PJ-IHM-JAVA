package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController.ChannelCreationCallback;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Liste défilante de canaux avec champ de recherche — JavaFX.
 */
public class FxListCanalView extends VBox implements View {

    private final ViewContext viewContext;
    private final VBox canalsBox = new VBox(4);
    private final TextField searchField = new TextField();
    private final List<FxCanalView> allCanals = new ArrayList<>();

    /**
     * Bottom-sheet de création de canal, superposée dans le StackPane parent.
     */
    private FxNewChannelBottomSheet bottomSheet;
    /**
     * Callback transmis depuis le graphic controller.
     */
    private ChannelCreationCallback onNewChannelConfirm;
    /**
     * Utilisateurs disponibles (sans l'utilisateur connecté).
     */
    private List<User> availableUsers = new ArrayList<>();

    public FxListCanalView(ViewContext viewContext) {
        this.viewContext = viewContext;
        setPadding(new Insets(8));
        setBackground(new Background(new BackgroundFill(Color.rgb(54, 57, 63), CornerRadii.EMPTY, Insets.EMPTY)));

        searchField.setPromptText("Rechercher un canal…");
        searchField.textProperty().addListener((obs, o, n) -> filterCanals(n));

        canalsBox.setPadding(new Insets(4, 0, 0, 0));

        ScrollPane scroll = new ScrollPane(canalsBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(searchField, scroll);

        // Menu contextuel clic droit
        ContextMenu contextMenu = new ContextMenu();
        MenuItem newChannelItem = new MenuItem("✦  Nouveau canal");
        newChannelItem.setStyle("-fx-font-size: 13; -fx-padding: 6 16 6 16;");
        newChannelItem.setOnAction(e -> openNewChannelSheetPublic());
        contextMenu.getItems().add(newChannelItem);

        setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });

        if (viewContext.logger() != null) viewContext.logger().debug("FxListCanalView initialisée");
    }

    // -------------------------------------------------------------------------
    // Bottom-sheet
    // -------------------------------------------------------------------------

    /**
     * Enregistre le callback de création de canal.
     */
    public void setOnNewChannelConfirm(ChannelCreationCallback onConfirm) {
        this.onNewChannelConfirm = onConfirm;
    }

    /**
     * Met à jour la liste des utilisateurs disponibles pour le formulaire.
     */
    public void setAvailableUsers(List<User> users) {
        this.availableUsers = users != null ? new ArrayList<>(users) : new ArrayList<>();
        if (bottomSheet != null) bottomSheet.setAvailableUsers(this.availableUsers);
    }

    /**
     * Ouvre la bottom-sheet dans le StackPane ancêtre.
     */
    public void openNewChannelSheetPublic() {
        javafx.scene.Parent parent = getParent();
        while (parent != null && !(parent instanceof StackPane)) {
            parent = parent.getParent();
        }
        if (parent == null) {
            if (viewContext.logger() != null)
                viewContext.logger().warn("Aucun StackPane ancêtre trouvé pour la bottom-sheet");
            return;
        }
        StackPane stackPane = (StackPane) parent;

        if (bottomSheet == null) {
            bottomSheet = new FxNewChannelBottomSheet(viewContext);
        }
        bottomSheet.setOnConfirm(onNewChannelConfirm);
        bottomSheet.setAvailableUsers(availableUsers);

        if (!stackPane.getChildren().contains(bottomSheet)) {
            stackPane.getChildren().add(bottomSheet);
        }
        bottomSheet.show();
    }

    // -------------------------------------------------------------------------
    // API gestion des canaux
    // -------------------------------------------------------------------------

    public void addCanalUI(FxCanalView canalView) {
        allCanals.add(canalView);
        filterCanals(searchField.getText());
    }

    public void rebuildUI(List<FxCanalView> ordered) {
        allCanals.clear();
        allCanals.addAll(ordered);
        filterCanals(searchField.getText());
    }

    private void filterCanals(String query) {
        canalsBox.getChildren().clear();
        String q = query == null ? "" : query.trim().toLowerCase();
        for (FxCanalView cv : allCanals) {
            if (q.isEmpty() || cv.getChannel().getName().toLowerCase().contains(q))
                canalsBox.getChildren().add(cv);
        }
    }
}


