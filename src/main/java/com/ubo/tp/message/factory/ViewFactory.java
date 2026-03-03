package com.ubo.tp.message.factory;

import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.ihm.view.service.View;

/**
 * Interface de fabrique de vues — permet de découpler NavigationController
 * de toute implémentation graphique concrète (Swing ou JavaFX).
 */
public interface ViewFactory extends Factory {

    IAppMainController createAppMainController();

    View createLoginView();

    View createRegisterView();

    View createUpdateAccountView();

    View createChatMainView();
}

