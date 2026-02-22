package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.RegisterView;

public class RegisterGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final RegisterView registerView;
    private final IRegisterController registerController;

    public RegisterGraphicController(ViewContext viewContext, RegisterView registerView, IRegisterController registerController) {
        this.viewContext = viewContext;
        this.registerView = registerView;
        this.registerController = registerController;

        registerView.setOnRegisterRequested((tag, name, password, confirmPassword) -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Inscription demandée pour : " + tag);
            boolean created = registerController.onRegisterButtonClicked(tag, name, password, confirmPassword);
            if (created) {
                if (viewContext.logger() != null)
                    viewContext.logger().info("Inscription réussie, navigation vers login");
                viewContext.navigationController().navigateToLogin();
            } else {
                if (viewContext.logger() != null) viewContext.logger().warn("Inscription échouée pour : " + tag);
            }
        });

        registerView.setOnBackToLoginRequested(() -> {
            if (viewContext.logger() != null) viewContext.logger().debug("Retour vers la connexion");
            viewContext.navigationController().navigateToLogin();
        });
    }
}