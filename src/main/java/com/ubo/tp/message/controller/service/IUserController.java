package com.ubo.tp.message.controller.service;

/**
 * Interface minimum pour les actions relatives aux utilisateurs.
 */
public interface IUserController extends Controller{

    /**
     * Rafraîchit la liste des utilisateurs.
     */
    void refreshUsers();

    /**
     * Action appelée quand un utilisateur est sélectionné.
     * @param userTag identifiant (tag) de l'utilisateur
     */
    void onUserSelected(String userTag);

}

