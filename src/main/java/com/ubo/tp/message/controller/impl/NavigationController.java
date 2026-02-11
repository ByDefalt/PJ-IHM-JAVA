package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.ihm.service.IAppMainView;
import com.ubo.tp.message.ihm.view.LoginView;
import com.ubo.tp.message.ihm.view.RegisterView;
import com.ubo.tp.message.ihm.service.View;
import com.ubo.tp.message.logger.Logger;

public class NavigationController implements INavigationController {

    private final Logger LOGGER;
    private final IDataManager dataManager;
    private final IAppMainView appMainView;

    public NavigationController(Logger logger, IDataManager dataManager, IAppMainView appMainView) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.appMainView = appMainView;
    }

    private void setMainContent(View mainContent) {
        LOGGER.info("setMainContent");
        this.appMainView.setMainContent(mainContent);
    }

    @Override
    public void navigateToLogin() {
        LOGGER.info("navigateToLogin");
        LoginController loginController = new LoginController(LOGGER, dataManager);
        LoginView loginView = new LoginView(LOGGER, loginController, this);
        this.setMainContent(loginView);
    }

    @Override
    public void navigateToRegister() {
        LOGGER.info("navigateToRegister");
        RegisterController registerController = new RegisterController(LOGGER, dataManager);
        RegisterView registerView = new RegisterView(LOGGER, registerController, this);
        this.setMainContent(registerView);
    }

}
