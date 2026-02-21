package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.ihm.graphicController.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.LoginView;
import com.ubo.tp.message.logger.Logger;

public class LoginGraphicController implements GraphicController {

    private final Logger LOGGER;
    private final LoginView loginView;
    private final ILoginController loginController;
    private final INavigationController navigationController;

    public LoginGraphicController(Logger logger, LoginView loginView, ILoginController loginController, INavigationController navigationController) {
        LOGGER = logger;
        this.loginView = loginView;
        this.loginController = loginController;
        this.navigationController = navigationController;

        createConnector();
    }

    void createConnector() {
        loginView.setOnLoginRequested((tag, name, password) -> {
            if (LOGGER != null) LOGGER.debug("Connexion demandée pour : " + tag);
            loginController.onLoginButtonClicked(tag, name, password);
        });

        loginView.setOnRegisterRequested(() -> {
            if (LOGGER != null) LOGGER.debug("Navigation vers l'inscription");
            navigationController.navigateToRegister();
        });
    }
}