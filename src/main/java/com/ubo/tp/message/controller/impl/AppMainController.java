package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.core.session.ISessionObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphiccontroller.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.service.View;

import java.util.Objects;

/**
 * Contrôleur principal de l'application (contexte IHM global).
 * <p>
 * Expose les actions du menu principal (choix de répertoire, déconnexion,
 * suppression de compte, fermeture), et réagit aux événements de session.
 * </p>
 */
public class AppMainController implements IAppMainController, ISessionObserver {

    private final ControllerContext context;

    private final IAppMainGraphicController graphicController;

    /**
     * Crée un {@code AppMainController} et connecte les callbacks de la vue.
     */
    public AppMainController(ControllerContext context, IAppMainGraphicController graphicController, View firstView) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        // Abonnement aux événements de session (login / logout)
        context.session().addObserver(this);

        // Connecter le callback de la vue à la logique du contrôleur
        this.graphicController.setOnExchangeDirectorySelected(this::onExchangeDirectorySelected);

        // Enregistrer handlers pour les actions du menu Compte
        this.graphicController.setOnDisconnect(this::onDisconnectRequested);
        this.graphicController.setOnDeleteAccount(this::onDeleteAccountRequested);

        // La fermeture de fenêtre est gérée ici, pas dans la vue
        this.graphicController.setOnClose(this::onCloseRequested);

        this.graphicController.setMainView(firstView);

        this.graphicController.setVisibility(true);
    }

    /**
     * Callback appelé lorsque l'utilisateur choisit un répertoire d'échange.
     *
     * @param directoryPath chemin du répertoire sélectionné
     */
    private void onExchangeDirectorySelected(String directoryPath) {
        handleOnExchangeDirectorySelectedLogic(directoryPath);
    }

    /**
     * Logique interne pour traiter le répertoire d'échange sélectionné.
     *
     * @param directoryPath chemin sélectionné
     */
    private void handleOnExchangeDirectorySelectedLogic(String directoryPath) {
        context.logger().info("Controller: répertoire sélectionné -> " + directoryPath);
        context.dataManager().setExchangeDirectory(directoryPath);
    }

    /**
     * Retourne le contrôleur graphique principal.
     *
     * @return contrôleur graphique principal
     */
    public IAppMainGraphicController getGraphicController() {
        return this.graphicController;
    }

    /**
     * Callback déclenché par la vue lors d'une demande de fermeture de l'application.
     */
    private void onCloseRequested() {
        handleOnCloseRequestedLogic();
    }

    /**
     * Logique interne gérant la fermeture de l'application (déconnexion asynchrone si nécessaire).
     */
    private void handleOnCloseRequestedLogic() {
        context.logger().info("Controller: fermeture demandée");
        try {
            if (context.session() != null && context.session().getConnectedUser() != null) {
                new Thread(() -> {
                    try {
                        context.session().getConnectedUser().setOnline(false);
                        context.dataManager().sendUser(context.session().getConnectedUser());
                        context.session().disconnect();
                        System.exit(0);
                    } catch (Exception e) {
                        context.logger().error("Erreur lors de la fermeture (thread) : ", e);
                        System.exit(0);
                    }
                }, "app-close-thread").start();
            } else {
                System.exit(0);
            }
        } catch (Exception ex) {
            context.logger().error("Erreur lors de la fermeture", ex);
            System.exit(1);
        }
    }

    /**
     * Callback déclenché par la vue pour demander une déconnexion.
     */
    private void onDisconnectRequested() {
        handleOnDisconnectRequestedLogic();
    }

    /**
     * Logique interne de déconnexion : met l'utilisateur hors-ligne et déconnecte la session.
     */
    private void handleOnDisconnectRequestedLogic() {
        context.logger().info("Controller: demande de déconnexion reçue");
        try {
            if (context.session() != null && context.session().getConnectedUser() != null) {
                context.session().getConnectedUser().setOnline(false);
                context.dataManager().sendUser(context.session().getConnectedUser());
                context.session().disconnect();
            }
        } catch (Exception ex) {
            context.logger().error("Erreur lors de la déconnexion via controller", ex);
        }
    }

    /**
     * Callback déclenché par la vue pour demander la suppression du compte.
     */
    private void onDeleteAccountRequested() {
        handleOnDeleteAccountRequestedLogic();
    }

    /**
     * Logique interne pour la suppression du compte courant.
     */
    private void handleOnDeleteAccountRequestedLogic() {
        context.logger().info("Controller: demande de suppression du compte reçue");
        try {
            if (context.session() != null && context.session().getConnectedUser() != null) {
                var user = context.session().getConnectedUser();
                boolean deleted = context.dataManager().deleteUserFile(user);
                context.logger().info("Suppression du compte effectuée? " + deleted);
                if (deleted) {
                    context.session().disconnect();
                }
            }
        } catch (Exception ex) {
            context.logger().error("Erreur lors de la suppression du compte via controller", ex);
        }
    }

    /**
     * Réagit à la connexion d'un utilisateur à la session.
     *
     * @param connectedUser utilisateur connecté
     */
    @Override
    public void notifyLogin(User connectedUser) {
        handleNotifyLoginLogic(connectedUser);
    }

    /**
     * Logique interne exécutée lors d'une connexion de session.
     *
     * @param connectedUser utilisateur connecté
     */
    private void handleNotifyLoginLogic(User connectedUser) {
        context.logger().info("Session: utilisateur connecté -> " + (connectedUser != null ? connectedUser.getName() : "null"));
        graphicController.setConnectMenuVisible(true);
    }

    /**
     * Réagit à la déconnexion de la session.
     */
    @Override
    public void notifyLogout() {
        handleNotifyLogoutLogic();
    }

    /**
     * Logique interne exécutée lors d'une déconnexion de session.
     */
    private void handleNotifyLogoutLogic() {
        context.logger().info("Session: utilisateur déconnecté");
        graphicController.setConnectMenuVisible(false);
    }
}
