package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphicController.service.IListUserGraphicController;

public interface IListUserController extends Controller {
    IListUserGraphicController getGraphicController();

    /**
     * Indique si l'utilisateur donné est l'utilisateur actuellement connecté.
     * Permet au graphic controller de filtrer sans accéder directement à la session.
     */
    boolean isCurrentUser(User user);
}
