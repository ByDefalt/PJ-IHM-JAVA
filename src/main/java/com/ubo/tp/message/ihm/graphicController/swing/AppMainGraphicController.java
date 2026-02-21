package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.swing.AppMainView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.util.function.Consumer;

public class AppMainGraphicController implements IAppMainGraphicController {

    private final Logger LOGGER;
    private final AppMainView appMainView;

    public AppMainGraphicController(Logger logger, AppMainView appMainView) {
        this.LOGGER = logger;
        this.appMainView = appMainView;
    }

    @Override
    public void setVisibility(boolean visible) {
        LOGGER.debug("Request to show main frame");
        Runnable task = () -> {
            LOGGER.debug("Showing main frame on EDT");
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
}