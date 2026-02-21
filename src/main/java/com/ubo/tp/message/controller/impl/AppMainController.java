package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.factory.ComposantSwingFactory;
import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.logger.Logger;

/**
 * Contrôleur pour la vue principale de l'application.
 * <p>
 * Ce contrôleur orchestre l'initialisation de la vue principale et expose les
 * actions nécessaires à l'IHM (ex : sélection du répertoire d'échange).
 * </p>
 */
public class AppMainController implements IAppMainController {

    private final Logger logger;

    private final IDataManager dataManager;
    private final ISession session;
    private final IAppMainGraphicController graphicController;

    /**
     * Constructeur permettant l'injection d'une vue (utile pour tests).
     *
     * @param dataManager       service d'accès aux données
     * @param logger            logger de l'application
     * @param graphicController vue principale injectée
     */
    public AppMainController(IDataManager dataManager, Logger logger, ISession session, IAppMainGraphicController graphicController) {
        this.dataManager = dataManager;
        this.logger = logger;
        this.session = session;
        this.graphicController = graphicController;

        // Connecter le callback de la vue à la logique du contrôleur
        this.graphicController.setOnExchangeDirectorySelected(this::onExchangeDirectorySelected);

        this.graphicController.setMainContent(
                ComposantSwingFactory.createLoginView(
                        logger,
                        dataManager,
                        new NavigationController(logger, dataManager, session,this.graphicController.getAppMainView()),
                        session
                )
        );

        this.graphicController.setVisibility(true);
    }

    /**
     * Callback appelé lorsque l'utilisateur choisit un répertoire d'échange.
     *
     * @param directoryPath chemin du répertoire sélectionné
     */
    private void onExchangeDirectorySelected(String directoryPath) {
        logger.info("Controller: répertoire sélectionné -> " + directoryPath);
        dataManager.setExchangeDirectory(directoryPath);
    }

    public IAppMainGraphicController getGraphicController() {
        return this.graphicController;
    }
}
