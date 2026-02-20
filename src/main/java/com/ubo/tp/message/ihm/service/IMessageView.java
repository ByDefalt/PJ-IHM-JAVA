package com.ubo.tp.message.ihm.service;

import com.ubo.tp.message.datamodel.Message;

public interface IMessageView extends View {
    Message getMessage();

    void updateMessage(Message message);
}

