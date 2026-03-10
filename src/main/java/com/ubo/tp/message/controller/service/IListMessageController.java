package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.datamodel.Message;

import java.util.Collection;
import java.util.List;

/**
 * Interface pour le contrôleur gérant les messages affichés.
 */
public interface IListMessageController extends Controller {

    /**
     * Filtre une collection de messages selon la sélection courante
     * (canal ou utilisateur). Logique purement métier : ne dépend d'aucune vue.
     *
     * @param allMessages tous les messages connus
     * @return la liste ordonnée des messages correspondant à la sélection
     */
    List<Message> getFilteredMessages(Collection<Message> allMessages);
}
