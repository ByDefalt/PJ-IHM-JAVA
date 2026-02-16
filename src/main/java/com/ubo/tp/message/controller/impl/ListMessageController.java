package com.ubo.tp.message.controller.impl;


import com.ubo.tp.message.controller.service.IListMessageController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.service.IListMessageView;
import com.ubo.tp.message.ihm.service.IMessageView;
import com.ubo.tp.message.ihm.view.MessageView;
import com.ubo.tp.message.logger.Logger;

import java.util.List;
import java.util.stream.Collectors;

public class ListMessageController implements IListMessageController {

    private final Logger LOGGER;

    private final IDataManager dataManager;
    private final IListMessageView view;

    public ListMessageController(Logger logger, IDataManager dataManager, IListMessageView view) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.view = view;

        this.view.setOnRefreshRequested(this::refreshMessages);
    }

    private List<IMessageView> refreshListMessagesView() {
        return dataManager.getMessages().stream()
                .map( m -> (IMessageView) new MessageView(LOGGER, m) )
                .collect(Collectors.toList());
    }

    public void refreshMessages() {
        LOGGER.debug("ListMessageController: rafraîchissement des messages");
        this.view.setMessages(refreshListMessagesView());
    }

    public void addMessage(Message message) {
        LOGGER.debug("ListMessageController: ajout d'un message");
        dataManager.sendMessage(message);
        refreshMessages();
    }


}
