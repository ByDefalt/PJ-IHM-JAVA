package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.ihm.graphicController.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.LoginView;
import com.ubo.tp.message.ihm.view.contexte.ViewContext;

public class LoginGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final LoginView loginView;
    private final ILoginController loginController;
    private final INavigationController navigationController;

    public LoginGraphicController(ViewContext viewContext, LoginView loginView, ILoginController loginController, INavigationController navigationController) {
        this.viewContext = viewContext;
        this.loginView = loginView;
        this.loginController = loginController;
        this.navigationController = navigationController;

        createConnector();
    }

    void createConnector() {
        loginView.setOnLoginRequested((tag, name, password) -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Connexion demandée pour : " + tag);
            loginController.onLoginButtonClicked(tag, name, password);
        });

        loginView.setOnRegisterRequested(() -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Navigation vers l'inscription");
            navigationController.navigateToRegister();
        });
    }
}