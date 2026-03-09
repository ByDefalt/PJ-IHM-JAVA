package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController.ChannelEditCallback;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Représentation visuelle d'un canal — JavaFX.
 * Un bouton edit apparait au survol pour acceder aux options du canal.
 */
public class FxCanalView extends HBox implements View {

    private static final Color BG_NORMAL   = Color.rgb(54, 57, 63);
    private static final Color BG_HOVER    = Color.rgb(72, 76, 84);
    private static final Color PUBLIC_CLR  = Color.rgb(88, 101, 242);
    private static final Color PRIVATE_CLR = Color.rgb(250, 166, 26);

    private Channel channel;
    private int unreadCount = 0;
    private Label unreadBadge;

    public FxCanalView(ViewContext viewContext, Channel channel,
                       ChannelEditCallback onEdit, boolean isOwner, Supplier<List<User>> allUsersSupplier) {
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

        // Badge messages non lus (caché par défaut)
        unreadBadge = new Label("0");
        unreadBadge.setFont(Font.font("Arial", FontWeight.BOLD, 9));
        unreadBadge.setTextFill(Color.WHITE);
        unreadBadge.setMouseTransparent(false);
        unreadBadge.setVisible(false);
        unreadBadge.setMinWidth(16);
        unreadBadge.setMinHeight(16);
        unreadBadge.setAlignment(Pos.CENTER);
        unreadBadge.setPadding(new Insets(1, 4, 1, 4));
        unreadBadge.setBackground(new Background(new BackgroundFill(
                Color.rgb(240, 71, 71), new CornerRadii(8), Insets.EMPTY)));
        unreadBadge.setOnMouseClicked(e -> { e.consume(); clearUnread(); });
        getChildren().add(unreadBadge);

        if (isPrivate && onEdit != null) {
            Label editBtn = new Label("\u270F");
            editBtn.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            editBtn.setTextFill(Color.rgb(180, 180, 200));
            editBtn.setCursor(Cursor.HAND);
            editBtn.setOpacity(0);
            editBtn.setPadding(new Insets(0, 0, 0, 6));
            editBtn.setMouseTransparent(false);
            Tooltip.install(editBtn, new Tooltip("Options du canal"));

            editBtn.setOnMouseClicked(e -> {
                e.consume();
                // Evaluer la liste fraîche au moment du clic
                showEditMenu(editBtn, onEdit, isOwner, allUsersSupplier.get());
            });

            getChildren().add(editBtn);

            setOnMouseEntered(e -> {
                setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY)));
                editBtn.setOpacity(1);
            });
            setOnMouseExited(e -> {
                setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY)));
                editBtn.setOpacity(0);
            });
        } else {
            setOnMouseEntered(e -> setBackground(new Background(new BackgroundFill(BG_HOVER, new CornerRadii(6), Insets.EMPTY))));
            setOnMouseExited(e -> setBackground(new Background(new BackgroundFill(BG_NORMAL, new CornerRadii(6), Insets.EMPTY))));
        }

        if (viewContext.logger() != null) viewContext.logger().debug("FxCanalView initialisée : " + channel.getName());
    }

    private void showEditMenu(Label anchor, ChannelEditCallback onEdit,
                              boolean isOwner, List<User> allUsers) {
        ContextMenu menu = new ContextMenu();

        // ── Quitter / Supprimer ──────────────────────────────────────────
        if (isOwner) {
            MenuItem deleteItem = new MenuItem("🗑  Supprimer le canal");
            deleteItem.setStyle("-fx-text-fill: #f04747;");
            deleteItem.setOnAction(ev -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                        "Supprimer définitivement le canal « " + channel.getName() + " » ?",
                        ButtonType.YES, ButtonType.NO);
                confirm.setTitle("Confirmation");
                confirm.setHeaderText(null);
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES)
                    onEdit.onDelete(channel);
            });
            menu.getItems().add(deleteItem);
        } else {
            MenuItem leaveItem = new MenuItem("🚪  Quitter le canal");
            leaveItem.setStyle("-fx-text-fill: #dc9650;");
            leaveItem.setOnAction(ev -> onEdit.onLeave(channel));
            menu.getItems().add(leaveItem);
        }

        // ── Gestion des membres (propriétaire seulement) ─────────────────
        if (isOwner) {
            menu.getItems().add(new SeparatorMenuItem());

            // Ajouter un membre
            List<User> currentMembers = channel.getUsers();
            List<User> addable = (allUsers == null) ? Collections.emptyList() :
                    allUsers.stream().filter(u -> !currentMembers.contains(u)).toList();

            Menu addMenu = new Menu("➕  Ajouter un membre");
            if (addable.isEmpty()) {
                MenuItem none = new MenuItem("(aucun utilisateur disponible)");
                none.setDisable(true);
                addMenu.getItems().add(none);
            } else {
                for (User u : addable) {
                    MenuItem item = new MenuItem(u.getName() + " (@" + u.getUserTag() + ")");
                    item.setOnAction(ev -> onEdit.onAddUser(channel, u));
                    addMenu.getItems().add(item);
                }
            }
            menu.getItems().add(addMenu);

            // Retirer un membre
            Menu removeMenu = new Menu("➖  Retirer un membre");
            if (currentMembers.isEmpty()) {
                MenuItem none = new MenuItem("(aucun membre)");
                none.setDisable(true);
                removeMenu.getItems().add(none);
            } else {
                for (User u : currentMembers) {
                    MenuItem item = new MenuItem(u.getName() + " (@" + u.getUserTag() + ")");
                    item.setOnAction(ev -> onEdit.onRemoveUser(channel, u));
                    removeMenu.getItems().add(item);
                }
            }
            menu.getItems().add(removeMenu);
        }

        menu.show(anchor, javafx.geometry.Side.BOTTOM, 0, 0);
    }

    public Channel getChannel() {
        return channel;
    }

    /** Met à jour le canal stocké (membres, nom) sans recréer la vue. */
    public void updateChannel(Channel updated) {
        this.channel = updated;
    }

    /** Incrémente le compteur de messages non lus et affiche le badge. */
    public void incrementUnread() {
        unreadCount++;
        String text = unreadCount > 99 ? "99+" : String.valueOf(unreadCount);
        unreadBadge.setText(text);
        unreadBadge.setVisible(true);
    }

    /** Remet le compteur à zéro et masque le badge. */
    public void clearUnread() {
        unreadCount = 0;
        unreadBadge.setVisible(false);
    }
}
