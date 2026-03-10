package com.ubo.tp.message.ihm.graphiccontroller.javafx;

import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListUserGraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxListUserView;
import com.ubo.tp.message.ihm.view.javafx.FxUserView;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Graphic controller de la liste des utilisateurs — JavaFX.
 */
public class FxListUserGraphicController implements IListUserGraphicController {

    private final ViewContext viewContext;
    private final FxListUserView listUserView;
    private final List<FxUserView> userViews = new ArrayList<>();

    public FxListUserGraphicController(ViewContext viewContext, FxListUserView listUserView) {
        this.viewContext = viewContext;
        this.listUserView = listUserView;
    }

    @Override
    public void addUser(User user, Consumer<User> added) {
        if (user == null) return;
        boolean exists = userViews.stream().anyMatch(uv -> uv.getUser().equals(user));
        if (exists) {
            if (viewContext.logger() != null) viewContext.logger().warn("(FX) User déjà présent : " + user.getName());
            return;
        }
        FxUserView view = new FxUserView(viewContext, user);
        view.setOnMouseClicked(e -> added.accept(user));
        userViews.add(view);

        Platform.runLater(() -> listUserView.addUserUI(view));
        if (viewContext.logger() != null) viewContext.logger().debug("(FX) User ajouté : " + user.getName());
    }

    @Override
    public void removeUser(User user) {
        if (user == null) return;

        Optional<FxUserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        if (opt.isPresent()) {
            userViews.remove(opt.get());
            Platform.runLater(() -> listUserView.rebuildUI(new ArrayList<>(userViews)));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) User supprimé : " + user.getName());
        }
    }

    @Override
    public void updateUser(User user) {
        if (user == null) return;

        Optional<FxUserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        if (opt.isPresent()) {
            FxUserView view = opt.get();
            Platform.runLater(() -> listUserView.updateUserUI(view, user));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) User mis à jour : " + user.getName());
        }
    }

    @Override
    public void incrementUnread(User user) {
        if (user == null) return;
        Optional<FxUserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        if (opt.isPresent()) {
            Platform.runLater(() -> opt.get().incrementUnread());
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("(FX) User non trouvé pour incrément unread: " + user.getName());
        }
    }

    @Override
    public void clearUnread(User user) {
        if (user == null) return;
        Optional<FxUserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        if (opt.isPresent()) {
            Platform.runLater(() -> opt.get().clearUnread());
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("(FX) User non trouvé pour clear unread: " + user.getName());
        }
    }
}
