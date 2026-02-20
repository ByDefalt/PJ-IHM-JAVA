package com.ubo.tp.message.ihm.graphicController.service;

public interface IInputMessageGraphicController extends GraphicController {
    String getMessageText();

    String consumeMessage();

    void clearInput();

    void setOnSendRequested(Runnable handler);
}

