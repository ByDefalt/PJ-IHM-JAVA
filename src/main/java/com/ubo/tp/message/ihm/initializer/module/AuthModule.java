package com.ubo.tp.message.ihm.initializer.module;

import com.ubo.tp.message.controller.impl.LoginController;
import com.ubo.tp.message.controller.impl.RegisterController;
import com.ubo.tp.message.ihm.component.LoginComponent;
import com.ubo.tp.message.ihm.component.RegisterComponent;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.ViewRegistry;
import com.ubo.tp.message.ihm.screen.LoginView;
import com.ubo.tp.message.ihm.screen.RegisterView;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;

import javax.swing.JComponent;

/**
 * Module d'authentification : enregistre les vues de login et register via le ViewRegistry.
 */
public class AuthModule implements UIModule {
    @Override
    public void register(NavigationService navigation, com.ubo.tp.message.core.DataManager dataManager, Logger logger, ControllerRegistry controllerRegistry, ViewRegistry viewRegistry) {
        // register controllers into controllerRegistry using InitializationContext
        controllerRegistry.register("loginController", com.ubo.tp.message.controller.service.ILoginController.class,
                ctx -> new LoginController(ctx.getLogger(), ctx.getNavigation()));
        controllerRegistry.register("registerController", com.ubo.tp.message.controller.service.IRegisterController.class,
                ctx -> new RegisterController());

        // register creators that will be invoked later by UIInitializer
        viewRegistry.register("login", ctx -> {
            // prefer controllerRegistry-created controller when available
            com.ubo.tp.message.controller.service.ILoginController loginController = controllerRegistry.create("loginController", ctx, com.ubo.tp.message.controller.service.ILoginController.class);
            if (loginController == null) {
                loginController = new LoginController(ctx.getLogger(), ctx.getNavigation());
            }
            LoginComponent comp = new LoginComponent(ctx.getLogger());
            LoginView view = new LoginView(loginController, comp, ctx.getLogger());
            return (JComponent) view;
        });

        viewRegistry.register("register", ctx -> {
            com.ubo.tp.message.controller.service.IRegisterController registerController = controllerRegistry.create("registerController", ctx, com.ubo.tp.message.controller.service.IRegisterController.class);
            if (registerController == null) {
                registerController = new RegisterController();
            }
            RegisterComponent comp = new RegisterComponent(ctx.getLogger());
            RegisterView view = new RegisterView(registerController, comp, ctx.getLogger());
            return (JComponent) view;
        });
    }
}
