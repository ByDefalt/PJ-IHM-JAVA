package com.ubo.tp.message.factory;

import com.ubo.tp.message.controller.impl.LoginController;
import com.ubo.tp.message.controller.impl.NavigationController;
import com.ubo.tp.message.controller.impl.RegisterController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.view.LoginView;
import com.ubo.tp.message.ihm.view.RegisterView;
import com.ubo.tp.message.logger.Logger;

public class ComposantFactory implements Factory{

    public static LoginView createLoginView(Logger LOGGER, IDataManager dataManager, NavigationController navigationController) {
        LoginController loginController = new LoginController(LOGGER, dataManager);
        return new LoginView(LOGGER, loginController, navigationController);
    }

    public static RegisterView createRegisterView(Logger LOGGER, IDataManager dataManager, NavigationController navigationController) {
        RegisterController registerController = new RegisterController(LOGGER, dataManager);
        return new RegisterView(LOGGER, registerController, navigationController);
    }
}
