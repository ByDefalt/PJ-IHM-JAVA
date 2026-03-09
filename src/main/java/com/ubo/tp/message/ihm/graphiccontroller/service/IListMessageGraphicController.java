package com.ubo.tp.message.ihm.graphiccontroller.service;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import java.util.List;
import java.util.function.Consumer;

public interface IListMessageGraphicController extends GraphicController {

    /**
     * Enregistre le callback appelé quand l'utilisateur clique sur 🗑 dans une MessageView,
     * ainsi que l'UUID de l'utilisateur connecté (seuls ses messages affichent le bouton).
     * Doit être appelé une fois avant tout addMessage.
     */
    void setOnDeleteMessage(Consumer<Message> onDelete, java.util.UUID connectedUserUuid);

    /**
     * Ajoute un message et reconstruit l'affichage avec la liste filtrée fournie
     * par le controller métier.
     */
    void addMessage(Message message, List<Message> filteredMessages);

    /**
     * Supprime un message et reconstruit l'affichage avec la liste filtrée fournie.
     */
    void removeMessage(Message message, List<Message> filteredMessages);

    /**
     * Met à jour un message et reconstruit l'affichage avec la liste filtrée fournie.
     */
    void updateMessage(Message message, List<Message> filteredMessages);

    /**
     * Met à jour le sender dans toutes les MessageView correspondantes (même UUID
     * que {@code updatedUser}), puis reconstruit l'affichage avec la liste filtrée.
     * <p>
     * À appeler quand un utilisateur est modifié (ex : changement de nom) afin que
     * tous ses messages affichent le nouveau nom.
     * </p>
     */
    void refreshSenderInMessages(User updatedUser, List<Message> filteredMessages);

    /**
     * Notifie que la sélection a changé.
     * La liste des messages à afficher (déjà filtrée par le controller métier)
     * est fournie directement pour éviter tout re-filtrage dans la couche vue.
     */
    void selectedChanged(List<Message> filteredMessages);
}
