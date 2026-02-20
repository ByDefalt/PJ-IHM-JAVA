package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.service.IListMessageView;

public interface IListMessageController extends Controller {

    void refreshMessages();

    void addMessage(Message message);

    IListMessageView getView();
}
