package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.ihm.graphicController.service.IRegisterGraphicController;
import com.ubo.tp.message.ihm.view.swing.RegisterView;
import com.ubo.tp.message.logger.Logger;

public class RegisterGraphicController implements IRegisterGraphicController {

    private final Logger LOGGER;
    private final RegisterView registerView;
    private final IRegisterController registerController;
    private final INavigationController navigationController;

    public RegisterGraphicController(Logger logger, RegisterView registerView, IRegisterController registerController, INavigationController navigationController) {
        LOGGER = logger;
        this.registerView = registerView;
        this.registerController = registerController;
        this.navigationController = navigationController;

        registerView.setOnRegisterRequested((tag, name, password, confirmPassword) -> {
            if (LOGGER != null) LOGGER.debug("Inscription demandée pour : " + tag);
            boolean created = registerController.onRegisterButtonClicked(tag, name, password, confirmPassword);
            if (created) {
                if (LOGGER != null) LOGGER.info("Inscription réussie, navigation vers login");
                navigationController.navigateToLogin();
            } else {
                if (LOGGER != null) LOGGER.warn("Inscription échouée pour : " + tag);
            }
        });

        registerView.setOnBackToLoginRequested(() -> {
            if (LOGGER != null) LOGGER.debug("Retour vers la connexion");
            navigationController.navigateToLogin();
        });
    }
}