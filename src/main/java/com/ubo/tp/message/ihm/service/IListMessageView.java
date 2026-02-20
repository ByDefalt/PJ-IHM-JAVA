package com.ubo.tp.message.ihm.service;

import java.util.List;

/**
 * Interface pour la vue listant les messages. Utilise l'interface IMessageView
 * afin de découpler la vue du type concret.
 */
public interface IListMessageView extends View {
    void setMessages(List<IMessageView> newMessages);

    void setOnRefreshRequested(Runnable onRefreshRequested);
}
