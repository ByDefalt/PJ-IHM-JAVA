package com.ubo.tp.message.ihm.graphiccontroller.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController;
import com.ubo.tp.message.ihm.view.swing.CanalView;
import com.ubo.tp.message.ihm.view.swing.ListCanalView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ListCanalGraphicController implements IListCanalGraphicController {

    private final ViewContext viewContext;
    private final ListCanalView listCanalView;
    private final List<CanalView> canalViews = new ArrayList<>();

    public ListCanalGraphicController(ViewContext viewContext, ListCanalView listCanalView) {
        this.viewContext = viewContext;
        this.listCanalView = listCanalView;
    }

    @Override
    public void addCanal(Channel canal, Consumer<Channel> onSelect, Consumer<Channel> onLeave, boolean isOwner) {
        if (canal == null || listCanalView == null) return;
        boolean alreadyPresent = canalViews.stream().anyMatch(cv -> cv.getChannel().equals(canal));
        if (alreadyPresent) {
            if (viewContext.logger() != null) viewContext.logger().warn("Canal déjà présent, ignoré : " + canal);
            return;
        }
        CanalView canalView = new CanalView(viewContext, canal, onLeave, isOwner);
        canalView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onSelect.accept(canalView.getChannel());
            }
        });
        int row = canalViews.size();
        canalViews.add(canalView);
        listCanalView.addCanalUI(canalView, row);
        if (viewContext.logger() != null) viewContext.logger().debug("Canal ajouté : " + canal);
    }

    @Override
    public void removeCanal(Channel canal) {
        if (canal == null || listCanalView == null) return;
        Optional<CanalView> opt = canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst();
        if (opt.isPresent()) {
            canalViews.remove(opt.get());
            listCanalView.rebuildUI(canalViews);
            if (viewContext.logger() != null) viewContext.logger().debug("Canal supprimé : " + canal);
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("Canal non trouvé, pas supprimé : " + canal);
        }
    }

    @Override
    public void updateCanal(Channel canal) {
        if (canal == null || listCanalView == null) return;
        Optional<CanalView> opt = canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst();
        if (opt.isPresent()) {
            listCanalView.updateCanalUI(opt.get(), canal);
            if (viewContext.logger() != null) viewContext.logger().debug("Canal mis à jour : " + canal);
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("Canal non trouvé pour mise à jour : " + canal);
        }
    }

    @Override
    public void setupNewChannelForm(List<User> availableUsers, ChannelCreationCallback onConfirm) {
        listCanalView.setAvailableUsers(availableUsers);
        listCanalView.setOnNewChannelConfirm(onConfirm);
        if (viewContext.logger() != null)
            viewContext.logger().debug("Formulaire canal configuré (Swing) avec " +
                    (availableUsers != null ? availableUsers.size() : 0) + " utilisateurs");
    }
}
