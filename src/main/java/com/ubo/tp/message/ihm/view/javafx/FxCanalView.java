package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

/**
 * Représentation visuelle d'un canal — JavaFX.
 * Affiche # (public) ou 🔒 (privé) avec un badge coloré.
 * Une croix rouge apparaît au survol si l'utilisateur peut quitter le canal.
 */
public class FxCanalView extends HBox implements View {

    private static final Color BG_NORMAL   = Color.rgb(54, 57, 63);
    private static final Color BG_HOVER    = Color.rgb(72, 76, 84);
    private static final Color PUBLIC_CLR  = Color.rgb(88, 101, 242);
    private static final Color PRIVATE_CLR = Color.rgb(250, 166, 26);

    private final Channel channel;

    public FxCanalView(ViewContext viewContext, Channel channel, Consumer<Channel> onLeave) {
        this.channel = channel;
        setPadding(new Insets(6, 8, 6, 8));
        setSpacing(7);
        setAlignment(Pos.CENTER_LEFT);
        setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY)));
        setCursor(Cursor.HAND);

        boolean isPrivate = channel.isPrivate();

        Label badge = new Label(isPrivate ? "🔒" : "#");
        badge.setFont(Font.font("Arial", FontWeight.BOLD, isPrivate ? 11 : 14));
        badge.setTextFill(isPrivate ? PRIVATE_CLR : PUBLIC_CLR);
        badge.setMouseTransparent(true);
        badge.setMinWidth(16);

        Label nameLabel = new Label(channel.getName() != null ? channel.getName() : "");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLabel.setTextFill(Color.rgb(220, 221, 222));
        nameLabel.setMouseTransparent(true);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label visibilityTag = new Label(isPrivate ? "privé" : "public");
        visibilityTag.setFont(Font.font("Arial", 9));
        visibilityTag.setTextFill(isPrivate ? PRIVATE_CLR : PUBLIC_CLR);
        visibilityTag.setOpacity(0.75);
        visibilityTag.setMouseTransparent(true);
        visibilityTag.setPadding(new Insets(1, 4, 1, 4));
        visibilityTag.setBackground(new Background(new BackgroundFill(
                (isPrivate ? PRIVATE_CLR : PUBLIC_CLR).deriveColor(0, 1, 1, 0.15),
                new CornerRadii(4), Insets.EMPTY)));

        getChildren().addAll(badge, nameLabel, spacer, visibilityTag);

        if (isPrivate && onLeave != null) {
            Label leaveBtn = new Label("✕");
            leaveBtn.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            leaveBtn.setTextFill(Color.rgb(240, 71, 71));
            leaveBtn.setCursor(Cursor.HAND);
            leaveBtn.setOpacity(0);
            leaveBtn.setPadding(new Insets(0, 0, 0, 6));
            leaveBtn.setMouseTransparent(false);
            Tooltip.install(leaveBtn, new Tooltip("Quitter le canal"));
            leaveBtn.setOnMouseClicked(e -> { e.consume(); onLeave.accept(channel); });

            getChildren().add(leaveBtn);

            setOnMouseEntered(e -> {
                setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY)));
                leaveBtn.setOpacity(1);
            });
            setOnMouseExited(e -> {
                setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY)));
                leaveBtn.setOpacity(0);
            });
        } else {
            setOnMouseEntered(e -> setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY))));
            setOnMouseExited(e -> setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY))));
        }

        if (viewContext.logger() != null) viewContext.logger().debug("FxCanalView initialisée : " + channel.getName());
    }

    public Channel getChannel() { return channel; }
}
