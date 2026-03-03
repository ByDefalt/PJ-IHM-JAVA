package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IListMessageGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListMessageView;
import com.ubo.tp.message.ihm.view.swing.MessageView;

import javax.swing.*;
import java.util.*;

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

    public ListMessageGraphicController(ViewContext viewContext, ListMessageView listMessageView) {
        this.viewContext = viewContext;
        this.listMessageView = listMessageView;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Mappe une liste de Message métier vers les MessageView correspondantes,
     * en respectant l'ordre chronologique du TreeSet.
     */
    private ArrayList<MessageView> toViewList(List<Message> filteredMessages) {
        if (filteredMessages == null || filteredMessages.isEmpty()) return new ArrayList<>();
        // On itère sur le TreeSet (trié par date) et on ne retient que
        // les MessageView dont le message figure dans la liste filtrée.
        // Ainsi l'ordre chronologique est toujours garanti, quelle que soit
        // l'ordre dans lequel le controller transmet les messages filtrés.
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

    // -------------------------------------------------------------------------
    // IListMessageGraphicController
    // -------------------------------------------------------------------------

    @Override
    public void addMessage(Message message, List<Message> filteredMessages) {
        if (message == null || listMessageView == null) return;

        Runnable task = () -> {
            boolean alreadyPresent = messages.stream()
                    .anyMatch(mv -> mv.getMessage().equals(message));
            if (!alreadyPresent) {
                messages.add(new MessageView(viewContext, message));
                if (viewContext.logger() != null)
                    viewContext.logger().debug("Message ajouté : " + message);
            }
            rebuildView(filteredMessages);
        };

        if (SwingUtilities.isEventDispatchThread()) task.run();
        else SwingUtilities.invokeLater(task);
    }

    @Override
    public void removeMessage(Message message, List<Message> filteredMessages) {
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

        if (SwingUtilities.isEventDispatchThread()) task.run();
        else SwingUtilities.invokeLater(task);
    }

    @Override
    public void updateMessage(Message message, List<Message> filteredMessages) {
        if (message == null || listMessageView == null) return;

        Runnable task = () -> {
            Optional<MessageView> opt = messages.stream()
                    .filter(mv -> mv.getMessage().equals(message))
                    .findFirst();
            if (opt.isPresent()) {
                MessageView mv = opt.get();
                messages.remove(mv);
                listMessageView.updateMessageUI(mv, message);
                messages.add(mv);
                if (viewContext.logger() != null)
                    viewContext.logger().debug("Message mis à jour : " + message);
            } else {
                if (viewContext.logger() != null)
                    viewContext.logger().warn("Message non trouvé pour mise à jour : " + message);
            }
            rebuildView(filteredMessages);
        };

        if (SwingUtilities.isEventDispatchThread()) task.run();
        else SwingUtilities.invokeLater(task);
    }

    @Override
    public void selectedChanged(List<Message> filteredMessages) {
        if (viewContext == null || listMessageView == null) return;

        Runnable task = () -> rebuildView(filteredMessages);

        if (SwingUtilities.isEventDispatchThread()) task.run();
        else SwingUtilities.invokeLater(task);
    }
}