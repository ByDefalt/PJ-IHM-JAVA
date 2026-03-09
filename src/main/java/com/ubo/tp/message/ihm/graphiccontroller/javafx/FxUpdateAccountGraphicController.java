package com.ubo.tp.message.ihm.graphiccontroller.javafx;

import com.ubo.tp.message.controller.service.IUpdateAccountController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.GraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxUpdateAccountView;

/**
 * Graphic controller de la vue modification de profil — JavaFX.
 */
public class FxUpdateAccountGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final FxUpdateAccountView updateView;
    private final IUpdateAccountController updateController;

    public FxUpdateAccountGraphicController(ViewContext viewContext,
                                            FxUpdateAccountView updateView,
                                            IUpdateAccountController updateController) {
        this.viewContext = viewContext;
        this.updateView = updateView;
        this.updateController = updateController;
        wire();
    }

    private void wire() {
        var user = updateController.getConnectedUser();
        if (user != null) updateView.setUser(user);

        updateView.setOnUpdateRequested(newName -> {
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Mise à jour demandée : " + newName);
            boolean ok = updateController.onUpdateNameClicked(newName);
            if (ok) viewContext.navigationController().navigateToChat();
        });
    }
}

