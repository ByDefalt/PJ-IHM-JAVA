package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.core.session.ISessionObserver;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.factory.ComposantSwingFactory;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IAppMainGraphicController;
import com.ubo.tp.message.ihm.view.service.View;

import java.util.Objects;
import java.util.function.Consumer;

public class NavigationController implements INavigationController, ISessionObserver {

    private final ControllerContext context;
    private final ViewContext viewContext;
    private Consumer<View> mainView;

    public NavigationController(ControllerContext context, ViewContext viewContext) {
        this.context = Objects.requireNonNull(context);
        this.viewContext = viewContext;

        this.context.session().addObserver(this);
    }

    private void setMainContent(View mainContent) {
        context.logger().info("setMainContent");
        this.mainView.accept(mainContent);
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
    public void navigateToProfile() {

    }

    @Override
    public void setMainView(Consumer<View> mainView) {
        this.mainView = mainView;
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
