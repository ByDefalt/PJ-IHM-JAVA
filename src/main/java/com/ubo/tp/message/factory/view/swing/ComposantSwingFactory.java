package com.ubo.tp.message.factory.view.swing;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.impl.*;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.factory.view.ViewFactory;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.graphiccontroller.swing.*;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.swing.*;

/**
 * Fabrique concrète pour la version Swing de l'IHM.
 * Cette classe construit les vues Swing et leurs contrôleurs graphiques associés
 * puis retourne les vues ou contrôleurs demandés par l'application.
 */
public class ComposantSwingFactory implements ViewFactory {

    private static ControllerContext controllerContext;
    private static ViewContext viewContext;

    /**
     * Construit et retourne le contrôleur principal de l'application (Swing).
     *
     * @return instance de {@link IAppMainController}
     */
    public static IAppMainController createAppMainControllerStatic() {
        AppMainView view = new AppMainView(viewContext);
        IAppMainGraphicController graphicController = new AppMainGraphicController(viewContext, view);
        return new AppMainController(controllerContext, graphicController, createLoginViewStatic());
    }

    /**
     * Construit la vue de login Swing et connecte son contrôleur graphique.
     *
     * @return vue Swing de login
     */
    public static LoginView createLoginViewStatic() {
        LoginController loginController = new LoginController(controllerContext);
        LoginView loginView = new LoginView(viewContext);
        new LoginGraphicController(viewContext, loginView, loginController);
        return loginView;
    }

    /**
     * Construit la vue d'enregistrement (Register) et ses composants.
     *
     * @return vue d'enregistrement Swing
     */
    public static RegisterView createRegisterViewStatic() {
        RegisterController registerController = new RegisterController(controllerContext);
        RegisterView registerView = new RegisterView(viewContext);
        new RegisterGraphicController(viewContext, registerView, registerController);
        return registerView;
    }

    /**
     * Construit la vue des canaux et son contrôleur graphique.
     *
     * @return vue de liste des canaux
     */
    public static ListCanalView createListCanalView() {
        ListCanalView listCanalView = new ListCanalView(viewContext);
        ListCanalGraphicController listCanalGraphicController = new ListCanalGraphicController(viewContext, listCanalView);
        new ListCanalController(controllerContext, listCanalGraphicController);
        return listCanalView;
    }

    /**
     * Construit la vue des messages et son contrôleur graphique.
     *
     * @return vue de liste des messages
     */
    public static ListMessageView createListMessageView() {
        ListMessageView listMessageView = new ListMessageView(viewContext);
        ListMessageGraphicController listMessageGraphicController = new ListMessageGraphicController(viewContext, listMessageView);
        new ListMessageController(controllerContext, listMessageGraphicController);
        return listMessageView;
    }

    /**
     * Construit la vue des utilisateurs et son contrôleur graphique.
     *
     * @return vue de liste des utilisateurs
     */
    public static ListUserView createListUserView() {
        ListUserView listUserView = new ListUserView(viewContext);
        ListUserGraphicController listUserGraphicController = new ListUserGraphicController(viewContext, listUserView);
        new ListUserController(controllerContext, listUserGraphicController);
        return listUserView;
    }

    /**
     * Construit la vue d'entrée de message et son contrôleur graphique.
     *
     * @return vue d'envoi de message
     */
    public static InputMessageView createInputMessageView() {
        InputMessageController inputMessageController = new InputMessageController(controllerContext);
        InputMessageView inputMessageView = new InputMessageView(viewContext);
        new InputMessageGraphicController(viewContext, inputMessageView, inputMessageController);
        return inputMessageView;
    }

    /**
     * Construit la vue de mise à jour de compte et ses composants.
     *
     * @return vue de mise à jour du compte
     */
    public static UpdateAccountView createUpdateAccountViewStatic() {
        UpdateAccountController updateAccountController = new UpdateAccountController(controllerContext);
        UpdateAccountView updateAccountView = new UpdateAccountView(viewContext);
        new UpdateAccountGraphicController(viewContext, updateAccountView, updateAccountController);
        return updateAccountView;
    }

    /**
     * Construit la vue principale de chat (composition des sous-vues).
     *
     * @return vue principale du chat
     */
    public static ChatMainView createChatMainViewStatic() {
        InputMessageView inputMessageView = createInputMessageView();
        ListCanalView listCanalView = createListCanalView();
        ListMessageView listMessageView = createListMessageView();
        ListUserView listUserView = createListUserView();

        new NotificationController(controllerContext);

        return new ChatMainView(
                viewContext,
                listCanalView,
                listUserView,
                listMessageView,
                inputMessageView
        );
    }

    /**
     * Définition du contexte des contrôleurs partagés (injection statique utilisée par la fabrique).
     *
     * @param controllerContext contexte applicatif partagé
     */
    public static void setControllerContext(ControllerContext controllerContext) {
        ComposantSwingFactory.controllerContext = controllerContext;
    }

    /**
     * Définition du contexte de la vue (injection statique utilisée par la fabrique).
     *
     * @param viewContext contexte de la vue
     */
    public static void setViewContext(ViewContext viewContext) {
        ComposantSwingFactory.viewContext = viewContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAppMainController createAppMainController() {
        return ComposantSwingFactory.createAppMainControllerStatic();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View createLoginView() {
        return ComposantSwingFactory.createLoginViewStatic();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View createRegisterView() {
        return ComposantSwingFactory.createRegisterViewStatic();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View createUpdateAccountView() {
        return ComposantSwingFactory.createUpdateAccountViewStatic();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View createChatMainView() {
        return ComposantSwingFactory.createChatMainViewStatic();
    }
}
