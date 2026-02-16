package com.ubo.tp.message.ihm.service;

public interface IChatMainView extends View{
    IListCanalView getListCanalView();
    IListUserView getListUserView();
    IListMessageView getListMessageView();
    IInputMessageView getInputMessageView();
}

