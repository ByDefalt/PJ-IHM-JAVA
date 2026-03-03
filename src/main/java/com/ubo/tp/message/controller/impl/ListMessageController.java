package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListMessageController;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.core.selected.ISelectedObserver;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphicController.service.IListMessageGraphicController;

import java.util.*;

public class ListMessageController implements IListMessageController, IMessageDatabaseObserver, ISelectedObserver, IUserDatabaseObserver {

    private final ControllerContext context;
    private final IListMessageGraphicController graphicController;

    public ListMessageController(ControllerContext context, IListMessageGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        this.context.dataManager().addObserver(this);
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

    @Override
    public void notifyUserAdded(User addedUser) {

    }

    @Override
    public void notifyUserDeleted(User deletedUser) {

    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        
    }
}
