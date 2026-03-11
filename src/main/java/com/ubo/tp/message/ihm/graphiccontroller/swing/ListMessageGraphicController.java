package com.ubo.tp.message.ihm.graphiccontroller.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphiccontroller.service.IListMessageGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListMessageView;
import com.ubo.tp.message.ihm.view.swing.MessageView;

import javax.swing.*;
import java.util.*;
import java.util.function.Consumer;

/**
 * Contrôleur graphique Swing pour la liste des messages.
 * <p>
 * Responsable de la représentation triée des messages et de
 * la reconstruction de la vue lorsque le controller métier
 * fournit une liste filtrée.
 */
public class ListMessageGraphicController implements IListMessageGraphicController {

    private final ViewContext viewContext;
    private final ListMessageView listMessageView;
    /**
     * Source de vérité : toutes les MessageView connues, triées chronologiquement.
     * Le filtrage est entièrement délégué au controller métier.
     */
    private final TreeSet<MessageView> messages = new TreeSet<>(
            Comparator.comparingLong((MessageView mv) -> mv.getMessage().getEmissionDate())
                    .thenComparing(mv -> mv.getMessage().getUuid().toString())
    );
    private Consumer<Message> onDeleteMessage;
    private UUID deletableSenderUuid;

    public ListMessageGraphicController(ViewContext viewContext, ListMessageView listMessageView) {
        this.viewContext = viewContext;
        this.listMessageView = listMessageView;
    }

    /**
     * Construit une liste de MessageView à partir de la liste filtrée fournie.
     */
    private ArrayList<MessageView> toViewList(List<Message> filteredMessages) {
        if (filteredMessages == null || filteredMessages.isEmpty()) return new ArrayList<>();
        java.util.Set<Message> filteredSet = new java.util.HashSet<>(filteredMessages);
        ArrayList<MessageView> result = new ArrayList<>();
        for (MessageView mv : messages) {
            if (filteredSet.contains(mv.getMessage())) {
                result.add(mv);
            }
        }
        return result;
    }

    /**
     * Reconstruit la vue avec la liste filtrée fournie par le controller métier.
     */
    private void rebuildView(List<Message> filteredMessages) {
        if (listMessageView == null) return;
        ArrayList<MessageView> viewList = toViewList(filteredMessages);
        listMessageView.requestScrollToBottomOnce();
        listMessageView.rebuildUI(viewList);
        if (viewContext.logger() != null)
            viewContext.logger().debug("Vue reconstruite avec " + viewList.size() + " message(s)");
    }

    private Consumer<Message> resolveDeleteCallback(Message message) {
        if (onDeleteMessage == null || message.getSender() == null || deletableSenderUuid == null) return null;
        return message.getSender().getUuid().equals(deletableSenderUuid) ? onDeleteMessage : null;
    }

    private void runOnEDT(Runnable task) {
        if (SwingUtilities.isEventDispatchThread()) task.run();
        else SwingUtilities.invokeLater(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnDeleteMessage(Consumer<Message> onDelete, UUID connectedUserUuid) {
        this.onDeleteMessage = onDelete;
        this.deletableSenderUuid = connectedUserUuid;
    }

    @Override
    public void addMessage(Message message, List<Message> filteredMessages) {
        handleAddMessage(message, filteredMessages);
    }

    private void handleAddMessage(Message message, List<Message> filteredMessages) {
        if (message == null || listMessageView == null) return;
        Runnable task = () -> {
            boolean alreadyPresent = messages.stream()
                    .anyMatch(mv -> mv.getMessage().equals(message));
            if (!alreadyPresent) {
                Consumer<Message> cb = resolveDeleteCallback(message);
                messages.add(new MessageView(viewContext, message, cb, cb != null));
                if (viewContext.logger() != null)
                    viewContext.logger().debug("Message ajouté : " + message);
            }
            rebuildView(filteredMessages);
        };

        runOnEDT(task);
    }

    @Override
    public void removeMessage(Message message, List<Message> filteredMessages) {
        handleRemoveMessage(message, filteredMessages);
    }

    private void handleRemoveMessage(Message message, List<Message> filteredMessages) {
        if (message == null || listMessageView == null) return;

        Runnable task = () -> {
            Optional<MessageView> opt = messages.stream()
                    .filter(mv -> mv.getMessage().equals(message))
                    .findFirst();
            if (opt.isPresent()) {
                messages.remove(opt.get());
                if (viewContext.logger() != null)
                    viewContext.logger().debug("Message supprimé : " + message);
            } else {
                if (viewContext.logger() != null)
                    viewContext.logger().warn("Message non trouvé, pas supprimé : " + message);
            }
            rebuildView(filteredMessages);
        };

        runOnEDT(task);
    }

    @Override
    public void updateMessage(Message message, List<Message> filteredMessages) {
        handleUpdateMessage(message, filteredMessages);
    }

    private void handleUpdateMessage(Message message, List<Message> filteredMessages) {
        if (message == null || listMessageView == null) return;

        Runnable task = () -> {
            Optional<MessageView> opt = messages.stream()
                    .filter(mv -> mv.getMessage().getUuid().equals(message.getUuid()))
                    .findFirst();
            if (opt.isPresent()) {
                messages.remove(opt.get());
                Consumer<Message> cb = resolveDeleteCallback(message);
                messages.add(new MessageView(viewContext, message, cb, cb != null));
                if (viewContext.logger() != null)
                    viewContext.logger().debug("Message mis à jour : " + message);
            } else {
                if (viewContext.logger() != null)
                    viewContext.logger().warn("Message non trouvé pour mise à jour : " + message);
            }
            rebuildView(filteredMessages);
        };

        runOnEDT(task);
    }

    @Override
    public void refreshSenderInMessages(User updatedUser, List<Message> filteredMessages) {
        handleRefreshSenderInMessages(updatedUser, filteredMessages);
    }

    private void handleRefreshSenderInMessages(User updatedUser, List<Message> filteredMessages) {
        if (updatedUser == null || listMessageView == null) return;

        Runnable task = () -> {
            List<MessageView> toReplace = messages.stream()
                    .filter(mv -> mv.getMessage().getSender() != null
                            && mv.getMessage().getSender().getUuid().equals(updatedUser.getUuid()))
                    .toList();

            for (MessageView mv : toReplace) {
                Message old = mv.getMessage();
                messages.remove(mv);
                Message updated = new Message(old.getUuid(), updatedUser, old.getRecipient(), old.getEmissionDate(), old.getText());
                Consumer<Message> cb = resolveDeleteCallback(updated);
                messages.add(new MessageView(viewContext, updated, cb, cb != null));
            }
            if (!toReplace.isEmpty()) {
                if (viewContext.logger() != null)
                    viewContext.logger().debug("Sender mis à jour dans " + toReplace.size() + " message(s)");
                rebuildView(filteredMessages);
            }
        };

        runOnEDT(task);
    }

    @Override
    public void selectedChanged(List<Message> filteredMessages) {
        handleSelectedChanged(filteredMessages);
    }

    private void handleSelectedChanged(List<Message> filteredMessages) {
        if (viewContext == null || listMessageView == null) return;

        Runnable task = () -> rebuildView(filteredMessages);

        runOnEDT(task);
    }
}