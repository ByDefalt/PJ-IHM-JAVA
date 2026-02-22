package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.factory.ComposantSwingFactory;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.service.View;

import java.util.Objects;

/**
 * Contrôleur pour la vue principale de l'application.
 * <p>
 * Ce contrôleur orchestre l'initialisation de la vue principale et expose les
 * actions nécessaires à l'IHM (ex : sélection du répertoire d'échange).
 * </p>
 */
public class AppMainController implements IAppMainController {

    private final ControllerContext context;

    private final IAppMainGraphicController graphicController;

    /**
     * Constructeur permettant l'injection d'une vue (utile pour tests).
     *
     * @param context           contexte regroupant les services
     * @param graphicController vue principale injectée
     * @param firstView       première vue à afficher dans la vue principale
     */
    public AppMainController(ControllerContext context, IAppMainGraphicController graphicController, View firstView) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        // Connecter le callback de la vue à la logique du contrôleur
        this.graphicController.setOnExchangeDirectorySelected(this::onExchangeDirectorySelected);
        this.graphicController.setClearSelected(this::clearSelected);

        this.graphicController.setMainView(firstView);

        this.graphicController.setVisibility(true);
    }

    /**
     * Callback appelé lorsque l'utilisateur choisit un répertoire d'échange.
     *
     * @param directoryPath chemin du répertoire sélectionné
     */
    private void onExchangeDirectorySelected(String directoryPath) {
        context.logger().info("Controller: répertoire sélectionné -> " + directoryPath);
        context.dataManager().setExchangeDirectory(directoryPath);
    }

    public IAppMainGraphicController getGraphicController() {
        return this.graphicController;
    }

    public void clearSelected() {
        context.selected().clearSelectedChannel();
    }
}
