package com.ubo.tp.message.ihm.initializer.module;

import com.ubo.tp.message.controller.impl.LoginController;
import com.ubo.tp.message.controller.impl.RegisterController;
import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.ihm.component.LoginComponent;
import com.ubo.tp.message.ihm.component.RegisterComponent;
import com.ubo.tp.message.ihm.initializer.registry.ControllerRegistry;
import com.ubo.tp.message.ihm.initializer.registry.ViewRegistry;
import com.ubo.tp.message.ihm.initializer.registry.utils.ViewRegistryUtils;
import com.ubo.tp.message.ihm.screen.LoginView;
import com.ubo.tp.message.ihm.screen.RegisterView;
import com.ubo.tp.message.logger.Logger;
import com.ubo.tp.message.navigation.NavigationService;
import com.ubo.tp.message.core.IDataManager;

import java.util.Collections;

/**
 * Module d'authentification : enregistre les vues de login et register via le ViewRegistry.
 */
public class AuthModule implements UIModule {
    @Override
    public void register(NavigationService navigation, IDataManager dataManager, Logger logger, ControllerRegistry controllerRegistry, ViewRegistry viewRegistry) {

        // register controllers into controllerRegistry using InitializationContext
        controllerRegistry.register("loginController", ILoginController.class,
                ctx -> new LoginController(ctx.getLogger(), ctx.getNavigation(), ctx.getDataManager()));
        controllerRegistry.register("registerController", IRegisterController.class,
                ctx -> new RegisterController(ctx.getLogger(), ctx.getNavigation(), ctx.getDataManager()));

        // register creators that will be invoked later by UIInitializer
        viewRegistry.register("login", ViewRegistryUtils.createViewFromController(
                "loginController",
                ILoginController.class,
                ctx -> new LoginController(ctx.getLogger(), ctx.getNavigation(), ctx.getDataManager()),
                ctx -> Collections.singletonList(new LoginComponent(ctx.getLogger())),
                (ctrl, comps, ctx) -> new LoginView(ctrl, comps.getFirst(), ctx.getLogger())
        ));

        viewRegistry.register("register", ViewRegistryUtils.createViewFromController(
                "registerController",
                IRegisterController.class,
                ctx -> new RegisterController(ctx.getLogger(), ctx.getNavigation(), ctx.getDataManager()),
                ctx -> Collections.singletonList(new RegisterComponent(ctx.getLogger())),
                (ctrl, comps, ctx) -> new RegisterView(ctrl, comps.getFirst(), ctx.getLogger())
        ));
    }
}
