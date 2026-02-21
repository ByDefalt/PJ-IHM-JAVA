package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.ihm.graphicController.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.InputMessageView;
import com.ubo.tp.message.logger.Logger;

public class InputMessageGraphicController implements GraphicController {

    private final Logger LOGGER;
    private final InputMessageView inputMessageView;
    private final IInputMessageController inputMessageController;

    public InputMessageGraphicController(Logger logger, InputMessageView inputMessageView, IInputMessageController inputMessageController) {
        LOGGER = logger;
        this.inputMessageView = inputMessageView;
        this.inputMessageController = inputMessageController;

        inputMessageView.setOnSendRequested(this::handleSendAction);
    }

    private void handleSendAction() {
        String message = inputMessageView.getText().trim();
        if (LOGGER != null) LOGGER.debug("Envoi demandé : " + message);
        if (message.isEmpty()) return;

        if (inputMessageController != null) {
            inputMessageController.sendMessage(message);
        }

        inputMessageView.clearText();
    }
}