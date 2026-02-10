package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.service.IMessageAppMainView;
import com.ubo.tp.message.ihm.screen.AppMainView;
import com.ubo.tp.message.logger.Logger;

/**
 * Contrôleur pour la vue principale. Contient la logique liée aux actions utilisateur
 * et manipule le DataManager.
 */
public class AppMainController {

    private final IDataManager dataManager;
    private final Logger logger;
    private final IMessageAppMainView view;

    /**
     * Constructeur par défaut : instancie la vue concrete.
     */
    public AppMainController(IDataManager dataManager, Logger logger) {
        this(dataManager, logger, new AppMainView(logger));
    }

    /**
     * Constructeur permettant l'injection d'une vue (utile pour tests).
     */
    public AppMainController(IDataManager dataManager, Logger logger, IMessageAppMainView view) {
        this.dataManager = dataManager;
        this.logger = logger;
        this.view = view;

        // Connecter le callback de la vue à la logique du contrôleur
        this.view.setOnExchangeDirectorySelected(this::onExchangeDirectorySelected);
    }

    private void onExchangeDirectorySelected(String directoryPath) {
        logger.info("Controller: répertoire sélectionné -> " + directoryPath);
        dataManager.setExchangeDirectory(directoryPath);
    }

    public IMessageAppMainView getView() {
        return this.view;
    }
}
