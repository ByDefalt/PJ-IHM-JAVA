package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.ihm.graphicController.service.IInputMessageGraphicController;
import com.ubo.tp.message.logger.Logger;

public class InputMessageController implements IInputMessageController {

    private final Logger LOGGER;
    private final IDataManager dataManager;
    private final ISession session;

    public InputMessageController(Logger logger, IDataManager dataManager, ISession session) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.session = session;
    }

    @Override
    public void sendMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            LOGGER.warn("Message vide, envoi annulé");
            return;
        }

        String trimmedMessage = message.trim();
        LOGGER.debug("Envoi du message : " + trimmedMessage);
    }
}
