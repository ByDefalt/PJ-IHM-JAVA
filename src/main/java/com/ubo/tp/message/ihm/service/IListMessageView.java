package com.ubo.tp.message.ihm.service;

import com.ubo.tp.message.datamodel.Message;

/**
 * Interface pour la vue listant les messages. Maintenant la vue travaille
 * directement avec des objets `Message` : elle est responsable de créer
 * les `MessageView` nécessaires.
 */
public interface IListMessageView extends View {
    void addMessage(Message message);

    void removeMessage(Message message);

    void updateMessage(Message message);
}
