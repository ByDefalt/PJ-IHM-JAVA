package com.ubo.tp.message.core.database.observer;

import com.ubo.tp.message.datamodel.User;

/**
 * Observateur des modifications liées aux utilisateurs.
 */
public interface IUserDatabaseObserver {

    /**
     * Notification lorsqu'un utilisateur est ajouté en base de données.
     *
     * @param addedUser utilisateur ajouté
     */
    void notifyUserAdded(User addedUser);

    /**
     * Notification lorsqu'un utilisateur est supprimé de la base de données.
     *
     * @param deletedUser utilisateur supprimé
     */
    void notifyUserDeleted(User deletedUser);

    /**
     * Notification lorsqu'un utilisateur est modifié en base de données.
     *
     * @param modifiedUser utilisateur modifié
     */
    void notifyUserModified(User modifiedUser);
}

