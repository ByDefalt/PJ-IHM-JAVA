package com.ubo.tp.message.binder;

import com.ubo.tp.message.controller.service.IInputMessageController;
import com.ubo.tp.message.ihm.graphicController.service.IInputMessageGraphicController;

public record InputMessageBinder(
        IInputMessageController controller,
        IInputMessageGraphicController graphicController
)  implements Binder {

    @Override
    public void bind() {
        graphicController.getText().on()
                .nonNull()
                .changed(controller::sendMessageToSelected)
                .bind();
    }
}