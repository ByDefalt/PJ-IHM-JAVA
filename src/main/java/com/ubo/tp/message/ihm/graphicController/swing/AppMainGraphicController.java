package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.swing.AppMainView;
import com.ubo.tp.message.ihm.contexte.ViewContext;

import javax.swing.*;
import java.util.function.Consumer;

public class AppMainGraphicController implements IAppMainGraphicController {

    private final ViewContext viewContext;
    private final AppMainView appMainView;

    public AppMainGraphicController(ViewContext viewContext, AppMainView appMainView) {
        this.viewContext = viewContext;
        this.appMainView = appMainView;
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
}