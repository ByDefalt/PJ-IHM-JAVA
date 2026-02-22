package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.GraphicController;
import com.ubo.tp.message.ihm.view.swing.InputMessageView;

public class InputMessageGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final InputMessageView inputMessageView;
    private final IInputMessageController inputMessageController;

    public InputMessageGraphicController(ViewContext viewContext, InputMessageView inputMessageView, IInputMessageController inputMessageController) {
        this.viewContext = viewContext;
        this.inputMessageView = inputMessageView;
        this.inputMessageController = inputMessageController;

        inputMessageView.setOnSendRequested(this::handleSendAction);
    }

    private void handleSendAction() {
        String message = inputMessageView.getText().trim();
        if (viewContext.logger() != null) viewContext.logger().debug("Envoi demandé : " + message);
        if (message.isEmpty()) return;

        if (inputMessageController != null) {
            if (viewContext.selected().getSelectedChannel() != null) {
                viewContext.logger().debug("Envoi du message au canal : " + viewContext.selected().getSelectedChannel().getName());
                inputMessageController.sendMessage(viewContext.selected().getSelectedChannel().getUuid(), message);
            } else if (viewContext.selected().getSelectedUser() != null) {
                viewContext.logger().debug("Envoi du message à l'utilisateur : " + viewContext.selected().getSelectedUser().getName());
                inputMessageController.sendMessage(viewContext.selected().getSelectedUser().getUuid(), message);
            }
        }

        inputMessageView.clearText();
    }
}