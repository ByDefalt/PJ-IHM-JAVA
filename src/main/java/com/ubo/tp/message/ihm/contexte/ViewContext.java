package com.ubo.tp.message.ihm.contexte;

import com.ubo.tp.message.controller.service.INavigationController;
import com.ubo.tp.message.logger.Logger;

public record ViewContext(
        Logger logger,
        INavigationController navigationController
) {
}
