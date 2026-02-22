package com.ubo.tp.message.controller.service;

import com.ubo.tp.message.ihm.view.service.View;

import java.util.function.Consumer;

public interface INavigationController extends Controller {
    void navigateToLogin();

    void navigateToRegister();

    void navigateToProfile();

    void navigateToChat();

    void setMainView(Consumer<View> mainView);
}
