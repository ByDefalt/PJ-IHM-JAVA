package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Bulle représentant un message — JavaFX.
 * Affiche un bouton 🗑 au survol si l'utilisateur connecté est l'auteur.
 */
public class FxMessageView extends HBox implements View {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH'h'mm").withLocale(Locale.FRANCE);
    private static final Color BG_NORMAL = Color.rgb(54, 57, 63);
    private static final Color BG_HOVER  = Color.rgb(72, 76, 84);

    private final Message message;
    private final TextArea contentArea;

    public FxMessageView(ViewContext viewContext, Message message) {
        this(viewContext, message, null, false);
    }

    public FxMessageView(ViewContext viewContext, Message message,
                         Consumer<Message> onDelete, boolean canDelete) {
        this.message = message;
        setAlignment(Pos.TOP_LEFT);
        setPadding(new Insets(4));
        setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY)));

        // ── Corps du message ─────────────────────────────────────────────
        VBox body = new VBox(2);
        body.setMouseTransparent(false);
        HBox.setHgrow(body, Priority.ALWAYS);

        String senderName = message.getSender() != null ? message.getSender().getName() : "?";
        String timeStr = DATE_FMT.format(
                Instant.ofEpochSecond(message.getEmissionDate()).atZone(ZoneId.systemDefault()));

        Label authorLabel = new Label(senderName);
        authorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        authorLabel.setTextFill(Color.rgb(88, 101, 242));
        authorLabel.setMouseTransparent(true);

        Label timeLabel = new Label(timeStr);
        timeLabel.setFont(Font.font("Arial", 10));
        timeLabel.setTextFill(Color.rgb(142, 146, 151));
        timeLabel.setMouseTransparent(true);

        HBox header = new HBox(8, authorLabel, timeLabel);
        header.setMouseTransparent(true);

        contentArea = new TextArea(message.getText());
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(2);
        contentArea.setMouseTransparent(true);
        contentArea.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

        body.getChildren().addAll(header, contentArea);

        // ── Bouton suppression ───────────────────────────────────────────
        Label deleteBtn = new Label("\uD83D\uDDD1"); // 🗑
        deleteBtn.setFont(Font.font("Arial", 13));
        deleteBtn.setTextFill(Color.rgb(240, 71, 71));
        deleteBtn.setCursor(Cursor.HAND);
        deleteBtn.setOpacity(0);
        deleteBtn.setPadding(new Insets(2, 6, 2, 6));
        deleteBtn.setMouseTransparent(!canDelete);
        Tooltip.install(deleteBtn, new Tooltip("Supprimer le message"));

        if (canDelete && onDelete != null) {
            deleteBtn.setOnMouseClicked(e -> {
                e.consume();
                onDelete.accept(message);
            });
        }

        getChildren().addAll(body, deleteBtn);

        // ── Hover ────────────────────────────────────────────────────────
        setOnMouseEntered(e -> {
            setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY)));
            if (canDelete) deleteBtn.setOpacity(1);
        });
        setOnMouseExited(e -> {
            setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY)));
            deleteBtn.setOpacity(0);
        });
    }

    public Message getMessage() { return message; }

    public void updateContent(Message updated) {
        contentArea.setText(updated.getText());
    }
}

