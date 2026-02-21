package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.ihm.graphicController.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.RegisterView;
import com.ubo.tp.message.ihm.contexte.ViewContext;

public class RegisterGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final RegisterView registerView;
    private final IRegisterController registerController;
    private final INavigationController navigationController;

    public RegisterGraphicController(ViewContext viewContext, RegisterView registerView, IRegisterController registerController, INavigationController navigationController) {
        this.viewContext = viewContext;
        this.registerView = registerView;
        this.registerController = registerController;
        this.navigationController = navigationController;

        registerView.setOnRegisterRequested((tag, name, password, confirmPassword) -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Inscription demandée pour : " + tag);
            boolean created = registerController.onRegisterButtonClicked(tag, name, password, confirmPassword);
            if (created) {
                if (viewContext.logger() != null) viewContext.logger().info("Inscription réussie, navigation vers login");
                navigationController.navigateToLogin();
            } else {
                if (viewContext.logger() != null) viewContext.logger().warn("Inscription échouée pour : " + tag);
            }
        });

        registerView.setOnBackToLoginRequested(() -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Retour vers la connexion");
            navigationController.navigateToLogin();
        });
    }
}