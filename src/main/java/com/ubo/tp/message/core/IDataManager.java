package com.ubo.tp.message.core;

import java.util.Set;

import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.datamodel.IMessageRecipient;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

/**
 * Service d'accès aux données de l'application (abstraction).
 * <p>
 * Définit les opérations disponibles pour manipuler et interroger les entités
 * (utilisateurs, messages, canaux) et pour recevoir des notifications de
 * changement émanant de la source de données.
 * </p>
 * <p>
 * Implémentations : les méthodes peuvent être appelées depuis des threads
 * différents (EDT, watcher, services). Si l'implémentation n'est pas thread-safe,
 * il est de la responsabilité de l'appelant de synchroniser l'accès.
 * </p>
 */
public interface IDataManager {

    /**
     * Ajoute un observateur pour recevoir des notifications de modification
     * provenant de la couche de données.
     *
     * @param observer observateur à enregistrer (non-null)
     */
    void addObserver(IDatabaseObserver observer);

    /**
     * Retire un observateur précédemment enregistré.
     *
     * @param observer observateur à retirer
     */
    void removeObserver(IDatabaseObserver observer);

    /**
     * Retourne l'ensemble des utilisateurs connus.
     *
     * @return set d'utilisateurs
     */
    Set<User> getUsers();

    /**
     * Retourne l'ensemble des messages connus.
     *
     * @return set de messages
     */
    Set<Message> getMessages();

    /**
     * Retourne l'ensemble des canaux connus.
     *
     * @return set de canaux
     */
    Set<com.ubo.tp.message.datamodel.Channel> getChannels();

    /**
     * Écrit (publie) un message via le mécanisme d'échange (fichier).
     *
     * @param message message à envoyer
     */
    void sendMessage(Message message);

    /**
     * Écrit (publie) un utilisateur via le mécanisme d'échange (fichier).
     *
     * @param user utilisateur à écrire
     */
    void sendUser(User user);

    /**
     * Écrit (publie) un canal via le mécanisme d'échange (fichier).
     *
     * @param channel canal à écrire
     */
    void sendChannel(com.ubo.tp.message.datamodel.Channel channel);

    /**
     * Retourne les messages émis par un utilisateur donné.
     *
     * @param user émetteur
     * @return ensemble des messages émis
     */
    Set<Message> getMessagesFrom(User user);

    /**
     * Retourne les messages émis par un utilisateur vers un destinataire précis.
     *
     * @param sender    émetteur
     * @param recipient destinataire (User ou Channel)
     * @return ensemble des messages correspondants
     */
    Set<Message> getMessagesFrom(User sender, IMessageRecipient recipient);

    /**
     * Retourne les messages adressés à un utilisateur.
     *
     * @param user destinataire
     * @return ensemble des messages reçus
     */
    Set<Message> getMessagesTo(User user);

    /**
     * Configure le répertoire d'échange (utilisé pour la lecture/écriture des fichiers).
     *
     * @param directoryPath chemin du répertoire
     */
    void setExchangeDirectory(String directoryPath);
}
