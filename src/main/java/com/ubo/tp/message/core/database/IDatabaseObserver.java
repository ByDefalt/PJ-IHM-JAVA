package com.ubo.tp.message.core.database;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

/**
 * Observateur des modifications de la base de données.
 * <p>
 * Les implémentations reçoivent des notifications précises lorsque des entités
 * sont ajoutées, modifiées ou supprimées. Les notifications sont exécutées par
 * l'implémentation de la base (potentiellement depuis un thread différent),
 * donc l'implémentation doit gérer le threading si nécessaire (ex : invoquer
 * SwingUtilities.invokeLater pour mises à jour UI).
 * </p>
 */
public interface IDatabaseObserver {
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
