package com.ubo.tp.message.ihm.graphiccontroller.service;

import com.ubo.tp.message.datamodel.User;

import java.util.function.Consumer;

/**
 * Contrat graphique pour la liste des utilisateurs affichés dans l'UI.
 */
public interface IListUserGraphicController extends GraphicController {

    /**
     * Ajoute un utilisateur et définit le callback appelé lors de la sélection.
     *
     * @param user  utilisateur à ajouter
     * @param added callback appelé avec l'utilisateur sélectionné
     */
    void addUser(User user, Consumer<User> added);

    /**
     * Supprime un utilisateur de la vue.
     *
     * @param user utilisateur à supprimer
     */
    void removeUser(User user);

    /**
     * Met à jour l'affichage d'un utilisateur.
     *
     * @param user utilisateur mis à jour
     */
    void updateUser(User user);

    /**
     * Incrémente le badge de messages non lus pour un utilisateur donné.
     *
     * @param user utilisateur ciblé
     */
    void incrementUnread(User user);

    /**
     * Remet à zéro le badge de messages non lus pour un utilisateur donné.
     *
     * @param user utilisateur ciblé
     */
    void clearUnread(User user);
}
