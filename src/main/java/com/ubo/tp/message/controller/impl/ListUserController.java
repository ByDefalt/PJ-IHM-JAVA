package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListUserController;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphicController.service.IListUserGraphicController;

import java.util.Objects;

public class ListUserController implements IListUserController, IUserDatabaseObserver {

    private final ControllerContext context;
    private final IListUserGraphicController graphicController;

    public ListUserController(ControllerContext context, IListUserGraphicController listUserView) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = listUserView;
        this.context.dataManager().addObserver(this);
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
        if (context.logger() != null) context.logger().debug("Utilisateur supprimé : " + deletedUser);
        this.graphicController.removeUser(deletedUser);
    }

    @Override
    public void notifyUserModified(User newUser) {
        if (context.logger() != null) context.logger().debug("Utilisateur modifié : nouveau=" + newUser);
        this.graphicController.updateUser(newUser);
    }
}
