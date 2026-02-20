package com.ubo.tp.message.core.session;

import com.ubo.tp.message.datamodel.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Session de l'application.
 *
 * @author S.Lucas
 */
public class Session implements ISession {

    /**
     * Utilisateur connecté
     */
    protected User mConnectedUser;

    /**
     * Liste des observateurs de la session.
     */
    protected List<ISessionObserver> mObservers = new ArrayList<>();

    @Override
    public void addObserver(ISessionObserver observer) {
        this.mObservers.add(observer);
    }

    @Override
    public void removeObserver(ISessionObserver observer) {
        this.mObservers.remove(observer);
    }

    @Override
    public User getConnectedUser() {
        return mConnectedUser;
    }

    @Override
    public void connect(User connectedUser) {
        this.mConnectedUser = connectedUser;

        for (ISessionObserver observer : mObservers) {
            observer.notifyLogin(connectedUser);
        }
    }

    @Override
    public void disconnect() {
        this.mConnectedUser = null;

        for (ISessionObserver observer : mObservers) {
            observer.notifyLogout();
        }
    }
}
