package com.ubo.tp.message.controller.impl;


import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.service.IMessageView;
import com.ubo.tp.message.logger.Logger;

public class MessageController{

    private final Logger LOGGER;

    private final IDataManager dataManager;
    private final IMessageView view;

    public MessageController(Logger logger, IDataManager dataManager, IMessageView view) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.view = view;
    }

    public void setMessage(String author, String content, String time) {
        LOGGER.info("MessageController: setMessage" + author + ": " + content + ": " + time);
        this.view.setMessage(author, content, time);
    }

}
