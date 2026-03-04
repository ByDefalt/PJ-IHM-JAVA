package com.ubo.tp.message.controller.impl;

import com.ubo.tp.message.common.Constants;
import com.ubo.tp.message.controller.contexte.ControllerContext;
import com.ubo.tp.message.controller.service.IListCanalController;
import com.ubo.tp.message.core.database.observer.IChannelDatabaseObserver;
import com.ubo.tp.message.core.database.observer.IUserDatabaseObserver;
import com.ubo.tp.message.datamodel.Channel;
import com.ubo.tp.message.datamodel.User;
import com.ubo.tp.message.ihm.graphicController.service.IListCanalGraphicController;
import com.ubo.tp.message.observableProperty.ObservableTreeSet;

import java.util.*;
import java.util.function.Consumer;

public class ListCanalController implements IListCanalController, IChannelDatabaseObserver, IUserDatabaseObserver {

    private final ControllerContext context;
    private final IListCanalGraphicController graphicController;

    private final ObservableTreeSet<Channel> channels = new ObservableTreeSet<>(
            Comparator.comparing(Channel::getName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(Channel::getUuid)
    );

    public ListCanalController(ControllerContext context, IListCanalGraphicController graphicController) {
        this.context = Objects.requireNonNull(context);
        this.graphicController = graphicController;

        this.context.dataManager().addObserver((IChannelDatabaseObserver) this);
        this.context.dataManager().addObserver((IUserDatabaseObserver) this);

        refreshFormUsers();
        loadExistingChannels();
    }

    private void loadExistingChannels() {
        Set<Channel> existing = context.dataManager().getChannels();
        if (existing == null) return;
        for (Channel channel : existing) {
            notifyChannelAdded(channel);
        }
    }

    private void refreshFormUsers() {
        List<User> usersWithoutMe = getUsersWithoutMe();
        this.graphicController.setupNewChannelForm(usersWithoutMe, this::createNewChannel);
    }

    private List<User> getUsersWithoutMe() {
        Set<User> all = context.dataManager().getUsers();
        User me = context.session().getConnectedUser();
        List<User> result = new ArrayList<>();
        for (User u : all) {
            if (!u.equals(me) && !u.getUuid().equals(Constants.UNKNONWN_USER_UUID)) result.add(u);
        }
        return result;
    }

    @Override
    public IListCanalGraphicController getGraphicController() {
        return graphicController;
    }

    private void setSelected(Channel channel) {
        if (context.logger() != null) context.logger().debug("Canal sélectionné : " + channel);
        context.selected().setSelectedChannel(channel);
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        if (context.logger() != null) context.logger().debug("Canal ajouté : " + addedChannel);
        User me = context.session().getConnectedUser();

        if (addedChannel.isPrivate()
                && !addedChannel.getUsers().contains(me)
                && !addedChannel.getCreator().equals(me)) {
            if (context.logger() != null)
                context.logger().debug("Ignorer le canal privé qui ne m'inclut pas : " + addedChannel);
            return;
        }

        Consumer<Channel> onLeave = resolveLeaveAction(addedChannel, me);
        boolean isOwner = addedChannel.isPrivate() && addedChannel.getCreator().equals(me);
        //this.graphicController.addCanal(addedChannel, this::setSelected, onLeave, isOwner);
        if(context.logger() != null) context.logger().debug("TEST : Canal ajouté à la vue : " + addedChannel);
        this.channels.add(addedChannel);
    }


    private Consumer<Channel> resolveLeaveAction(Channel channel, User me) {
        if (!channel.isPrivate()) return null;
        if (channel.getCreator().equals(me)) return this::deleteChannel;
        if (channel.getUsers().contains(me)) return this::leaveChannel;
        return null;
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        if (context.logger() != null) context.logger().debug("Canal supprimé : " + deletedChannel);
        //this.graphicController.removeCanal(deletedChannel);
        this.channels.remove(deletedChannel);
    }

    @Override
    public void notifyChannelModified(Channel newChannel) {
        if (context.logger() != null) context.logger().debug("Canal modifié : nouveau=" + newChannel);
        User me = context.session().getConnectedUser();

        // Si le canal est privé et que je n'en fais plus partie → le retirer de la vue
        if (newChannel.isPrivate()
                && !newChannel.getCreator().equals(me)
                && !newChannel.getUsers().contains(me)) {
            this.channels.remove(newChannel);
            if (context.logger() != null)
                context.logger().debug("Canal privé modifié, je n'en fais plus partie, retiré de la vue : " + newChannel);
        } else {
            this.channels.remove(newChannel);
            this.channels.add(newChannel);
            if (context.logger() != null)
                context.logger().debug("Canal modifié, mis à jour dans la vue : " + newChannel);
        }
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        refreshFormUsers();
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        refreshFormUsers();
    }

    @Override
    public void notifyUserModified(User newUser) {
        refreshFormUsers();
    }

    public void createNewChannel(String channelName, boolean isPrivate, List<User> invitedUsers) {
        Channel newChannel;
        if (invitedUsers != null && !invitedUsers.isEmpty()) {
            List<User> members = new ArrayList<>(invitedUsers);
            newChannel = new Channel(context.session().getConnectedUser(), channelName, members);
        } else {
            newChannel = new Channel(context.session().getConnectedUser(), channelName, isPrivate);
        }
        context.dataManager().sendChannel(newChannel);
        if (context.logger() != null) context.logger().debug("Création d'un nouveau canal : " + newChannel);
    }

    /**
     * Supprime définitivement le canal (réservé au créateur).
     */
    public void deleteChannel(Channel channel) {
        if (channel == null) return;
        context.dataManager().deleteChannelFile(channel);
        if (context.logger() != null)
            context.logger().debug("Canal supprimé par son créateur : " + channel.getName());
    }

    /**
     * Retire l'utilisateur connecté de la liste des membres du canal,
     * puis persiste le canal mis à jour.
     * Le canal est supprimé de la vue via notifyChannelModified → removeCanal
     * (car il ne sera plus visible pour cet utilisateur).
     */
    public void leaveChannel(Channel channel) {
        if (channel == null) return;
        User me = context.session().getConnectedUser();

        List<User> newMembers = new ArrayList<>(channel.getUsers());
        newMembers.remove(me);

        // Reconstruire le canal avec la nouvelle liste
        Channel updated = new Channel(
                channel.getUuid(),
                channel.getCreator(),
                channel.getName(),
                newMembers,
                true
        );
        context.dataManager().sendChannel(updated);
        if (context.logger() != null)
            context.logger().debug("Quitté le canal : " + channel.getName());
    }

    @Override
    public ObservableTreeSet<Channel> getChannels() {
        return channels;
    }
}

