package com.ubo.tp.message.ihm.graphiccontroller.swing;

import com.ubo.tp.message.controller.service.IUpdateAccountController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.UpdateAccountView;

/**
 * Contrôleur graphique Swing de la vue de mise à jour du compte.
 */
public class UpdateAccountGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final UpdateAccountView updateAccountView;
    private final IUpdateAccountController updateAccountController;

    public UpdateAccountGraphicController(ViewContext viewContext,
                                          UpdateAccountView updateAccountView,
                                          IUpdateAccountController updateAccountController) {
        this.viewContext = viewContext;
        this.updateAccountView = updateAccountView;
        this.updateAccountController = updateAccountController;

        init();
    }

    private void init() {
        handleInitView();
        registerCallbacks();
    }

    private void handleInitView() {
        var user = updateAccountController.getConnectedUser();
        if (user != null) {
            updateAccountView.setUser(user);
        }
    }

    private void registerCallbacks() {
        updateAccountView.setOnUpdateRequested(this::handleUpdateRequested);
    }

    private void handleUpdateRequested(String newName) {
        if (viewContext != null && viewContext.logger() != null) {
            viewContext.logger().debug("Mise à jour demandée (nouveau nom): " + newName);
            boolean update = updateAccountController.onUpdateNameClicked(newName);
            if (update) {
                viewContext.navigationController().navigateToChat();
            }
        }
    }
}
