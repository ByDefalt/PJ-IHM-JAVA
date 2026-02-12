package com.ubo.tp.message.controller.impl;


import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.service.IListMessageView;
import com.ubo.tp.message.ihm.view.MessageView;
import com.ubo.tp.message.logger.Logger;

import java.util.List;
import java.util.Set;

public class ListMessageController {

    private final Logger LOGGER;

    private final IDataManager dataManager;
    private final IListMessageView view;

    public ListMessageController(Logger logger, IDataManager dataManager, IListMessageView view) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.view = view;

        this.view.setOnRefreshRequested(this::refreshMessages);
    }
    private Set<Message> getMessages() {
        return dataManager.getMessages();
    }

    private List<MessageView> createMessageViews() {
        return getMessages().stream()
                .map( m -> new MessageView(LOGGER, m) )
                .toList();
    }

    public void refreshMessages() {
        LOGGER.debug("ListMessageController: rafraîchissement des messages");
        this.view.setMessages(createMessageViews());
    }



}
