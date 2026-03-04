package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListMessageController;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.core.selected.ISelectedObserver;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphicController.service.IListMessageGraphicController;

import java.util.*;

public class ListMessageController implements IListMessageController, IMessageDatabaseObserver, IUserDatabaseObserver, ISelectedObserver {

    private final ControllerContext context;
    private final IListMessageGraphicController graphicController;

    public ListMessageController(ControllerContext context, IListMessageGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        this.context.dataManager().addObserver((IMessageDatabaseObserver) this);
        this.context.dataManager().addObserver((IUserDatabaseObserver) this);
        this.context.selected().addObserver(this);
    }

    @Override
    public IListMessageGraphicController getGraphicController() {
        return graphicController;
    }

    /**
     * Filtre métier : retourne les messages visibles selon la sélection courante
     * (canal ou conversation privée). Aucune dépendance à la couche vue.
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
            // Conversation privée : messages entre l'utilisateur connecté et le sélectionné
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
        } else {
            // Canal : messages dont le destinataire est ce canal
            UUID channelUuid = selectedChannel.getUuid();
            for (Message m : allMessages) {
                if (m == null) continue;
                if (m.getRecipient() != null && m.getRecipient().equals(channelUuid)) filtered.add(m);
            }
        }

        filtered.sort(Comparator.comparingLong(Message::getEmissionDate));
        return filtered;
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        if (context.logger() != null) context.logger().debug("Message ajouté : " + addedMessage);
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.addMessage(addedMessage, filtered);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        if (context.logger() != null) context.logger().debug("Message suppression : " + deletedMessage);
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.removeMessage(deletedMessage, filtered);
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        if (context.logger() != null) context.logger().debug("Message update : " + modifiedMessage);
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.updateMessage(modifiedMessage, filtered);
    }

    @Override
    public void notifySelectedChanged() {
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        if (context.logger() != null)
            context.logger().debug("Sélection changée, messages filtrés : " + filtered.size());
        this.graphicController.selectedChanged(filtered);
    }

    // -------------------------------------------------------------------------
    // IUserDatabaseObserver
    // -------------------------------------------------------------------------

    @Override
    public void notifyUserAdded(User addedUser) {
        // Rien à faire : les messages du nouvel utilisateur arrivent via notifyMessageAdded
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        if (deletedUser == null) return;
        if (context.logger() != null) context.logger().debug("User supprimé, mise à jour des messages : " + deletedUser);

        // Crée un "fantôme" UNKNOWN_USER qui conserve l'UUID du user supprimé
        // pour que refreshSenderInMessages trouve les bonnes MessageView
        User ghostUser = new User(
                deletedUser.getUuid(),
                Constants.UNKNOWN_USER.getUserTag(),
                "--",
                Constants.UNKNOWN_USER.getName(),
                false
        );

        // 1) Remplace le sender dans toutes les bulles qui référencent ce user
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.refreshSenderInMessages(ghostUser, filtered);

        // 2) Reconstruit la vue (gère le cas où la conversation affichée était avec ce user)
        filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.selectedChanged(filtered);
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        if (modifiedUser == null) return;
        // Le graphic controller met à jour son TreeSet interne pour tous les messages
        // dont le sender correspond à modifiedUser, puis reconstruit l'affichage
        // avec la filtered list propre à ce controller (session + selected).
        List<Message> filtered = getFilteredMessages(context.dataManager().getMessages());
        this.graphicController.refreshSenderInMessages(modifiedUser, filtered);
    }
}
