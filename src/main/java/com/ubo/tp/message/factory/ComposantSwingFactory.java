package com.ubo.tp.message.factory;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.impl.*;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.graphicController.swing.*;
import com.ubo.tp.message.ihm.view.swing.*;

public class ComposantSwingFactory implements Factory {

    private static ControllerContext controllerContext;
    private static ViewContext viewContext;

    public static IAppMainController createAppMainController() {
        AppMainView view = new AppMainView(viewContext);
        IAppMainGraphicController graphicController = new AppMainGraphicController(viewContext, view);
        return new AppMainController(controllerContext, graphicController, ComposantSwingFactory.createLoginView());
    }

    public static LoginView createLoginView() {
        LoginController loginController = new LoginController(controllerContext);
        LoginView loginView = new LoginView(viewContext);
        new LoginGraphicController(viewContext, loginView, loginController);
        return loginView;
    }

    public static RegisterView createRegisterView() {
        RegisterController registerController = new RegisterController(controllerContext);
        RegisterView registerView = new RegisterView(viewContext);
        new RegisterGraphicController(viewContext, registerView, registerController);
        return registerView;
    }

    public static ListCanalView createListCanalView() {
        ListCanalView listCanalView = new ListCanalView(viewContext);
        ListCanalGraphicController listCanalGraphicController = new ListCanalGraphicController(viewContext, listCanalView);
        new ListCanalController(controllerContext, listCanalGraphicController);
        return listCanalView;
    }

    public static ListMessageView createListMessageView() {
        ListMessageView listMessageView = new ListMessageView(viewContext);
        ListMessageGraphicController listMessageGraphicController = new ListMessageGraphicController(viewContext, listMessageView);
        new ListMessageController(controllerContext, listMessageGraphicController);
        return listMessageView;
    }

    public static ListUserView createListUserView() {
        ListUserView listUserView = new ListUserView(viewContext);
        ListUserGraphicController listUserGraphicController = new ListUserGraphicController(viewContext, listUserView);
        new ListUserController(controllerContext, listUserGraphicController);
        return listUserView;
    }

    public static InputMessageView createInputMessageView() {
        InputMessageController inputMessageController = new InputMessageController(controllerContext);
        InputMessageView inputMessageView = new InputMessageView(viewContext);
        new InputMessageGraphicController(viewContext, inputMessageView, inputMessageController);
        return inputMessageView;
    }

    public static ChatMainView createChatMainView() {
        InputMessageView inputMessageView = createInputMessageView();
        ListCanalView listCanalView = createListCanalView();
        ListMessageView listMessageView = createListMessageView();
        ListUserView listUserView = createListUserView();

        return new ChatMainView(
                viewContext,
                listCanalView,
                listUserView,
                listMessageView,
                inputMessageView
        );
    }

    public static void setControllerContext(ControllerContext controllerContext) {
        ComposantSwingFactory.controllerContext = controllerContext;
    }

    public static void setViewContext(ViewContext viewContext) {
        ComposantSwingFactory.viewContext = viewContext;
    }
}
