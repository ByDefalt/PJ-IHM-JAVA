package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * Zone de saisie d'un message — JavaFX.
 * Entrée valide avec Entrée (Shift+Entrée = retour à la ligne).
 */
public class FxInputMessageView extends HBox implements View {

    private final ViewContext viewContext;
    private final TextArea inputField = new TextArea();
    private final Button sendButton   = new Button("Envoyer");
    private Runnable onSendRequested;

    public FxInputMessageView(ViewContext viewContext) {
        this.viewContext = viewContext;
        setPadding(new Insets(8));
        setSpacing(8);
        setBackground(new Background(new BackgroundFill(Color.rgb(47, 49, 54), CornerRadii.EMPTY, Insets.EMPTY)));

        inputField.setPromptText("Écrire un message…");
        inputField.setPrefRowCount(1);
        inputField.setWrapText(true);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        inputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER && !e.isShiftDown()) {
                e.consume();
                fireSend();
            }
        });

        sendButton.setOnAction(e -> fireSend());

        getChildren().addAll(inputField, sendButton);
        if (viewContext.logger() != null) viewContext.logger().debug("FxInputMessageView initialisée");
    }

    private void fireSend() {
        if (viewContext.logger() != null) viewContext.logger().debug("Envoi demandé (FX)");
        if (onSendRequested != null) onSendRequested.run();
    }

    public void setOnSendRequested(Runnable listener) { this.onSendRequested = listener; }

    public String getText()   { return inputField.getText(); }

    public void clearText() {
        inputField.clear();
        inputField.requestFocus();
    }
}

