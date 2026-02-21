package com.ubo.tp.message.ihm.graphicController.swing;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.graphicController.service.IListMessageGraphicController;
import com.ubo.tp.message.ihm.view.swing.ListMessageView;
import com.ubo.tp.message.ihm.view.swing.MessageView;
import com.ubo.tp.message.ihm.view.contexte.ViewContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

public class ListMessageGraphicController implements IListMessageGraphicController {

    private final ViewContext viewContext;
    private final ListMessageView listMessageView;

    /**
     * Source de vérité : ensemble trié chronologiquement.
     * Le comparateur secondaire sur l'UUID garantit qu'aucun doublon n'est silencieusement
     * écrasé en cas de messages avec un timestamp identique.
     */
    private final TreeSet<MessageView> messages = new TreeSet<>(
            Comparator.comparingLong((MessageView mv) -> mv.getMessage().getEmissionDate())
                    .thenComparing(mv -> mv.getMessage().getUuid().toString())
    );

    public ListMessageGraphicController(ViewContext viewContext, ListMessageView listMessageView) {
        this.viewContext = viewContext;
        this.listMessageView = listMessageView;
    }

    @Override
    public void addMessage(Message message) {
        if (message == null || listMessageView == null) return;

        boolean alreadyPresent = messages.stream()
                .anyMatch(mv -> mv.getMessage().equals(message));

        if (alreadyPresent) {
            if (viewContext.logger() != null) viewContext.logger().debug("Message déjà présent, ignoré : " + message);
            return;
        }

        MessageView messageView = new MessageView(viewContext, message);
        messages.add(messageView);

        listMessageView.rebuildUI(new ArrayList<>(messages));

        if (messages.last() == messageView) {
            listMessageView.scrollToBottom();
        }

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
            listMessageView.rebuildUI(new ArrayList<>(messages));
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

            listMessageView.rebuildUI(new ArrayList<>(messages));
            if (viewContext.logger() != null) viewContext.logger().debug("Message mis à jour : " + message);
        } else {
            if (viewContext.logger() != null) viewContext.logger().warn("Message non trouvé pour mise à jour : " + message);
        }
    }
}