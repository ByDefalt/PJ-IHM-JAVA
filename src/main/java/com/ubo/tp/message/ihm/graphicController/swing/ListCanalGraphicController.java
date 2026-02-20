package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListCanalView;
import com.ubo.tp.message.logger.Logger;

public class ListCanalGraphicController implements IListCanalGraphicController {

    private final Logger LOGGER;
    private final ListCanalView listCanalView;

    public ListCanalGraphicController(Logger logger, ListCanalView listCanalView) {
        LOGGER = logger;
        this.listCanalView = listCanalView;
    }

    @Override
    public void addCanal(Channel canal) {
        if (listCanalView != null) listCanalView.addCanal(canal);
    }

    @Override
    public void removeCanal(Channel canal) {
        if (listCanalView != null) listCanalView.removeCanal(canal);
    }

    @Override
    public void updateCanal(Channel canal) {
        if (listCanalView != null) listCanalView.updateCanal(canal);
    }
}
