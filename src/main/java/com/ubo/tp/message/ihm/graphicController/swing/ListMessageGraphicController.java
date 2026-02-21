package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.core.selected.ISelectedObserver;
import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.graphicController.service.IListMessageGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListMessageView;
import com.ubo.tp.message.ihm.view.swing.MessageView;
import com.ubo.tp.message.ihm.contexte.ViewContext;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

public class ListMessageGraphicController implements IListMessageGraphicController, ISelectedObserver {

    private final ViewContext viewContext;
    private final ListMessageView listMessageView;

    /**
     * Source de vérité : ensemble trié chronologiquement
     */
    private final TreeSet<MessageView> messages = new TreeSet<>(
            Comparator.comparingLong((MessageView mv) -> mv.getMessage().getEmissionDate())
                    .thenComparing(mv -> mv.getMessage().getUuid().toString())
    );

    public ListMessageGraphicController(ViewContext viewContext, ListMessageView listMessageView) {
        this.viewContext = viewContext;
        this.listMessageView = listMessageView;

        // S'enregistrer comme observateur de la sélection (Selected garantit user XOR channel)
        // Protection null : viewContext et selected peuvent être null lors de certains tests
        if (this.viewContext != null && this.viewContext.selected() != null) {
            this.viewContext.selected().addObserver(this);
        }
    }

    // Retourne la liste de MessageView à afficher en fonction de la sélection courante
    private ArrayList<MessageView> getFilteredMessageViews() {
        ArrayList<MessageView> filtered = new ArrayList<>();
        if (viewContext == null || listMessageView == null) return filtered;
        if(viewContext.selected().getSelectedChannel() == null && viewContext.selected().getSelectedUser() == null) return filtered;

        var sel = viewContext.selected();
        if (sel.getSelectedUser() != null) {
            var selectedUser = sel.getSelectedUser();
            var connectedUser = (viewContext.session() != null) ? viewContext.session().getConnectedUser() : null;
            if (connectedUser == null) {
                // Pas d'utilisateur connecté -> aucun message privé à afficher
                return filtered;
            }
            var selUuid = selectedUser.getUuid();
            var meUuid = connectedUser.getUuid();

            for (MessageView mv : messages) {
                Message m = mv.getMessage();
                if (m == null) continue;
                boolean fromMeToSel = (m.getSender() != null && m.getSender().getUuid().equals(meUuid))
                        && (m.getRecipient() != null && m.getRecipient().equals(selUuid));
                boolean fromSelToMe = (m.getSender() != null && m.getSender().getUuid().equals(selUuid))
                        && (m.getRecipient() != null && m.getRecipient().equals(meUuid));

                if (fromMeToSel || fromSelToMe) filtered.add(mv);
            }
        } else {
            var channel = sel.getSelectedChannel();
            if (channel == null) return filtered;
            var channelUuid = channel.getUuid();
            for (MessageView mv : messages) {
                Message m = mv.getMessage();
                if (m == null) continue;
                if (m.getRecipient() != null && m.getRecipient().equals(channelUuid)) filtered.add(mv);
            }
        }

        return filtered;
    }

    @Override
    public void addMessage(Message message) {
        if (message == null || listMessageView == null) return;

        // Toujours exécuter l'ajout et la reconstruction UI sur l'Event Dispatch Thread.
        if (SwingUtilities.isEventDispatchThread()) {
            doAddMessageOnEDT(message);
        } else {
            SwingUtilities.invokeLater(() -> doAddMessageOnEDT(message));
        }
    }

    // Ajout exécuté sur l'EDT
    private void doAddMessageOnEDT(Message message) {
        boolean alreadyPresent = messages.stream()
                .anyMatch(mv -> mv.getMessage().equals(message));

        if (alreadyPresent) {
            if (viewContext.logger() != null) viewContext.logger().debug("Message déjà présent, ignoré : " + message);
            return;
        }

        MessageView messageView = new MessageView(viewContext, message);
        messages.add(messageView);

        ArrayList<MessageView> filtered = getFilteredMessageViews();

        // Scroller vers le bas si le nouveau message est visible dans la vue filtrée
        if (filtered.contains(messageView)) {
            listMessageView.requestScrollToBottomOnce();
        }

        listMessageView.rebuildUI(filtered);

        if (viewContext.logger() != null) viewContext.logger().debug("Message ajouté : " + message);
    }

    @Override
    public void removeMessage(Message message) {
        if (message == null || listMessageView == null) return;

        Optional<MessageView> opt = messages.stream()
                .filter(mv -> mv.getMessage().equals(message))
                .findFirst();

        if (opt.isPresent()) {
            messages.remove(opt.get());
            listMessageView.rebuildUI(getFilteredMessageViews());
            if (viewContext.logger() != null) viewContext.logger().debug("Message supprimé : " + message);
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("Message non trouvé, pas supprimé : " + message);
        }
    }

    @Override
    public void updateMessage(Message message) {
        if (message == null || listMessageView == null) return;

        Optional<MessageView> opt = messages.stream()
                .filter(mv -> mv.getMessage().equals(message))
                .findFirst();

        if (opt.isPresent()) {
            MessageView mv = opt.get();
            // Retirer avant de mettre à jour pour que le TreeSet se repositionne correctement
            messages.remove(mv);
            listMessageView.updateMessageUI(mv, message);
            messages.add(mv);

            listMessageView.rebuildUI(getFilteredMessageViews());
            if (viewContext.logger() != null) viewContext.logger().debug("Message mis à jour : " + message);
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("Message non trouvé pour mise à jour : " + message);
        }
    }

    @Override
    public void notifySelectedChanged() {
        if (viewContext == null || listMessageView == null) return;

        listMessageView.rebuildUI(getFilteredMessageViews());
        if (viewContext.logger() != null)
            viewContext.logger().debug("Messages filtrés selon la sélection courante : user=" + viewContext.selected().getSelectedUser() + " channel=" + viewContext.selected().getSelectedChannel());
    }
}