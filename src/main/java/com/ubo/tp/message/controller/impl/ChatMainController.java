package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IChatMainController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.logger.Logger;

public class ChatMainController implements IChatMainController {

    private final Logger LOGGER;
    private final IDataManager dataManager;

    public ChatMainController(Logger logger, IDataManager dataManager) {
        this.LOGGER = logger;
        this.dataManager = dataManager;
    }
}
