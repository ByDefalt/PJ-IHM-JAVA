package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.core.session.ISessionObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.swing.AppMainView;

import javax.swing.*;
import java.util.function.Consumer;

public class AppMainGraphicController implements IAppMainGraphicController, ISessionObserver {

    private final ViewContext viewContext;
    private final AppMainView appMainView;
    private Runnable clearSelected;

    public AppMainGraphicController(ViewContext viewContext, AppMainView appMainView) {
        this.viewContext = viewContext;
        this.appMainView = appMainView;

        this.viewContext.session().addObserver(this);

        this.viewContext.navigationController().setMainView(this::setMainView);
        this.setOnUpdateProfile(this::truc);
    }

    @Override
    public void setVisibility(boolean visible) {
        if (viewContext.logger() != null) viewContext.logger().debug("Request to show main frame");
        Runnable task = () -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Showing main frame on EDT");
            appMainView.getMainFrame().setVisible(visible);
        };
        if (SwingUtilities.isEventDispatchThread()) {
            task.run();
        } else {
            SwingUtilities.invokeLater(task);
        }
    }

    @Override
    public void setOnExchangeDirectorySelected(Consumer<String> onExchangeDirectorySelected) {
        appMainView.setOnExchangeDirectorySelected(onExchangeDirectorySelected);
    }

    @Override
    public void setMainView(View component) {
        if (component == null) return;
        appMainView.setContent((JComponent) component);
    }

    @Override
    public void setClearSelected(Runnable clearSelected) {
        this.clearSelected = clearSelected;
    }

    @Override
    public void notifyLogin(User connectedUser) {
        appMainView.setConnectMenuVisible(true);
    }

    @Override
    public void notifyLogout() {
        appMainView.setConnectMenuVisible(false);
        if (clearSelected != null) clearSelected.run();
    }

    @Override
    public void setOnDisconnect(Runnable onDisconnect) {
        appMainView.setOnDisconnect(onDisconnect);
    }

    @Override
    public void setOnDeleteAccount(Runnable onDeleteAccount) {
        appMainView.setOnDeleteAccount(onDeleteAccount);
    }

    public void setOnUpdateProfile(Runnable onUpdateProfile) {
        appMainView.setOnUpdateProfile(onUpdateProfile);
    }

    public void truc() {
        viewContext.navigationController().navigateToProfile();
    }
}