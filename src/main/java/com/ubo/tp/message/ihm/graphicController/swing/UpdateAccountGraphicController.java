package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.IUpdateAccountController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.UpdateAccountView;

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
        // Récupérer l'utilisateur connecté et initialiser la vue
        if (viewContext != null && viewContext.session() != null) {
            var user = viewContext.session().getConnectedUser();
            updateAccountView.setUser(user);
        }

        // Brancher le callback UI vers le contrôleur métier
        updateAccountView.setOnUpdateRequested(newName -> {
            if (viewContext != null && viewContext.logger() != null) {
                viewContext.logger().debug("Mise à jour demandée (nouveau nom): " + newName);
                boolean update = updateAccountController.onUpdateNameClicked(newName);
                if (update) {
                    viewContext.navigationController().navigateToChat();
                }
            }
        });
    }
}
