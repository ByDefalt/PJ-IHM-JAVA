package com.ubo.tp.message.ihm.service;

import java.util.List;

public interface IListCanalView extends View{
    void setCanals(List<ICanalView> newCanals);
    void addCanal(ICanalView canal);
    void clearCanals();
}

