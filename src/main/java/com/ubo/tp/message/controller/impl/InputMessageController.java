package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.datamodel.Message;

import java.util.Objects;
import java.util.UUID;

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
        UUID recipientUuid = null;
        if(context.selected().getSelectedChannel()!=null){
            recipientUuid = context.selected().getSelectedChannel().getUuid();
        } else if(context.selected().getSelectedUser()!=null){
            recipientUuid = context.selected().getSelectedUser().getUuid();
        } else {
            context.logger().warn("Aucun destinataire sélectionné, envoi annulé");
            return;
        }
        context.dataManager().sendMessage(
                new Message(
                        context.session().getConnectedUser(),
                        recipientUuid,
                        trimmedMessage
                )
        );
    }
}
