package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IListUserController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphicController.service.IListUserGraphicController;
import com.ubo.tp.message.logger.Logger;

public class ListUserController implements IListUserController, IUserDatabaseObserver {

    private final Logger logger;
    private final IDataManager dataManager;
    private final IListUserGraphicController graphicController;

    public ListUserController(Logger logger, IDataManager dataManager, IListUserGraphicController listUserView) {
        this.logger = logger;
        this.dataManager = dataManager;
        this.graphicController = listUserView;
        this.dataManager.addObserver(this);
    }

    @Override
    public IListUserGraphicController getGraphicController() {
        return graphicController;
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        if (logger != null) logger.debug("Utilisateur ajouté : " + addedUser);
        this.graphicController.addUser(addedUser);
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        if (logger != null) logger.debug("Utilisateur supprimé : " + deletedUser);
        this.graphicController.removeUser(deletedUser);
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        if (logger != null) logger.debug("Utilisateur modifié : " + modifiedUser);
        this.graphicController.updateUser(modifiedUser);
    }
}
