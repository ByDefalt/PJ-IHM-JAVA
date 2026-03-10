package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.datamodel.User;

/**
 * Interface du contrôleur gérant la mise à jour du compte utilisateur.
 */
public interface IUpdateAccountController extends Controller {

    /**
     * Appelé lorsque l'utilisateur valide la mise à jour de son profil (nom).
     *
     * @param newName nouveau nom à enregistrer
     * @return {@code true} si la mise à jour a réussi
     */
    boolean onUpdateNameClicked(String newName);

    /**
     * Retourne l'utilisateur actuellement connecté (lu depuis la session métier).
     *
     * @return utilisateur connecté ou {@code null}
     */
    User getConnectedUser();
}
