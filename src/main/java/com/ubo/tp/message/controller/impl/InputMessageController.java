package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.datamodel.Message;

import java.util.Objects;
import java.util.UUID;

public class InputMessageController implements IInputMessageController {

    private final ControllerContext context;
    private static final int MAX_LENGTH = 200;

    public InputMessageController(ControllerContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public void sendMessage(UUID uuid, String message) {
        if (message == null || message.trim().isEmpty()) {
            context.logger().warn("Message vide, envoi annulé");
            return;
        }

        String trimmedMessage = message.trim();
        if (trimmedMessage.length() > MAX_LENGTH) {
            if (context.logger() != null) context.logger().warn("Message trop long (" + trimmedMessage.length() + " caractères). Maximum autorisé: " + MAX_LENGTH);
            return;
        }

        if (context.logger() != null) context.logger().debug("Envoi du message : " + trimmedMessage);
        context.dataManager().sendMessage(
                new Message(
                        context.session().getConnectedUser(),
                        uuid,
                        trimmedMessage
                )
        );
    }
}
