package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IInputMessageController;

import java.util.Objects;

public class InputMessageController implements IInputMessageController {

    private final ControllerContext context;

    public InputMessageController(ControllerContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public void sendMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            context.logger().warn("Message vide, envoi annulé");
            return;
        }

        String trimmedMessage = message.trim();
        context.logger().debug("Envoi du message : " + trimmedMessage);
    }
}
