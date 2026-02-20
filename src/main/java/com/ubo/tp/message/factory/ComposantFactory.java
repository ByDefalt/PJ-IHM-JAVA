package com.ubo.tp.message.factory;

import com.ubo.tp.message.controller.impl.*;
import com.ubo.tp.message.controller.service.*;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.service.*;
import com.ubo.tp.message.ihm.view.swing.*;
import com.ubo.tp.message.logger.Logger;

public class ComposantFactory implements Factory {

    public static IAppMainController createAppMainController(IDataManager dataManager, Logger logger) {
        IAppMainView view = new AppMainView(logger);
        return new AppMainController(dataManager, logger, view);
    }

    public static ILoginView createLoginView(Logger LOGGER, IDataManager dataManager, NavigationController navigationController) {
        LoginController loginController = new LoginController(LOGGER, dataManager);
        return new LoginView(LOGGER, loginController, navigationController);
    }

    public static IRegisterView createRegisterView(Logger LOGGER, IDataManager dataManager, NavigationController navigationController) {
        RegisterController registerController = new RegisterController(LOGGER, dataManager);
        return new RegisterView(LOGGER, registerController, navigationController);
    }

    public static IListCanalController createListCanalController(Logger LOGGER, IDataManager dataManager) {
        ListCanalView listCanalView = new ListCanalView(LOGGER);
        return new ListCanalController(LOGGER, dataManager, listCanalView);
    }

    public static IListMessageController createListMessageController(Logger LOGGER, IDataManager dataManager) {
        ListMessageView listMessageView = new ListMessageView(LOGGER);
        return new ListMessageController(LOGGER, dataManager, listMessageView);
    }

    public static IListUserController createListUserController(Logger LOGGER, IDataManager dataManager) {
        ListUserView listUserView = new ListUserView(LOGGER);
        return new ListUserController(LOGGER, dataManager, listUserView);
    }

    public static IInputMessageView createInputMessageView(Logger LOGGER, IDataManager dataManager) {
        InputMessageController inputMessageController = new InputMessageController(LOGGER, dataManager);
        return new InputMessageView(LOGGER, inputMessageController);
    }

    public static IChatMainView createChatMainView(Logger LOGGER, IDataManager dataManager) {
        IInputMessageView inputMessageView = createInputMessageView(LOGGER, dataManager);
        IListMessageController listMessageController = createListMessageController(LOGGER, dataManager);
        IListUserController listUserController = createListUserController(LOGGER, dataManager);
        IListCanalController listCanalController = createListCanalController(LOGGER, dataManager);
        IChatMainController chatMainController = new ChatMainController(LOGGER, dataManager);
        return new ChatMainView(LOGGER, listCanalController.getView(), listUserController.getView(), listMessageController.getView(), inputMessageView, chatMainController);
    }
}
