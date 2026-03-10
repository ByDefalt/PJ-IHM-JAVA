package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.datamodel.Message;

import java.util.Objects;
import java.util.UUID;

public class InputMessageController implements IInputMessageController {

    private static final int MAX_LENGTH = 200;
    private final ControllerContext context;

    public InputMessageController(ControllerContext context) {
        this.context = Objects.requireNonNull(context);
    }

    @Override
    public void sendMessage(UUID recipientUuid, String message) {
        handleSendMessageLogic(recipientUuid, message);
    }

    private void handleSendMessageLogic(UUID recipientUuid, String message) {
        if (message == null || message.trim().isEmpty()) {
            if (context.logger() != null) context.logger().warn("Message vide, envoi annulé");
            return;
        }

        String trimmedMessage = message.trim();
        if (trimmedMessage.length() > MAX_LENGTH) {
            if (context.logger() != null)
                context.logger().warn("Message trop long (" + trimmedMessage.length() + " caractères). Maximum autorisé: " + MAX_LENGTH);
            return;
        }

        if (context.logger() != null) context.logger().debug("Envoi du message : " + trimmedMessage);
        context.dataManager().sendMessage(
                new Message(
                        context.session().getConnectedUser(),
                        recipientUuid,
                        trimmedMessage
                )
        );
    }

    /**
     * Résout le destinataire depuis la sélection courante (logique métier)
     * puis délègue à sendMessage.
     */
    @Override
    public void sendMessageToSelected(String text) {
        handleSendMessageToSelectedLogic(text);
    }

    private void handleSendMessageToSelectedLogic(String text) {
        var sel = context.selected();
        if (sel == null) {
            if (context.logger() != null) context.logger().warn("Aucune sélection, envoi annulé");
            return;
        }

        if (sel.getSelectedChannel() != null) {
            if (context.logger() != null)
                context.logger().debug("Envoi au canal : " + sel.getSelectedChannel().getName());
            sendMessage(sel.getSelectedChannel().getUuid(), text);
        } else if (sel.getSelectedUser() != null) {
            if (context.logger() != null)
                context.logger().debug("Envoi à l'utilisateur : " + sel.getSelectedUser().getName());
            sendMessage(sel.getSelectedUser().getUuid(), text);
        } else {
            if (context.logger() != null) context.logger().warn("Aucun canal ni utilisateur sélectionné, envoi annulé");
        }
    }
}
