package com.ubo.tp.message.ihm.graphiccontroller.service;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;

import java.util.List;
import java.util.function.Consumer;

public interface IListCanalGraphicController extends GraphicController {
    /**
     * @param canal    canal à ajouter
     * @param onSelect callback de sélection (clic sur le canal)
     * @param onLeave  callback de la croix (quitter ou supprimer) — null si non applicable
     * @param isOwner  true si l'utilisateur est le créateur (croix = supprimer), false = quitter
     */
    void addCanal(Channel canal, Consumer<Channel> onSelect, Consumer<Channel> onLeave, boolean isOwner);

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

    /**
     * Callback de création de canal : nom, privé, liste d'utilisateurs invités.
     */
    @FunctionalInterface
    interface ChannelCreationCallback {
        void onCreate(String name, boolean isPrivate, List<User> invitedUsers);
    }
}



