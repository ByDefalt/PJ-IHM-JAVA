package com.ubo.tp.message.ihm.graphicController.javafx;

import com.ubo.tp.message.core.session.ISessionObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxAppMainView;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.function.Consumer;

/**
 * Graphic controller de la fenêtre principale — JavaFX.
 */
public class FxAppMainGraphicController implements IAppMainGraphicController, ISessionObserver {

    private final ViewContext viewContext;
    private final FxAppMainView appMainView;
    private Runnable clearSelected;

    public FxAppMainGraphicController(ViewContext viewContext, FxAppMainView appMainView) {
        this.viewContext = viewContext;
        this.appMainView = appMainView;

        viewContext.session().addObserver(this);
        viewContext.navigationController().setMainView(this::setMainView);
        // Câbler "Modifier le profil" vers la navigation
        appMainView.setOnUpdateProfile(() -> viewContext.navigationController().navigateToProfile());
    }

    @Override
    public void setVisibility(boolean visible) {
        Runnable task = () -> {
            if (viewContext.logger() != null)
                viewContext.logger().debug("(FX) Visibilité fenêtre : " + visible);
            appMainView.setVisible(visible);
        };
        if (Platform.isFxApplicationThread()) task.run();
        else Platform.runLater(task);
    }

    @Override
    public void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected) {
        appMainView.setOnExchangeDirectorySelected(onExchangeDirectorySelected);
    }

    @Override
    public void setMainView(View component) {
        if (component == null) return;
        appMainView.setContent((Node) component);
    }

    @Override
    public void setClearSelected(Runnable clearSelected) {
        this.clearSelected = clearSelected;
    }

    @Override
    public void setOnDisconnect(Runnable onDisconnect) {
        appMainView.setOnDisconnect(onDisconnect);
    }

    @Override
    public void setOnDeleteAccount(Runnable onDeleteAccount) {
        appMainView.setOnDeleteAccount(onDeleteAccount);
    }

    @Override
    public void notifyLogin(User connectedUser) {
        Platform.runLater(() -> appMainView.setConnectMenuVisible(true));
    }

    @Override
    public void notifyLogout() {
        Platform.runLater(() -> {
            appMainView.setConnectMenuVisible(false);
            if (clearSelected != null) clearSelected.run();
        });
    }
}

