package com.ubo.tp.message.core.database;

import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import java.util.Set;

/**
 * Abstraction de la base de données utilisée par l'application.
 * <p>
 * Fournit les opérations de lecture/écriture basiques et un mécanisme
 * d'observer les changements (ajout/suppression/modification).
 * </p>
 * <p>
 * Contrat : les implémentations doivent être thread-safe si la base est
 * accédée depuis plusieurs threads (ex : EDT + watcher). Les méthodes
 * n'acceptent pas d'arguments nulls sauf mention contraire.
 * </p>
 */
public interface IDatabase {

    /**
     * Ajoute un observateur sur les modifications de la base de données.
     *
     * @param observer observateur à ajouter (non-null)
     */
    void addObserver(IDatabaseObserver observer);

    /**
     * Supprime un observateur sur les modifications de la base de données.
     *
     * @param observer observateur à supprimer
     */
    void removeObserver(IDatabaseObserver observer);

    /**
     * Ajoute un observateur spécialisé pour les messages.
     * <p>
     * Méthode ajoutée pour permettre l'enregistrement granulaire d'observeurs.
     * </p>
     *
     * @param observer observateur de messages
     */
    void addObserver(IMessageDatabaseObserver observer);

    /**
     * Retire un observateur spécialisé pour les messages.
     *
     * @param observer observateur de messages
     */
    void removeObserver(IMessageDatabaseObserver observer);

    /**
     * Ajoute un observateur spécialisé pour les utilisateurs.
     *
     * @param observer observateur d'utilisateurs
     */
    void addObserver(IUserDatabaseObserver observer);

    /**
     * Retire un observateur spécialisé pour les utilisateurs.
     *
     * @param observer observateur d'utilisateurs
     */
    void removeObserver(IUserDatabaseObserver observer);

    /**
     * Ajoute un observateur spécialisé pour les canaux.
     *
     * @param observer observateur de canaux
     */
    void addObserver(IChannelDatabaseObserver observer);

    /**
     * Retire un observateur spécialisé pour les canaux.
     *
     * @param observer observateur de canaux
     */
    void removeObserver(IChannelDatabaseObserver observer);

    /**
     * Retourne la liste des utilisateurs actuellement connus.
     *
     * @return ensemble des utilisateurs (vue non modifiable recommandée)
     */
    Set<User> getUsers();

    /**
     * Retourne la liste des messages enregistrés.
     *
     * @return ensemble des messages
     */
    Set<Message> getMessages();

    /**
     * Retourne la liste des canaux.
     *
     * @return ensemble des canaux
     */
    Set<Channel> getChannels();
}
