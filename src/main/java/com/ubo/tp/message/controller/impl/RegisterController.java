package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.logger.Logger;

import java.util.Set;

/**
 * Implémentation du contrôleur d'enregistrement d'utilisateur.
 * <p>
 * Pour l'instant le contrôleur se contente de logger l'action. À terme il
 * devra valider les données, créer l'utilisateur via IDataManager et naviguer.
 * </p>
 */
public class RegisterController implements IRegisterController {

    private final Logger logger;
    private final IDataManager dataManager;

    /**
     * Constructeur du RegisterController.
     *
     * @param logger service de logging (peut être null)
     */
    public RegisterController(Logger logger, IDataManager dataManager) {
        this.logger = logger;
        this.dataManager = dataManager;
        if (this.logger != null) this.logger.debug("RegisterController created");
    }

    @Override
    public boolean onRegisterButtonClicked(String tag, String name, String password, String confirmPassword) {
        return register(tag, name, password, confirmPassword);
    }

    public boolean register(String tag, String name, String password, String confirmPassword) {
        logger.debug("RegisterController: onRegisterButtonClicked called");
        logger.info("Registering user with tag: " + tag + ", name: " + name + ", password: " + password + ", confirmPassword: " + confirmPassword);
        if (validateUserData(tag, name, password, confirmPassword)) {
            logger.info("User data is valid");
        } else {
            logger.warn("User data is invalid");
             return false;
        }
        return createUserIfNotExist(tag, name, password, confirmPassword);
    }

    public boolean createUserIfNotExist(String tag, String name, String password, String confirmPassword) {
        logger.debug("RegisterController: addUser called");
        logger.info("Adding user with tag: " + tag + ", name: " + name + ", password: " + password + ", confirmPassword: " + confirmPassword);
        Set<User> users = dataManager.getUsers();
        boolean userExists = users.stream().anyMatch(u -> u.getName().equals(name));
        if (!userExists) {
            dataManager.sendUser(new User(tag, password, name));
            logger.info("User added successfully: " + name);
            return true;
        } else {
            logger.warn("User already exists: " + name);
            return false;
        }
    }

    public boolean validateUserData(String tag, String name, String password, String confirmPassword) {
        if (tag == null || tag.isEmpty()) {
            logger.warn("Validation failed: tag is empty");
            return false;
        }
        if (name == null || name.isEmpty()) {
            logger.warn("Validation failed: name is empty");
            return false;
        }
        if (password == null || password.isEmpty()) {
            logger.warn("Validation failed: password is empty");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            logger.warn("Validation failed: password and confirmPassword do not match");
            return false;
        }
        return true;
    }
}
