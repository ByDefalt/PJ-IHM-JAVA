package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListMessageGraphicController;

import java.util.Collection;
import java.util.List;

public interface IListMessageController extends Controller {

    IListMessageGraphicController getGraphicController();

    /**
     * Filtre une collection de messages selon la sélection courante
     * (canal ou utilisateur). Logique purement métier : ne dépend d'aucune vue.
     *
     * @param allMessages tous les messages connus
     * @return la liste ordonnée des messages correspondant à la sélection
     */
    List<Message> getFilteredMessages(Collection<Message> allMessages);
}
