package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Représentation visuelle d'un utilisateur — JavaFX.
 * Affiche une pastille colorée indiquant le statut de connexion.
 */
public class FxUserView extends HBox implements View {

    private static final Color BG_NORMAL = Color.rgb(54, 57, 63);
    private static final Color BG_HOVER = Color.rgb(72, 76, 84);
    private static final Color ONLINE_CLR = Color.rgb(35, 165, 90);
    private static final Color OFFLINE_CLR = Color.rgb(116, 127, 141);
    private final Label nameLabel;
    private final Label tagLabel;
    private final Circle statusDot;
    private User user;

    public FxUserView(ViewContext viewContext, User user) {
        this.user = user;
        setPadding(new Insets(6, 8, 6, 8));
        setSpacing(8);
        setAlignment(Pos.CENTER_LEFT);
        setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY)));
        setCursor(Cursor.HAND);

        // ── Pastille statut ──
        statusDot = new Circle(5);
        statusDot.setMouseTransparent(true);

        // ── Infos ──
        nameLabel = new Label(user.getName() != null ? user.getName() : "");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLabel.setTextFill(Color.rgb(220, 221, 222));
        nameLabel.setMouseTransparent(true);

        tagLabel = new Label("@" + user.getUserTag());
        tagLabel.setFont(Font.font("Arial", 10));
        tagLabel.setTextFill(Color.rgb(142, 146, 151));
        tagLabel.setMouseTransparent(true);

        VBox info = new VBox(1, nameLabel, tagLabel);
        info.setMouseTransparent(true);

        getChildren().addAll(statusDot, info);

        // Appliquer l'état initial
        applyStatus(user.isOnline());

        setOnMouseEntered(e -> setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY))));
        setOnMouseExited(e -> setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY))));

        if (viewContext.logger() != null) viewContext.logger().debug("FxUserView initialisée : " + user.getName());
    }

    private void applyStatus(boolean online) {
        statusDot.setFill(online ? ONLINE_CLR : OFFLINE_CLR);
        statusDot.setEffect(online
                ? new javafx.scene.effect.DropShadow(6, ONLINE_CLR)
                : null);
    }

    public User getUser() {
        return user;
    }

    public void updateUser(User updated) {
        this.user = updated;
        nameLabel.setText(updated.getName() != null ? updated.getName() : "");
        tagLabel.setText("@" + updated.getUserTag());
        applyStatus(updated.isOnline());
    }
}



