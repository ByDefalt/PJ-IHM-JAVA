package com.ubo.tp.message.core.database.observer;

import com.ubo.tp.message.datamodel.Channel;

/**
 * Observateur des modifications liées aux canaux.
 */
public interface IChannelDatabaseObserver {

    /**
     * Notification lorsqu'un canal est ajouté en base de données.
     *
     * @param addedChannel canal ajouté
     */
    void notifyChannelAdded(Channel addedChannel);

    /**
     * Notification lorsqu'un canal est supprimé de la base de données.
     *
     * @param deletedChannel canal supprimé
     */
    void notifyChannelDeleted(Channel deletedChannel);

    /**
     * Notification lorsqu'un canal est modifié en base de données.
     *
     * @param modifiedChannel canal modifié
     */
    void notifyChannelModified(Channel modifiedChannel);
}

