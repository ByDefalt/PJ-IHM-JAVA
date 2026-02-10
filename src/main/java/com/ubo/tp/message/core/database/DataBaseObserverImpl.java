package com.ubo.tp.message.core.database;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

public class DataBaseObserverImpl implements IDatabaseObserver{
    @Override
    public void notifyMessageAdded(Message addedMessage) {
        System.out.println("[MESSAGE AJOUTÉ] " + addedMessage);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        System.out.println("[MESSAGE SUPPRIMÉ] " + deletedMessage);
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        System.out.println("[MESSAGE MODIFIÉ] " + modifiedMessage);
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        System.out.println("[UTILISATEUR AJOUTÉ] " + addedUser);
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        System.out.println("[UTILISATEUR SUPPRIMÉ] " + deletedUser);
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        System.out.println("[UTILISATEUR MODIFIÉ] " + modifiedUser);
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        System.out.println("[CANAL AJOUTÉ] " + addedChannel);
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        System.out.println("[CANAL SUPPRIMÉ] " + deletedChannel);
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        System.out.println("[CANAL MODIFIÉ] " + modifiedChannel);
    }
}
