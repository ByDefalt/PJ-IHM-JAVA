package com.ubo.tp.message.ihm.graphicController.service;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;

import java.util.List;
import java.util.function.Consumer;

public interface IListCanalGraphicController extends GraphicController {
    void addCanal(Channel canal, Consumer<Channel> consumer);

    void removeCanal(Channel canal);

    void updateCanal(Channel canal);

    /**
     * Enregistre le callback et la liste d'utilisateurs disponibles pour le formulaire
     * de création de canal. Le formulaire s'ouvre via le menu contextuel clic droit.
     *
     * @param availableUsers utilisateurs disponibles (sans l'utilisateur connecté)
     * @param onConfirm      callback appelé avec (nom, estPrivé, usersInvités)
     */
    void setupNewChannelForm(List<User> availableUsers, ChannelCreationCallback onConfirm);

    /** Callback de création de canal : nom, privé, liste d'utilisateurs invités. */
    @FunctionalInterface
    interface ChannelCreationCallback {
        void onCreate(String name, boolean isPrivate, List<User> invitedUsers);
    }
}



