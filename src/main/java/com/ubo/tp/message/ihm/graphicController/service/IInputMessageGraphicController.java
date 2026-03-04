package com.ubo.tp.message.ihm.graphicController.service;

import com.ubo.tp.message.observableProperty.ObservableProperty;

public interface IInputMessageGraphicController extends GraphicController {
    /**
     * @return La propriété observable du texte saisi par l'utilisateur.
     */
    ObservableProperty<String> getText();
}
