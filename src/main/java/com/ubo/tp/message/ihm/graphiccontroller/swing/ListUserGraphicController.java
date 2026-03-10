package com.ubo.tp.message.ihm.graphiccontroller.swing;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListUserGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListUserView;
import com.ubo.tp.message.ihm.view.swing.UserView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

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
    public void addUser(User user, Consumer<User> added) {
        handleAddUser(user, added);
    }

    private void handleAddUser(User user, Consumer<User> added) {
        if (user == null) return;

        var logger = viewContext.logger();

        if (isAlreadyPresent(user)) {
            if (logger != null) logger.warn("User déjà présent, ignoré : " + user.getName());
            return;
        }

        UserView userView = createUserView(user);
        registerSelection(userView, added);
        int row = userViews.size();
        userViews.add(userView);
        listUserView.addUserUI(userView, row);

        if (logger != null) logger.debug("User ajouté : " + user.getName());
    }

    private boolean isAlreadyPresent(User user) {
        return userViews.stream().anyMatch(uv -> uv.getUser().equals(user));
    }

    private UserView createUserView(User user) {
        return new UserView(viewContext, user);
    }

    private void registerSelection(UserView userView, Consumer<User> onSelect) {
        userView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onSelect.accept(userView.getUser());
            }
        });
    }

    @Override
    public void removeUser(User user) {
        handleRemoveUser(user);
    }

    private void handleRemoveUser(User user) {
        if (user == null) return;
        var logger = viewContext.logger();

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
        handleUpdateUser(user);
    }

    private void handleUpdateUser(User user) {
        if (user == null) return;
        var logger = viewContext.logger();

        Optional<UserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();

        if (opt.isPresent()) {
            listUserView.updateUserUI(opt.get(), user);
            if (logger != null) logger.debug("User mis à jour : " + user.getName());
        } else {
            if (logger != null) logger.warn("User non trouvé pour mise à jour : " + user.getName());
        }
    }

    @Override
    public void incrementUnread(User user) {
        handleIncrementUnread(user);
    }

    private void handleIncrementUnread(User user) {
        if (user == null) return;
        Optional<UserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        if (opt.isPresent()) {
            opt.get().incrementUnread();
        } else {
            if (viewContext.logger() != null)
                viewContext.logger().warn("User non trouvé pour incrément unread: " + user.getName());
        }
    }

    @Override
    public void clearUnread(User user) {
        handleClearUnread(user);
    }

    private void handleClearUnread(User user) {
        if (user == null) return;
        Optional<UserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        if (opt.isPresent()) {
            opt.get().clearUnread();
        } else {
            if (viewContext.logger() != null)
                viewContext.logger().warn("User non trouvé pour clear unread: " + user.getName());
        }
    }
}