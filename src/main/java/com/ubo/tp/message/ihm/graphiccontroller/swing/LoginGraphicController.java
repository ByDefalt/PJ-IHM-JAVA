package com.ubo.tp.message.ihm.graphiccontroller.swing;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.LoginView;

public class LoginGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final LoginView loginView;
    private final ILoginController loginController;

    public LoginGraphicController(ViewContext viewContext, LoginView loginView, ILoginController loginController) {
        this.viewContext = viewContext;
        this.loginView = loginView;
        this.loginController = loginController;

        createConnector();
    }

    private void createConnector() {
        loginView.setOnLoginRequested(this::handleLoginRequested);
        loginView.setOnRegisterRequested(this::handleRegisterRequested);
    }

    private void handleLoginRequested(String tag, String name, String password) {
        if (viewContext.logger() != null) viewContext.logger().debug("Connexion demandée pour : " + tag);
        loginController.onLoginButtonClicked(tag, name, password);
    }

    private void handleRegisterRequested() {
        if (viewContext.logger() != null) viewContext.logger().debug("Navigation vers l'inscription");
        viewContext.navigationController().navigateToRegister();
    }
}