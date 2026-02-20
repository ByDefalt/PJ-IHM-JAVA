package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IListUserController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.service.IListUserView;
import com.ubo.tp.message.logger.Logger;

public class ListUserController implements IListUserController, IUserDatabaseObserver {

    private final Logger logger;
    private final IDataManager dataManager;
    private final IListUserView listUserView;

    public ListUserController(Logger logger, IDataManager dataManager, IListUserView listUserView) {
        this.logger = logger;
        this.dataManager = dataManager;
        this.listUserView = listUserView;
        this.dataManager.addObserver(this);
    }

    @Override
    public IListUserView getView() {
        return listUserView;
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        if (logger != null) logger.debug("Utilisateur ajouté : " + addedUser);
        this.listUserView.addUser(addedUser);
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        if (logger != null) logger.debug("Utilisateur supprimé : " + deletedUser);
        this.listUserView.removeUser(deletedUser);
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        if (logger != null) logger.debug("Utilisateur modifié : " + modifiedUser);
        this.listUserView.updateUser(modifiedUser);
    }
}
