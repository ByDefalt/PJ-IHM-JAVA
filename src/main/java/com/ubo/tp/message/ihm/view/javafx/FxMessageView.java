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
import java.util.function.Supplier;
import javafx.scene.input.MouseButton;

/**
 * Bulle représentant un message — JavaFX.
 * Affiche un menu contextuel (clic droit) pour supprimer le message si autorisé.
 */
public class FxMessageView extends HBox implements View {

     private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
             .ofPattern("dd/MM/yyyy HH'h'mm").withLocale(Locale.FRANCE);
     private static final Color BG_NORMAL = Color.rgb(54, 57, 63);
     private static final Color BG_HOVER  = Color.rgb(72, 76, 84);

     private final Message message;
     private final TextArea contentArea;
     private final ViewContext viewContext;

    public FxMessageView(ViewContext viewContext, Message message) {
        this(viewContext, message, null, false);
    }

     public FxMessageView(ViewContext viewContext, Message message,
                          Supplier<Consumer<Message>> onDeleteSupplier, boolean canDelete) {
        this.viewContext = viewContext;
         this.message = message;
         if (this.viewContext != null && this.viewContext.logger() != null) {
            Consumer<Message> cb = onDeleteSupplier == null ? null : onDeleteSupplier.get();
            this.viewContext.logger().debug("FxMessageView created for message=" + message.getUuid()
                    + " canDelete=" + canDelete + " onDeletePresent=" + (cb != null));
         }
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
        // laisser authorLabel transmettre les événements au header
        authorLabel.setMouseTransparent(true);

        Label timeLabel = new Label(timeStr);
        timeLabel.setFont(Font.font("Arial", 10));
        timeLabel.setTextFill(Color.rgb(142, 146, 151));
        timeLabel.setMouseTransparent(true);

        HBox header = new HBox(8, authorLabel, timeLabel);
        // header doit recevoir les événements pour ouvrir le menu si l'utilisateur clique dessus
        header.setMouseTransparent(false);

        contentArea = new TextArea(message.getText());
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setPrefRowCount(2);
        // Le TextArea doit recevoir le clic droit pour ouvrir le ContextMenu. Ne pas le rendre mouseTransparent.
        contentArea.setMouseTransparent(false);
        // Ne pas recevoir le focus clavier lors de clics pour conserver le comportement read-only
        contentArea.setFocusTraversable(false);
        contentArea.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent;");

        body.getChildren().addAll(header, contentArea);

        // Pas d'icône : suppression via menu contextuel (clic droit)
        getChildren().addAll(body);

        // ContextMenu sur clic droit pour la suppression (sans confirmation)
        // On attache toujours le menu, mais on active/désactive l'item au moment de l'ouverture
        javafx.scene.control.ContextMenu ctx = new javafx.scene.control.ContextMenu();
        javafx.scene.control.MenuItem del = new javafx.scene.control.MenuItem("Supprimer le message");
        del.setOnAction(ev -> {
            Consumer<Message> cb = onDeleteSupplier == null ? null : onDeleteSupplier.get();
            if (cb != null) cb.accept(message);
        });
        ctx.getItems().add(del);

        // Supporter à la fois l'événement ContextMenuRequested (platform) et le clic droit classique
        // Attacher le menu au HBox (this) et aussi aux sous-nœuds pour éviter les cas
        // où un enfant intercepte l'événement et empêche l'ouverture du menu.
        javafx.event.EventHandler<javafx.scene.input.ContextMenuEvent> ctxHandler = e -> {
            Consumer<Message> cb = onDeleteSupplier == null ? null : onDeleteSupplier.get();
            boolean disabled = (cb == null);
            del.setDisable(disabled);
            if (viewContext != null && viewContext.logger() != null)
                viewContext.logger().debug("FxMessageView.contextMenuRequested for message=" + message.getUuid() + " disabled=" + disabled);
            ctx.show(this, e.getScreenX(), e.getScreenY());
            e.consume();
        };
        this.setOnContextMenuRequested(ctxHandler);
        body.setOnContextMenuRequested(ctxHandler);
        header.setOnContextMenuRequested(ctxHandler);
        contentArea.setOnContextMenuRequested(ctxHandler);

        javafx.event.EventHandler<javafx.scene.input.MouseEvent> mouseHandler = e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                Consumer<Message> cb = onDeleteSupplier == null ? null : onDeleteSupplier.get();
                boolean disabled = (cb == null);
                del.setDisable(disabled);
                if (viewContext != null && viewContext.logger() != null)
                    viewContext.logger().debug("FxMessageView.mouseClicked(SECONDARY) for message=" + message.getUuid() + " disabled=" + disabled);
                ctx.show(this, e.getScreenX(), e.getScreenY());
                e.consume();
            }
        };
        this.setOnMouseClicked(mouseHandler);
        body.setOnMouseClicked(mouseHandler);
        header.setOnMouseClicked(mouseHandler);
        contentArea.setOnMouseClicked(mouseHandler);

        // ── Hover ────────────────────────────────────────────────────────
        setOnMouseEntered(e -> {
            setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY)));
        });
        setOnMouseExited(e -> {
            setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY)));
        });
    }

    public Message getMessage() { return message; }

    public void updateContent(Message updated) {
        contentArea.setText(updated.getText());
    }
}
