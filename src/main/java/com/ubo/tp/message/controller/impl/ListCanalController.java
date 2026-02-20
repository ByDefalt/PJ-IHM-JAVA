package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.service.IListCanalView;
import com.ubo.tp.message.logger.Logger;

public class ListCanalController implements IListCanalController, IChannelDatabaseObserver {

    private final Logger LOGGER;
    private final IDataManager dataManager;
    private final IListCanalView view;

    public ListCanalController(Logger logger, IDataManager dataManager, IListCanalView view) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.view = view;
        this.dataManager.addObserver(this);
    }

    @Override
    public IListCanalView getView() {
        return view;
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        if (LOGGER != null) LOGGER.debug("Canal ajouté : " + addedChannel);
        this.view.addCanal(addedChannel);
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        if (LOGGER != null) LOGGER.debug("Canal supprimé : " + deletedChannel);
        this.view.removeCanal(deletedChannel);
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        if (LOGGER != null) LOGGER.debug("Canal modifié : " + modifiedChannel);
        this.view.updateCanal(modifiedChannel);
    }
}
