package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

/**
 * Implémentation du contrôleur d'enregistrement d'utilisateur.
 * <p>
 * Pour l'instant le contrôleur se contente de logger l'action. À terme il
 * devra valider les données, créer l'utilisateur via IDataManager et naviguer.
 * </p>
 */
public class RegisterController implements IRegisterController {

    private final Logger logger;
    private final NavigationService navigation;

    /**
     * Constructeur du RegisterController.
     *
     * @param logger service de logging (peut être null)
     * @param navigation service de navigation pour changer de vue
     */
    public RegisterController(Logger logger, NavigationService navigation) {
        this.logger = logger;
        this.navigation = navigation;
        if (this.logger != null) this.logger.debug("RegisterController created");
    }

    @Override
    public void onRegisterButtonClicked(String tag, String name, String password, String confirmPassword) {
        logger.debug("RegisterController: onRegisterButtonClicked called");
        logger.info("Registering user with tag: " + tag + ", name: " + name + ", password: " + password + ", confirmPassword: " + confirmPassword);
        
    }
}
