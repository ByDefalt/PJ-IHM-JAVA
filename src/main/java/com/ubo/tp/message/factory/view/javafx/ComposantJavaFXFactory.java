package com.ubo.tp.message.factory.view.javafx;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.impl.*;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.factory.view.ViewFactory;
import com.ubo.tp.message.factory.view.swing.ComposantSwingFactory;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.javafx.*;
import com.ubo.tp.message.ihm.view.javafx.*;
import com.ubo.tp.message.ihm.view.service.View;
import javafx.stage.Stage;

/**
 * Fabrique de composants JavaFX — symétrique à {@link ComposantSwingFactory}.
 */
public class ComposantJavaFXFactory implements ViewFactory {

    private final ControllerContext controllerContext;
    private final ViewContext viewContext;
    private final Stage primaryStage;

    public ComposantJavaFXFactory(ControllerContext controllerContext, ViewContext viewContext, Stage primaryStage) {
        this.controllerContext = controllerContext;
        this.viewContext = viewContext;
        this.primaryStage = primaryStage;
    }

    // -------------------------------------------------------------------------
    // ViewFactory
    // -------------------------------------------------------------------------

    @Override
    public IAppMainController createAppMainController() {
        FxAppMainView appMainView = new FxAppMainView(viewContext, primaryStage);
        FxAppMainGraphicController gc = new FxAppMainGraphicController(viewContext, appMainView);
        return new AppMainController(controllerContext, gc, createLoginView());
    }

    @Override
    public View createLoginView() {
        LoginController loginController = new LoginController(controllerContext);
        FxLoginView loginView = new FxLoginView(viewContext);
        new FxLoginGraphicController(viewContext, loginView, loginController);
        return loginView;
    }

    @Override
    public View createRegisterView() {
        RegisterController registerController = new RegisterController(controllerContext);
        FxRegisterView registerView = new FxRegisterView(viewContext);
        new FxRegisterGraphicController(viewContext, registerView, registerController);
        return registerView;
    }

    @Override
    public View createUpdateAccountView() {
        UpdateAccountController updateController = new UpdateAccountController(controllerContext);
        FxUpdateAccountView updateView = new FxUpdateAccountView(viewContext);
        new FxUpdateAccountGraphicController(viewContext, updateView, updateController);
        return updateView;
    }

    @Override
    public View createChatMainView() {
        // Input
        InputMessageController inputController = new InputMessageController(controllerContext);
        FxInputMessageView inputView = new FxInputMessageView(viewContext);
        new FxInputMessageGraphicController(viewContext, inputView, inputController);

        // Canaux
        FxListCanalView listCanalView = new FxListCanalView(viewContext);
        FxListCanalGraphicController listCanalGC = new FxListCanalGraphicController(viewContext, listCanalView);
        new ListCanalController(controllerContext, listCanalGC);

        // Messages
        FxListMessageView listMessageView = new FxListMessageView(viewContext);
        FxListMessageGraphicController listMessageGC = new FxListMessageGraphicController(viewContext, listMessageView);
        new ListMessageController(controllerContext, listMessageGC);

        // Utilisateurs
        FxListUserView listUserView = new FxListUserView(viewContext);
        FxListUserGraphicController listUserGC = new FxListUserGraphicController(viewContext, listUserView);
        new ListUserController(controllerContext, listUserGC);

        return new FxChatMainView(viewContext, listCanalView, listUserView, listMessageView, inputView);
    }
}

