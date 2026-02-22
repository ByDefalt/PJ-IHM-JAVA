package com.ubo.tp.message.controller.service;

public interface IUpdateAccountController extends Controller {
    /**
     * Appelé lorsque l'utilisateur valide la mise à jour de son profil (nom).
     *
     * @param newName nouveau nom à enregistrer
     */
    boolean onUpdateNameClicked(String newName);
}
