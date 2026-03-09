package com.ubo.tp.message.ihm.graphiccontroller.service;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IListCanalGraphicController extends GraphicController {
    /**
     * @param canal      canal à ajouter
     * @param onSelect   callback de sélection (clic gauche sur le canal)
     * @param onEdit     callback d'edition (bouton edit) -- null si non applicable
     * @param isOwner    true si l'utilisateur est le créateur du canal
     * @param allUsersSupplier fournisseur évalué au moment du clic (liste fraîche à chaque ouverture du menu)
     */
    void addCanal(Channel canal, Consumer<Channel> onSelect, ChannelEditCallback onEdit, boolean isOwner, Supplier<List<User>> allUsersSupplier);

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
     * Callback d'édition d'un canal : quitter, supprimer, ajouter ou retirer un membre.
     */
    interface ChannelEditCallback {
        /** Quitter le canal (membre non-propriétaire). */
        void onLeave(Channel channel);

        /** Supprimer le canal (propriétaire uniquement). */
        void onDelete(Channel channel);

        /** Ajouter un utilisateur au canal (propriétaire uniquement). */
        void onAddUser(Channel channel, User user);

        /** Retirer un utilisateur du canal (propriétaire uniquement). */
        void onRemoveUser(Channel channel, User user);
    }

    /**
     * Callback de création de canal : nom, privé, liste d'utilisateurs invités.
     */
    @FunctionalInterface
    interface ChannelCreationCallback {
        void onCreate(String name, boolean isPrivate, List<User> invitedUsers);
    }
}
