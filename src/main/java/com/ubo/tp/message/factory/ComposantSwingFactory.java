package com.ubo.tp.message.factory;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.impl.*;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.graphicController.swing.*;
import com.ubo.tp.message.ihm.view.swing.*;
import com.ubo.tp.message.ihm.contexte.ViewContext;

public class ComposantSwingFactory implements Factory {

    public static IAppMainController createAppMainController(ControllerContext context, ViewContext vc) {
        AppMainView view = new AppMainView(vc);
        IAppMainGraphicController graphicController = new AppMainGraphicController(vc, view);
        return new com.ubo.tp.message.controller.impl.AppMainController(context, graphicController, vc);
    }

    public static LoginView createLoginView(ControllerContext context, ViewContext vc, NavigationController navigationController) {
        LoginController loginController = new LoginController(context);
        LoginView loginView = new LoginView(vc);
        new LoginGraphicController(vc, loginView, loginController, navigationController);
        return loginView;
    }

    public static RegisterView createRegisterView(ControllerContext context, ViewContext vc, NavigationController navigationController) {
        RegisterController registerController = new RegisterController(context);
        RegisterView registerView = new RegisterView(vc);
        new RegisterGraphicController(vc, registerView, registerController, navigationController);
        return registerView;
    }

    public static ListCanalView createListCanalView(ControllerContext context, ViewContext vc) {
        ListCanalView listCanalView = new ListCanalView(vc);
        ListCanalGraphicController listCanalGraphicController = new ListCanalGraphicController(vc, listCanalView);
        new ListCanalController(context, listCanalGraphicController);
        return listCanalView;
    }

    public static ListMessageView createListMessageView(ControllerContext context, ViewContext vc) {
        ListMessageView listMessageView = new ListMessageView(vc);
        ListMessageGraphicController listMessageGraphicController = new ListMessageGraphicController(vc, listMessageView);
        new ListMessageController(context, listMessageGraphicController);
        return listMessageView;
    }

    public static ListUserView createListUserView(ControllerContext context, ViewContext vc) {
        ListUserView listUserView = new ListUserView(vc);
        ListUserGraphicController listUserGraphicController = new ListUserGraphicController(vc, listUserView);
        new ListUserController(context, listUserGraphicController);
        return listUserView;
    }

    public static InputMessageView createInputMessageView(ControllerContext context, ViewContext vc) {
        InputMessageController inputMessageController = new InputMessageController(context);
        InputMessageView inputMessageView = new InputMessageView(vc);
        new InputMessageGraphicController(vc, inputMessageView, inputMessageController);
        return inputMessageView;
    }

    public static ChatMainView createChatMainView(ControllerContext context, ViewContext vc) {
        InputMessageView inputMessageView = createInputMessageView(context, vc);
        ListCanalView listCanalView = createListCanalView(context, vc);
        ListMessageView listMessageView = createListMessageView(context, vc);
        ListUserView listUserView = createListUserView(context, vc);

        return new ChatMainView(
                vc,
                listCanalView,
                listUserView,
                listMessageView,
                inputMessageView
        );
    }
}
