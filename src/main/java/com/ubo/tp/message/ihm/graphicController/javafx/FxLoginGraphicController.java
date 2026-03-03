package com.ubo.tp.message.ihm.graphicController.javafx;

import com.ubo.tp.message.controller.service.ILoginController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.GraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxLoginView;

/**
 * Graphic controller de la vue connexion — JavaFX.
 */
public class FxLoginGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final FxLoginView loginView;
    private final ILoginController loginController;

    public FxLoginGraphicController(ViewContext viewContext, FxLoginView loginView, ILoginController loginController) {
        this.viewContext = viewContext;
        this.loginView = loginView;
        this.loginController = loginController;
        wire();
    }

    private void wire() {
        loginView.setOnLoginRequested((tag, name, pwd) -> {
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Connexion demandée : " + tag);
            loginController.onLoginButtonClicked(tag, name, pwd);
        });
        loginView.setOnRegisterRequested(() -> {
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Navigation vers inscription");
            viewContext.navigationController().navigateToRegister();
        });
    }
}

