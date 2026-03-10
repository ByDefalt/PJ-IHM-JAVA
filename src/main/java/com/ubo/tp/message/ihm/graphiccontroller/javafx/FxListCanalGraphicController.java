package com.ubo.tp.message.ihm.graphiccontroller.javafx;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxCanalView;
import com.ubo.tp.message.ihm.view.javafx.FxListCanalView;
import javafx.application.Platform;
import javafx.scene.input.MouseButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Graphic controller de la liste des canaux — JavaFX.
 */
public class FxListCanalGraphicController implements IListCanalGraphicController {

    private final ViewContext viewContext;
    private final FxListCanalView listCanalView;
    private final List<FxCanalView> canalViews = new ArrayList<>();

    public FxListCanalGraphicController(ViewContext viewContext, FxListCanalView listCanalView) {
        this.viewContext = viewContext;
        this.listCanalView = listCanalView;
    }

    @Override
    public void addCanal(Channel canal, Consumer<Channel> onSelect,
                         ChannelEditCallback onEdit, boolean isOwner, Supplier<List<User>> allUsersSupplier) {
        handleAddCanal(canal, onSelect, onEdit, isOwner, allUsersSupplier);
    }

    private void handleAddCanal(Channel canal, Consumer<Channel> onSelect,
                                ChannelEditCallback onEdit, boolean isOwner, Supplier<List<User>> allUsersSupplier) {
        if (canal == null) return;
        if (exists(canal)) {
            if (viewContext.logger() != null) viewContext.logger().warn("(FX) Canal déjà présent : " + canal.getName());
            return;
        }
        FxCanalView view = createCanalView(canal, onEdit, isOwner, allUsersSupplier);
        registerSelection(view, onSelect);
        canalViews.add(view);
        runOnFx(() -> listCanalView.addCanalUI(view));
        if (viewContext.logger() != null) viewContext.logger().debug("(FX) Canal ajouté : " + canal.getName());
    }

    private boolean exists(Channel canal) {
        return canalViews.stream().anyMatch(cv -> cv.getChannel().equals(canal));
    }

    private FxCanalView createCanalView(Channel canal, ChannelEditCallback onEdit, boolean isOwner, Supplier<List<User>> allUsersSupplier) {
        return new FxCanalView(viewContext, canal, onEdit, isOwner, allUsersSupplier);
    }

    private void registerSelection(FxCanalView view, Consumer<Channel> onSelect) {
        view.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) onSelect.accept(view.getChannel());
        });
    }

    @Override
    public void removeCanal(Channel canal) {
        handleRemoveCanal(canal);
    }

    private void handleRemoveCanal(Channel canal) {
        if (canal == null) return;
        Optional<FxCanalView> opt = canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst();
        opt.ifPresent(v -> {
            canalViews.remove(v);
            runOnFx(() -> listCanalView.rebuildUI(new ArrayList<>(canalViews)));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Canal supprimé : " + canal.getName());
        });
    }

    @Override
    public void updateCanal(Channel canal) {
        handleUpdateCanal(canal);
    }

    private void handleUpdateCanal(Channel canal) {
        if (canal == null) return;
        Optional<FxCanalView> opt = canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst();
        if (opt.isPresent()) {
            FxCanalView view = opt.get();
            runOnFx(() -> view.updateChannel(canal));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Canal mis à jour : " + canal.getName());
        } else {
            if (viewContext.logger() != null)
                viewContext.logger().warn("(FX) Canal non trouvé pour mise à jour : " + canal.getName());
        }
    }

    @Override
    public void incrementUnread(Channel canal) {
        handleIncrementUnread(canal);
    }

    private void handleIncrementUnread(Channel canal) {
        if (canal == null) return;
        canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst()
                .ifPresent(view -> runOnFx(view::incrementUnread));
    }

    @Override
    public void clearUnread(Channel canal) {
        handleClearUnread(canal);
    }

    private void handleClearUnread(Channel canal) {
        if (canal == null) return;
        canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst()
                .ifPresent(view -> runOnFx(view::clearUnread));
    }

    @Override
    public void setupNewChannelForm(List<User> availableUsers, ChannelCreationCallback onConfirm) {
        handleSetupNewChannelForm(availableUsers, onConfirm);
    }

    private void handleSetupNewChannelForm(List<User> availableUsers, ChannelCreationCallback onConfirm) {
        runOnFx(() -> {
            listCanalView.setOnNewChannelConfirm(onConfirm);
            listCanalView.setAvailableUsers(availableUsers);
        });
        if (viewContext.logger() != null)
            viewContext.logger().debug("(FX) Formulaire canal configuré avec " + (availableUsers != null ? availableUsers.size() : 0) + " utilisateurs");
    }

    private void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }
}
