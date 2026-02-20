package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.ihm.graphicController.service.IInputMessageGraphicController;
import com.ubo.tp.message.ihm.view.swing.InputMessageView;
import com.ubo.tp.message.logger.Logger;

public class InputMessageGraphicController implements IInputMessageGraphicController {

    private final Logger LOGGER;
    private final InputMessageView inputMessageView;

    public InputMessageGraphicController(Logger logger, InputMessageView inputMessageView) {
        LOGGER = logger;
        this.inputMessageView = inputMessageView;
    }

    @Override
    public String getMessageText() {
        return inputMessageView != null ? inputMessageView.getMessageText() : null;
    }

    @Override
    public String consumeMessage() {
        return inputMessageView != null ? inputMessageView.consumeMessage() : null;
    }

    @Override
    public void clearInput() {
        if (inputMessageView != null) inputMessageView.clearInput();
    }

    @Override
    public void setOnSendRequested(Runnable handler) {
        if (inputMessageView != null) inputMessageView.setOnSendRequested(handler);
    }
}
