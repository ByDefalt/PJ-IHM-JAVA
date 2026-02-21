package com.ubo.tp.message.core.selected;

import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;

public interface ISelected {
    /**
     * Ajoute un observateur à la session.
     *
     * @param observer
     */
    void addObserver(ISelectedObserver observer);

    /**
     * Retire un observateur à la session.
     *
     * @param observer
     */
    void removeObserver(ISelectedObserver observer);

    /**
     * @return l'utilisateur sélectionné.
     */
    User getSelectedUser();

    void setSelectedUser(User user);

    /**
     * @return le channel sélectionné.
     */
    Channel getSelectedChannel();

    void setSelectedChannel(Channel channel);

}
