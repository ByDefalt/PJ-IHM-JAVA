package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.INotificationController;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.datamodel.Message;

public class NotificationController implements INotificationController, IMessageDatabaseObserver {

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        if(addedMessage.getText().contains("@")) {
        }
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {

    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {

    }
}
