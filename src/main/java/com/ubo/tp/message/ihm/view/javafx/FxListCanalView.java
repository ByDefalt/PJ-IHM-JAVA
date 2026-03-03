package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Liste défilante de canaux avec champ de recherche — JavaFX.
 */
public class FxListCanalView extends VBox implements View {

    private final ViewContext viewContext;
    private final VBox canalsBox    = new VBox(4);
    private final TextField searchField = new TextField();
    private final List<FxCanalView> allCanals = new ArrayList<>();

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
        if (viewContext.logger() != null) viewContext.logger().debug("FxListCanalView initialisée");
    }

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

