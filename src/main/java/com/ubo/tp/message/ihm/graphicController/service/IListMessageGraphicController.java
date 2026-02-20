package com.ubo.tp.message.ihm.graphicController.service;

import com.ubo.tp.message.datamodel.Message;

public interface IListMessageGraphicController extends GraphicController {

    void addMessage(Message message);

    void removeMessage(Message message);

    void updateMessage(Message message);
}
