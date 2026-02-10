package com.ubo.tp.message.core.database;

import java.util.Set;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

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
