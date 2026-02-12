package com.ubo.tp.message.ihm.service;

import com.ubo.tp.message.ihm.view.MessageView;

import java.util.List;

public interface IListMessageView extends View{
    void setMessages(List<MessageView> newMessages);
    void setOnRefreshRequested(Runnable onRefreshRequested);
}
