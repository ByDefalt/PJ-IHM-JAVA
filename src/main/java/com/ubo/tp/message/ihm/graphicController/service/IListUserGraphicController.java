package com.ubo.tp.message.ihm.graphicController.service;

import com.ubo.tp.message.datamodel.User;

public interface IListUserGraphicController extends GraphicController {
    void addUser(User user);

    void removeUser(User user);

    void updateUser(User user);
}
