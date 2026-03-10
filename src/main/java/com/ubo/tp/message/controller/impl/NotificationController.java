package com.ubo.tp.message.controller.impl;

import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastType;
import com.sshtools.twoslices.ToasterFactory;
import com.sshtools.twoslices.ToasterSettings;
import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.INotificationController;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

public class NotificationController implements INotificationController, IMessageDatabaseObserver {

    private ControllerContext context;

    public NotificationController(ControllerContext context) {
        this.context = context;
        context.dataManager().addObserver(this);

        ToasterFactory.setSettings(new ToasterSettings().setAppName("MessageApp"));
    }

    private boolean isUserMentioned(User user, Message message) {
        String mention = "@" + user.getUserTag();
        return message.getText().contains(mention);
    }

    private void sendOsNotification(String title, String content) {
        try {
            Toast.builder()
                    .type(ToastType.INFO)
                    .title(title)
                    .content(content)
                    .toast();
        } catch (Exception e) {
            context.logger().warn("Notification OS échouée : " + e.getMessage());
        }
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        if (addedMessage == null) return;
        if (context.session() == null || context.session().getConnectedUser() == null) return;
        if (isUserMentioned(context.session().getConnectedUser(), addedMessage)) {
            context.logger().debug("Utilisateur mentionné dans un message");
            sendOsNotification(
                    "Nouvelle mention",
                    "Vous avez été mentionné"
            );
        } else if (addedMessage.getRecipient().equals(context.session().getConnectedUser().getUuid())) {
            context.logger().debug("Vous avez reçu un message privé");
            sendOsNotification(
                    "Nouveau message privé",
                    "Vous avez reçu un message privé de " + addedMessage.getSender().getUserTag()
            );
        }
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {}

    @Override
    public void notifyMessageModified(Message modifiedMessage) {}
}