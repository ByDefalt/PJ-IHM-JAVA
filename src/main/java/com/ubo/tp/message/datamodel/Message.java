package com.ubo.tp.message.datamodel;

import java.util.UUID;

/**
 * Modèle représentant un message échangé dans l'application.
 * <p>
 * Contient l'émetteur (User), l'identifiant du destinataire (UUID), la date
 * d'émission et le texte du message.
 * </p>
 */
public class Message extends AbstractMessageAppObject {

    /**
     * Utilisateur source du message.
     */
    protected final User mSender;

    /**
     * Destinataire du message (UUID d'un User ou d'un Channel).
     */
    protected final UUID mRecipient;

    /**
     * Date d'émission du message (millis epoch).
     */
    protected final long mEmissionDate;

    /**
     * Corps du message.
     */
    protected final String mText;

    /**
     * Constructeur convenience : génère un UUID et utilise la date courante.
     *
     * @param sender    utilisateur à l'origine du message
     * @param recipient identifiant du destinataire (user ou channel)
     * @param text      corps du message
     */
    public Message(User sender, UUID recipient, String text) {
        this(UUID.randomUUID(), sender, recipient, System.currentTimeMillis(), text);
    }

    /**
     * Constructeur complet.
     *
     * @param messageUuid  identifiant du message
     * @param sender       utilisateur émetteur
     * @param recipient    identifiant du destinataire
     * @param emissionDate date d'émission en millis
     * @param text         contenu du message
     */
    public Message(UUID messageUuid, User sender, UUID recipient, long emissionDate, String text) {
        super(messageUuid);
        mSender = sender;
        mRecipient = recipient;
        mEmissionDate = emissionDate;
        mText = text;
    }

    /**
     * @return l'utilisateur source du message.
     */
    public User getSender() {
        return mSender;
    }

    /**
     * @return le destinataire du message.
     */
    public UUID getRecipient() {
        return mRecipient;
    }

    /**
     * @return le corps du message.
     */
    public String getText() {
        return mText;
    }

    /**
     * Retourne la date d'émission sous forme de timestamp en millisecondes.
     *
     * @return timestamp d'émission
     */
    public long getEmissionDate() {
        return this.mEmissionDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return "[" +
                this.getClass().getName() +
                "] : " +
                this.getUuid() +
                " {" +
                this.getText() +
                "}";
    }
}
