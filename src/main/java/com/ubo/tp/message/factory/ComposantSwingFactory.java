package com.ubo.tp.message.factory;

import com.ubo.tp.message.controller.impl.*;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.controller.service.IListMessageController;
import com.ubo.tp.message.controller.service.IListUserController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.ihm.graphicController.service.*;
import com.ubo.tp.message.ihm.graphicController.swing.*;
import com.ubo.tp.message.ihm.view.swing.*;
import com.ubo.tp.message.logger.Logger;

public class ComposantSwingFactory implements Factory {

    public static IAppMainController createAppMainController(IDataManager dataManager, Logger logger, ISession session) {
        AppMainView view = new AppMainView(logger);
        IAppMainGraphicController graphicController = new AppMainGraphicController(logger, view);
        return new AppMainController(dataManager, logger, session, graphicController);
    }

    public static LoginView createLoginView(Logger LOGGER, IDataManager dataManager, NavigationController navigationController, ISession session) {
        LoginController loginController = new LoginController(LOGGER, dataManager, session);
        LoginView loginView = new LoginView(LOGGER);
        ILoginGraphicController loginGraphicController = new LoginGraphicController(LOGGER, loginView, loginController, navigationController);
        return loginView;
    }

    public static RegisterView createRegisterView(Logger LOGGER, IDataManager dataManager, NavigationController navigationController) {
        RegisterController registerController = new RegisterController(LOGGER, dataManager);
        RegisterView registerView = new RegisterView(LOGGER);
        IRegisterGraphicController registerGraphicController = new RegisterGraphicController(LOGGER, registerView, registerController, navigationController);
        return registerView;
    }

    public static ListCanalView createListCanalView(Logger LOGGER, IDataManager dataManager) {
        ListCanalView listCanalView = new ListCanalView(LOGGER);
        IListCanalGraphicController listCanalGraphicController = new ListCanalGraphicController(LOGGER, listCanalView);
        IListCanalController canalController = new ListCanalController(LOGGER, dataManager, listCanalGraphicController);
        return listCanalView;
    }

    public static ListMessageView createListMessageView(Logger LOGGER, IDataManager dataManager, ISession session) {
        ListMessageView listMessageView = new ListMessageView(LOGGER);
        IListMessageGraphicController listMessageGraphicController = new ListMessageGraphicController(LOGGER, listMessageView);
        IListMessageController listMessageController = new ListMessageController(LOGGER, dataManager, session, listMessageGraphicController);
        return listMessageView;
    }

    public static ListUserView createListUserView(Logger LOGGER, IDataManager dataManager) {
        ListUserView listUserView = new ListUserView(LOGGER);
        IListUserGraphicController listUserGraphicController = new ListUserGraphicController(LOGGER, listUserView);
        IListUserController listUserController = new ListUserController(LOGGER, dataManager, listUserGraphicController);
        return listUserView;
    }

    public static InputMessageView createInputMessageView(Logger LOGGER, IDataManager dataManager, ISession session) {
        InputMessageController inputMessageController = new InputMessageController(LOGGER, dataManager, session);
        InputMessageView inputMessageView = new InputMessageView(LOGGER);
        IInputMessageGraphicController inputMessageGraphicController = new InputMessageGraphicController(LOGGER, inputMessageView, inputMessageController);
        return inputMessageView;
    }

    public static ChatMainView createChatMainView(Logger LOGGER, IDataManager dataManager, ISession session) {
        InputMessageView inputMessageView = createInputMessageView(LOGGER, dataManager, session);
        ListCanalView listCanalView = createListCanalView(LOGGER, dataManager);
        ListMessageView listMessageView = createListMessageView(LOGGER, dataManager, session);
        ListUserView listUserView = createListUserView(LOGGER, dataManager);

        return new ChatMainView(
                LOGGER,
                listCanalView,
                listUserView,
                listMessageView,
                inputMessageView
        );
    }
}
