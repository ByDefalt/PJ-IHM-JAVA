package com.ubo.tp.message.controller;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.ihm.AppNavigator;
import com.ubo.tp.message.ihm.screen.LoginView;
import com.ubo.tp.message.ihm.screen.RegisterView;
import com.ubo.tp.message.logger.Logger;

public class LoginController implements ILoginController {

    private LoginView view;
    private final Logger logger;

    public LoginController(Logger logger) {
        this.logger = logger;
        if (this.logger != null) this.logger.debug("LoginController created");
    }

    public void setView(LoginView view) {
        this.view = view;
        initListeners();
    }

    private void initListeners() {
        if (view == null) return;

        view.getRegisterButton().addActionListener(e -> {
            // Déléguer la navigation au navigator (ajoute un onglet Inscription ou sélectionne s'il existe)
            if (logger != null) logger.info("Register button clicked, opening RegisterView");
            RegisterView registerView = new RegisterView(null, logger);
            AppNavigator.getInstance(logger).addTab("Inscription", registerView);
        });
    }
}
