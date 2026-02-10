package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

import java.util.Optional;

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
    private final IDataManager dataManager;

    /**
     * Crée un LoginController.
     *
     * @param logger service de logging (peut être null)
     * @param navigation service de navigation utilisé pour changer de vue
     */
    public LoginController(Logger logger, NavigationService navigation, IDataManager dataManager) {
        this.logger = logger;
        this.navigation = navigation;
        this.dataManager = dataManager;
        if (this.logger != null) this.logger.debug("LoginController created");
    }

    @Override
    public void onRegisterButtonClicked() {
        logger.debug("LoginController: onRegisterButtonClicked called");
        navigation.showView("register");
    }

    @Override
    public void onLoginButtonClicked(String tag, String name, String password) {
        logger.debug("LoginController: onLoginButtonClicked called");
        Optional<User> userOpt = validateLogin(tag, name, password);
        if(userOpt.isPresent()) {
            User user = userOpt.get();
            logger.info("LoginController: User logged in - " + tag);
            user.setOnline(true);
            logger.info("LoginController: User set online - " + tag);
            navigation.showView("empty");

        } else {
            // Ici on pourrait afficher un message d'erreur dans la vue
            logger.warn("LoginController: Login failed for tag - " + tag);
        }

    }

    public Optional<User> validateLogin(String tag, String name, String password) {
        if (tag == null || name == null || password == null || tag.isEmpty() || name.isEmpty() || password.isEmpty()) {
            logger.warn("LoginController: validateLogin - missing fields");
            return Optional.empty();
        }
        Optional<User> user = dataManager.getUsers().stream()
                .filter(u -> u.getUserTag().equals(tag) && u.getName().equals(name) && u.getUserPassword().equals(password))
                .findFirst();
        if (user.isPresent()) {
            logger.info("LoginController: User logged in successfully - " + tag);
        } else {
            logger.warn("LoginController: Invalid login attempt - " + tag);
        }
        return user;
    }

}
