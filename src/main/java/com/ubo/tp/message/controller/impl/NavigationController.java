package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.factory.ComposantSwingFactory;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.swing.AppMainView;
import com.ubo.tp.message.logger.Logger;

public class NavigationController implements INavigationController {

    private final Logger LOGGER;
    private final IDataManager dataManager;
    private final AppMainView appMainView;

    public NavigationController(Logger logger, IDataManager dataManager, AppMainView appMainView) {
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
        this.setMainContent(ComposantSwingFactory.createLoginView(LOGGER, dataManager, this));
    }

    @Override
    public void navigateToRegister() {
        LOGGER.info("navigateToRegister");
        this.setMainContent(ComposantSwingFactory.createRegisterView(LOGGER, dataManager, this));
    }

}
