package com.ubo.tp.message.factory.view;

import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.factory.Factory;
import com.ubo.tp.message.ihm.view.service.View;

/**
 * Interface de fabrique de vues — permet de découpler NavigationController
 * de toute implémentation graphique concrète (Swing ou JavaFX).
 */
public interface ViewFactory extends Factory {

    /**
     * Crée le contrôleur principal (IHM) de l'application.
     *
     * @return instance de {@link IAppMainController}
     */
    IAppMainController createAppMainController();

    /**
     * Construit la vue de connexion (login).
     *
     * @return vue de login
     */
    View createLoginView();

    /**
     * Construit la vue d'inscription (register).
     *
     * @return vue d'enregistrement
     */
    View createRegisterView();

    /**
     * Construit la vue de mise à jour du compte (profil).
     *
     * @return vue de mise à jour du compte
     */
    View createUpdateAccountView();

    /**
     * Construit la vue principale de chat regroupant les composants (canaux, messages, input, users).
     *
     * @return vue principale de chat
     */
    View createChatMainView();
}
