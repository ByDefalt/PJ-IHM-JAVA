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

    private void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }

    @Override
    public void addUser(User user, Consumer<User> added) {
        handleAddUser(user, added);
    }

    private void handleAddUser(User user, Consumer<User> added) {
        if (user == null) return;
        if (exists(user)) {
            if (viewContext.logger() != null) viewContext.logger().warn("(FX) User déjà présent : " + user.getName());
            return;
        }
        FxUserView view = new FxUserView(viewContext, user);
        view.setOnMouseClicked(e -> {
            added.accept(user);
            e.consume();
        });
        userViews.add(view);
        runOnFx(() -> listUserView.addUserUI(view));
        if (viewContext.logger() != null) viewContext.logger().debug("(FX) User ajouté : " + user.getName());
    }

    private boolean exists(User user) {
        return userViews.stream().anyMatch(uv -> uv.getUser().equals(user));
    }

    @Override
    public void removeUser(User user) {
        handleRemoveUser(user);
    }

    private void handleRemoveUser(User user) {
        if (user == null) return;
        Optional<FxUserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        opt.ifPresent(v -> {
            userViews.remove(v);
            runOnFx(() -> listUserView.rebuildUI(new ArrayList<>(userViews)));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) User supprimé : " + user.getName());
        });
    }

    @Override
    public void updateUser(User user) {
        handleUpdateUser(user);
    }

    private void handleUpdateUser(User user) {
        if (user == null) return;
        Optional<FxUserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        opt.ifPresent(v -> {
            runOnFx(() -> listUserView.updateUserUI(v, user));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) User mis à jour : " + user.getName());
        });
    }

    @Override
    public void incrementUnread(User user) {
        handleIncrementUnread(user);
    }

    private void handleIncrementUnread(User user) {
        if (user == null) return;
        Optional<FxUserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        opt.ifPresent(v -> runOnFx(v::incrementUnread));
        if (opt.isEmpty() && viewContext.logger() != null)
            viewContext.logger().warn("(FX) User non trouvé pour incrément unread: " + user.getName());
    }

    @Override
    public void clearUnread(User user) {
        handleClearUnread(user);
    }

    private void handleClearUnread(User user) {
        if (user == null) return;
        Optional<FxUserView> opt = userViews.stream().filter(uv -> uv.getUser().equals(user)).findFirst();
        opt.ifPresent(v -> runOnFx(v::clearUnread));
        if (opt.isEmpty() && viewContext.logger() != null)
            viewContext.logger().warn("(FX) User non trouvé pour clear unread: " + user.getName());
    }
}
