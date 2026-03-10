package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.ihm.view.service.View;

import java.util.function.Consumer;

/**
 * Interface pour le contrôleur de navigation entre les vues principales.
 */
public interface INavigationController extends Controller {

    /**
     * Navigue vers la vue de connexion.
     */
    void navigateToLogin();

    /**
     * Navigue vers la vue d'inscription.
     */
    void navigateToRegister();

    /**
     * Navigue vers la vue de profil (compte).
     */
    void navigateToProfile();

    /**
     * Navigue vers la vue principale de chat.
     */
    void navigateToChat();

    /**
     * Définit le consommateur permettant d'afficher la vue principale.
     *
     * @param mainView consommateur recevant la vue à afficher
     */
    void setMainView(Consumer<View> mainView);
}
