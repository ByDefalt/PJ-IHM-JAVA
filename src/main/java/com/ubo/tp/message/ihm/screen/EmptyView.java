package com.ubo.tp.message.ihm.screen;

import com.ubo.tp.message.controller.service.IEmptyController;
import com.ubo.tp.message.logger.Logger;

public class EmptyView extends View {
    private final IEmptyController emptyController;
    private final Logger LOGGER;

    public EmptyView(IEmptyController emptyController, Logger logger) {
        this.emptyController = emptyController;
        LOGGER = logger;

        this.init();
    }

    private void init() {}
}
