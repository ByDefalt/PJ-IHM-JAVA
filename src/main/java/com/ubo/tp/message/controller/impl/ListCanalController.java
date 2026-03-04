package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ListCanalController implements IListCanalController, IChannelDatabaseObserver, IUserDatabaseObserver {

    private final ControllerContext context;
    private final IListCanalGraphicController graphicController;

    public ListCanalController(ControllerContext context, IListCanalGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        this.context.dataManager().addObserver((IChannelDatabaseObserver) this);
        this.context.dataManager().addObserver((IUserDatabaseObserver) this);

        // Configurer le formulaire avec les users actuels
        refreshFormUsers();
    }

    // ── Refresh de la liste d'users dans le formulaire ───────────────────────

    private void refreshFormUsers() {
        List<User> usersWithoutMe = getUsersWithoutMe();
        this.graphicController.setupNewChannelForm(usersWithoutMe, this::createNewChannel);
    }

    private List<User> getUsersWithoutMe() {
        Set<User> all = context.dataManager().getUsers();
        User me = context.session().getConnectedUser();
        List<User> result = new ArrayList<>();
        for (User u : all) {
            if (!u.equals(me)) result.add(u);
        }
        return result;
    }

    // ── IListCanalController ─────────────────────────────────────────────────

    @Override
    public IListCanalGraphicController getGraphicController() {
        return graphicController;
    }

    private void setSelected(Channel channel) {
        if (context.logger() != null) context.logger().debug("Canal sélectionné : " + channel);
        context.selected().setSelectedChannel(channel);
    }

    // ── IChannelDatabaseObserver ─────────────────────────────────────────────

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        if (context.logger() != null) context.logger().debug("Canal ajouté : " + addedChannel);
        this.graphicController.addCanal(addedChannel, this::setSelected);
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        if (context.logger() != null) context.logger().debug("Canal supprimé : " + deletedChannel);
        this.graphicController.removeCanal(deletedChannel);
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        if (context.logger() != null) context.logger().debug("Canal modifié : " + modifiedChannel);
        this.graphicController.updateCanal(modifiedChannel);
    }

    // ── IUserDatabaseObserver — met à jour la liste dans le formulaire ────────

    @Override
    public void notifyUserAdded(User addedUser) {
        refreshFormUsers();
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        refreshFormUsers();
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        refreshFormUsers();
    }

    // ── Création de canal ────────────────────────────────────────────────────

    public void createNewChannel(String channelName, boolean isPrivate) {
        createNewChannel(channelName, isPrivate, new ArrayList<>());
    }

    public void createNewChannel(String channelName, boolean isPrivate, List<User> invitedUsers) {
        Channel newChannel;
        if (invitedUsers != null && !invitedUsers.isEmpty()) {
            // Canal avec liste d'utilisateurs → utilise le constructeur dédié
            List<User> members = new ArrayList<>(invitedUsers);
            newChannel = new Channel(context.session().getConnectedUser(), channelName, members);
        } else {
            newChannel = new Channel(context.session().getConnectedUser(), channelName, isPrivate);
        }
        context.dataManager().sendChannel(newChannel);
        if (context.logger() != null) context.logger().debug("Création d'un nouveau canal : " + newChannel);
    }

    public boolean getAllUsersWithoutMe() {
        return context
                .dataManager()
                .getUsers()
                .remove(context.session().getConnectedUser());
    }
}

