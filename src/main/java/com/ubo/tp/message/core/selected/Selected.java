package com.ubo.tp.message.core.selected;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.List;

public class Selected implements ISelected {

    private final List<ISelectedObserver> observers = new ArrayList<>();
    private User selectedUser;
    private Channel selectedChannel;

    @Override
    public void addObserver(ISelectedObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(ISelectedObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public User getSelectedUser() {
        return selectedUser;
    }

    @Override
    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
        this.selectedChannel = null;
        for (ISelectedObserver observer : observers) {
            observer.notifySelectedChanged();
        }
    }

    @Override
    public Channel getSelectedChannel() {
        return selectedChannel;
    }

    @Override
    public void setSelectedChannel(Channel selectedChannel) {
        this.selectedChannel = selectedChannel;
        this.selectedUser = null;
        for (ISelectedObserver observer : observers) {
            observer.notifySelectedChanged();
        }
    }

    @Override
    public void clearSelectedChannel() {
        this.selectedChannel = null;
        this.selectedUser = null;
    }
}
