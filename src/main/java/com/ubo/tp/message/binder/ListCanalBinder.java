package com.ubo.tp.message.binder;

import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;

public record ListCanalBinder(
        IListCanalController controller,
        IListCanalGraphicController graphicController
)  implements Binder {
    @Override
    public void bind() {
        controller.getChannels().on()
                .cleared(graphicController::clearCanals)
                .nonNull()
                .added(graphicController::addCanal)
                .removed(graphicController::removeCanal)
                .updated(graphicController::updateCanal)
                .bind();

        // Rejouer les éléments déjà présents dans le set avant l'abonnement
        controller.getChannels().snapshot().forEach(graphicController::addCanal);
    }
}
