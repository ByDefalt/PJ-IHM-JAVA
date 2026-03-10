package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListMessageController;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.core.selected.ISelectedObserver;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListMessageGraphicController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.Collection;
import java.util.Objects;

/**
 * Contrôleur responsable de la liste des messages affichés dans la vue.
 * Fournit les opérations de filtrage et réagit aux événements de la base
 * (messages, utilisateurs) et aux changements de sélection.
 */
public class ListMessageController implements IListMessageController, IMessageDatabaseObserver, IUserDatabaseObserver, ISelectedObserver {

    private final ControllerContext context;
    private final IListMessageGraphicController graphicController;

    /**
     * Construit le contrôleur des messages.
     *
     * @param context           contexte applicatif
     * @param graphicController contrôleur graphique associé
     */
    public ListMessageController(ControllerContext context, IListMessageGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        this.context.dataManager().addObserver((IMessageDatabaseObserver) this);
        this.context.dataManager().addObserver((IUserDatabaseObserver) this);
        this.context.selected().addObserver(this);

        User me = context.session() != null ? context.session().getConnectedUser() : null;
        if (me != null) {
            graphicController.setOnDeleteMessage(this::deleteMessage, me.getUuid());
        }
    }

    /**
     * Supprime un message via le DataManager.
     *
     * @param message message à supprimer
     */
    private void deleteMessage(Message message) {
        if (message == null) return;
        context.dataManager().deleteMessageFile(message);
        if (context.logger() != null)
            context.logger().debug("Message supprimé par l'utilisateur : " + message.getUuid());
    }

    /**
     * Filtre les messages en fonction de la sélection courante.
     *
     * @param allMessages ensemble des messages disponible
     * @return liste filtrée et triée des messages à afficher
     */
    @Override
    public List<Message> getFilteredMessages(Collection<Message> allMessages) {
        List<Message> filtered = new ArrayList<>();
        if (allMessages == null || allMessages.isEmpty()) return filtered;

        var sel = context.selected();
        if (sel == null) return filtered;

        User selectedUser = sel.getSelectedUser();
        var selectedChannel = sel.getSelectedChannel();

        if (selectedUser == null && selectedChannel == null) return filtered;

        if (selectedUser != null) {
            filtered = filterForPrivateConversation(allMessages, selectedUser);
        } else {
            filtered = filterForChannel(allMessages, selectedChannel.getUuid());
        }

        filtered.sort(Comparator.comparingLong(Message::getEmissionDate));
        return filtered;
    }

    /**
     * Filtre pour une conversation privée entre l'utilisateur connecté et le sélectionné.
     *
     * @param allMessages  tous les messages
     * @param selectedUser utilisateur sélectionné
     * @return liste des messages pertinents
     */
    private List<Message> filterForPrivateConversation(Collection<Message> allMessages, User selectedUser) {
        List<Message> filtered = new ArrayList<>();
        User connectedUser = (context.session() != null) ? context.session().getConnectedUser() : null;
        if (connectedUser == null) return filtered;

        UUID selUuid = selectedUser.getUuid();
        UUID meUuid = connectedUser.getUuid();

        for (Message m : allMessages) {
            if (m == null) continue;
            boolean fromMeToSel = m.getSender() != null
                    && m.getSender().getUuid().equals(meUuid)
                    && m.getRecipient() != null
                    && m.getRecipient().equals(selUuid);
            boolean fromSelToMe = m.getSender() != null
                    && m.getSender().getUuid().equals(selUuid)
                    && m.getRecipient() != null
                    && m.getRecipient().equals(meUuid);
            if (fromMeToSel || fromSelToMe) filtered.add(m);
        }
        return filtered;
    }

    /**
     * Filtre pour un canal (messages dont le recipient est l'UUID du canal).
     *
     * @param allMessages collection de messages
     * @param channelUuid UUID du canal
     * @return messages destinés au canal
     */
    private List<Message> filterForChannel(Collection<Message> allMessages, UUID channelUuid) {
        List<Message> filtered = new ArrayList<>();
        for (Message m : allMessages) {
            if (m == null) continue;
            if (m.getRecipient() != null && m.getRecipient().equals(channelUuid)) filtered.add(m);
        }
        return filtered;
    }

