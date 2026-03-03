package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Représentation visuelle d'un utilisateur — JavaFX.
 */
public class FxUserView extends VBox implements View {

    private static final Color BG_NORMAL = Color.rgb(54, 57, 63);
    private static final Color BG_HOVER  = Color.rgb(72, 76, 84);

    private final User user;
    private final Label nameLabel;

    public FxUserView(ViewContext viewContext, User user) {
        this.user = user;
        setPadding(new Insets(8));
        setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY)));
        setCursor(Cursor.HAND);

        nameLabel = new Label(user.getName() != null ? user.getName() : "");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLabel.setTextFill(Color.rgb(220, 221, 222));
        nameLabel.setMouseTransparent(true);
        getChildren().add(nameLabel);

        setOnMouseEntered(e -> setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY))));
        setOnMouseExited(e  -> setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY))));

        if (viewContext.logger() != null) viewContext.logger().debug("FxUserView initialisée : " + user.getName());
    }

    public User getUser() { return user; }

    public void updateUser(User updated) {
        nameLabel.setText(updated.getName() != null ? updated.getName() : "");
    }
}

