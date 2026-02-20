package com.ubo.tp.message.ihm.service;

public interface IInputMessageView extends View {
    String getMessageText();

    String consumeMessage();

    void clearInput();

    void setOnSendRequested(Runnable handler);
}

