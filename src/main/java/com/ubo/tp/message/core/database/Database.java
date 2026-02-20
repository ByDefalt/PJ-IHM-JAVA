package com.ubo.tp.message.core.database;

import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import java.util.HashSet;
import java.util.Set;

/**
 * Classe représentant les données chargées dans l'application.
 *
 * @author S.Lucas
 */
public class Database implements IDatabase {
    /**
     * Liste des utilisateurs enregistrés.
     */
    protected final Set<User> mUsers;

    /**
     * Liste des Message enregistrés.
     */
    protected final Set<Message> mMessages;

    /**
     * Liste des canaux enregistrés.
     */
    protected final Set<Channel> mChannels;

    /**
     * Liste des observateurs combinés (rétro-compatibilité).
     */
    protected final Set<IDatabaseObserver> mObservers;

    /**
     * Observateurs spécialisés.
     */
    protected final Set<IMessageDatabaseObserver> mMessageObservers;
    protected final Set<IUserDatabaseObserver> mUserObservers;
    protected final Set<IChannelDatabaseObserver> mChannelObservers;

    /**
     * Constructeur.
     */
    public Database() {
        mUsers = new HashSet<>();
        mMessages = new HashSet<>();
        mObservers = new HashSet<>();
        mChannels = new HashSet<>();

        mMessageObservers = new HashSet<>();
        mUserObservers = new HashSet<>();
        mChannelObservers = new HashSet<>();
    }

    @Override
    public Set<User> getUsers() {
        // Clonage pour éviter les modifications extérieures.
        return new HashSet<>(this.mUsers);
    }

    @Override
    public Set<Message> getMessages() {
        // Clonage pour éviter les modifications extérieures.
        return new HashSet<>(this.mMessages);
    }

    @Override
    public Set<Channel> getChannels() {
        // Clonage pour éviter les modifications extérieures.
        return new HashSet<>(this.mChannels);
    }

    /**
     * Ajoute un message à la base de données.
     *
     * @param messageToAdd message à ajouter en base
     */
    protected void addMessage(Message messageToAdd) {
        // Ajout du message
        this.mMessages.add(messageToAdd);

        // Notification des observateurs (rétro+spécialisés) sans doublons
        Set<Object> notified = new HashSet<>();
        for (IDatabaseObserver observer : mObservers) {
            observer.notifyMessageAdded(messageToAdd);
            notified.add(observer);
        }
        for (IMessageDatabaseObserver observer : mMessageObservers) {
            if (!notified.contains(observer)) {
                observer.notifyMessageAdded(messageToAdd);
                notified.add(observer);
            }
        }
    }

    /**
     * Supprime un message de la base de données.
     *
     * @param messageToRemove message à supprimer de la base
     */
    protected void deleteMessage(Message messageToRemove) {
        // Suppression du message
        this.mMessages.remove(messageToRemove);

        // Notification des observateurs sans doublons
        Set<Object> notified = new HashSet<>();
        for (IDatabaseObserver observer : mObservers) {
            observer.notifyMessageDeleted(messageToRemove);
            notified.add(observer);
        }
        for (IMessageDatabaseObserver observer : mMessageObservers) {
            if (!notified.contains(observer)) {
                observer.notifyMessageDeleted(messageToRemove);
                notified.add(observer);
            }
        }
    }

    /**
     * Modification d'un message de la base de données.
     *
     * @param messageToModify message à mettre à jour
     */
    protected void modifiyMessage(Message messageToModify) {
        // Ré-ajout pour écraser l'ancienne copie.
        this.mMessages.remove(messageToModify);
        this.mMessages.add(messageToModify);

        // Notification des observateurs sans doublons
        Set<Object> notified = new HashSet<>();
        for (IDatabaseObserver observer : mObservers) {
            observer.notifyMessageModified(messageToModify);
            notified.add(observer);
        }
        for (IMessageDatabaseObserver observer : mMessageObservers) {
            if (!notified.contains(observer)) {
                observer.notifyMessageModified(messageToModify);
                notified.add(observer);
            }
        }
    }

    /**
     * Ajoute un utilisateur à la base de données.
     *
     * @param userToAdd utilisateur à ajouter
     */
    protected void addUser(User userToAdd) {
        // Ajout de l'utilisateur
        this.mUsers.add(userToAdd);

        // Notification des observateurs sans doublons
        Set<Object> notified = new HashSet<>();
        for (IDatabaseObserver observer : mObservers) {
            observer.notifyUserAdded(userToAdd);
            notified.add(observer);
        }
        for (IUserDatabaseObserver observer : mUserObservers) {
            if (!notified.contains(observer)) {
                observer.notifyUserAdded(userToAdd);
                notified.add(observer);
            }
        }
    }

    /**
     * Supprime un utilisateur de la base de données.
     *
     * @param userToRemove utilisateur à supprimer
     */
    protected void deleteUser(User userToRemove) {
        // Suppression de l'utilisateur
        this.mUsers.remove(userToRemove);

        // Notification des observateurs sans doublons
        Set<Object> notified = new HashSet<>();
        for (IDatabaseObserver observer : mObservers) {
            observer.notifyUserDeleted(userToRemove);
            notified.add(observer);
        }
        for (IUserDatabaseObserver observer : mUserObservers) {
            if (!notified.contains(observer)) {
                observer.notifyUserDeleted(userToRemove);
                notified.add(observer);
            }
        }
    }

