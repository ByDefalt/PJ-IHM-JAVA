package com.ubo.tp.message.controller.service;

import java.util.UUID;

/**
 * Interface pour le contrôleur d'envoi de messages.
 * <p>
 * Définit les opérations d'envoi de message depuis l'IHM.
 * </p>
 */
public interface IInputMessageController extends Controller {

    /**
     * Envoie un message vers l'UUID spécifié (canal ou utilisateur).
     *
     * @param recipientUuid UUID du destinataire
     * @param message       contenu du message
     */
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
