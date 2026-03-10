package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.core.session.ISessionObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.factory.view.ViewFactory;
import com.ubo.tp.message.ihm.view.service.View;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Contrôleur de navigation principal.
 * <p>
 * Gère la navigation entre les vues (login, register, profile, chat) et réagit
 * aux événements de session (login/logout).
 * </p>
 */
public class NavigationController implements INavigationController, ISessionObserver {

    private final ControllerContext context;
    private final ViewFactory viewFactory;
    private Consumer<View> mainView;

    /**
     * Crée un {@code NavigationController}.
     *
     * @param context     contexte contenant les services partagés
     * @param viewFactory factory de vues pour construire les vues nécessaires
     */
    public NavigationController(ControllerContext context, ViewFactory viewFactory) {
        this.context = Objects.requireNonNull(context);
        this.viewFactory = Objects.requireNonNull(viewFactory);
        this.context.session().addObserver(this);
    }

    /**
     * Remplace le contenu principal de l'IHM par la vue fournie.
     *
     * @param mainContent vue à afficher dans la zone principale
     */
    private void setMainContent(View mainContent) {
        context.logger().info("setMainContent");
        this.mainView.accept(mainContent);
    }

    /**
     * Navigue vers la vue de connexion.
     */
    @Override
    public void navigateToLogin() {
        handleNavigateToLoginLogic();
    }

    /**
     * Logique interne de navigation vers la page de connexion.
     */
    private void handleNavigateToLoginLogic() {
        context.logger().info("navigateToLogin");
        this.setMainContent(viewFactory.createLoginView());
    }

    /**
     * Navigue vers la vue d'inscription.
     */
    @Override
    public void navigateToRegister() {
        handleNavigateToRegisterLogic();
    }

    /**
     * Logique interne de navigation vers la page d'inscription.
     */
    private void handleNavigateToRegisterLogic() {
        context.logger().info("navigateToRegister");
        this.setMainContent(viewFactory.createRegisterView());
    }

    /**
     * Navigue vers la vue de profil (mise à jour du compte).
     */
    @Override
    public void navigateToProfile() {
        handleNavigateToProfileLogic();
    }

    /**
     * Logique interne de navigation vers la page de profil.
     */
    private void handleNavigateToProfileLogic() {
        context.logger().info("navigateToProfile");
        this.setMainContent(viewFactory.createUpdateAccountView());
    }

    /**
     * Navigue vers la vue de discussion principale (chat).
     */
    @Override
    public void navigateToChat() {
        handleNavigateToChatLogic();
    }

    /**
     * Logique interne de navigation vers la page chat.
     */
    private void handleNavigateToChatLogic() {
        context.logger().info("navigateToChat");
        this.setMainContent(viewFactory.createChatMainView());
    }

    /**
     * Définit la fonction d'affichage principale (injection depuis la vue).
     *
     * @param mainView consommateur recevant la vue principale à afficher
     */
    @Override
    public void setMainView(Consumer<View> mainView) {
        this.mainView = mainView;
    }

    /**
     * Réagit à l'événement de connexion de session : affiche la vue chat.
     *
     * @param connectedUser utilisateur connecté (peut être null)
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
        if (connectedUser != null) {
            context.logger().info("notifyLogin -> utilisateur connecté : " + connectedUser.getName());
        } else {
            context.logger().info("notifyLogin -> utilisateur connecté : null");
        }
        this.setMainContent(viewFactory.createChatMainView());
    }

    /**
     * Réagit à l'événement de déconnexion de session.
     */
    @Override
    public void notifyLogout() {
        handleNotifyLogoutLogic();
    }

    /**
     * Logique interne exécutée lors d'une déconnexion de session.
     */
    private void handleNotifyLogoutLogic() {
        this.navigateToLogin();
    }
}
