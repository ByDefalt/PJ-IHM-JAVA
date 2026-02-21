package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;
import com.ubo.tp.message.ihm.view.swing.CanalView;
import com.ubo.tp.message.ihm.view.swing.ListCanalView;
import com.ubo.tp.message.logger.Logger;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ListCanalGraphicController implements IListCanalGraphicController {

    private final Logger LOGGER;
    private final ListCanalView listCanalView;

    private final List<CanalView> canalViews = new ArrayList<>();

    private Channel selectedCanal;

    public ListCanalGraphicController(Logger logger, ListCanalView listCanalView) {
        LOGGER = logger;
        this.listCanalView = listCanalView;
    }

    @Override
    public void addCanal(Channel canal) {
        if (canal == null || listCanalView == null) return;

        boolean alreadyPresent = canalViews.stream()
                .anyMatch(cv -> cv.getChannel().equals(canal));

        if (alreadyPresent) {
            if (LOGGER != null) LOGGER.warn("Canal déjà présent, ignoré : " + canal);
            return;
        }

        CanalView canalView = new CanalView(LOGGER, canal);

        canalView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (LOGGER != null) LOGGER.debug("CanalView cliqué: " + canal.getName());
                selectedCanal = canal;
            }
        });

        int row = canalViews.size();
        canalViews.add(canalView);
        listCanalView.addCanalUI(canalView, row);

        if (LOGGER != null) LOGGER.debug("Canal ajouté : " + canal);
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
                if (LOGGER != null) LOGGER.debug("Canal sélectionné supprimé, sélection réinitialisée");
            }
            listCanalView.rebuildUI(canalViews);
            if (LOGGER != null) LOGGER.debug("Canal supprimé : " + canal);
        } else {
            if (LOGGER != null) LOGGER.warn("Canal non trouvé, pas supprimé : " + canal);
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
            if (LOGGER != null) LOGGER.debug("Canal mis à jour : " + canal);
        } else {
            if (LOGGER != null) LOGGER.warn("Canal non trouvé pour mise à jour : " + canal);
        }
    }
}