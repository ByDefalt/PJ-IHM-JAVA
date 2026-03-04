package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;
import com.ubo.tp.message.observableProperty.ObservableTreeSet;

public interface IListCanalController extends Controller {
    IListCanalGraphicController getGraphicController();
    ObservableTreeSet<Channel> getChannels();
}
