package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Représentation visuelle d'un canal — JavaFX.
 */
public class FxCanalView extends HBox implements View {

    private static final Color BG_NORMAL = Color.rgb(54, 57, 63);
    private static final Color BG_HOVER = Color.rgb(72, 76, 84);

    private final Channel channel;

    public FxCanalView(ViewContext viewContext, Channel channel) {
        this.channel = channel;
        setPadding(new Insets(6));
        setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY)));
        setCursor(Cursor.HAND);

        Label name = new Label(channel.getName() != null ? "#" + channel.getName() : "");
        name.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        name.setTextFill(Color.rgb(220, 221, 222));
        name.setMouseTransparent(true);
        getChildren().add(name);

        setOnMouseEntered(e -> setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY))));
        setOnMouseExited(e -> setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY))));

        if (viewContext.logger() != null) viewContext.logger().debug("FxCanalView initialisée : " + channel.getName());
    }

    public Channel getChannel() {
        return channel;
    }
}

