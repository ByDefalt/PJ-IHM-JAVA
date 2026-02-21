package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.swing.AppMainView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;
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
        this.LOGGER.debug("Request to show main frame");
        Runnable task = () -> {
            this.LOGGER.debug("Showing main frame on EDT");
            this.appMainView.getMainFrame().setVisible(visible);
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
        if (SwingUtilities.isEventDispatchThread()) {
            LOGGER.debug("Setting main view");
            JPanel contentPanel = appMainView.getContentPanel();
            contentPanel.removeAll();
            JPanel wrapper = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.fill = GridBagConstraints.BOTH;
            wrapper.add((JComponent) component, gbc);
            contentPanel.add(wrapper);
            contentPanel.revalidate();
            contentPanel.repaint();
            LOGGER.debug("Main view set");
        } else {
            SwingUtilities.invokeLater(() -> {
                setMainView(component);
            });
        }
    }
}
