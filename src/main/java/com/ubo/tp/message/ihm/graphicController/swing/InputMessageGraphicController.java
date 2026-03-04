package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IInputMessageGraphicController;
import com.ubo.tp.message.ihm.view.swing.InputMessageView;
import com.ubo.tp.message.observableProperty.ObservableProperty;

public class InputMessageGraphicController implements IInputMessageGraphicController {

    private final ViewContext viewContext;
    private final InputMessageView inputMessageView;

    private final ObservableProperty<String> textProperty = new ObservableProperty<>();

    public InputMessageGraphicController(ViewContext viewContext, InputMessageView inputMessageView) {
        this.viewContext = viewContext;
        this.inputMessageView = inputMessageView;
        inputMessageView.setOnSendRequested(this::setText);
    }


    private void setText(String text) {
        if (viewContext.logger() != null) viewContext.logger().debug("Envoi demandé : " + text);
        if (text.isEmpty()) return;
        textProperty.set(text);
        inputMessageView.clearText();
    }

    public ObservableProperty<String> getText() {
        return textProperty;
    }
}