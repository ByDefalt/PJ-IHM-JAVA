package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IUpdateAccountController;

import java.util.Objects;

public class UpdateAccountController implements IUpdateAccountController {

    private final ControllerContext context;

    public UpdateAccountController(ControllerContext context) {
        this.context = Objects.requireNonNull(context);
        if (this.context.logger() != null) this.context.logger().debug("UpdateAccountController created");
    }

    @Override
    public boolean onUpdateNameClicked(String newName) {
        if (newName == null) return false;
        if (context.logger() != null) context.logger().debug("Mise à jour du nom utilisateur vers: " + newName);

        var session = context.session();
        var dataManager = context.dataManager();
        if (session == null || dataManager == null) return false;

        var user = session.getConnectedUser();
        if (user == null) return false;

        user.setName(newName);
        // Propager la modification via le DataManager (écriture/synchronisation)
        dataManager.sendUser(user);
        return true;
    }
}
