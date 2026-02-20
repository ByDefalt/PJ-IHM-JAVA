package com.ubo.tp.message.ihm.service;

import com.ubo.tp.message.datamodel.User;

public interface IListUserView extends View {

    void addUser(User user);

    void removeUser(User user);

    void updateUser(User user);
}

