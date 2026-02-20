package com.ubo.tp.message.factory;

import com.ubo.tp.message.controller.impl.*;
import com.ubo.tp.message.controller.service.IAppMainController;
import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.controller.service.IListMessageController;
import com.ubo.tp.message.controller.service.IListUserController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.graphicController.service.*;
import com.ubo.tp.message.ihm.graphicController.swing.*;
import com.ubo.tp.message.ihm.view.swing.*;
import com.ubo.tp.message.logger.Logger;

public class ComposantSwingFactory implements Factory {

    public static IAppMainController createAppMainController(IDataManager dataManager, Logger logger) {
        AppMainView view = new AppMainView(logger);
        IAppMainGraphicController graphicController = new AppMainGraphicController(logger, view);
        return new AppMainController(dataManager, logger, graphicController);
    }

    public static LoginView createLoginView(Logger LOGGER, IDataManager dataManager, NavigationController navigationController) {
        LoginController loginController = new LoginController(LOGGER, dataManager);
        LoginView loginView = new LoginView(LOGGER);
        ILoginGraphicController loginGraphicController = new LoginGraphicController(LOGGER, loginView, loginController, navigationController);
        return loginView;
    }

    public static RegisterView createRegisterView(Logger LOGGER, IDataManager dataManager, NavigationController navigationController) {
        RegisterController registerController = new RegisterController(LOGGER, dataManager);
        return new RegisterView(LOGGER, registerController, navigationController);
    }

    public static ListCanalView createListCanalController(Logger LOGGER, IDataManager dataManager) {
        ListCanalView listCanalView = new ListCanalView(LOGGER);
        IListCanalGraphicController listCanalGraphicController = new ListCanalGraphicController(LOGGER, listCanalView);
        IListCanalController canalController = new ListCanalController(LOGGER, dataManager, listCanalGraphicController);
        return listCanalView;
    }

    public static ListMessageView createListMessageController(Logger LOGGER, IDataManager dataManager) {
        ListMessageView listMessageView = new ListMessageView(LOGGER);
        IListMessageGraphicController listMessageGraphicController = new ListMessageGraphicController(LOGGER, listMessageView);
        IListMessageController listMessageController = new ListMessageController(LOGGER, dataManager, listMessageGraphicController);
        return listMessageView;
    }

    public static ListUserView createListUserController(Logger LOGGER, IDataManager dataManager) {
        ListUserView listUserView = new ListUserView(LOGGER);
        IListUserGraphicController listUserGraphicController = new ListUserGraphicController(LOGGER, listUserView);
        IListUserController listUserController = new ListUserController(LOGGER, dataManager, listUserGraphicController);
        return listUserView;
    }

    public static InputMessageView createInputMessageView(Logger LOGGER, IDataManager dataManager) {
        InputMessageController inputMessageController = new InputMessageController(LOGGER, dataManager);
        return new InputMessageView(LOGGER, inputMessageController);
    }

    public static ChatMainView createChatMainView(Logger LOGGER, IDataManager dataManager) {
        InputMessageView inputMessageView = createInputMessageView(LOGGER, dataManager);
        ListCanalView listCanalView = createListCanalController(LOGGER, dataManager);
        ListMessageView listMessageView = createListMessageController(LOGGER, dataManager);
        ListUserView listUserView = createListUserController(LOGGER, dataManager);

        return new ChatMainView(
                LOGGER,
                listCanalView,
                listUserView,
                listMessageView,
                inputMessageView
        );
    }
}
