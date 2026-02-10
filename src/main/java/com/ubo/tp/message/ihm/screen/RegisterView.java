package com.ubo.tp.message.ihm.screen;

import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.ihm.component.RegisterComponent;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

public class RegisterView extends JPanel {

    private final IRegisterController controller;
    private final RegisterComponent component;
    private final Logger LOGGER;

    public RegisterView(IRegisterController controller, RegisterComponent component, Logger logger) {
        this.controller = controller;
        this.component = component;
        this.LOGGER = logger;

        this.init();
    }

    private void init() {
        if (LOGGER != null) LOGGER.debug("Initialisation de RegisterView");
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        this.add(component, BorderLayout.CENTER);

        this.component.addRegisterListener(e -> {
            if (LOGGER != null) LOGGER.debug("Register button clicked");
            controller.onRegisterButtonClicked();
        });

        if (LOGGER != null) LOGGER.debug("RegisterView initialisée");
    }
}
