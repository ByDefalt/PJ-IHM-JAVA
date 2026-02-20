package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.ihm.graphicController.service.IRegisterGraphicController;
import com.ubo.tp.message.ihm.view.swing.RegisterView;
import com.ubo.tp.message.logger.Logger;

public class RegisterGraphicController implements IRegisterGraphicController {

    private final Logger LOGGER;
    private final RegisterView registerView;
    private final IRegisterController registerController;

    public RegisterGraphicController(Logger logger, RegisterView registerView, IRegisterController registerController) {
        LOGGER = logger;
        this.registerView = registerView;
        this.registerController = registerController;
    }
}
