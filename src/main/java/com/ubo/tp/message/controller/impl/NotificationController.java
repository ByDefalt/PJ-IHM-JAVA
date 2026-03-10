package com.ubo.tp.message.controller.impl;

import com.sshtools.twoslices.Toast;
import com.sshtools.twoslices.ToastType;
import com.sshtools.twoslices.ToasterFactory;
import com.sshtools.twoslices.ToasterSettings;
import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur de notifications locales (OS) en réponse aux changements de la
 * base de messages.
 * <p>
 * Détecte les mentions et les messages privés destinés à l'utilisateur connecté
 * et émet des notifications système via la librairie Toast.
 * </p>
 */
public class NotificationController implements IMessageDatabaseObserver {

    private final ControllerContext context;

    /**
     * Crée un {@code NotificationController} et enregistre le controller auprès du DataManager.
     *
     * @param context contexte applicatif fournissant l'accès aux services
     */
    public NotificationController(ControllerContext context) {
        this.context = context;
        context.dataManager().addObserver(this);

        ToasterFactory.setSettings(new ToasterSettings().setAppName("MessageApp"));
    }

    /**
     * Vérifie si un utilisateur est mentionné dans un message.
     *
     * @param user    utilisateur à vérifier
     * @param message message analysé
     * @return {@code true} si le message contient une mention au format @userTag
     */
    private boolean isUserMentioned(User user, Message message) {
        String mention = "@" + user.getUserTag();
        return message.getText().contains(mention);
    }

    /**
     * Envoie une notification système (OS) avec un titre et un contenu.
     */
    @SuppressWarnings("resource")
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

    /**
     * Appelé quand un message est ajouté dans la base de données.
     *
     * @param addedMessage message ajouté
     */
    @Override
    public void notifyMessageAdded(Message addedMessage) {
        handleNotifyMessageAddedLogic(addedMessage);
    }

    /**
     * Logique interne pour traiter un message ajouté (mention ou message privé).
     */
    private void handleNotifyMessageAddedLogic(Message addedMessage) {
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

    /**
     * Appelé quand un message est supprimé (actuellement sans action locale).
     *
     * @param deletedMessage message supprimé
     */
    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        handleNotifyMessageDeletedLogic(deletedMessage);
    }

    private void handleNotifyMessageDeletedLogic(Message deletedMessage) {
        if (deletedMessage != null && context.logger() != null) {
            context.logger().debug("notifyMessageDeleted called for: " + deletedMessage);
        }
    }

    /**
     * Appelé quand un message est modifié (actuellement sans action locale).
     *
     * @param modifiedMessage message modifié
     */
    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        handleNotifyMessageModifiedLogic(modifiedMessage);
    }

    private void handleNotifyMessageModifiedLogic(Message modifiedMessage) {
        if (modifiedMessage != null && context.logger() != null) {
            context.logger().debug("notifyMessageModified called for: " + modifiedMessage);
        }
    }
}