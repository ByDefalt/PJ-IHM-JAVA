package com.ubo.tp.message.ihm.contexte;

import com.ubo.tp.message.core.selected.ISelected;
import com.ubo.tp.message.core.session.ISession;
import com.ubo.tp.message.logger.Logger;

public record ViewContext(
        Logger logger,
        ISession session,
        ISelected selected
) {
}
