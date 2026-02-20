package com.ubo.tp.message.datamodel;

import java.util.UUID;

/**
 * Modèle représentant un utilisateur de l'application.
 * <p>
 * Contient un tag unique, mot de passe, nom affiché et un statut de connexion.
 * </p>
 */
public class User extends AbstractMessageAppObject implements IMessageRecipient {

    /**
     * Tag non modifiable correspondant à l'utilisateur. <br/>
     * <i>Doit être unique dans le système</i>
     */
    protected final String mUserTag;

    /**
     * Mot de passe de l'utilisateur.
     */
    protected String mUserPassword;

    /**
     * Nom de l'utilisateur.
     */
    protected String mName;

    /**
     * Booléen indiquant si l'utilisateur est connecté.
     */
    protected boolean mOnline = false;

    /**
     * Constructeur convenience : génère un UUID aléatoire.
     *
     * @param userTag      tag unique de l'utilisateur
     * @param userPassword mot de passe
     * @param name         nom affiché
     */
    public User(String userTag, String userPassword, String name) {
        this(UUID.randomUUID(), userTag, userPassword, name);
    }

    /**
     * Constructeur complet.
     *
     * @param uuid         identifiant unique
     * @param userTag      tag unique
     * @param userPassword mot de passe
     * @param name         nom affiché
     */
    public User(UUID uuid, String userTag, String userPassword, String name) {
        super(uuid);
        mUserTag = userTag;
        mUserPassword = userPassword;
        mName = name;
    }

    /**
     * Retourne le nom de l'utilisateur.
     *
     * @return nom affiché
     */
    public String getName() {
        return mName;
    }

    /**
     * Modifie le nom affiché de l'utilisateur.
     *
     * @param name nouveau nom
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * Retourne le tag unique de l'utilisateur.
     *
     * @return tag unique
     */
    public String getUserTag() {
        return this.mUserTag;
    }

    /**
     * Retourne le mot de passe (brut). Utiliser uniquement pour sérialisation/échange.
     *
     * @return mot de passe
     */
    public String getUserPassword() {
        return this.mUserPassword;
    }

    /**
     * Modifie le mot de passe de l'utilisateur.
     *
     * @param userPassword nouveau mot de passe
     */
    public void setUserPassword(String userPassword) {
        this.mUserPassword = userPassword;
    }

    /**
     * @return true si l'utilisateur est connecté
     */
    public boolean isOnline() {
        return this.mOnline;
    }

    /**
     * Définit le statut en ligne de l'utilisateur.
     *
     * @param online flag en ligne
     */
    public void setOnline(boolean online) {
        this.mOnline = online;
    }

    @Override
    public String toString() {

        return "User{" +
                "uuid=" + getUuid() +
                ", userTag='" + mUserTag + '\'' +
                ", name='" + mName + '\'' +
                ", online=" + mOnline +
                '}';
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return getUuid().equals(user.getUuid());
    }

    @Override
    public int hashCode() {
        // Utilise uniquement l'UUID pour rester cohérent avec equals()
        return super.hashCode();
    }
}
