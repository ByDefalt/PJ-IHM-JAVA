package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.ihm.graphiccontroller.service.IAppMainGraphicController;

public interface IAppMainController extends Controller {
    IAppMainGraphicController getGraphicController();

    /**
     * Enregistre un callback appelé par la vue quand l'utilisateur ferme l'application.
     * Le controller décide ensuite de déconnecter, puis de quitter.
     */
    void setOnClose(Runnable onClose);
}
