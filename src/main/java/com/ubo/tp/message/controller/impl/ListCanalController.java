package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;
import com.ubo.tp.message.logger.Logger;

public class ListCanalController implements IListCanalController, IChannelDatabaseObserver {

    private final Logger LOGGER;
    private final IDataManager dataManager;
    private final IListCanalGraphicController graphicController;

    public ListCanalController(Logger logger, IDataManager dataManager, IListCanalGraphicController graphicController) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.graphicController = graphicController;
        this.dataManager.addObserver(this);
    }

    @Override
    public IListCanalGraphicController getGraphicController() {
        return graphicController;
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        if (LOGGER != null) LOGGER.debug("Canal ajouté : " + addedChannel);
        this.graphicController.addCanal(addedChannel);
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        if (LOGGER != null) LOGGER.debug("Canal supprimé : " + deletedChannel);
        this.graphicController.removeCanal(deletedChannel);
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        if (LOGGER != null) LOGGER.debug("Canal modifié : " + modifiedChannel);
        this.graphicController.updateCanal(modifiedChannel);
    }
}
