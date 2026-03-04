package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController.ChannelCreationCallback;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Bottom-sheet de création d'un nouveau canal — JavaFX.
 * Nom du canal · Canal privé · Sélection d'utilisateurs avec recherche.
 */
public class FxNewChannelBottomSheet extends StackPane {

    private static final Color OVERLAY = Color.rgb(0, 0, 0, 0.55);
    private static final Color SHEET_BG = Color.rgb(32, 34, 37);
    private static final Color INPUT_BG = Color.rgb(64, 68, 75);
    private static final Color ITEM_BG = Color.rgb(47, 49, 54);
    private static final Color ACCENT = Color.rgb(88, 101, 242);
    private static final Color MUTED = Color.rgb(185, 187, 190);

    private final VBox sheet;
    private final ViewContext viewContext;

    // Widgets principaux
    private final TextField nameField = new TextField();
    private final Label nameError = new Label();
    private final CheckBox privateCheck = new CheckBox("Canal privé");
    private final TextField userSearch = new TextField();
    private final VBox userListBox = new VBox(4);
    private final FlowPane chipsPane = new FlowPane(6, 6);
    private final Set<User> selectedUsers = new LinkedHashSet<>();
    private ChannelCreationCallback onConfirm;
    private List<User> availableUsers = new ArrayList<>();

    public FxNewChannelBottomSheet(ViewContext viewContext) {
        this.viewContext = viewContext;

        setBackground(new Background(new BackgroundFill(OVERLAY, CornerRadii.EMPTY, Insets.EMPTY)));
        setAlignment(Pos.BOTTOM_CENTER);
        setOnMouseClicked(e -> {
            if (e.getTarget() == this) hide();
        });

        sheet = new VBox(16);
        sheet.setBackground(new Background(new BackgroundFill(SHEET_BG, new CornerRadii(16, 16, 0, 0, false), Insets.EMPTY)));
        sheet.setPadding(new Insets(24, 32, 32, 32));
        sheet.setMaxWidth(560);
        sheet.setOnMouseClicked(javafx.scene.input.MouseEvent::consume);
        StackPane.setAlignment(sheet, Pos.BOTTOM_CENTER);

        sheet.getChildren().addAll(
                buildTitle(),
                new Separator(),
                buildNameSection(),
                buildPrivateRow(),
                buildUsersSection(),
                buildButtons()
        );
        getChildren().add(sheet);

        setVisible(false);
        setMouseTransparent(true);
        if (viewContext.logger() != null) viewContext.logger().debug("FxNewChannelBottomSheet initialisée");
    }

    // ── Sections ─────────────────────────────────────────────────────────────

    private Label buildTitle() {
        Label t = new Label("Créer un canal");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        t.setTextFill(Color.WHITE);
        return t;
    }

    private VBox buildNameSection() {
        Label lbl = miniLabel("NOM DU CANAL");

        styleInput(nameField);
        nameField.setPromptText("nouveau-canal");

        nameError.setTextFill(Color.rgb(240, 71, 71));
        nameError.setFont(Font.font("Arial", 11));
        nameError.setVisible(false);

        return new VBox(6, lbl, nameField, nameError);
    }

