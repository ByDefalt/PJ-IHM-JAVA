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
    }

    private boolean isUserMentioned(User user, Message message) {
        String mention = "@" + user.getUserTag();
        return message.getText().contains(mention);
    }

    private void sendOsNotification(String title, String content) {
        try {
            // Forcer le provider natif Windows (WinToast) au lieu de AWT
            ToasterSettings settings = new ToasterSettings()
                    .setAppName("MessageApp");
            ToasterFactory.setSettings(settings);

            Toast.builder()
                    .type(ToastType.INFO)
                    .title(title)
                    .content(content)
                    .toast();
        } catch (Exception e) {
            System.err.println("Notification OS échouée : " + e.getMessage());
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
                    "@" + context.session().getConnectedUser().getUserTag() + " a été mentionné"
            );
        }
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {}

    @Override
    public void notifyMessageModified(Message modifiedMessage) {}
}