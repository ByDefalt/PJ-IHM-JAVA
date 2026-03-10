package com.ubo.tp.message.ihm.graphiccontroller.swing;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.swing.AppMainView;

import javax.swing.*;
import java.util.function.Consumer;

public class AppMainGraphicController implements IAppMainGraphicController {

    private final ViewContext viewContext;
    private final AppMainView appMainView;
    private Runnable clearSelected;

    public AppMainGraphicController(ViewContext viewContext, AppMainView appMainView) {
        this.viewContext = viewContext;
        this.appMainView = appMainView;
        setupBindings();
    }

    private void setupBindings() {
        this.viewContext.navigationController().setMainView(this::setMainView);
        this.setOnUpdateProfile(this::handleUpdateProfileRequested);
    }

    @Override
    public void setVisibility(boolean visible) {
        handleSetVisibility(visible);
    }

    private void handleSetVisibility(boolean visible) {
        if (viewContext.logger() != null) viewContext.logger().debug("Request to show main frame");
        Runnable task = () -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Showing main frame on EDT");
            appMainView.getMainFrame().setVisible(visible);
        };
        runOnEDT(task);
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
        appMainView.setContent((JComponent) component);
    }

    public void setClearSelected(Runnable clearSelected) {
        handleSetClearSelected(clearSelected);
    }

    private void handleSetClearSelected(Runnable clearSelected) {
        this.clearSelected = clearSelected;
    }

    @Override
    public void setOnClose(Runnable onClose) {
        handleSetOnClose(onClose);
    }

    private void handleSetOnClose(Runnable onClose) {
        appMainView.setOnClose(onClose);
    }

    @Override
    public void setConnectMenuVisible(boolean visible) {
        handleSetConnectMenuVisible(visible);
    }

    private void handleSetConnectMenuVisible(boolean visible) {
        appMainView.setConnectMenuVisible(visible);
        if (!visible && clearSelected != null) clearSelected.run();
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

    public void setOnUpdateProfile(Runnable onUpdateProfile) {
        handleSetOnUpdateProfile(onUpdateProfile);
    }

    private void handleSetOnUpdateProfile(Runnable onUpdateProfile) {
        appMainView.setOnUpdateProfile(onUpdateProfile);
    }

    private void handleUpdateProfileRequested() {
        viewContext.navigationController().navigateToProfile();
    }

    private void runOnEDT(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) task.run();
        else SwingUtilities.invokeLater(task);
    }
}