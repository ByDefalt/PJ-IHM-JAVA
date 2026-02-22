package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IAppMainController;
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
     * @param firstView         première vue à afficher dans la vue principale
     */
    public AppMainController(ControllerContext context, IAppMainGraphicController graphicController, View firstView) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        // Connecter le callback de la vue à la logique du contrôleur
        this.graphicController.setOnExchangeDirectorySelected(this::onExchangeDirectorySelected);
        this.graphicController.setClearSelected(this::clearSelected);

        // Enregistrer handlers pour les actions du menu Compte
        this.graphicController.setOnDisconnect(this::onDisconnectRequested);
        this.graphicController.setOnDeleteAccount(this::onDeleteAccountRequested);

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

    // ----------------------------
    // Handlers pour actions Compte
    // ----------------------------
    private void onDisconnectRequested() {
        context.logger().info("Controller: demande de déconnexion reçue");
        try {
            if (context.session() != null && context.session().getConnectedUser() != null) {
                context.session().disconnect();
            }
        } catch (Exception ex) {
            context.logger().error("Erreur lors de la déconnexion via controller", ex);
        }
    }

    private void onDeleteAccountRequested() {
        context.logger().info("Controller: demande de suppression du compte reçue");
        try {
            if (context.session() != null && context.session().getConnectedUser() != null) {
                var user = context.session().getConnectedUser();
                boolean deleted = context.dataManager().deleteUserFile(user);
                context.logger().info("Suppression du compte effectuée? " + deleted);
                if (deleted) {
                    // après suppression, déco
                    context.session().disconnect();
                }
            }
        } catch (Exception ex) {
            context.logger().error("Erreur lors de la suppression du compte via controller", ex);
        }
    }
}
