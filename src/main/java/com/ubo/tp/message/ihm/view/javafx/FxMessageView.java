package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.ubo.tp.message.utils.EmojiBinders;
import javafx.scene.layout.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javafx.scene.input.MouseButton;
import javafx.event.EventHandler;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;

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
     private TextFlow contentArea;
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

        String senderName = "?";
        if (message.getSender() != null) {
            String tag = message.getSender().getUserTag() != null ? message.getSender().getUserTag() : "";
            String name = message.getSender().getName() != null ? message.getSender().getName() : "";
            senderName = "@" + tag + " - " + name;
        }
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

        contentArea = new TextFlow();
        contentArea.setLineSpacing(2);
        contentArea.setPrefWidth(400); // largeur initiale
        contentArea.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(contentArea, Priority.ALWAYS);
        // Lier directement la largeur préférée du TextFlow à la largeur du VBox 'body'
        contentArea.prefWidthProperty().bind(body.widthProperty());
         // Construire le TextFlow en colorant les mentions
         buildTextFlow(contentArea, message.getText());

        body.getChildren().addAll(header, contentArea);

        // Pas d'icône : suppression via menu contextuel (clic droit)
        getChildren().addAll(body);

        // ContextMenu sur clic droit pour la suppression (sans confirmation)
        // On attache toujours le menu, mais on active/désactive l'item au moment de l'ouverture
        ContextMenu ctx = new ContextMenu();
        MenuItem del = new MenuItem("Supprimer le message");
        del.setOnAction(ev -> {
            Consumer<Message> cb = onDeleteSupplier == null ? null : onDeleteSupplier.get();
            if (cb != null) cb.accept(message);
        });
        ctx.getItems().add(del);

        // Supporter à la fois l'événement ContextMenuRequested (platform) et le clic droit classique
        // Attacher le menu au HBox (this) et aussi aux sous-nœuds pour éviter les cas
        // où un enfant intercepte l'événement et empêche l'ouverture du menu.
        EventHandler<ContextMenuEvent> ctxHandler = e -> {
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

        EventHandler<MouseEvent> mouseHandler = e -> {
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
        buildTextFlow(contentArea, updated.getText());
    }

    private void buildTextFlow(TextFlow flow, String text) {
         flow.getChildren().clear();
         if (text == null || text.isEmpty()) return;
         String raw = text;
        // Detect emoji codes count and whether message contains only codes
        java.util.regex.Pattern codeOnlyPattern = java.util.regex.Pattern.compile("^(?:(:\\w+:)\\s*)+$");
        boolean onlyEmojiCodes = codeOnlyPattern.matcher(raw.trim()).matches();
        java.util.regex.Pattern codeFinder = java.util.regex.Pattern.compile("(:\\w+:)");
        java.util.regex.Matcher cm = codeFinder.matcher(raw);
        int codeCount = 0;
        while (cm.find()) codeCount++;

        int imgSize = 16;
        if (onlyEmojiCodes) imgSize = (codeCount == 1) ? 48 : 32;

        // Ensure left alignment and that flow expands to full width so left alignment is visible
        flow.setTextAlignment(TextAlignment.LEFT);
        flow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(flow, Priority.ALWAYS);

        Color normalColor = Color.web("#DCDEDF");
        Color mentionColor = Color.web("#5865F2");

        if (onlyEmojiCodes) {
            // Add images left-aligned, with small gap
            java.util.regex.Pattern p = java.util.regex.Pattern.compile("(:\\w+:)");
            java.util.regex.Matcher m = p.matcher(raw);
            boolean first = true;
            while (m.find()) {
                String code = m.group(1);
                String url = EmojiBinders.getEmojiImageUrl(code);
                if (url != null) {
                    try {
                        Image img = new Image(url, imgSize, imgSize, true, true);
                        ImageView iv = new ImageView(img);
                        iv.setFitWidth(imgSize);
                        iv.setFitHeight(imgSize);
                        if (!first) {
                            // add small spacing
                            Text spacer = new Text(" ");
                            spacer.setStyle("-fx-font-size: " + (imgSize/2) + "px;");
                            flow.getChildren().add(spacer);
                        }
                        flow.getChildren().add(iv);
                    } catch (Exception ex) {
                        String uni = EmojiBinders.replaceEmojiCodesUnicode(code);
                        uni = insertZWSEveryN(uni, 40);
                        Text t = new Text(uni);
                        t.setFill(normalColor);
                        t.setStyle("-fx-font-size: " + (imgSize) + "px;");
                        flow.getChildren().add(t);
                    }
                } else {
                    String uni = EmojiBinders.replaceEmojiCodesUnicode(code);
                    uni = insertZWSEveryN(uni, 40);
                    Text t = new Text(uni);
                    t.setFill(normalColor);
                    t.setStyle("-fx-font-size: " + (imgSize) + "px;");
                    flow.getChildren().add(t);
                }
                first = false;
            }
            return;
        }

        // Mixed content: keep inline text and small emoji sized to ~13px
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("(:\\w+:)|(@\\w+)");
        java.util.regex.Matcher m = p.matcher(raw);
        int last = 0;
        int inlineSize = 13;
        while (m.find()) {
            if (m.start() > last) {
                String part = raw.substring(last, m.start());
                if (!part.isEmpty()) {
                    part = insertZWSEveryN(part, 40);
                    Text t = new Text(part);
                    t.setFill(normalColor);
                    t.setStyle("-fx-font-size: " + inlineSize + "px;");
                    flow.getChildren().add(t);
                }
            }
            String emojiCode = m.group(1);
            String mention = m.group(2);
            if (emojiCode != null) {
                String url = EmojiBinders.getEmojiImageUrl(emojiCode);
                if (url != null) {
                    try {
                        Image img = new Image(url, inlineSize, inlineSize, true, true);
                        ImageView iv = new ImageView(img);
                        iv.setFitWidth(inlineSize);
                        iv.setFitHeight(inlineSize);
                        flow.getChildren().add(iv);
                    } catch (Exception ex) {
                        String uni = EmojiBinders.replaceEmojiCodesUnicode(emojiCode);
                        uni = insertZWSEveryN(uni, 40);
                        Text t = new Text(uni);
                        t.setFill(normalColor);
                        t.setStyle("-fx-font-size: " + inlineSize + "px;");
                        flow.getChildren().add(t);
                    }
                } else {
                    String uni = EmojiBinders.replaceEmojiCodesUnicode(emojiCode);
                    uni = insertZWSEveryN(uni, 40);
                    Text t = new Text(uni);
                    t.setFill(normalColor);
                    t.setStyle("-fx-font-size: " + inlineSize + "px;");
                    flow.getChildren().add(t);
                }
            } else if (mention != null) {
                Text tm = new Text(mention);
                tm.setFill(mentionColor);
                tm.setStyle("-fx-font-size: " + inlineSize + "px; -fx-font-weight: bold;");
                flow.getChildren().add(tm);
            }
            last = m.end();
        }
        if (last < raw.length()) {
            String part = raw.substring(last);
            if (!part.isEmpty()) {
                part = insertZWSEveryN(part, 40);
                Text t = new Text(part);
                t.setFill(normalColor);
                t.setStyle("-fx-font-size: " + inlineSize + "px;");
                flow.getChildren().add(t);
            }
        }
    }

    // Insert zero-width space \u200B every n characters in long sequences without whitespace to enable wrapping in TextFlow
    private static String insertZWSEveryN(String s, int n) {
        if (s == null || s.length() <= n) return s;
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            sb.append(c);
            count++;
            if (Character.isWhitespace(c)) {
                count = 0;
            } else if (count >= n) {
                sb.append('\u200B');
                count = 0;
            }
        }
        return sb.toString();
    }

}
