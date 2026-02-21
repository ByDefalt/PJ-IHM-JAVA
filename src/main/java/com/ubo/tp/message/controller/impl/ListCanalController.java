package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;

import java.util.Objects;

public class ListCanalController implements IListCanalController, IChannelDatabaseObserver {

    private final ControllerContext context;
    private final IListCanalGraphicController graphicController;

    public ListCanalController(ControllerContext context, IListCanalGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;
        this.context.dataManager().addObserver(this);
    }

    @Override
    public IListCanalGraphicController getGraphicController() {
        return graphicController;
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        if (context.logger() != null) context.logger().debug("Canal ajouté : " + addedChannel);
        this.graphicController.addCanal(addedChannel);
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        if (context.logger() != null) context.logger().debug("Canal supprimé : " + deletedChannel);
        this.graphicController.removeCanal(deletedChannel);
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        if (context.logger() != null) context.logger().debug("Canal modifié : " + modifiedChannel);
        this.graphicController.updateCanal(modifiedChannel);
    }
}
