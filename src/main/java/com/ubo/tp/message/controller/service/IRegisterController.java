package com.ubo.tp.message.controller.service;

/**
 * Interface du contrôleur de la page d'enregistrement (Register).
 * Définit les callbacks que la vue peut invoquer.
 */
public interface IRegisterController extends Controller {
    /**
     * Callback invoqué lorsque l'utilisateur valide son inscription.
     * Le contrôleur doit créer l'utilisateur, gérer les erreurs et naviguer si nécessaire.
     */
    boolean onRegisterButtonClicked(String tag, String name, String password, String confirmPassword);
}