    /**
     * Notification : un message a été ajouté.
     *
     * @param addedMessage message ajouté
     */
    @Override
    public void notifyMessageAdded(Message addedMessage) {
        handleNotifyMessageAddedLogic(addedMessage);
    }

    /**
     * Logique interne pour traiter l'ajout d'un message et mettre à jour la vue.
     *
     * @param addedMessage message ajouté
     */
    private void handleNotifyMessageAddedLogic(Message addedMessage) {
        if (context.logger() != null) context.logger().debug("Message ajouté : " + addedMessage);
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.addMessage(addedMessage, filtered);
    }

    /**
     * Notification : un message a été supprimé.
     *
     * @param deletedMessage message supprimé
     */
    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        handleNotifyMessageDeletedLogic(deletedMessage);
    }

    /**
     * Logique interne pour traiter la suppression d'un message et mettre à jour la vue.
     *
     * @param deletedMessage message supprimé
     */
    private void handleNotifyMessageDeletedLogic(Message deletedMessage) {
        if (context.logger() != null) context.logger().debug("Message suppression : " + deletedMessage);
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.removeMessage(deletedMessage, filtered);
    }

    /**
     * Notification : un message a été modifié.
     *
     * @param modifiedMessage message modifié
     */
    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        handleNotifyMessageModifiedLogic(modifiedMessage);
    }

    /**
     * Logique interne pour traiter la modification d'un message et mettre à jour la vue.
     *
     * @param modifiedMessage message modifié
     */
    private void handleNotifyMessageModifiedLogic(Message modifiedMessage) {
        if (context.logger() != null) context.logger().debug("Message update : " + modifiedMessage);
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.updateMessage(modifiedMessage, filtered);
    }

    /**
     * Notification : la sélection a changé (canal/utilisateur sélectionné).
     */
    @Override
    public void notifySelectedChanged() {
        handleNotifySelectedChangedLogic();
    }

    /**
     * Logique interne exécutée lorsqu'on change la sélection : recalcul et rafraîchissement.
     */
    private void handleNotifySelectedChangedLogic() {
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        if (context.logger() != null)
            context.logger().debug("Sélection changée, messages filtrés : " + filtered.size());
        this.graphicController.selectedChanged(filtered);
    }

    /**
     * Notification : un utilisateur a été ajouté (observateur utilisateur).
     *
     * @param addedUser nouvel utilisateur
     */
    @Override
    public void notifyUserAdded(User addedUser) {
        handleNotifyUserAddedLogic(addedUser);
    }

    /**
     * Logique interne pour la notification d'ajout d'utilisateur (aucune action principale).
     *
     * @param addedUser utilisateur ajouté
     */
    private void handleNotifyUserAddedLogic(User addedUser) {
        if (addedUser != null && context.logger() != null) context.logger().debug("notifyUserAdded received for: " + addedUser);
    }

    /**
     * Notification : un utilisateur a été supprimé.
     *
     * @param deletedUser utilisateur supprimé
     */
    @Override
    public void notifyUserDeleted(User deletedUser) {
        handleNotifyUserDeletedLogic(deletedUser);
    }

    /**
     * Logique interne pour traiter la suppression d'un utilisateur (création d'un ghostUser pour l'affichage).
     *
     * @param deletedUser utilisateur supprimé
     */
    private void handleNotifyUserDeletedLogic(User deletedUser) {
        if (deletedUser == null) return;
        if (context.logger() != null)
            context.logger().debug("User supprimé, mise à jour des messages : " + deletedUser);

        User ghostUser = new User(
                deletedUser.getUuid(),
                Constants.UNKNOWN_USER.getUserTag(),
                "--",
                Constants.UNKNOWN_USER.getName(),
                false
        );

        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.refreshSenderInMessages(ghostUser, filtered);

        filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.selectedChanged(filtered);
    }

    /**
     * Notification : un utilisateur a été modifié.
     *
     * @param modifiedUser utilisateur modifié
     */
    @Override
    public void notifyUserModified(User modifiedUser) {
        handleNotifyUserModifiedLogic(modifiedUser);
    }

    /**
     * Logique interne pour traiter la modification d'un utilisateur (rafraîchir les senders dans les messages).
     *
     * @param modifiedUser utilisateur modifié
     */
    private void handleNotifyUserModifiedLogic(User modifiedUser) {
        if (modifiedUser == null) return;
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.refreshSenderInMessages(modifiedUser, filtered);
    }
}
