package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Liste défilante de messages — JavaFX.
 * Le scroll est automatiquement positionné en bas après chaque reconstruction.
 */
public class FxListMessageView extends VBox implements View {

    private final ViewContext viewContext;
    private final VBox messagesBox = new VBox(4);
    private final ScrollPane scrollPane;

    public FxListMessageView(ViewContext viewContext) {
        this.viewContext = viewContext;
        setPadding(new Insets(8));
        setBackground(new Background(new BackgroundFill(Color.rgb(54, 57, 63), CornerRadii.EMPTY, Insets.EMPTY)));

        messagesBox.setPadding(new Insets(4));

        scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().add(scrollPane);
        if (viewContext.logger() != null) viewContext.logger().debug("FxListMessageView initialisée");
    }

    /**
     * Reconstruit entièrement la liste à partir des vues ordonnées fournies.
     */
    public void rebuildUI(List<FxMessageView> ordered) {
        messagesBox.getChildren().setAll(ordered);
        // Scroll vers le bas après le rendu
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
        if (viewContext.logger() != null)
            viewContext.logger().debug("FxListMessageView reconstruite : " + ordered.size() + " message(s)");
    }

    public void updateMessageUI(FxMessageView view, com.ubo.tp.message.datamodel.Message updated) {
        view.updateContent(updated);
    }
}

