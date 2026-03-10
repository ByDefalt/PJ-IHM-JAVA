package com.ubo.tp.message.ihm.graphiccontroller.javafx;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxAppMainView;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.application.Platform;
import javafx.scene.Node;

import java.util.function.Consumer;

/**
 * Graphic controller de la fenêtre principale — JavaFX.
 */
public class FxAppMainGraphicController implements IAppMainGraphicController {

    private final ViewContext viewContext;
    private final FxAppMainView appMainView;
    private Runnable clearSelected;

    public FxAppMainGraphicController(ViewContext viewContext, FxAppMainView appMainView) {
        this.viewContext = viewContext;
        this.appMainView = appMainView;

        viewContext.navigationController().setMainView(this::setMainView);
        appMainView.setOnUpdateProfile(this::handleUpdateProfileRequested);
    }

    public void setClearSelected(Runnable clearSelected) {
        handleSetClearSelected(clearSelected);
    }

    private void handleSetClearSelected(Runnable clearSelected) {
        this.clearSelected = clearSelected;
    }

    private void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }

    @Override
    public void setVisibility(boolean visible) {
        handleSetVisibility(visible);
    }

    private void handleSetVisibility(boolean visible) {
        Runnable task = () -> {
            if (viewContext.logger() != null)
                viewContext.logger().debug("(FX) Visibilité fenêtre : " + visible);
            appMainView.setVisible(visible);
        };
        runOnFx(task);
    }

    @Override
    public void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected) {
        handleSetOnExchangeDirectorySelected(onExchangeDirectorySelected);
    }

    private void handleSetOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected) {
        appMainView.setOnExchangeDirectorySelected(onExchangeDirectorySelected);
    }

    @Override
    public void setMainView(View component) {
        handleSetMainView(component);
    }

    private void handleSetMainView(View component) {
        if (component == null) return;
        appMainView.setContent((Node) component);
    }

    @Override
    public void setOnClose(Runnable onClose) {
        handleSetOnClose(onClose);
    }

    private void handleSetOnClose(Runnable onClose) {
        appMainView.setOnClose(onClose);
    }

    @Override
    public void setOnDisconnect(Runnable onDisconnect) {
        handleSetOnDisconnect(onDisconnect);
    }

    private void handleSetOnDisconnect(Runnable onDisconnect) {
        appMainView.setOnDisconnect(onDisconnect);
    }

    @Override
    public void setOnDeleteAccount(Runnable onDeleteAccount) {
        handleSetOnDeleteAccount(onDeleteAccount);
    }

    private void handleSetOnDeleteAccount(Runnable onDeleteAccount) {
        appMainView.setOnDeleteAccount(onDeleteAccount);
    }

    @Override
    public void setConnectMenuVisible(boolean visible) {
        handleSetConnectMenuVisible(visible);
    }

    private void handleSetConnectMenuVisible(boolean visible) {
        runOnFx(() -> {
            appMainView.setConnectMenuVisible(visible);
            if (!visible && clearSelected != null) clearSelected.run();
        });
    }

    private void handleUpdateProfileRequested() {
        viewContext.navigationController().navigateToProfile();
    }
}
