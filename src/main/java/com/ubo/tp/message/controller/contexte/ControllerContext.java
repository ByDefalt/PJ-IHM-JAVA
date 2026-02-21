package com.ubo.tp.message.controller.contexte;

import com.ubo.tp.message.core.IDataManager;
import com.ubo.tp.message.core.selected.ISelected;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.logger.Logger;

public record ControllerContext(
        Logger logger,
        IDataManager dataManager,
        ISession session,
        ISelected selected
) {
}
