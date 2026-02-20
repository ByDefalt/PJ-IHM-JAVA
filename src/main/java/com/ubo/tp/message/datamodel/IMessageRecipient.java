package com.ubo.tp.message.datamodel;

import java.util.UUID;

/**
 * Représente une destination possible d'un message (utilisateur ou canal).
 * Fournit uniquement l'identifiant unique de la destination.
 */
public interface IMessageRecipient {

    /**
     * Retourne l'identifiant de la destination.
     *
     * @return UUID de la destination
     */
    UUID getUuid();
}
