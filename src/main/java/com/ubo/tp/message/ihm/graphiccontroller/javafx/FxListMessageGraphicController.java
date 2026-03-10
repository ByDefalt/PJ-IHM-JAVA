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
    /**
     * Source de vérité triée chronologiquement.
     */
    private final TreeSet<FxMessageView> messages = new TreeSet<>(
            Comparator.comparingLong((FxMessageView mv) -> mv.getMessage().getEmissionDate())
                    .thenComparing(mv -> mv.getMessage().getUuid().toString())
    );
    private Consumer<Message> onDeleteMessage;
    private java.util.UUID deletableSenderUuid;

    public FxListMessageGraphicController(ViewContext viewContext, FxListMessageView listMessageView) {
        this.viewContext = viewContext;
        this.listMessageView = listMessageView;
    }

    // Helpers
    private List<FxMessageView> toViewList(List<Message> filtered) {
        if (filtered == null || filtered.isEmpty()) return Collections.emptyList();
        Set<Message> set = new HashSet<>(filtered);
        List<FxMessageView> result = new ArrayList<>();
        for (FxMessageView mv : messages) {
            if (set.contains(mv.getMessage())) result.add(mv);
        }
        return result;
    }

    private void runOnFx(Runnable r) {
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }

    private void rebuildView(List<Message> filtered) {
        List<FxMessageView> ordered = toViewList(filtered);
        runOnFx(() -> listMessageView.rebuildUI(ordered));
        if (viewContext.logger() != null)
            viewContext.logger().debug("(FX) Vue messages reconstruite : " + ordered.size());
    }

    // IListMessageGraphicController

    @Override
    public void setOnDeleteMessage(Consumer<Message> onDelete, java.util.UUID connectedUserUuid) {
        handleSetOnDeleteMessage(onDelete, connectedUserUuid);
    }

    private void handleSetOnDeleteMessage(Consumer<Message> onDelete, java.util.UUID connectedUserUuid) {
        this.onDeleteMessage = onDelete;
        this.deletableSenderUuid = connectedUserUuid;
        if (viewContext != null && viewContext.logger() != null) {
            viewContext.logger().debug("(FX) setOnDeleteMessage called; onDeletePresent=" + (onDelete != null) + ", connectedUserUuid=" + connectedUserUuid);
        }
    }

    @Override
    public void addMessage(Message message, List<Message> filteredMessages) {
        handleAddMessage(message, filteredMessages);
    }

    private void handleAddMessage(Message message, List<Message> filteredMessages) {
        if (message == null) return;
        boolean exists = messages.stream().anyMatch(mv -> mv.getMessage().equals(message));
        if (!exists) {
            Consumer<Message> deleteCallback = resolveDeleteCallback(message);
            messages.add(new FxMessageView(viewContext, message, () -> resolveDeleteCallback(message), deleteCallback != null));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Message ajouté");
        }
        rebuildView(filteredMessages);
    }

    private Consumer<Message> resolveDeleteCallback(Message message) {
        if (onDeleteMessage == null || message.getSender() == null) return null;
        return java.util.Objects.equals(message.getSender().getUuid(), deletableSenderUuid) ? onDeleteMessage : null;
    }

    @Override
    public void removeMessage(Message message, List<Message> filteredMessages) {
        handleRemoveMessage(message, filteredMessages);
    }

    private void handleRemoveMessage(Message message, List<Message> filteredMessages) {
        if (message == null) return;
        messages.removeIf(mv -> mv.getMessage().equals(message));
        rebuildView(filteredMessages);
    }

    @Override
    public void updateMessage(Message message, List<Message> filteredMessages) {
        handleUpdateMessage(message, filteredMessages);
    }

    private void handleUpdateMessage(Message message, List<Message> filteredMessages) {
        if (message == null) return;
        Optional<FxMessageView> opt = messages.stream()
                .filter(mv -> mv.getMessage().getUuid().equals(message.getUuid()))
                .findFirst();
        if (opt.isPresent()) {
            messages.remove(opt.get());
            Consumer<Message> cb = resolveDeleteCallback(message);
            messages.add(new FxMessageView(viewContext, message, () -> resolveDeleteCallback(message), cb != null));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Message mis à jour");
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("(FX) Message non trouvé pour mise à jour");
        }
        rebuildView(filteredMessages);
    }

    @Override
    public void refreshSenderInMessages(User updatedUser, List<Message> filteredMessages) {
        handleRefreshSenderInMessages(updatedUser, filteredMessages);
    }

    private void handleRefreshSenderInMessages(User updatedUser, List<Message> filteredMessages) {
        if (updatedUser == null) return;
        List<FxMessageView> toReplace = messages.stream()
                .filter(mv -> mv.getMessage().getSender() != null
                        && mv.getMessage().getSender().getUuid().equals(updatedUser.getUuid()))
                .toList();

        for (FxMessageView mv : toReplace) {
            Message old = mv.getMessage();
            messages.remove(mv);
            Message updated = new Message(old.getUuid(), updatedUser, old.getRecipient(), old.getEmissionDate(), old.getText());
            Consumer<Message> cb = resolveDeleteCallback(updated);
            messages.add(new FxMessageView(viewContext, updated, () -> resolveDeleteCallback(updated), cb != null));
        }
        if (!toReplace.isEmpty()) {
            if (viewContext.logger() != null)
                viewContext.logger().debug("(FX) Sender mis à jour dans " + toReplace.size() + " message(s)");
            rebuildView(filteredMessages);
        }
    }

    @Override
    public void selectedChanged(List<Message> filteredMessages) {
        handleSelectedChanged(filteredMessages);
    }

    private void handleSelectedChanged(List<Message> filteredMessages) {
        rebuildView(filteredMessages);
    }

}
