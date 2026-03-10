package com.ubo.tp.message.ihm.graphiccontroller.service;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;

import java.util.List;
import java.util.function.Consumer;

/**
 * Contrat graphique pour la liste des messages.
 *
 * La couche métier fournit une liste filtrée de messages à afficher ;
 * la couche graphique se contente d'afficher et de gérer les interactions.
 */
public interface IListMessageGraphicController extends GraphicController {

    /**
     * Enregistre le callback appelé quand l'utilisateur clique sur la poubelle
     * dans une MessageView, ainsi que l'UUID de l'utilisateur connecté
     * (seuls ses messages affichent le bouton). Doit être appelé une fois avant tout addMessage.
     *
     * @param onDelete          callback de suppression
     * @param connectedUserUuid UUID de l'utilisateur connecté
     */
    void setOnDeleteMessage(Consumer<Message> onDelete, java.util.UUID connectedUserUuid);

    /**
     * Ajoute un message et reconstruit l'affichage avec la liste filtrée fournie
     * par le controller métier.
     *
     * @param message          message à ajouter
     * @param filteredMessages liste filtrée fournie par le controller métier
     */
    void addMessage(Message message, List<Message> filteredMessages);

    /**
     * Supprime un message et reconstruit l'affichage avec la liste filtrée fournie.
     *
     * @param message          message à supprimer
     * @param filteredMessages liste filtrée fournie par le controller métier
     */
    void removeMessage(Message message, List<Message> filteredMessages);

    /**
     * Met à jour un message et reconstruit l'affichage avec la liste filtrée fournie.
     *
     * @param message          message mis à jour
     * @param filteredMessages liste filtrée fournie par le controller métier
     */
    void updateMessage(Message message, List<Message> filteredMessages);

    /**
     * Met à jour le sender dans toutes les MessageView correspondantes (même UUID
     * que {@code updatedUser}), puis reconstruit l'affichage avec la liste filtrée.
     *
     * @param updatedUser      utilisateur modifié
     * @param filteredMessages liste filtrée fournie par le controller métier
     */
    void refreshSenderInMessages(User updatedUser, List<Message> filteredMessages);

    /**
     * Notifie que la sélection a changé. La liste des messages à afficher (déjà
     * filtrée par le controller métier) est fournie directement pour éviter tout
     * re-filtrage dans la couche vue.
     *
     * @param filteredMessages liste filtrée fournie par le controller métier
     */
    void selectedChanged(List<Message> filteredMessages);
}
