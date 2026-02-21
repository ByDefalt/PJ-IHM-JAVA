package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;
import com.ubo.tp.message.ihm.view.swing.CanalView;
import com.ubo.tp.message.ihm.view.swing.ListCanalView;
import com.ubo.tp.message.ihm.view.contexte.ViewContext;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ListCanalGraphicController implements IListCanalGraphicController {

    private final ViewContext viewContext;
    private final ListCanalView listCanalView;

    private final List<CanalView> canalViews = new ArrayList<>();

    private Channel selectedCanal;

    public ListCanalGraphicController(ViewContext viewContext, ListCanalView listCanalView) {
        this.viewContext = viewContext;
        this.listCanalView = listCanalView;
    }

    @Override
    public void addCanal(Channel canal) {
        if (canal == null || listCanalView == null) return;

        boolean alreadyPresent = canalViews.stream()
                .anyMatch(cv -> cv.getChannel().equals(canal));

        if (alreadyPresent) {
            if (viewContext.logger() != null) viewContext.logger().warn("Canal déjà présent, ignoré : " + canal);
            return;
        }

        CanalView canalView = new CanalView(viewContext, canal);

        canalView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (viewContext.logger() != null) viewContext.logger().debug("CanalView cliqué: " + canal.getName());
                selectedCanal = canal;
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

        Optional<CanalView> opt = canalViews.stream()
                .filter(cv -> cv.getChannel().equals(canal))
                .findFirst();

        if (opt.isPresent()) {
            canalViews.remove(opt.get());
            if (canal.equals(selectedCanal)) {
                selectedCanal = null;
                if (viewContext.logger() != null) viewContext.logger().debug("Canal sélectionné supprimé, sélection réinitialisée");
            }
            listCanalView.rebuildUI(canalViews);
            if (viewContext.logger() != null) viewContext.logger().debug("Canal supprimé : " + canal);
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("Canal non trouvé, pas supprimé : " + canal);
        }
    }

    @Override
    public void updateCanal(Channel canal) {
        if (canal == null || listCanalView == null) return;

        Optional<CanalView> opt = canalViews.stream()
                .filter(cv -> cv.getChannel().equals(canal))
                .findFirst();

        if (opt.isPresent()) {
            listCanalView.updateCanalUI(opt.get(), canal);
            if (viewContext.logger() != null) viewContext.logger().debug("Canal mis à jour : " + canal);
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("Canal non trouvé pour mise à jour : " + canal);
        }
    }
}