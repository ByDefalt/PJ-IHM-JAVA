package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.utils.EmojiBinders;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Bulle représentant un message — JavaFX.
 * Affiche un menu contextuel (clic droit) pour supprimer le message si autorisé.
 */
public class FxMessageView extends HBox implements View {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH'h'mm").withLocale(Locale.FRANCE);
    private static final Color BG_NORMAL = Color.rgb(54, 57, 63);
    private static final Color BG_HOVER = Color.rgb(72, 76, 84);

    private final Message message;
    private final ViewContext viewContext;
    private TextFlow contentArea;

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

        VBox body = createBody(message);
        getChildren().addAll(body);

        ContextMenu ctx = createContextMenu(onDeleteSupplier);
        installContextMenuHandlers(body, ctx, onDeleteSupplier);

        // Hover visual
        setOnMouseEntered(e -> setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY))));
        setOnMouseExited(e -> setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY))));
    }

    private VBox createBody(Message message) {
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

        HBox header = buildHeader(senderName, timeStr);

        contentArea = new TextFlow();
        contentArea.setLineSpacing(2);
        contentArea.setPrefWidth(400);
        contentArea.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(contentArea, Priority.ALWAYS);
        contentArea.prefWidthProperty().bind(body.widthProperty());
        buildTextFlow(contentArea, message.getText());

        body.getChildren().addAll(header, contentArea);
        return body;
    }

    private HBox buildHeader(String senderName, String timeStr) {
        Label authorLabel = new Label(senderName);
        authorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        authorLabel.setTextFill(Color.rgb(88, 101, 242));
        authorLabel.setMouseTransparent(true);

        Label timeLabel = new Label(timeStr);
        timeLabel.setFont(Font.font("Arial", 10));
        timeLabel.setTextFill(Color.rgb(142, 146, 151));
        timeLabel.setMouseTransparent(true);

        HBox header = new HBox(8, authorLabel, timeLabel);
        header.setMouseTransparent(false);
        return header;
    }

    private ContextMenu createContextMenu(Supplier<Consumer<Message>> onDeleteSupplier) {
        ContextMenu ctx = new ContextMenu();
        MenuItem del = new MenuItem("Supprimer le message");
        del.setOnAction(ev -> {
            Consumer<Message> cb = onDeleteSupplier == null ? null : onDeleteSupplier.get();
            if (cb != null) cb.accept(message);
        });
        ctx.getItems().add(del);
        return ctx;
    }

    private void installContextMenuHandlers(VBox body, ContextMenu ctx, Supplier<Consumer<Message>> onDeleteSupplier) {
        MenuItem del = ctx.getItems().isEmpty() ? null : ctx.getItems().get(0);
        EventHandler<ContextMenuEvent> ctxHandler = e -> {
            Consumer<Message> cb = onDeleteSupplier == null ? null : onDeleteSupplier.get();
            boolean disabled = (cb == null);
            if (del != null) del.setDisable(disabled);
            if (viewContext != null && viewContext.logger() != null)
                viewContext.logger().debug("FxMessageView.contextMenuRequested for message=" + message.getUuid() + " disabled=" + disabled);
            ctx.show(this, e.getScreenX(), e.getScreenY());
            e.consume();
        };
        this.setOnContextMenuRequested(ctxHandler);
        body.setOnContextMenuRequested(ctxHandler);
        // header and contentArea set inside createBody; attach handlers if present
        this.setOnMouseClicked(e -> handleMouseClicked(e, ctx, onDeleteSupplier));
        body.setOnMouseClicked(e -> handleMouseClicked(e, ctx, onDeleteSupplier));
    }

    private void handleMouseClicked(MouseEvent e, ContextMenu ctx, Supplier<Consumer<Message>> onDeleteSupplier) {
        if (e.getButton() == MouseButton.SECONDARY) {
            MenuItem del = ctx.getItems().isEmpty() ? null : ctx.getItems().get(0);
            Consumer<Message> cb = onDeleteSupplier == null ? null : onDeleteSupplier.get();
            boolean disabled = (cb == null);
            if (del != null) del.setDisable(disabled);
            if (viewContext != null && viewContext.logger() != null)
                viewContext.logger().debug("FxMessageView.mouseClicked(SECONDARY) for message=" + message.getUuid() + " disabled=" + disabled);
            ctx.show(this, e.getScreenX(), e.getScreenY());
            e.consume();
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

    public Message getMessage() {
        return message;
    }

    public void updateContent(Message updated) {
        buildTextFlow(contentArea, updated.getText());
    }

    private void buildTextFlow(TextFlow flow, String text) {
        flow.getChildren().clear();
        if (text == null || text.isEmpty()) return;
        String raw = text;
        java.util.regex.Pattern codeOnlyPattern = java.util.regex.Pattern.compile("^(?:(:\\w+:)\\s*)+$");
        boolean onlyEmojiCodes = codeOnlyPattern.matcher(raw.trim()).matches();
        java.util.regex.Pattern codeFinder = java.util.regex.Pattern.compile("(:\\w+:)");
        java.util.regex.Matcher cm = codeFinder.matcher(raw);
        int codeCount = 0;
        while (cm.find()) codeCount++;

        int imgSize = 16;
        if (onlyEmojiCodes) imgSize = (codeCount == 1) ? 48 : 32;

        flow.setTextAlignment(TextAlignment.LEFT);
        flow.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(flow, Priority.ALWAYS);

        Color normalColor = Color.web("#DCDEDF");
        Color mentionColor = Color.web("#5865F2");

        if (onlyEmojiCodes) {
            renderOnlyEmoji(flow, raw, imgSize, normalColor);
            return;
        }
        renderMixedContent(flow, raw, normalColor, mentionColor);
    }

    private void renderOnlyEmoji(TextFlow flow, String raw, int imgSize, Color normalColor) {
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
                        Text spacer = new Text(" ");
                        spacer.setStyle("-fx-font-size: " + (imgSize / 2) + "px;");
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
    }

    private void renderMixedContent(TextFlow flow, String raw, Color normalColor, Color mentionColor) {
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

}
