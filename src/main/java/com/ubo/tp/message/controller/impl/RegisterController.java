package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

public class RegisterController implements IRegisterController {

    private final Logger logger;
    private final NavigationService navigation;

    public RegisterController(Logger logger, NavigationService navigation) {
        this.logger = logger;
        this.navigation = navigation;
        if (this.logger != null) this.logger.debug("LoginController created");
    }

    @Override
    public void onRegisterButtonClicked() {
        logger.debug("LoginController: onRegisterButtonClicked called");

    }
}
