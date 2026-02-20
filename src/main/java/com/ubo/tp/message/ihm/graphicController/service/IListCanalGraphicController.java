package com.ubo.tp.message.ihm.graphicController.service;

import com.ubo.tp.message.datamodel.Channel;

public interface IListCanalGraphicController extends GraphicController {
    void addCanal(Channel canal);

    void removeCanal(Channel canal);

    void updateCanal(Channel canal);
}

