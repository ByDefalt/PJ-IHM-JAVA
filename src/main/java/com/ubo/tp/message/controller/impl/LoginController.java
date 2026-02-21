package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.datamodel.User;

import java.util.Objects;
import java.util.Optional;

/**
 * Implémentation simple du {@link ILoginController}.
 * <p>
 * Délègue essentiellement la navigation entre les vues (login -> register)
 * et consigne les actions dans le logger.
 * </p>
 */
public class LoginController implements ILoginController {

    private final ControllerContext context;

    /**
     * Crée un LoginController.
     *
     * @param context contexte regroupant les services nécessaires
     */
    public LoginController(ControllerContext context) {
        this.context = Objects.requireNonNull(context);
        if (this.context.logger() != null) this.context.logger().debug("LoginController created");
    }

    @Override
    public void onLoginButtonClicked(String tag, String name, String password) {
        login(tag, name, password);
    }

    public void login(String tag, String name, String password) {
        context.logger().debug("LoginController: onLoginButtonClicked called");
        Optional<User> userOpt = validateLogin(tag, name, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            context.logger().info("LoginController: User logged in - " + tag);
            user.setOnline(true);
            context.dataManager().sendUser(user);
            context.logger().info("LoginController: User set online - " + tag);
            this.context.session().connect(user);
        } else {
            context.logger().warn("LoginController: Login failed for tag - " + tag);
        }
    }

    public Optional<User> validateLogin(String tag, String name, String password) {
        if (tag == null || name == null || password == null || tag.isEmpty() || name.isEmpty() || password.isEmpty()) {
            context.logger().warn("LoginController: validateLogin - missing fields");
            return Optional.empty();
        }
        Optional<User> user = context.dataManager().getUsers().stream()
                .filter(u -> u.getUserTag().equals(tag) && u.getName().equals(name) && u.getUserPassword().equals(password))
                .findFirst();
        if (user.isPresent()) {
            context.logger().info("LoginController: User logged in successfully - " + tag);
        } else {
            context.logger().warn("LoginController: Invalid login attempt - " + tag);
        }
        return user;
    }

}
