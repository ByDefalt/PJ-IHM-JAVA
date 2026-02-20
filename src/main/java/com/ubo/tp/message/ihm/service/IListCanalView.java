package com.ubo.tp.message.ihm.service;

import com.ubo.tp.message.datamodel.Channel;

public interface IListCanalView extends View {
    void addCanal(Channel canal);

    void removeCanal(Channel canal);

    void updateCanal(Channel canal);
}

