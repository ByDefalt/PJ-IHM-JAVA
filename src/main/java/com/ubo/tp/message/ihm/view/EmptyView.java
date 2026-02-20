package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.controller.service.IEmptyController;
import com.ubo.tp.message.ihm.service.IEmptyView;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;

public class EmptyView extends JComponent implements IEmptyView {
    private final IEmptyController emptyController;
    private final Logger LOGGER;

    public EmptyView(IEmptyController emptyController, Logger logger) {
        this.emptyController = emptyController;
        LOGGER = logger;

        // initialisation via la méthode publique de l'interface
        this.init();
    }

    @Override
    public void init() {
        // no-op default
    }
}
