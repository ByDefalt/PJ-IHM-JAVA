package com.ubo.tp.message.ihm.screen;

import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.ihm.component.RegisterComponent;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Vue de l'écran d'inscription.
 * <p>
 * Assemble le {@link RegisterComponent} et branche le bouton d'inscription
 * vers le {@link IRegisterController} fourni.
 * </p>
 */
public class RegisterView extends View{

    private final IRegisterController controller;
    private final RegisterComponent component;
    private final Logger LOGGER;

    /**
     * Crée la vue d'inscription.
     *
     * @param controller contrôleur d'inscription
     * @param component composant UI pur pour l'inscription
     * @param logger logger optionnel
     */
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
            boolean userIsCreated = controller.onRegisterButtonClicked(this.component.getTagField().getText(),
                    this.component.getNameField().getText(),
                    new String(this.component.getPasswordField().getPassword()),
                    new String(this.component.getConfirmPasswordField().getPassword()));
                if (userIsCreated) {
                    if (LOGGER != null) LOGGER.info("User registered successfully, navigating to login view");
                }else{
                    if (LOGGER != null) LOGGER.warn("User registration failed, user already exists");
                }
        });

        if (LOGGER != null) LOGGER.debug("RegisterView initialisée");
    }
}
