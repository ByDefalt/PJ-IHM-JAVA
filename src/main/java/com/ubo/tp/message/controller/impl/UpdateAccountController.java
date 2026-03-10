package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IUpdateAccountController;
import com.ubo.tp.message.datamodel.User;

import java.util.Objects;

/**
 * Contrôleur gérant la mise à jour des informations de compte utilisateur.
 */
public class UpdateAccountController implements IUpdateAccountController {

    private final ControllerContext context;

    /**
     * Crée un {@code UpdateAccountController}.
     *
     * @param context contexte d'exécution contenant les services partagés
     */
    public UpdateAccountController(ControllerContext context) {
        this.context = Objects.requireNonNull(context);
        if (this.context.logger() != null) this.context.logger().debug("UpdateAccountController created");
    }

    /**
     * Tentative de mise à jour du nom de l'utilisateur connecté.
     *
     * @param newName nouveau nom souhaité
     * @return {@code true} si la mise à jour a été effectuée, {@code false} sinon
     */
    @Override
    public boolean onUpdateNameClicked(String newName) {
        return handleOnUpdateNameClickedLogic(newName);
    }

    /**
     * Logique interne de mise à jour du nom.
     */
    private boolean handleOnUpdateNameClickedLogic(String newName) {
        if (newName == null) return false;
        if (context.logger() != null) context.logger().debug("Mise à jour du nom utilisateur vers: " + newName);

        var session = context.session();
        var dataManager = context.dataManager();
        if (session == null || dataManager == null) return false;

        var user = session.getConnectedUser();
        if (user == null) return false;

        user.setName(newName);
        dataManager.sendUser(user);
        return true;
    }

    /**
     * Retourne l'utilisateur actuellement connecté, ou {@code null} si aucune session.
     *
     * @return utilisateur connecté ou {@code null}
     */
    @Override
    public User getConnectedUser() {
        if (context.session() == null) return null;
        return context.session().getConnectedUser();
    }
}
