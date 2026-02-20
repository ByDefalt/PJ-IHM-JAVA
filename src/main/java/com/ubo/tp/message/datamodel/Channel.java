package com.ubo.tp.message.datamodel;

import java.util.*;

/**
 * Modèle représentant un canal de discussion.
 * <p>
 * Un canal possède un créateur, un nom et une liste d'utilisateurs. Il peut
 * être public ou privé (si une liste d'utilisateurs est fournie).
 * </p>
 *
 * @author S.Lucas
 */
public class Channel extends AbstractMessageAppObject implements IMessageRecipient {

    /**
     * Créateur du canal.
     */
    protected final User mCreator;

    /**
     * Nom du canal.
     */
    protected final String mName;
    /**
     * Liste des Utilisateurs du canal.
     */
    protected final Set<User> mUsers = new HashSet<User>();
    /**
     * Statut privé ou public du canal.
     */
    protected boolean mPrivate;

    /**
     * Constructeur public : crée un canal avec un UUID aléatoire.
     *
     * @param creator utilisateur créateur du canal
     * @param name    nom du canal
     */
    public Channel(User creator, String name) {
        this(UUID.randomUUID(), creator, name);
    }

    /**
     * Constructeur complet avec UUID explicite.
     *
     * @param channelUuid identifiant du canal
     * @param creator     utilisateur créateur
     * @param name        nom du canal
     */
    public Channel(UUID channelUuid, User creator, String name) {
        super(channelUuid);
        mCreator = creator;
        mName = name;
    }

    /**
     * Constructeur pour canal privé avec liste d'utilisateurs.
     *
     * @param creator utilisateur créateur
     * @param name    nom du canal
     * @param users   liste d'utilisateurs autorisés
     */
    public Channel(User creator, String name, List<User> users) {
        this(UUID.randomUUID(), creator, name, users);
    }

    /**
     * Constructeur complet pour canal privé.
     *
     * @param messageUuid identifiant du canal
     * @param creator     utilisateur créateur
     * @param name        nom du canal
     * @param users       liste des utilisateurs autorisés
     */
    public Channel(UUID messageUuid, User creator, String name, List<User> users) {
        this(messageUuid, creator, name);
        if (!users.isEmpty()) {
            mPrivate = true;
            mUsers.addAll(users);
        }
    }

    /**
     * @return l'utilisateur source du canal.
     */
    public User getCreator() {
        return mCreator;
    }

    /**
     * @return le nom du canal.
     */
    public String getName() {
        return mName;
    }

    /**
     * @return la liste des utilisateurs de ce canal (copie).
     */
    public List<User> getUsers() {
        return new ArrayList<User>(mUsers);
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
                this.getName() +
                "}";
    }

}
