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
    public void setOnClose(Runnable onClose) {
        appMainView.setOnClose(onClose);
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
    public void setConnectMenuVisible(boolean visible) {
        Platform.runLater(() -> {
            appMainView.setConnectMenuVisible(visible);
            if (!visible && clearSelected != null) clearSelected.run();
        });
    }
}

