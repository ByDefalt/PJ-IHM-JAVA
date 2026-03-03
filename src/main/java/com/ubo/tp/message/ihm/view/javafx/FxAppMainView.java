package com.ubo.tp.message.ihm.view.javafx;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.function.Consumer;

/**
 * Fenêtre principale de l'application — JavaFX.
 * Contient la MenuBar et un StackPane central où les vues sont empilées.
 */
public class FxAppMainView implements View {

    private final ViewContext viewContext;
    private final Stage stage;
    private final StackPane contentPane = new StackPane();
    private final Menu connectMenu = new Menu("Compte");

    private Consumer<String> onExchangeDirectorySelected;
    private Runnable onDisconnect;
    private Runnable onUpdateProfile;
    private Runnable onDeleteAccount;

    public FxAppMainView(ViewContext viewContext, Stage stage) {
        this.viewContext = viewContext;
        this.stage = stage;

        BorderPane root = new BorderPane();
        root.setBackground(new Background(new BackgroundFill(Color.rgb(54, 57, 63), CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
        root.setTop(buildMenuBar());
        root.setCenter(contentPane);

        Scene scene = new Scene(root, 900, 650);
        stage.setTitle("MessageApp");
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setScene(scene);

        // Charger l'icône si disponible
        try {
            var url = getClass().getResource("/images/logo_20.png");
            if (url != null) stage.getIcons().add(new Image(url.toExternalForm()));
        } catch (Exception ignored) {
        }

        stage.setOnCloseRequest(e -> {
            e.consume();
            handleClose();
        });

        connectMenu.setVisible(false);
        if (viewContext.logger() != null) viewContext.logger().info("FxAppMainView initialisée");
    }

    // -------------------------------------------------------------------------
    // API publique
    // -------------------------------------------------------------------------

    public void setContent(javafx.scene.Node node) {
        if (node == null) return;
        Runnable task = () -> {
            contentPane.getChildren().setAll(node);
            if (viewContext.logger() != null) viewContext.logger().debug("FX main view set");
        };
        if (Platform.isFxApplicationThread()) task.run();
        else Platform.runLater(task);
    }

    public void setVisible(boolean visible) {
        Runnable task = () -> {
            if (visible) stage.show();
            else stage.hide();
        };
        if (Platform.isFxApplicationThread()) task.run();
        else Platform.runLater(task);
    }

    public void setConnectMenuVisible(boolean visible) {
        Platform.runLater(() -> connectMenu.setVisible(visible));
    }

    public Stage getStage() {
        return stage;
    }

    // Setters callbacks
    public void setOnExchangeDirectorySelected(Consumer<String> cb) {
        this.onExchangeDirectorySelected = cb;
    }

    public void setOnDisconnect(Runnable cb) {
        this.onDisconnect = cb;
    }

    public void setOnUpdateProfile(Runnable cb) {
        this.onUpdateProfile = cb;
    }

    public void setOnDeleteAccount(Runnable cb) {
        this.onDeleteAccount = cb;
    }

    // -------------------------------------------------------------------------
    // Menu
    // -------------------------------------------------------------------------

    private MenuBar buildMenuBar() {
        MenuBar menuBar = new MenuBar();

        // Fichier
        Menu fileMenu = new Menu("Fichier");
        MenuItem selectDir = new MenuItem("Sélectionner répertoire");
        selectDir.setOnAction(e -> showDirectoryChooser());
        MenuItem exit = new MenuItem("Quitter");
        exit.setOnAction(e -> handleClose());
        fileMenu.getItems().addAll(selectDir, new SeparatorMenuItem(), exit);

        // Compte (caché par défaut)
        MenuItem disconnectItem = new MenuItem("Déconnexion");
        disconnectItem.setOnAction(e -> {
            if (onDisconnect != null) onDisconnect.run();
        });
        MenuItem updateItem = new MenuItem("Modifier le profil");
        updateItem.setOnAction(e -> {
            if (onUpdateProfile != null) onUpdateProfile.run();
        });
        MenuItem deleteItem = new MenuItem("Supprimer le compte");
        deleteItem.setOnAction(e -> {
            if (onDeleteAccount != null) onDeleteAccount.run();
        });
        connectMenu.getItems().addAll(disconnectItem, updateItem, deleteItem);

        // Aide
        Menu helpMenu = new Menu("Aide");
        MenuItem about = new MenuItem("À propos");
        about.setOnAction(e -> showAbout());
        helpMenu.getItems().add(about);

        menuBar.getMenus().addAll(fileMenu, connectMenu, helpMenu);
        return menuBar;
    }

    private void showDirectoryChooser() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Sélectionner le répertoire d'échange");
        File dir = chooser.showDialog(stage);
        if (dir != null && onExchangeDirectorySelected != null)
            onExchangeDirectorySelected.accept(dir.getAbsolutePath());
    }

    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("MessageApp");
        alert.setContentText("Application de messagerie\n\nVersion 1.0\n\n© 2026 Message App");
        alert.initOwner(stage);
        alert.showAndWait();
    }

    private void handleClose() {
        if (viewContext.logger() != null) viewContext.logger().info("Fermeture demandée (FX)");
        try {
            if (viewContext.session() != null && viewContext.session().getConnectedUser() != null) {
                new Thread(() -> {
                    try {
                        viewContext.session().disconnect();
                    } catch (Exception ignored) {
                    } finally {
                        Platform.runLater(() -> {
                            stage.close();
                            Platform.exit();
                        });
                    }
                }, "fx-disconnect-thread").start();
            } else {
                stage.close();
                Platform.exit();
            }
        } catch (Exception ex) {
            stage.close();
            Platform.exit();
        }
    }
}

