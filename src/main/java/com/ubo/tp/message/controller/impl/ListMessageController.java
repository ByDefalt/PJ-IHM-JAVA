package com.ubo.tp.message.controller.impl;


import com.ubo.tp.message.controller.service.IListMessageController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.service.IListMessageView;
import com.ubo.tp.message.logger.Logger;

public class ListMessageController implements IListMessageController, IMessageDatabaseObserver {

    private final Logger LOGGER;

    private final IDataManager dataManager;
    private final IListMessageView view;

    public ListMessageController(Logger logger, IDataManager dataManager, IListMessageView view) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.view = view;

        this.dataManager.addObserver(this);
    }

    @Override
    public IListMessageView getView() {
        return view;
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        if (LOGGER != null) LOGGER.debug("Message ajouté : " + addedMessage);
        this.view.addMessage(addedMessage);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        if (LOGGER != null) LOGGER.debug("Message suppression : " + deletedMessage);
        this.view.removeMessage(deletedMessage);
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        if (LOGGER != null) LOGGER.debug("Message update : " + modifiedMessage);
        this.view.updateMessage(modifiedMessage);
    }
}
