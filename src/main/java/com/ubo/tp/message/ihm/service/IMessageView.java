package com.ubo.tp.message.ihm.service;

import com.ubo.tp.message.datamodel.Message;

public interface IMessageView extends View{
    void setMessage(Message message);
    String getAuthor();
    String getContent();
    String getTime();
}

