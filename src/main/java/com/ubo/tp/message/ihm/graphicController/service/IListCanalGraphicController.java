package com.ubo.tp.message.ihm.graphicController.service;

import com.ubo.tp.message.datamodel.Channel;

import java.util.function.Consumer;

public interface IListCanalGraphicController extends GraphicController {
    void addCanal(Channel canal, Consumer<Channel> consumer);

    void removeCanal(Channel canal);

    void updateCanal(Channel canal);
}

