package com.ubo.tp.message.core;

import java.util.Set;

import com.ubo.tp.message.core.database.IDatabaseObserver;
import com.ubo.tp.message.datamodel.IMessageRecipient;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

/**
 * Interface exposant les opérations principales de gestion des données de
 * l'application. Permet d'abstraire l'implémentation concrète `DataManager`.
 */
public interface IDataManager {

    void addObserver(IDatabaseObserver observer);
    void removeObserver(IDatabaseObserver observer);

    Set<User> getUsers();
    Set<Message> getMessages();
    Set<com.ubo.tp.message.datamodel.Channel> getChannels();

    void sendMessage(Message message);
    void sendUser(User user);
    void sendChannel(com.ubo.tp.message.datamodel.Channel channel);

    Set<Message> getMessagesFrom(User user);
    Set<Message> getMessagesFrom(User sender, IMessageRecipient recipient);
    Set<Message> getMessagesTo(User user);

    void setExchangeDirectory(String directoryPath);
}
