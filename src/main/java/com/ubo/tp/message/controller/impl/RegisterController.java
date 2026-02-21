package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.datamodel.User;

import java.util.Objects;
import java.util.Set;

/**
 * Implémentation du contrôleur d'enregistrement d'utilisateur.
 * <p>
 * Pour l'instant le contrôleur se contente de logger l'action. À terme il
 * devra valider les données, créer l'utilisateur via IDataManager et naviguer.
 * </p>
 */
public class RegisterController implements IRegisterController {

    private final ControllerContext context;

    /**
     * Constructeur du RegisterController.
     *
     * @param context contexte regroupant les services nécessaires
     */
    public RegisterController(ControllerContext context) {
        this.context = Objects.requireNonNull(context);
        if (this.context.logger() != null) this.context.logger().debug("RegisterController created");
    }

    @Override
    public boolean onRegisterButtonClicked(String tag, String name, String password, String confirmPassword) {
        return register(tag, name, password, confirmPassword);
    }

    public boolean register(String tag, String name, String password, String confirmPassword) {
        context.logger().debug("RegisterController: onRegisterButtonClicked called");
        context.logger().info("Registering user with tag: " + tag + ", name: " + name + ", password: " + password + ", confirmPassword: " + confirmPassword);
        if (validateUserData(tag, name, password, confirmPassword)) {
            context.logger().info("User data is valid");
        } else {
            context.logger().warn("User data is invalid");
            return false;
        }
        return createUserIfNotExist(tag, name, password, confirmPassword);
    }

    public boolean createUserIfNotExist(String tag, String name, String password, String confirmPassword) {
        context.logger().debug("RegisterController: addUser called");
        context.logger().info("Adding user with tag: " + tag + ", name: " + name + ", password: " + password + ", confirmPassword: " + confirmPassword);
        Set<User> users = context.dataManager().getUsers();
        boolean userExists = users.stream().anyMatch(u -> u.getName().equals(name));
        if (!userExists) {
            context.dataManager().sendUser(new User(tag, password, name));
            context.logger().info("User added successfully: " + name);
            return true;
        } else {
            context.logger().warn("User already exists: " + name);
            return false;
        }
    }

    public boolean validateUserData(String tag, String name, String password, String confirmPassword) {
        if (tag == null || tag.isEmpty()) {
            context.logger().warn("Validation failed: tag is empty");
            return false;
        }
        if (name == null || name.isEmpty()) {
            context.logger().warn("Validation failed: name is empty");
            return false;
        }
        if (password == null || password.isEmpty()) {
            context.logger().warn("Validation failed: password is empty");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            context.logger().warn("Validation failed: password and confirmPassword do not match");
            return false;
        }
        return true;
    }
}
