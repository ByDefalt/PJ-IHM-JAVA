package com.ubo.tp.message.ihm.service;

import com.ubo.tp.message.controller.service.IChatMainController;

public interface IChatMainView extends View {
    IListCanalView getListCanalView();

    IListUserView getListUserView();

    IListMessageView getListMessageView();

    IInputMessageView getInputMessageView();

    IChatMainController getController();
}

