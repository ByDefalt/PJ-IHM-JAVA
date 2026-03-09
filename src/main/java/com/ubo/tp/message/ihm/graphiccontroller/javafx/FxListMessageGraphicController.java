package com.ubo.tp.message.ihm.graphiccontroller.javafx;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListMessageGraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxListMessageView;
import com.ubo.tp.message.ihm.view.javafx.FxMessageView;
import javafx.application.Platform;

import java.util.*;
import java.util.function.Consumer;

/**
 * Graphic controller de la liste des messages — JavaFX.
 * Délègue tout filtrage au controller métier.
 */
public class FxListMessageGraphicController implements IListMessageGraphicController {

    private final ViewContext viewContext;
    private final FxListMessageView listMessageView;
    private Consumer<Message> onDeleteMessage;
    private java.util.UUID deletableSenderUuid;

    /**
     * Source de vérité triée chronologiquement.
     */
    private final TreeSet<FxMessageView> messages = new TreeSet<>(
            Comparator.comparingLong((FxMessageView mv) -> mv.getMessage().getEmissionDate())
                    .thenComparing(mv -> mv.getMessage().getUuid().toString())
    );

    public FxListMessageGraphicController(ViewContext viewContext, FxListMessageView listMessageView) {
        this.viewContext = viewContext;
        this.listMessageView = listMessageView;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private List<FxMessageView> toViewList(List<Message> filtered) {
        if (filtered == null || filtered.isEmpty()) return Collections.emptyList();
        Set<Message> set = new HashSet<>(filtered);
        List<FxMessageView> result = new ArrayList<>();
        for (FxMessageView mv : messages) {
            if (set.contains(mv.getMessage())) result.add(mv);
        }
        return result;
    }

    private void rebuildView(List<Message> filtered) {
        List<FxMessageView> ordered = toViewList(filtered);
        Platform.runLater(() -> listMessageView.rebuildUI(ordered));
        if (viewContext.logger() != null)
            viewContext.logger().debug("(FX) Vue messages reconstruite : " + ordered.size());
    }

    // -------------------------------------------------------------------------
    // IListMessageGraphicController
    // -------------------------------------------------------------------------

    @Override
    public void setOnDeleteMessage(Consumer<Message> onDelete, java.util.UUID connectedUserUuid) {
        this.onDeleteMessage = onDelete;
        this.deletableSenderUuid = connectedUserUuid;
    }

    @Override
    public void addMessage(Message message, List<Message> filteredMessages) {
        if (message == null) return;
        boolean exists = messages.stream().anyMatch(mv -> mv.getMessage().equals(message));
        if (!exists) {
            // canDelete géré par le controller métier via setOnDeleteMessage :
            // si onDeleteMessage != null c'est que ce message peut être supprimé par cet utilisateur
            Consumer<Message> deleteCallback = resolveDeleteCallback(message);
            messages.add(new FxMessageView(viewContext, message, deleteCallback, deleteCallback != null));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Message ajouté");
        }
        rebuildView(filteredMessages);
    }

    /** Retourne le callback de suppression si l'auteur du message correspond au filtre enregistré, null sinon. */
    private Consumer<Message> resolveDeleteCallback(Message message) {
        if (onDeleteMessage == null || message.getSender() == null) return null;
        // Le controller métier a défini onDeleteMessage = deleteForCurrentUser
        // On stocke aussi l'UUID de l'utilisateur connecté pour filtrer
        return deletableSenderUuid != null
                && message.getSender().getUuid().equals(deletableSenderUuid)
                ? onDeleteMessage : null;
    }

    @Override
    public void removeMessage(Message message, List<Message> filteredMessages) {
        if (message == null) return;
        messages.removeIf(mv -> mv.getMessage().equals(message));
        rebuildView(filteredMessages);
    }

    @Override
    public void updateMessage(Message message, List<Message> filteredMessages) {
        if (message == null) return;
        Optional<FxMessageView> opt = messages.stream()
                .filter(mv -> mv.getMessage().getUuid().equals(message.getUuid()))
                .findFirst();
        if (opt.isPresent()) {
            messages.remove(opt.get());
            Consumer<Message> cb = resolveDeleteCallback(message);
            messages.add(new FxMessageView(viewContext, message, cb, cb != null));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Message mis à jour");
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("(FX) Message non trouvé pour mise à jour");
        }
        rebuildView(filteredMessages);
    }

    @Override
    public void refreshSenderInMessages(User updatedUser, List<Message> filteredMessages) {
        if (updatedUser == null) return;
        // Reconstruit toutes les FxMessageView dont le sender correspond à updatedUser
        List<FxMessageView> toReplace = messages.stream()
                .filter(mv -> mv.getMessage().getSender() != null
                        && mv.getMessage().getSender().getUuid().equals(updatedUser.getUuid()))
                .toList();

        for (FxMessageView mv : toReplace) {
            Message old = mv.getMessage();
            messages.remove(mv);
            messages.add(new FxMessageView(viewContext, new Message(
                    old.getUuid(), updatedUser, old.getRecipient(), old.getEmissionDate(), old.getText()
            )));
        }
        if (!toReplace.isEmpty()) {
            if (viewContext.logger() != null)
                viewContext.logger().debug("(FX) Sender mis à jour dans " + toReplace.size() + " message(s)");
            rebuildView(filteredMessages);
        }
    }

    @Override
    public void selectedChanged(List<Message> filteredMessages) {
        rebuildView(filteredMessages);
    }

}

