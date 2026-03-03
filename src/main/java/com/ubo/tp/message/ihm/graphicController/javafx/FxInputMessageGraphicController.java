package com.ubo.tp.message.ihm.graphicController.javafx;

import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.GraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxInputMessageView;

/**
 * Graphic controller de la saisie de message — JavaFX.
 */
public class FxInputMessageGraphicController implements GraphicController {

    private final ViewContext viewContext;
    private final FxInputMessageView inputView;
    private final IInputMessageController inputController;

    public FxInputMessageGraphicController(ViewContext viewContext,
                                           FxInputMessageView inputView,
                                           IInputMessageController inputController) {
        this.viewContext = viewContext;
        this.inputView = inputView;
        this.inputController = inputController;

        inputView.setOnSendRequested(this::handleSend);
    }

    private void handleSend() {
        String text = inputView.getText().trim();
        if (viewContext.logger() != null) viewContext.logger().debug("(FX) Envoi demandé : " + text);
        if (text.isEmpty()) return;
        if (inputController != null) inputController.sendMessageToSelected(text);
        inputView.clearText();
    }
}

