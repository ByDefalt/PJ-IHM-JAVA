package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.core.session.ISessionObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.factory.ComposantSwingFactory;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.swing.AppMainView;
import com.ubo.tp.message.logger.Logger;

public class NavigationController implements INavigationController, ISessionObserver {

    private final Logger LOGGER;
    private final IDataManager dataManager;
    private final ISession session;
    private final AppMainView appMainView;

    public NavigationController(Logger logger, IDataManager dataManager, ISession session, AppMainView appMainView) {
        LOGGER = logger;
        this.dataManager = dataManager;
        this.session = session;
        this.appMainView = appMainView;

        this.session.addObserver(this);
    }

    private void setMainContent(View mainContent) {
        LOGGER.info("setMainContent");
        this.appMainView.setMainContent(mainContent);
    }

    @Override
    public void navigateToLogin() {
        LOGGER.info("navigateToLogin");
        this.setMainContent(ComposantSwingFactory.createLoginView(LOGGER, dataManager, this, session));
    }

    @Override
    public void navigateToRegister() {
        LOGGER.info("navigateToRegister");
        this.setMainContent(ComposantSwingFactory.createRegisterView(LOGGER, dataManager, this));
    }

    @Override
    public void notifyLogin(User connectedUser) {
        LOGGER.info("notifyLogin");
        this.setMainContent(ComposantSwingFactory.createChatMainView(LOGGER, dataManager, session));
    }

    @Override
    public void notifyLogout() {
        this.navigateToLogin();
    }
}
