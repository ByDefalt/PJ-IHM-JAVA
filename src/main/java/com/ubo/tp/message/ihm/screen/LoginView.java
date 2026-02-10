package com.ubo.tp.message.ihm.screen;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.ihm.component.LoginComponent;
import com.ubo.tp.message.logger.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Vue de l'écran de connexion.
 * <p>
 * Cette classe assemble le composant UI pur {@link LoginComponent} et branche
 * ses événements vers le {@link ILoginController} fourni.
 * </p>
 */
public class LoginView extends View {

    private final ILoginController controller;

    private final LoginComponent component;
    private final Logger LOGGER;

    /**
     * Crée la vue de connexion.
     *
     * @param controller contrôleur à invoquer pour les actions utilisateur
     * @param component composant UI pur contenant les champs et boutons
     * @param logger logger optionnel pour trace
     */
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

        this.component.addLoginListener(e -> {
            if (LOGGER != null) LOGGER.debug("Bouton de connexion cliqué");
            controller.onLoginButtonClicked(this.component.getTagField().getText(),
                    this.component.getNameField().getText(),
                    new String(this.component.getPasswordField().getPassword()));
        });


        if (LOGGER != null) LOGGER.debug("LoginView initialisée");
    }
}
