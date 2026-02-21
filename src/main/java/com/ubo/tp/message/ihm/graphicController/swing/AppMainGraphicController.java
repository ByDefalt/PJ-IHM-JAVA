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

    public AppMainGraphicController(ViewContext viewContext, AppMainView appMainView) {
        this.viewContext = viewContext;
        this.appMainView = appMainView;

        this.viewContext.session().addObserver(this);
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
    public void notifyLogin(User connectedUser) {
        appMainView.setConnectMenuVisible(true);
    }

    @Override
    public void notifyLogout() {
        appMainView.setConnectMenuVisible(false);
    }
}