package com.ubo.tp.message.ihm.view;

import com.ubo.tp.message.controller.service.IEmptyController;
import com.ubo.tp.message.ihm.service.View;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;

public class EmptyView extends JComponent implements View {
    private final IEmptyController emptyController;
    private final Logger LOGGER;

    public EmptyView(IEmptyController emptyController, Logger logger) {
        this.emptyController = emptyController;
        LOGGER = logger;

        this.init();
    }

    private void init() {}
}
