package com.ubo.tp.message.ihm.graphicController.javafx;

import com.ubo.tp.message.datamodel.Message;
import com.ubo.tp.message.ihm.contexte.ViewContext;
import com.ubo.tp.message.ihm.graphicController.service.IListMessageGraphicController;
import com.ubo.tp.message.ihm.view.javafx.FxListMessageView;
import com.ubo.tp.message.ihm.view.javafx.FxMessageView;
import javafx.application.Platform;

import java.util.*;

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
    public void addMessage(Message message, List<Message> filteredMessages) {
        if (message == null) return;
        boolean exists = messages.stream().anyMatch(mv -> mv.getMessage().equals(message));
        if (!exists) {
            messages.add(new FxMessageView(viewContext, message));
            if (viewContext.logger() != null) viewContext.logger().debug("(FX) Message ajouté");
        }
        rebuildView(filteredMessages);
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
        messages.stream().filter(mv -> mv.getMessage().equals(message)).findFirst()
                .ifPresent(mv -> {
                    messages.remove(mv);
                    Platform.runLater(() -> listMessageView.updateMessageUI(mv, message));
                    messages.add(mv);
                });
        rebuildView(filteredMessages);
    }

    @Override
    public void selectedChanged(List<Message> filteredMessages) {
        rebuildView(filteredMessages);
    }
}