    private HBox buildPrivateRow() {
        privateCheck.setStyle("-fx-text-fill: rgb(220,221,222); -fx-font-size: 14;");
        HBox row = new HBox(privateCheck);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox buildUsersSection() {
        Label lbl = miniLabel("INVITER DES MEMBRES");

        styleInput(userSearch);
        userSearch.setPromptText("Rechercher un utilisateur…");
        userSearch.textProperty().addListener((obs, o, n) -> rebuildUserList(n));

        userListBox.setStyle("-fx-background-color: transparent;");
        ScrollPane scroll = new ScrollPane(userListBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(160);
        scroll.setMaxHeight(160);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        chipsPane.setPadding(new Insets(4, 0, 0, 0));

        return new VBox(8, lbl, userSearch, scroll, chipsPane);
    }

    private HBox buildButtons() {
        Button cancelBtn = makeButton("Annuler", "transparent", "rgb(185,187,190)", "transparent", "white");
        cancelBtn.setOnAction(e -> hide());

        Button createBtn = makeButton("Créer le canal", "rgb(88,101,242)", "white", "rgb(71,82,196)", "white");
        createBtn.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        createBtn.setOnAction(e -> handleCreate());

        nameField.setOnAction(e -> createBtn.fire());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return new HBox(12, spacer, cancelBtn, createBtn);
    }

    // ── Logique ──────────────────────────────────────────────────────────────

    private void handleCreate() {
        String raw = nameField.getText().trim().toLowerCase().replaceAll("\\s+", "-");
        if (raw.isEmpty()) {
            nameError.setText("Le nom du canal ne peut pas être vide.");
            nameError.setVisible(true);
            return;
        }
        if (!raw.matches("[a-z0-9\\-_]+")) {
            nameError.setText("Uniquement lettres, chiffres, tirets et underscores.");
            nameError.setVisible(true);
            return;
        }
        nameError.setVisible(false);
        if (onConfirm != null) onConfirm.onCreate(raw, privateCheck.isSelected(), new ArrayList<>(selectedUsers));
        resetForm();
        hide();
    }

    private void resetForm() {
        nameField.clear();
        nameError.setVisible(false);
        privateCheck.setSelected(false);
        selectedUsers.clear();
        userSearch.clear();
        rebuildUserList("");
        chipsPane.getChildren().clear();
    }

    private void rebuildUserList(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        userListBox.getChildren().clear();
        for (User u : availableUsers) {
            String display = (u.getName() != null ? u.getName() : "") + " @" + u.getUserTag();
            if (q.isEmpty() || display.toLowerCase().contains(q)) {
                userListBox.getChildren().add(buildUserRow(u));
            }
        }
    }

    private HBox buildUserRow(User user) {
        boolean sel = selectedUsers.contains(user);

        HBox row = new HBox(10);
        row.setPadding(new Insets(7, 10, 7, 10));
        row.setAlignment(Pos.CENTER_LEFT);
        row.setCursor(Cursor.HAND);
        row.setBackground(rowBg(sel));

        // Avatar
        String initial = (user.getName() != null && !user.getName().isEmpty())
                ? String.valueOf(user.getName().charAt(0)).toUpperCase() : "?";
        Label avatar = new Label(initial);
        avatar.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        avatar.setTextFill(Color.WHITE);
        avatar.setMinSize(30, 30);
        avatar.setMaxSize(30, 30);
        avatar.setAlignment(Pos.CENTER);
        avatar.setBackground(new Background(new BackgroundFill(ACCENT, new CornerRadii(15), Insets.EMPTY)));

        Label nameLbl = new Label(user.getName() != null ? user.getName() : "(sans nom)");
        nameLbl.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        nameLbl.setTextFill(Color.WHITE);

        Label tagLbl = new Label("@" + user.getUserTag());
        tagLbl.setFont(Font.font("Arial", 11));
        tagLbl.setTextFill(MUTED);

        VBox info = new VBox(2, nameLbl, tagLbl);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label check = new Label(sel ? "✓" : "");
        check.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        check.setTextFill(ACCENT);
        check.setMinWidth(20);

        row.getChildren().addAll(avatar, info, spacer, check);

        row.setOnMouseEntered(ev -> {
            if (!selectedUsers.contains(user))
                row.setBackground(new Background(new BackgroundFill(Color.rgb(64, 68, 75), new CornerRadii(6), Insets.EMPTY)));
        });
        row.setOnMouseExited(ev -> row.setBackground(rowBg(selectedUsers.contains(user))));

        row.setOnMouseClicked(ev -> {
            if (selectedUsers.contains(user)) selectedUsers.remove(user);
            else selectedUsers.add(user);
            rebuildUserList(userSearch.getText());
            refreshChips();
        });

        return row;
    }

    private Background rowBg(boolean selected) {
        Color c = selected ? Color.rgb(88, 101, 242, 0.30) : ITEM_BG;
        return new Background(new BackgroundFill(c, new CornerRadii(6), Insets.EMPTY));
    }

    private void refreshChips() {
        chipsPane.getChildren().clear();
        for (User u : selectedUsers) {
            HBox chip = new HBox(6);
            chip.setPadding(new Insets(3, 8, 3, 8));
            chip.setAlignment(Pos.CENTER_LEFT);
            chip.setBackground(new Background(new BackgroundFill(ACCENT, new CornerRadii(12), Insets.EMPTY)));

            Label chipName = new Label(u.getName() != null ? u.getName() : "@" + u.getUserTag());
            chipName.setTextFill(Color.WHITE);
            chipName.setFont(Font.font("Arial", 12));

            Label x = new Label("✕");
            x.setTextFill(Color.WHITE);
            x.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            x.setCursor(Cursor.HAND);
            x.setOnMouseClicked(ev -> {
                ev.consume();
                selectedUsers.remove(u);
                rebuildUserList(userSearch.getText());
                refreshChips();
            });

            chip.getChildren().addAll(chipName, x);
            chipsPane.getChildren().add(chip);
        }
    }

    // ── API publique ─────────────────────────────────────────────────────────

    public void setAvailableUsers(List<User> users) {
        this.availableUsers = users != null ? new ArrayList<>(users) : new ArrayList<>();
        rebuildUserList(userSearch.getText());
    }

    public void setOnConfirm(ChannelCreationCallback onConfirm) {
        this.onConfirm = onConfirm;
    }

    public void show() {
        resetForm();
        setVisible(true);
        setMouseTransparent(false);
        sheet.setTranslateY(400);
        sheet.setOpacity(0);
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(sheet.translateYProperty(), 400),
                        new KeyValue(sheet.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(300),
                        new KeyValue(sheet.translateYProperty(), 0),
                        new KeyValue(sheet.opacityProperty(), 1))
        );
        tl.play();
    }

    public void hide() {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(sheet.translateYProperty(), 0),
                        new KeyValue(sheet.opacityProperty(), 1)),
                new KeyFrame(Duration.millis(220),
                        new KeyValue(sheet.translateYProperty(), 400),
                        new KeyValue(sheet.opacityProperty(), 0))
        );
        tl.setOnFinished(e -> {
            setVisible(false);
            setMouseTransparent(true);
        });
        tl.play();
    }

    // ── Helpers visuels ──────────────────────────────────────────────────────

    private Label miniLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        l.setTextFill(MUTED);
        return l;
    }

    private void styleInput(TextField tf) {
        tf.setStyle(
                "-fx-background-color: rgb(64,68,75);" +
                        "-fx-text-fill: white;" +
                        "-fx-prompt-text-fill: rgba(255,255,255,0.4);" +
                        "-fx-border-color: transparent;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 8 12 8 12;"
        );
    }

    private Button makeButton(String text, String bg, String fg, String bgH, String fgH) {
        Button btn = new Button(text);
        String base = String.format("-fx-background-color:%s;-fx-text-fill:%s;-fx-font-size:14;-fx-background-radius:4;-fx-cursor:hand;-fx-border-color:transparent;", bg, fg);
        String hover = String.format("-fx-background-color:%s;-fx-text-fill:%s;-fx-font-size:14;-fx-background-radius:4;-fx-cursor:hand;-fx-border-color:transparent;", bgH, fgH);
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }
}
