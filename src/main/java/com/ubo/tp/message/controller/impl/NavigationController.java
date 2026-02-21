package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.core.session.ISessionObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.factory.ComposantSwingFactory;
import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.service.View;
import com.ubo.tp.message.ihm.view.contexte.ViewContext;

import java.util.Objects;

public class NavigationController implements INavigationController, ISessionObserver {

    private final ControllerContext context;
    private final IAppMainGraphicController graphicController;
    private final ViewContext viewContext;

    public NavigationController(ControllerContext context, IAppMainGraphicController graphicController, ViewContext viewContext) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;
        this.viewContext = viewContext;

        this.context.session().addObserver(this);
    }

    private void setMainContent(View mainContent) {
        context.logger().info("setMainContent");
        this.graphicController.setMainView(mainContent);
    }

    @Override
    public void navigateToLogin() {
        context.logger().info("navigateToLogin");
        this.setMainContent(ComposantSwingFactory.createLoginView(context, viewContext, this));
    }

    @Override
    public void navigateToRegister() {
        context.logger().info("navigateToRegister");
        this.setMainContent(ComposantSwingFactory.createRegisterView(context, viewContext, this));
    }

    @Override
    public void notifyLogin(User connectedUser) {
        context.logger().info("notifyLogin");
        this.setMainContent(ComposantSwingFactory.createChatMainView(context, viewContext));
    }

    @Override
    public void notifyLogout() {
        this.navigateToLogin();
    }
}
