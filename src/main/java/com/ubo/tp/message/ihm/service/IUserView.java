package com.ubo.tp.message.ihm.service;


import com.ubo.tp.message.datamodel.User;

public interface IUserView extends View {
    User getUser();

    void updateUser(User user);
}
