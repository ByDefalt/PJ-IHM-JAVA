package com.ubo.tp.message.ihm.screen;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.ihm.component.LoginComponent;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {

    private final ILoginController controller;

    private final LoginComponent component;
    private final Logger LOGGER;

    public LoginView(ILoginController controller, LoginComponent component, Logger logger) {
        this.controller = controller;
        this.component = component;
        this.LOGGER = logger;

        this.init();
    }

    private void init(){
        if (LOGGER != null) LOGGER.debug("Initialisation de la LoginView");
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Ajouter le composant UI pur au centre
        this.add(component, BorderLayout.CENTER);

        // Brancher les actions du component vers le controller
        this.component.addRegisterListener(e -> {
            if (LOGGER != null) LOGGER.debug("Bouton d'inscription cliqué");
            controller.onRegisterButtonClicked();
        });

        if (LOGGER != null) LOGGER.debug("LoginView initialisée");
    }
}
