package com.ubo.tp.message.controller.service;

/**
 * Interface du contrôleur de la page de connexion (Login).
 * <p>
 * Définit les callbacks attendus depuis la vue de login (actions utilisateur)
 * afin de séparer la logique métier/contrôleur de la vue.
 * </p>
 */
public interface ILoginController extends Controller {

    /**
     * Callback invoqué lorsque l'utilisateur demande l'écran d'enregistrement.
     * Le contrôleur doit déclencher la navigation appropriée.
     */
    void onRegisterButtonClicked();

    /**
     * Callback invoqué lorsque l'utilisateur valide le formulaire de connexion.
     * Le contrôleur doit authentifier l'utilisateur et naviguer si nécessaire.
     */
    void onLoginButtonClicked(String tag, String name, String password);
}
