package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListMessageController;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.graphicController.service.IListMessageGraphicController;

import java.util.Objects;

public class ListMessageController implements IListMessageController, IMessageDatabaseObserver {

    private final ControllerContext context;
    private final IListMessageGraphicController graphicController;

    public ListMessageController(ControllerContext context, IListMessageGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        this.context.dataManager().addObserver(this);
    }

    @Override
    public IListMessageGraphicController getGraphicController() {
        return graphicController;
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        if (context.logger() != null) context.logger().debug("Message ajouté : " + addedMessage);
        this.graphicController.addMessage(addedMessage);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        if (context.logger() != null) context.logger().debug("Message suppression : " + deletedMessage);
        this.graphicController.removeMessage(deletedMessage);
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        if (context.logger() != null) context.logger().debug("Message update : " + modifiedMessage);
        this.graphicController.updateMessage(modifiedMessage);
    }
}
