package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.ihm.graphiccontroller.service.IAppMainGraphicController;

/**
 * Interface du contrôleur principal de l'application.
 * <p>
 * Expose l'accès au contrôleur graphique principal.
 * </p>
 */
public interface IAppMainController extends Controller {

    /**
     * Retourne le contrôleur graphique principal.
     *
     * @return instance d'IAppMainGraphicController
     */
    IAppMainGraphicController getGraphicController();
}