    /**
     * Modification d'un utilisateur de la base de données.
     *
     * @param userToModify utilisateur à mettre à jour
     */
    protected void modifiyUser(User userToModify) {
        // Ré-ajout pour écraser l'ancienne copie.
        this.mUsers.remove(userToModify);
        this.mUsers.add(userToModify);

        // Notification des observateurs sans doublons
        Set<Object> notified = new HashSet<>();
        for (IDatabaseObserver observer : mObservers) {
            observer.notifyUserModified(userToModify);
            notified.add(observer);
        }
        for (IUserDatabaseObserver observer : mUserObservers) {
            if (!notified.contains(observer)) {
                observer.notifyUserModified(userToModify);
                notified.add(observer);
            }
        }
    }

    /**
     * Ajoute un canal à la base de données.
     *
     * @param channelToAdd canal à ajouter
     */
    protected void addChannel(Channel channelToAdd) {
        // Ajout du canal
        this.mChannels.add(channelToAdd);

        // Notification des observateurs sans doublons
        Set<Object> notified = new HashSet<>();
        for (IDatabaseObserver observer : mObservers) {
            observer.notifyChannelAdded(channelToAdd);
            notified.add(observer);
        }
        for (IChannelDatabaseObserver observer : mChannelObservers) {
            if (!notified.contains(observer)) {
                observer.notifyChannelAdded(channelToAdd);
                notified.add(observer);
            }
        }
    }

    /**
     * Supprime un canal de la base de données.
     *
     * @param channelToRemove canal à supprimer
     */
    protected void deleteChannel(Channel channelToRemove) {
        // Suppression de l'utilisateur
        this.mChannels.remove(channelToRemove);

        // Notification des observateurs sans doublons
        Set<Object> notified = new HashSet<>();
        for (IDatabaseObserver observer : mObservers) {
            observer.notifyChannelDeleted(channelToRemove);
            notified.add(observer);
        }
        for (IChannelDatabaseObserver observer : mChannelObservers) {
            if (!notified.contains(observer)) {
                observer.notifyChannelDeleted(channelToRemove);
                notified.add(observer);
            }
        }
    }

    /**
     * Modification d'un canal de la base de données.
     *
     * @param channelToModify canal à mettre à jour
     */
    protected void modifiyChannel(Channel channelToModify) {
        // Ré-ajout pour écraser l'ancienne copie.
        this.mChannels.remove(channelToModify);
        this.mChannels.add(channelToModify);

        // Notification des observateurs sans doublons
        Set<Object> notified = new HashSet<>();
        for (IDatabaseObserver observer : mObservers) {
            observer.notifyChannelModified(channelToModify);
            notified.add(observer);
        }
        for (IChannelDatabaseObserver observer : mChannelObservers) {
            if (!notified.contains(observer)) {
                observer.notifyChannelModified(channelToModify);
                notified.add(observer);
            }
        }
    }

    @Override
    public void addObserver(IDatabaseObserver observer) {
        // Retro-compatible : enregistre l'observateur combiné et dans les sets spécialisés
        this.mObservers.add(observer);
        this.mMessageObservers.add(observer);
        this.mUserObservers.add(observer);
        this.mChannelObservers.add(observer);

        // Notification pour le nouvel observateur (messages)
        for (Message message : this.getMessages()) {
            observer.notifyMessageAdded(message);
        }

        // Notification pour le nouvel observateur (utilisateurs)
        for (User user : this.getUsers()) {
            // Pas de notification pour l'utilisateur inconnu
            if (!user.getUuid().equals(Constants.UNKNONWN_USER_UUID)) {
                observer.notifyUserAdded(user);
            }
        }

        // Notification pour le nouvel observateur (canaux)
        for (Channel channel : this.getChannels()) {
            observer.notifyChannelAdded(channel);
        }
    }

    /**
     * Enregistre un observateur spécialisé sur les messages.
     * (méthode additionnelle, non présente dans l'interface `IDatabase`)
     *
     * @param observer observateur de messages
     */
    public void addObserver(IMessageDatabaseObserver observer) {
        this.mMessageObservers.add(observer);

        // Notification initiale des messages existants
        for (Message message : this.getMessages()) {
            observer.notifyMessageAdded(message);
        }
    }

    /**
     * Enregistre un observateur spécialisé sur les utilisateurs.
     *
     * @param observer observateur d'utilisateurs
     */
    public void addObserver(IUserDatabaseObserver observer) {
        this.mUserObservers.add(observer);

        // Notification initiale des utilisateurs existants (excepté unknown)
        for (User user : this.getUsers()) {
            if (!user.getUuid().equals(Constants.UNKNONWN_USER_UUID)) {
                observer.notifyUserAdded(user);
            }
        }
    }

    /**
     * Enregistre un observateur spécialisé sur les canaux.
     *
     * @param observer observateur de canaux
     */
    public void addObserver(IChannelDatabaseObserver observer) {
        this.mChannelObservers.add(observer);

        // Notification initiale des canaux existants
        for (Channel channel : this.getChannels()) {
            observer.notifyChannelAdded(channel);
        }
    }

    @Override
    public void removeObserver(IDatabaseObserver observer) {
        this.mObservers.remove(observer);
        this.mMessageObservers.remove(observer);
        this.mUserObservers.remove(observer);
        this.mChannelObservers.remove(observer);
    }

    /**
     * Retire un observateur spécialisé sur les messages.
     *
     * @param observer observateur de messages
     */
    public void removeObserver(IMessageDatabaseObserver observer) {
        this.mMessageObservers.remove(observer);
    }

    /**
     * Retire un observateur spécialisé sur les utilisateurs.
     *
     * @param observer observateur d'utilisateurs
     */
    public void removeObserver(IUserDatabaseObserver observer) {
        this.mUserObservers.remove(observer);
    }

    /**
     * Retire un observateur spécialisé sur les canaux.
     *
     * @param observer observateur de canaux
     */
    public void removeObserver(IChannelDatabaseObserver observer) {
        this.mChannelObservers.remove(observer);
    }
}
