package com.ubo.tp.message.ihm.graphiccontroller.service;

import com.ubo.tp.message.datamodel.User;

import java.util.function.Consumer;

public interface IListUserGraphicController extends GraphicController {
    void addUser(User user, Consumer<User> added);

    void removeUser(User user);

    void updateUser(User user);

    /** Incrémente le badge de messages non lus pour un utilisateur donné. */
    void incrementUnread(User user);

    /** Remet à zéro le badge de messages non lus pour un utilisateur donné. */
    void clearUnread(User user);
}
