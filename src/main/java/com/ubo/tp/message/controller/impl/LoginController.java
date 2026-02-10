package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

/**
 * Implémentation simple du {@link ILoginController}.
 * <p>
 * Délègue essentiellement la navigation entre les vues (login -> register)
 * et consigne les actions dans le logger.
 * </p>
 */
public class LoginController implements ILoginController {

    private final Logger logger;
    private final NavigationService navigation;

    /**
     * Crée un LoginController.
     *
     * @param logger service de logging (peut être null)
     * @param navigation service de navigation utilisé pour changer de vue
     */
    public LoginController(Logger logger, NavigationService navigation) {
        this.logger = logger;
        this.navigation = navigation;
        if (this.logger != null) this.logger.debug("LoginController created");
    }

    @Override
    public void onRegisterButtonClicked() {
        logger.debug("LoginController: onRegisterButtonClicked called");
        navigation.showView("register");
    }

    @Override
    public void onLoginButtonClicked() {
        logger.debug("LoginController: onLoginButtonClicked called");

    }

}
