package com.ubo.tp.message.controller.impl;


import com.ubo.tp.message.controller.service.IListMessageController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.graphicController.service.IListMessageGraphicController;
import com.ubo.tp.message.logger.Logger;

public class ListMessageController implements IListMessageController, IMessageDatabaseObserver {

    private final Logger LOGGER;

    private final IDataManager dataManager;
    private final ISession session;
    private final IListMessageGraphicController graphicController;

    public ListMessageController(Logger logger, IDataManager dataManager, ISession session, IListMessageGraphicController graphicController) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.session = session;
        this.graphicController = graphicController;

        this.dataManager.addObserver(this);
    }

    @Override
    public IListMessageGraphicController getGraphicController() {
        return graphicController;
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        if (LOGGER != null) LOGGER.debug("Message ajouté : " + addedMessage);
        this.graphicController.addMessage(addedMessage);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        if (LOGGER != null) LOGGER.debug("Message suppression : " + deletedMessage);
        this.graphicController.removeMessage(deletedMessage);
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        if (LOGGER != null) LOGGER.debug("Message update : " + modifiedMessage);
        this.graphicController.updateMessage(modifiedMessage);
    }
}
