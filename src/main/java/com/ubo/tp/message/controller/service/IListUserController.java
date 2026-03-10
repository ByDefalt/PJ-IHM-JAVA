package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.datamodel.User;

/**
 * Interface pour le contrôleur gérant la liste des utilisateurs.
 */
public interface IListUserController extends Controller {

    /**
     * Indique si l'utilisateur donné est l'utilisateur actuellement connecté.
     * Permet au graphic controller de filtrer sans accéder directement à la session.
     *
     * @param user utilisateur à comparer
     * @return {@code true} si c'est l'utilisateur connecté
     */
    boolean isCurrentUser(User user);
}
