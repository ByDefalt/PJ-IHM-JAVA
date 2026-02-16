package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.datamodel.Message;

public interface IListMessageController extends Controller{

    void refreshMessages();
    void addMessage(Message message);
}
