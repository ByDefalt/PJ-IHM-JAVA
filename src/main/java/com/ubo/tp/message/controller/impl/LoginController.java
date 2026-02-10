package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

public class LoginController implements ILoginController {

    private final Logger logger;
    private final NavigationService navigation;

    public LoginController(Logger logger, NavigationService navigation) {
        this.logger = logger;
        this.navigation = navigation;
        if (this.logger != null) this.logger.debug("LoginController created");
    }

    @Override
    public void onRegisterButtonClicked() {
        logger.debug("LoginController: onRegisterButtonClicked called");

        // La vue d'inscription est pré-enregistrée dans la vue principale par l'amont
        navigation.showView("register");
    }

}
