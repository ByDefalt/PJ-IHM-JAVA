package com.ubo.tp.message.datamodel;

import java.util.UUID;

/**
 * Objet de base pour les entités du modèle de l'application qui possèdent
 * un identifiant unique {@link UUID}.
 * <p>
 * Fournit l'égalité et le calcul de hashCode basés sur l'UUID.
 * </p>
 */
public abstract class AbstractMessageAppObject extends Observable {
    /**
     * Identifiant unique de l'objet.
     */
    protected final UUID mUuid;

    /**
     * Constructeur.
     *
     * @param uuid Identifiant unique de l'objet (non-null).
     */
    public AbstractMessageAppObject(UUID uuid) {
        mUuid = uuid;
    }

    /**
     * Retourne l'identifiant unique de l'objet.
     *
     * @return UUID de l'objet
     */
    public UUID getUuid() {
        return this.mUuid;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;

        if (this.mUuid != null) {
            hashCode = this.mUuid.hashCode();
        }

        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        boolean equals = false;

        if (other != null && other instanceof AbstractMessageAppObject) {
            equals = (this.getUuid().equals(((AbstractMessageAppObject) other).getUuid()));
        }

        return equals;
    }
}
