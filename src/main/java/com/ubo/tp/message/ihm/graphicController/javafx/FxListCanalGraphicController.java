package com.ubo.tp.message.ihm.graphicController.javafx;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxCanalView;
import com.ubo.tp.message.ihm.view.javafx.FxListCanalView;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

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
    public void addCanal(Channel canal, Consumer<Channel> onSelect, Consumer<Channel> onLeave, boolean isOwner) {
        if (canal == null) return;
        boolean exists = canalViews.stream().anyMatch(cv -> cv.getChannel().equals(canal));
        if (exists) {
            if (viewContext.logger() != null) viewContext.logger().warn("(FX) Canal déjà présent : " + canal.getName());
            return;
        }
        FxCanalView view = new FxCanalView(viewContext, canal, onLeave, isOwner);
        view.setOnMouseClicked(e -> onSelect.accept(canal));
        canalViews.add(view);
        Platform.runLater(() -> listCanalView.addCanalUI(view));
        if (viewContext.logger() != null) viewContext.logger().debug("(FX) Canal ajouté : " + canal.getName());
    }

    @Override
    public void removeCanal(Channel canal) {
        if (canal == null) return;
        Optional<FxCanalView> opt = canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst();
        if (opt.isPresent()) {
            canalViews.remove(opt.get());
            Platform.runLater(() -> listCanalView.rebuildUI(new ArrayList<>(canalViews)));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Canal supprimé : " + canal.getName());
        }
    }

    @Override
    public void updateCanal(Channel canal) {
        if (canal == null) return;
        if (viewContext.logger() != null)
            viewContext.logger().debug("(FX) Canal mis à jour (no-op) : " + canal.getName());
    }

    @Override
    public void setupNewChannelForm(List<User> availableUsers, ChannelCreationCallback onConfirm) {
        Platform.runLater(() -> {
            listCanalView.setOnNewChannelConfirm(onConfirm);
            listCanalView.setAvailableUsers(availableUsers);
        });
        if (viewContext.logger() != null)
            viewContext.logger().debug("(FX) Formulaire canal configuré avec " +
                    (availableUsers != null ? availableUsers.size() : 0) + " utilisateurs");
    }
}
