package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.ihm.graphicController.service.ILoginGraphicController;
import com.ubo.tp.message.ihm.view.swing.LoginView;
import com.ubo.tp.message.logger.Logger;

public class LoginGraphicController implements ILoginGraphicController {

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

    private void createConnector() {

        this.loginView.getLoginButton().addActionListener(e -> {
            if (LOGGER != null) LOGGER.debug("Bouton de connexion cliqué");
            loginController.onLoginButtonClicked(
                    loginView.getTagField().getText(),
                    loginView.getNameField().getText(),
                    new String(loginView.getPasswordField().getPassword())
            );
        });

        this.loginView.getRegisterButton().addActionListener(e -> {
            if (LOGGER != null) LOGGER.debug("Bouton d'inscription cliqué");
            navigationController.navigateToRegister();
        });

    }
}
