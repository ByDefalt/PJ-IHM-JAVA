package com.ubo.tp.message.ihm.graphiccontroller.swing;

import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.RegisterView;

/**
 * Contrôleur graphique Swing pour la vue d'inscription.
 */
public class RegisterGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final RegisterView registerView;
    private final IRegisterController registerController;

    public RegisterGraphicController(ViewContext viewContext, RegisterView registerView, IRegisterController registerController) {
        this.viewContext = viewContext;
        this.registerView = registerView;
        this.registerController = registerController;

        createConnector();
    }

    private void createConnector() {
        registerView.setOnRegisterRequested(this::handleRegisterRequested);
        registerView.setOnBackToLoginRequested(this::handleBackToLoginRequested);
    }

    private void handleRegisterRequested(String tag, String name, String password, String confirmPassword) {
        if (viewContext.logger() != null) viewContext.logger().debug("Inscription demandée pour : " + tag);
        boolean created = registerController.onRegisterButtonClicked(tag, name, password, confirmPassword);
        if (created) {
            if (viewContext.logger() != null)
                viewContext.logger().info("Inscription réussie, navigation vers login");
            viewContext.navigationController().navigateToLogin();
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("Inscription échouée pour : " + tag);
        }
    }

    private void handleBackToLoginRequested() {
        if (viewContext.logger() != null) viewContext.logger().debug("Retour vers la connexion");
        viewContext.navigationController().navigateToLogin();
    }
}