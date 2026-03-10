package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListUserController;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListUserGraphicController;

import java.util.Objects;

public class ListUserController implements IListUserController, IUserDatabaseObserver, IMessageDatabaseObserver {

    private final ControllerContext context;
    private final IListUserGraphicController graphicController;

    public ListUserController(ControllerContext context, IListUserGraphicController listUserView) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = listUserView;
        this.context.dataManager().addObserver((IUserDatabaseObserver) this);
        this.context.dataManager().addObserver((IMessageDatabaseObserver) this);
    }

    @Override
    public IListUserGraphicController getGraphicController() {
        return graphicController;
    }

    @Override
    public boolean isCurrentUser(User user) {
        if (user == null || context.session() == null) return false;
        return user.equals(context.session().getConnectedUser());
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        handleNotifyUserAddedLogic(addedUser);
    }

    private void handleNotifyUserAddedLogic(User addedUser) {
        if (context.logger() != null) context.logger().debug("Utilisateur ajouté : " + addedUser);
        if (isCurrentUser(addedUser)) {
            if (context.logger() != null)
                context.logger().debug("Ignorer l'ajout de l'utilisateur courant: " + addedUser.getName());
            return;
        }
        this.graphicController.addUser(addedUser, this::setSelected);
    }

    private void setSelected(User selectedUser) {
        if (context.logger() != null) context.logger().debug("Sélectionner l'utilisateur : " + selectedUser);
        context.selected().setSelectedUser(selectedUser);
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        handleNotifyUserDeletedLogic(deletedUser);
    }

    private void handleNotifyUserDeletedLogic(User deletedUser) {
        if (context.logger() != null) context.logger().debug("Utilisateur supprimé : " + deletedUser);
        this.graphicController.removeUser(deletedUser);
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        handleNotifyUserModifiedLogic(modifiedUser);
    }

    private void handleNotifyUserModifiedLogic(User modifiedUser) {
        if (context.logger() != null) context.logger().debug("Utilisateur modifié : " + modifiedUser);
        this.graphicController.updateUser(modifiedUser);
    }

    @Override
    public void notifyMessageAdded(com.ubo.tp.message.datamodel.Message addedMessage) {
        handleNotifyMessageAddedLogic(addedMessage);
    }

    private void handleNotifyMessageAddedLogic(com.ubo.tp.message.datamodel.Message addedMessage) {
        if (addedMessage == null) return;
        // if the message is to a user (private conversation)
        if (addedMessage.getRecipient() == null) return;
        // recipient could be a user UUID; if it's equal to connected user's UUID, ignore
        if (context.session() == null || context.session().getConnectedUser() == null) return;

        // If currently selected user is the recipient or sender conversing with the recipient, don't badge
        var sel = context.selected();
        User selectedUser = sel != null ? sel.getSelectedUser() : null;

        // If selection is the same user conversation, skip
        if (selectedUser != null && addedMessage.getRecipient().equals(selectedUser.getUuid())) return;

        // If recipient is the connected user (incoming to me) and sender is another user: increment on sender
        if (addedMessage.getRecipient().equals(context.session().getConnectedUser().getUuid())) {
            User sender = addedMessage.getSender();
            if (sender != null) {
                this.graphicController.incrementUnread(sender);
            }
        }

        // Otherwise, if the recipient is some other user's UUID, we ignore (we only badge messages to me)
    }

    @Override
    public void notifyMessageDeleted(com.ubo.tp.message.datamodel.Message deletedMessage) {
        handleNotifyMessageDeletedLogic(deletedMessage);
    }

    private void handleNotifyMessageDeletedLogic(com.ubo.tp.message.datamodel.Message deletedMessage) {
        // no action
    }

    @Override
    public void notifyMessageModified(com.ubo.tp.message.datamodel.Message modifiedMessage) {
        handleNotifyMessageModifiedLogic(modifiedMessage);
    }

    private void handleNotifyMessageModifiedLogic(com.ubo.tp.message.datamodel.Message modifiedMessage) {
        // no action
    }
}
