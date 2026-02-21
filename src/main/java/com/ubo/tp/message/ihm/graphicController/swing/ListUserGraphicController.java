package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IListUserGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListUserView;
import com.ubo.tp.message.ihm.view.swing.UserView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ListUserGraphicController implements IListUserGraphicController {

    private final ViewContext viewContext;
    private final ListUserView listUserView;

    /**
     * Source de vérité : liste ordonnée des utilisateurs affichés.
     */
    private final List<UserView> userViews = new ArrayList<>();

    public ListUserGraphicController(ViewContext viewContext, ListUserView listUserView) {
        this.viewContext = Objects.requireNonNull(viewContext, "viewContext ne peut pas être null");
        this.listUserView = Objects.requireNonNull(listUserView, "listUserView ne peut pas être null");
    }

    @Override
    public void addUser(User user) {
        if (user == null) return;

        var logger = viewContext.logger();

        User connected = (viewContext.session() != null) ? viewContext.session().getConnectedUser() : null;
        if (connected != null && connected.equals(user)) {
            if (logger != null) logger.debug("Ignorer l'ajout de l'utilisateur courant: " + user.getName());
            return;
        }

        boolean alreadyPresent = userViews.stream().anyMatch(uv -> uv.getUser().equals(user));

        if (alreadyPresent) {
            if (logger != null) logger.warn("User déjà présent, ignoré : " + user.getName());
            return;
        }

        UserView userView = new UserView(viewContext, user);
        int row = userViews.size();
        userViews.add(userView);

        userView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var l = viewContext.logger();
                if (l != null) l.debug("UserView cliqué: " + user.getName());
                viewContext.selected().setSelectedUser(user);
            }
        });

        listUserView.addUserUI(userView, row);

        if (logger != null) logger.debug("User ajouté : " + user.getName());
    }

    @Override
    public void removeUser(User user) {
        if (user == null) return;

        var logger = viewContext.logger();

        // Ne pas tenter de supprimer l'utilisateur connecté car il n'est pas affiché
        User connected = (viewContext.session() != null) ? viewContext.session().getConnectedUser() : null;
        if (connected != null && connected.equals(user)) {
            if (logger != null)
                logger.debug("Ignorer la suppression de l'utilisateur courant (non affiché) : " + user.getName());
            return;
        }

        Optional<UserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();

        if (opt.isPresent()) {
            userViews.remove(opt.get());
            listUserView.rebuildUI(userViews);
            if (logger != null) logger.debug("User supprimé : " + user.getName());
        } else {
            if (logger != null) logger.warn("User non trouvé, pas supprimé : " + user.getName());
        }
    }

    @Override
    public void updateUser(User user) {
        if (user == null) return;

        var logger = viewContext.logger();

        // Si l'utilisateur connecté change ses infos, on n'a rien à mettre à jour dans la liste
        User connected = (viewContext.session() != null) ? viewContext.session().getConnectedUser() : null;
        if (connected != null && connected.equals(user)) {
            if (logger != null)
                logger.debug("Ignorer la mise à jour de l'utilisateur courant (non affiché) : " + user.getName());
            return;
        }

        Optional<UserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();

        if (opt.isPresent()) {
            listUserView.updateUserUI(opt.get(), user);
            if (logger != null) logger.debug("User mis à jour : " + user.getName());
        } else {
            if (logger != null) logger.warn("User non trouvé pour mise à jour : " + user.getName());
        }
    }
}