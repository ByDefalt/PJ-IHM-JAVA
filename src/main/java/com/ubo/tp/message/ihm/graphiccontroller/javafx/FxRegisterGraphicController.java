package com.ubo.tp.message.ihm.graphiccontroller.javafx;

import com.ubo.tp.message.controller.service.IRegisterController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.GraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxRegisterView;

/**
 * Graphic controller de la vue inscription — JavaFX.
 */
public class FxRegisterGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final FxRegisterView registerView;
    private final IRegisterController registerController;

    public FxRegisterGraphicController(ViewContext viewContext, FxRegisterView registerView, IRegisterController registerController) {
        this.viewContext = viewContext;
        this.registerView = registerView;
        this.registerController = registerController;
        wire();
    }

    private void wire() {
        registerView.setOnRegisterRequested((tag, name, pwd, confirm) -> {
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Inscription demandée : " + tag);
            boolean ok = registerController.onRegisterButtonClicked(tag, name, pwd, confirm);
            if (ok) {
                if (viewContext.logger() != null) viewContext.logger().info("(FX) Inscription réussie");
                viewContext.navigationController().navigateToLogin();
            } else {
                if (viewContext.logger() != null) viewContext.logger().warn("(FX) Inscription échouée : " + tag);
            }
        });
        registerView.setOnBackToLoginRequested(() -> {
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Retour vers connexion");
            viewContext.navigationController().navigateToLogin();
        });
    }
}

