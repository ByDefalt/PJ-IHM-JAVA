package com.ubo.tp.message.controller.service;

import java.util.UUID;

public interface IInputMessageController extends Controller {

    void sendMessage(UUID recipientUuid, String message);

    /**
     * Envoie un message au destinataire actuellement sélectionné
     * (canal ou utilisateur). La résolution du destinataire est
     * une responsabilité métier, elle est donc traitée ici plutôt
     * que dans la couche graphique.
     *
     * @param text contenu du message saisi
     */
    void sendMessageToSelected(String text);
}
