package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Vue principale du chat (style Discord) — JavaFX.
 * Sidebar avec onglets Canaux / Utilisateurs + zone messages + saisie.
 */
public class FxChatMainView extends BorderPane implements View {

    private final ViewContext viewContext;

    public FxChatMainView(
            ViewContext viewContext,
            FxListCanalView listCanalView,
            FxListUserView listUserView,
            FxListMessageView listMessageView,
            FxInputMessageView inputMessageView
    ) {
        this.viewContext = viewContext;
        setBackground(new Background(new BackgroundFill(Color.rgb(54, 57, 63), CornerRadii.EMPTY, Insets.EMPTY)));

        // --- Sidebar ---
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("-fx-background-color: rgb(47,49,54);");
        tabPane.setPrefWidth(300);
        tabPane.setMinWidth(220);

        Tab canalTab = new Tab("Canaux", listCanalView);
        Tab userTab = new Tab("Utilisateurs", listUserView);
        tabPane.getTabs().addAll(canalTab, userTab);

        setLeft(tabPane);

        // --- Zone droite : messages + saisie ---
        VBox rightPanel = new VBox();
        rightPanel.setBackground(new Background(new BackgroundFill(Color.rgb(54, 57, 63), CornerRadii.EMPTY, Insets.EMPTY)));
        VBox.setVgrow(listMessageView, Priority.ALWAYS);
        rightPanel.getChildren().addAll(listMessageView, inputMessageView);

        setCenter(rightPanel);

        if (viewContext.logger() != null) viewContext.logger().debug("FxChatMainView initialisée");
    }
}

