package com.ubo.tp.message.core.database.observer;

import com.ubo.tp.message.datamodel.Message;

/**
 * Observateur des modifications liées aux messages.
 */
public interface IMessageDatabaseObserver {

    /**
     * Notification lorsqu'un Message est ajouté en base de données.
     *
     * @param addedMessage message ajouté
     */
    void notifyMessageAdded(Message addedMessage);

    /**
     * Notification lorsqu'un Message est supprimé de la base de données.
     *
     * @param deletedMessage message supprimé
     */
    void notifyMessageDeleted(Message deletedMessage);

    /**
     * Notification lorsqu'un Message est modifié en base de données.
     *
     * @param modifiedMessage message modifié
     */
    void notifyMessageModified(Message modifiedMessage);
}

