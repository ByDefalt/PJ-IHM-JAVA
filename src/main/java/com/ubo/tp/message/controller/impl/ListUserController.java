package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListUserController;
import com.ubo.tp.message.core.database.observer.IMessageDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListUserGraphicController;

import java.util.Objects;

/**
 * Contrôleur gérant la liste des utilisateurs affichée dans l'IHM.
 * <p>
 * Écoute les changements dans la base d'utilisateurs et la base de messages
 * pour mettre à jour la vue correspondante via le {@link IListUserGraphicController}.
 * </p>
 */
public class ListUserController implements IListUserController, IUserDatabaseObserver, IMessageDatabaseObserver {

    private final ControllerContext context;
    private final IListUserGraphicController graphicController;

    /**
     * Crée un {@code ListUserController}.
     */
    public ListUserController(ControllerContext context, IListUserGraphicController listUserView) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = listUserView;
        this.context.dataManager().addObserver((IUserDatabaseObserver) this);
        this.context.dataManager().addObserver((IMessageDatabaseObserver) this);
    }

    /**
     * Indique si l'objet User fourni correspond à l'utilisateur connecté.
     */
    @Override
    public boolean isCurrentUser(User user) {
        if (user == null || context.session() == null) return false;
        return user.equals(context.session().getConnectedUser());
    }

    /**
     * Appelé lorsqu'un utilisateur est ajouté à la base.
     */
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

    /**
     * Appelé lorsqu'un utilisateur est supprimé.
     */
    @Override
    public void notifyUserDeleted(User deletedUser) {
        handleNotifyUserDeletedLogic(deletedUser);
    }

    private void handleNotifyUserDeletedLogic(User deletedUser) {
        if (context.logger() != null) context.logger().debug("Utilisateur supprimé : " + deletedUser);
        this.graphicController.removeUser(deletedUser);
    }

    /**
     * Appelé lorsqu'un utilisateur est modifié.
     */
    @Override
    public void notifyUserModified(User modifiedUser) {
        handleNotifyUserModifiedLogic(modifiedUser);
    }

    private void handleNotifyUserModifiedLogic(User modifiedUser) {
        if (context.logger() != null) context.logger().debug("Utilisateur modifié : " + modifiedUser);
        this.graphicController.updateUser(modifiedUser);
    }

    /**
     * Réagit à l'ajout d'un message pour incrémenter un badge si nécessaire.
     */
    @Override
    public void notifyMessageAdded(com.ubo.tp.message.datamodel.Message addedMessage) {
        handleNotifyMessageAddedLogic(addedMessage);
    }

    private void handleNotifyMessageAddedLogic(com.ubo.tp.message.datamodel.Message addedMessage) {
        if (addedMessage == null) return;
        if (addedMessage.getRecipient() == null) return;
        if (context.session() == null || context.session().getConnectedUser() == null) return;

        var sel = context.selected();
        User selectedUser = sel != null ? sel.getSelectedUser() : null;

        if (selectedUser != null && addedMessage.getRecipient().equals(selectedUser.getUuid())) return;

        if (addedMessage.getRecipient().equals(context.session().getConnectedUser().getUuid())) {
            User sender = addedMessage.getSender();
            if (sender != null) {
                this.graphicController.incrementUnread(sender);
            }
        }
    }

    @Override
    public void notifyMessageDeleted(com.ubo.tp.message.datamodel.Message deletedMessage) {
        handleNotifyMessageDeletedLogic(deletedMessage);
    }

    private void handleNotifyMessageDeletedLogic(com.ubo.tp.message.datamodel.Message deletedMessage) {
        if (deletedMessage != null && context.logger() != null) context.logger().debug("Message supprimé : " + deletedMessage);
    }

    @Override
    public void notifyMessageModified(com.ubo.tp.message.datamodel.Message modifiedMessage) {
        handleNotifyMessageModifiedLogic(modifiedMessage);
    }

    private void handleNotifyMessageModifiedLogic(com.ubo.tp.message.datamodel.Message modifiedMessage) {
        if (modifiedMessage != null && context.logger() != null) context.logger().debug("Message modifié : " + modifiedMessage);
    }
}
