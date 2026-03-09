package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Liste défilante de messages — JavaFX.
 * Contient une barre de recherche en haut pour filtrer les messages (UI uniquement).
 */
public class FxListMessageView extends VBox implements View {

    private static final Color BG = Color.rgb(54, 57, 63);

    private final ViewContext viewContext;
    private final VBox messagesBox = new VBox(4);
    private final ScrollPane scrollPane;
    private final TextField searchField;

    /** Dernière liste complète fournie par le graphic controller. */
    private List<FxMessageView> allMessages = List.of();

    public FxListMessageView(ViewContext viewContext) {
        this.viewContext = viewContext;
        setBackground(new Background(new BackgroundFill(BG, CornerRadii.EMPTY, Insets.EMPTY)));

        // ── Barre de recherche ────────────────────────────────────────────
        searchField = new TextField();
        searchField.setPromptText("Rechercher un message…");
        searchField.setStyle(
                "-fx-background-color: #2f3136;" +
                "-fx-text-fill: #dcddde;" +
                "-fx-prompt-text-fill: #72767d;" +
                "-fx-border-color: #202225;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 4;"
        );
        VBox.setMargin(searchField, new Insets(8, 8, 4, 8));
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter(newVal));

        // ── Zone de messages défilante ────────────────────────────────────
        messagesBox.setPadding(new Insets(4));

        scrollPane = new ScrollPane(messagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(searchField, scrollPane);
        if (viewContext.logger() != null) viewContext.logger().debug("FxListMessageView initialisée");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // API publique
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Reconstruit entièrement la liste à partir des vues ordonnées fournies.
     * Le filtre de recherche est réappliqué immédiatement.
     */
    public void rebuildUI(List<FxMessageView> ordered) {
        allMessages = ordered == null ? List.of() : List.copyOf(ordered);
        applyFilter(searchField.getText());
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
        if (viewContext.logger() != null)
            viewContext.logger().debug("FxListMessageView reconstruite : " + allMessages.size() + " message(s)");
    }

    public void updateMessageUI(FxMessageView view, com.ubo.tp.message.datamodel.Message updated) {
        view.updateContent(updated);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Filtre interne
    // ─────────────────────────────────────────────────────────────────────────

    private void applyFilter(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        if (q.isEmpty()) {
            messagesBox.getChildren().setAll(allMessages);
        } else {
            List<FxMessageView> filtered = allMessages.stream()
                    .filter(mv -> mv.getMessage() != null
                            && mv.getMessage().getText() != null
                            && mv.getMessage().getText().toLowerCase().contains(q))
                    .toList();
            messagesBox.getChildren().setAll(filtered);
        }
    }
}
