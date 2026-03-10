package com.ubo.tp.message.ihm.graphiccontroller.swing;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListCanalGraphicController;
import com.ubo.tp.message.ihm.view.swing.CanalView;
import com.ubo.tp.message.ihm.view.swing.ListCanalView;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Contrôleur graphique Swing pour la liste des canaux.
 * <p>
 * Responsable de l'ajout, suppression, mise à jour et de la gestion des badges
 * de non-lus des canaux dans l'interface Swing.
 */
public class ListCanalGraphicController implements IListCanalGraphicController {

    private final ViewContext viewContext;
    private final ListCanalView listCanalView;
    private final List<CanalView> canalViews = new ArrayList<>();

    public ListCanalGraphicController(ViewContext viewContext, ListCanalView listCanalView) {
        this.viewContext = viewContext;
        this.listCanalView = listCanalView;
    }

    /**
     * Ajoute un canal à la liste.
     *
     * @param canal            Le canal à ajouter.
     * @param onSelect         Action à effectuer lors de la sélection du canal.
     * @param onEdit           Callback pour l'édition du canal.
     * @param isOwner          Indique si l'utilisateur est le propriétaire du canal.
     * @param allUsersSupplier Fournisseur de la liste de tous les utilisateurs.
     */
    @Override
    public void addCanal(Channel canal, Consumer<Channel> onSelect,
                         ChannelEditCallback onEdit, boolean isOwner, Supplier<List<User>> allUsersSupplier) {
        handleAddCanal(canal, onSelect, onEdit, isOwner, allUsersSupplier);
    }

    private void handleAddCanal(Channel canal, Consumer<Channel> onSelect,
                                ChannelEditCallback onEdit, boolean isOwner, Supplier<List<User>> allUsersSupplier) {
        if (canal == null || listCanalView == null) return;
        if (isAlreadyPresent(canal)) {
            logWarn("Canal déjà présent, ignoré: ", canal);
            return;
        }

        CanalView canalView = createCanalView(canal, onEdit, isOwner, allUsersSupplier);
        registerSelection(canalView, onSelect);
        int row = canalViews.size();
        canalViews.add(canalView);
        listCanalView.addCanalUI(canalView, row);
        logDebug("Canal ajouté : ", canal);
    }

    private boolean isAlreadyPresent(Channel canal) {
        return canalViews.stream().anyMatch(cv -> cv.getChannel().equals(canal));
    }

    private CanalView createCanalView(Channel canal, ChannelEditCallback onEdit, boolean isOwner, Supplier<List<User>> allUsersSupplier) {
        return new CanalView(viewContext, canal, onEdit, isOwner, allUsersSupplier);
    }

    private void registerSelection(CanalView canalView, Consumer<Channel> onSelect) {
        canalView.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    onSelect.accept(canalView.getChannel());
                }
            }
        });
    }

    private void logWarn(String msg, Channel canal) {
        if (viewContext.logger() != null) viewContext.logger().warn(msg + canal);
    }

    private void logDebug(String msg, Channel canal) {
        if (viewContext.logger() != null) viewContext.logger().debug(msg + canal);
    }

    /**
     * Supprime un canal de la liste.
     *
     * @param canal Le canal à supprimer.
     */
    @Override
    public void removeCanal(Channel canal) {
        handleRemoveCanal(canal);
    }

    private void handleRemoveCanal(Channel canal) {
        if (canal == null || listCanalView == null) return;
        Optional<CanalView> opt = canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst();
        if (opt.isPresent()) {
            canalViews.remove(opt.get());
            listCanalView.rebuildUI(canalViews);
            logDebug("Canal supprimé : ", canal);
        } else {
            logWarn("Canal non trouvé, pas supprimé : ", canal);
        }
    }

    /**
     * Met à jour un canal existant.
     *
     * @param canal Le canal avec les nouvelles informations.
     */
    @Override
    public void updateCanal(Channel canal) {
        handleUpdateCanal(canal);
    }

    private void handleUpdateCanal(Channel canal) {
        if (canal == null || listCanalView == null) return;
        Optional<CanalView> opt = canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst();
        if (opt.isPresent()) {
            CanalView existing = opt.get();
            listCanalView.updateCanalUI(existing, canal);
            logDebug("Canal mis à jour : ", canal);
        } else {
            logWarn("Canal non trouvé pour mise à jour : ", canal);
        }
    }

    /**
     * Incrémente le compteur de messages non lus pour un canal.
     *
     * @param canal Le canal concerné.
     */
    @Override
    public void incrementUnread(Channel canal) {
        handleIncrementUnread(canal);
    }

    private void handleIncrementUnread(Channel canal) {
        if (canal == null) return;
        canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst()
                .ifPresent(cv -> SwingUtilities.invokeLater(cv::incrementUnread));
    }

    /**
     * Efface le compteur de messages non lus pour un canal.
     *
     * @param canal Le canal concerné.
     */
    @Override
    public void clearUnread(Channel canal) {
        handleClearUnread(canal);
    }

    private void handleClearUnread(Channel canal) {
        if (canal == null) return;
        canalViews.stream().filter(cv -> cv.getChannel().equals(canal)).findFirst()
                .ifPresent(cv -> SwingUtilities.invokeLater(cv::clearUnread));
    }

    /**
     * Configure le formulaire de création d'un nouveau canal.
     *
     * @param availableUsers Liste des utilisateurs disponibles.
     * @param onConfirm      Callback à appeler lors de la confirmation de la création du canal.
     */
    @Override
    public void setupNewChannelForm(List<User> availableUsers, ChannelCreationCallback onConfirm) {
        handleSetupNewChannelForm(availableUsers, onConfirm);
    }

    private void handleSetupNewChannelForm(List<User> availableUsers, ChannelCreationCallback onConfirm) {
        listCanalView.setAvailableUsers(availableUsers);
        listCanalView.setOnNewChannelConfirm(onConfirm);
        if (viewContext.logger() != null)
            viewContext.logger().debug("Formulaire canal configuré (Swing) avec " + (availableUsers != null ? availableUsers.size() : 0) + " utilisateurs");
    }
}
