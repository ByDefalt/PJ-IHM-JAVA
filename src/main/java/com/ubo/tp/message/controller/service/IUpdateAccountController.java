package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.datamodel.User;

public interface IUpdateAccountController extends Controller {
    /**
     * Appelé lorsque l'utilisateur valide la mise à jour de son profil (nom).
     *
     * @param newName nouveau nom à enregistrer
     */
    boolean onUpdateNameClicked(String newName);

    /**
     * Retourne l'utilisateur actuellement connecté (lu depuis la session métier).
     */
    User getConnectedUser();
}
