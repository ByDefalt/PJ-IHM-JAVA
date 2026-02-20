package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.graphicController.service.IListMessageGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListMessageView;
import com.ubo.tp.message.logger.Logger;


public class ListMessageGraphicController implements IListMessageGraphicController {

    private final Logger LOGGER;
    private final ListMessageView listMessageView;

    public ListMessageGraphicController(Logger logger, ListMessageView listMessageView) {
        LOGGER = logger;
        this.listMessageView = listMessageView;
    }

    @Override
    public void addMessage(Message message) {
        if (listMessageView != null) listMessageView.addMessage(message);
    }

    @Override
    public void removeMessage(Message message) {
        if (listMessageView != null) listMessageView.removeMessage(message);
    }

    @Override
    public void updateMessage(Message message) {
        if (listMessageView != null) listMessageView.updateMessage(message);
    }
}
