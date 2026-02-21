package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphicController.service.IListUserGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListUserView;
import com.ubo.tp.message.ihm.view.swing.UserView;
import com.ubo.tp.message.ihm.view.contexte.ViewContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListUserGraphicController implements IListUserGraphicController {

    private final ViewContext viewContext;
    private final ListUserView listUserView;

    /**
     * Source de vérité : liste ordonnée des utilisateurs affichés.
     */
    private final List<UserView> userViews = new ArrayList<>();

    public ListUserGraphicController(ViewContext viewContext, ListUserView listUserView) {
        this.viewContext = viewContext;
        this.listUserView = listUserView;
    }

    @Override
    public void addUser(User user) {
        if (user == null || listUserView == null) return;

        boolean alreadyPresent = userViews.stream()
                .anyMatch(uv -> uv.getUser().equals(user));

        if (alreadyPresent) {
            if (viewContext.logger() != null) viewContext.logger().warn("User déjà présent, ignoré : " + user.getName());
            return;
        }

        UserView userView = new UserView(viewContext, user);
        int row = userViews.size();
        userViews.add(userView);

        listUserView.addUserUI(userView, row);

        if (viewContext.logger() != null) viewContext.logger().debug("User ajouté : " + user.getName());
    }

    @Override
    public void removeUser(User user) {
        if (user == null || listUserView == null) return;

        Optional<UserView> opt = userViews.stream()
                .filter(uv -> uv.getUser().equals(user))
                .findFirst();

        if (opt.isPresent()) {
            userViews.remove(opt.get());
            listUserView.rebuildUI(userViews);
            if (viewContext.logger() != null) viewContext.logger().debug("User supprimé : " + user.getName());
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("User non trouvé, pas supprimé : " + user.getName());
        }
    }

    @Override
    public void updateUser(User user) {
        if (user == null || listUserView == null) return;

        Optional<UserView> opt = userViews.stream()
                .filter(uv -> uv.getUser().equals(user))
                .findFirst();

        if (opt.isPresent()) {
            listUserView.updateUserUI(opt.get(), user);
            if (viewContext.logger() != null) viewContext.logger().debug("User mis à jour : " + user.getName());
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("User non trouvé pour mise à jour : " + user.getName());
        }
    }
}